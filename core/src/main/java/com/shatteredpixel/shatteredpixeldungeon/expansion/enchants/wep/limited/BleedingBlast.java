package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.custom.buffs.ConsistBleeding;
import com.shatteredpixel.shatteredpixeldungeon.effects.Wound;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;

public class BleedingBlast extends CountInscription {
    {
        defaultTriggers = 150;
    }
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        ConsistBleeding bleeding = Buff.affect(defender, ConsistBleeding.class);
        if(bleeding != null) {
            boolean notFull = bleeding.newLayer(Math.min(4f + weapon.buffedLvl()*0.125f, 6f), Math.min(1.5f + weapon.buffedLvl()*0.05f, 2.25f));
            if (!notFull) {
                bleeding.burst();
                bleeding.detach();
            }
        }
        consume(weapon, attacker);
        return damage;
    }

    @Override
    public void useUp(Weapon w, Char attacker) {

        for(Mob m: Dungeon.level.mobs.toArray(new Mob[0])){
            if(m.alignment == Char.Alignment.ENEMY){
                if(Dungeon.level.distance(m.pos, attacker.pos) < 4){
                    if(!(m.properties().contains(Char.Property.BOSS))){
                        if(m.buff(Bleeding.class) != null || m.buff(ConsistBleeding.class) != null){
                            m.damage(m.HP + 1, attacker);
                            Wound.hit(m);
                        }
                    }
                }
            }
        }
        super.useUp(w, attacker);
    }
}
