package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.prison;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Skeleton;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.MobHard;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class SkeletonH extends MobHard {
    {
        EXP = 7;

        spriteClass = SkeletonSprite.class;

        HP = HT = 25;
        defenseSkill = 9;

        maxLvl = 10;

        loot = Generator.Category.WEAPON;
        lootChance = 0.1667f; //by default, see lootChance()

        properties.add(Property.UNDEAD);
        properties.add(Property.INORGANIC);
    }

    protected int revived = 0;
    @Override
    public void die(Object cause){
        revive();

        if (cause == Chasm.class) {
            super.die(cause);
            return;
        }

        boolean heroKilled = false;
        for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
            Char ch = findChar( pos + PathFinder.NEIGHBOURS8[i] );
            if (ch != null && ch.isAlive()) {
                int damage = Math.round(Random.NormalIntRange(6, 12));
                damage = Math.round( damage * AscensionChallenge.statModifier(this));
                damage = Math.max( 0,  damage - (ch.drRoll() + ch.drRoll()) );
                ch.damage( damage, this );
                if (ch == Dungeon.hero && !ch.isAlive()) {
                    heroKilled = true;
                }
            }
        }

        if (Dungeon.level.heroFOV[pos]) {
            Sample.INSTANCE.play( Assets.Sounds.BONES );
        }

        if (heroKilled) {
            Dungeon.fail( getClass() );
            GLog.n( Messages.get(this, "explo_kill") );
        }

        super.die(cause);
    }

    protected void revive(){
        revived++;
        if(revived>2) return;

        final int p = Dungeon.level.randomRespawnCell(this);
            MagicMissile.boltFromChar(sprite.parent,
                    MagicMissile.SHADOW,
                    sprite,
                    new Ballistica(pos, p, Ballistica.STOP_TARGET).collisionPos,
                    new Callback() {
                        @Override
                        public void call() {
                            CellEmitter.get(p).burst(Speck.factory(Speck.RATTLE), 16 - 4*revived);
                        }
                    });
            RevivedSkeleton rs = new RevivedSkeleton();
            rs.pos = p;
            rs.HP = (3-revived)*HT/4;
            rs.revived = revived;
            GameScene.add(rs);
            rs.beckon(pos);
    }

    @Override
    public void storeInBundle(Bundle b){
        super.storeInBundle(b);
        b.put("revived", revived);
    }

    @Override
    public void restoreFromBundle(Bundle b){
        super.restoreFromBundle(b);
        revived = b.getInt("revived");
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 2, 10 );
    }

    @Override
    public float lootChance() {
        //each drop makes future drops 1/2 as likely
        // so loot chance looks like: 1/6, 1/12, 1/24, 1/48, etc.
        return super.lootChance() * (float)Math.pow(1/2f, Dungeon.LimitedDrops.SKELE_WEP.count);
    }

    @Override
    public Item createLoot() {
        Dungeon.LimitedDrops.SKELE_WEP.count++;
        return super.createLoot();
    }

    @Override
    public int attackSkill( Char target ) {
        return 12;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 5);
    }


    public static class RevivedSkeleton extends SkeletonH{
        {
            maxLvl = -100;
        }
    }
}
