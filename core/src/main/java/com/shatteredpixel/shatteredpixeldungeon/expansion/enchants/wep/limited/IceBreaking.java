package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.custom.visuals.HaloQuick;
import com.shatteredpixel.shatteredpixeldungeon.effects.Wound;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class IceBreaking extends CountInscription {
    {
        defaultTriggers = 100;
    }

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        if(defender.buff(Frost.class) != null){
            damage *= Math.min(2.2f + weapon.buffedLvl() * 0.15f, 4f);
            Sample.INSTANCE.play(Assets.Sounds.HIT_SLASH, Random.Float(1.0f, 1.3f));
            Wound.hit(defender);

            float radius = Math.min(2f + weapon.buffedLvl()*0.12f, 3.6f);
            for(Char ch: Actor.chars()){
                if(ch.alignment == Char.Alignment.ENEMY && ch != defender && ch != attacker){
                    if(Dungeon.level.trueDistance(ch.pos, defender.pos)<radius){
                        Buff.affect(ch, Chill.class, 6f);
                    }
                }
            }

            attacker.sprite.parent.add(new HaloQuick(radius* DungeonTilemap.SIZE, 0x4479F2, 0.4f, 0.5f)
                    .point(DungeonTilemap.tileCenterToWorld(defender.pos).x, DungeonTilemap.tileCenterToWorld(defender.pos).y));
        }else{
            Chill chill = defender.buff(Chill.class);
            if(chill != null ){
                if(Random.Float()<chill.cooldown()/Chill.DURATION) {
                    new FlavourBuff() {
                        {
                            actPriority = VFX_PRIO;
                        }

                        public boolean act() {
                            Buff.affect(target, Frost.class, Frost.DURATION);
                            return super.act();
                        }
                    }.attachTo(defender);
                }else {
                    Buff.affect(defender, Chill.class, Random.Float(3f, 4f + weapon.buffedLvl() / 2f));
                }
            }else{
                Buff.affect(defender, Chill.class, Random.Float(3f, 4f + weapon.buffedLvl()/2f));
            }

        }
        consume(weapon, attacker);
        return damage;
    }

    @Override
    public void useUp(Weapon w, Char attacker) {

        GameScene.flash(0x4479F2);
        for(Char ch: Actor.chars()){
            if(ch.alignment == Char.Alignment.ENEMY){
                Buff.affect(ch, Frost.class, Random.Float(12f, 25f) + 2f * Math.min(w.buffedLvl(), 15));
            }
        }

        super.useUp(w, attacker);
    }
}
