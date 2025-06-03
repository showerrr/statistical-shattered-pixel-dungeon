package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mimic;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.ChallengeItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class MimicScroll extends ChallengeItem {
    {
        image = ItemSpriteSheet.SCROLL_YNGVI;
        bones = false;
        defaultAction = AC_READ;
        stackable = true;
        unique = true;
    }
    private static final String AC_READ = "read";

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        actions.add(AC_READ);
        return actions;
    }

    @Override
    public void execute( Hero hero, String action ) {
        super.execute(hero,action);
        if(action.equals(AC_READ)){
            if(Dungeon.bossLevel(Dungeon.depth)){
                GLog.n(Messages.get(this, "banished"));
                return;
            }
            for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                if (mob.alignment != Char.Alignment.ALLY) {
                    if (!mob.properties().contains(Char.Property.BOSS)
                            && !mob.properties().contains(Char.Property.MINIBOSS)){
                        int pos = mob.pos;

                        if(mob instanceof Sheep){
                            if(Random.Int(4)!=0){
                                continue;
                            }
                        }

                        //loot, copied from mimic
                        Item reward = null;
                        MimicForChallenge mimic;
                        if(Random.Int(8)==0) {
                            mimic = MimicForChallenge.spawnAt(pos, reward, GoldenMimicForChallenge.class);
                        }else {
                            mimic = MimicForChallenge.spawnAt(pos, reward);
                        }

                        mob.EXP = 0;
                        mob.destroy();
                        mob.sprite.killAndErase();
                        Dungeon.level.mobs.remove(mob);
                        TargetHealthIndicator.instance.target(null);

                        GameScene.add(mimic);
                    }
                }
            }
            for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )){
                if(mob instanceof MimicForChallenge){
                    Buff.affect(mob, MimicStatusShower.class);
                }
            }
            curUser.spend(1f);
            if(this.quantity == 1) this.detach(curUser.belongings.backpack);
            else this.quantity -= 1f;
            updateQuickslot();
        }
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }
}
