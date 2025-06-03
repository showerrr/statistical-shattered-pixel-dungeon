package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;

public class Gravity extends CountInscription {
    {
        defaultTriggers = 30;
    }
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        if(defender.flying){
            damage *= Math.min(1.5f + weapon.buffedLvl()*0.05f, 2.2f);
            defender.flying = false;
            defender.sprite.centerEmitter().burst(Speck.factory(Speck.STAR), 12);
            Dungeon.level.occupyCell(defender);
            if(defender.isAlive()){
                Buff.affect(defender, Vertigo.class, 10f);
                Buff.affect(defender, Cripple.class, 10f);
            }
            Camera.main.shake(2f, 0.3f);
            Sample.INSTANCE.play(Assets.Sounds.ROCKS, 0.85f);

            consume(weapon, attacker);
        }
        return damage;
    }

    @Override
    public void useUp(Weapon w, Char attacker) {
        for(Mob m: Dungeon.level.mobs.toArray(new Mob[0])){
            if(m.alignment == Char.Alignment.ENEMY){
                if(!m.properties().contains(Char.Property.BOSS)){
                    if(m.flying){
                        m.flying = false;
                        m.damage(m.HT - 1, attacker);
                        Buff.affect(m, Paralysis.class, 10f);
                    }
                    Buff.affect(m, Roots.class, 200f);
                }
            }
        }
        Camera.main.shake(5f, 1.5f);
        Sample.INSTANCE.play(Assets.Sounds.ROCKS, 1.2f);
        super.useUp(w, attacker);
    }
}
