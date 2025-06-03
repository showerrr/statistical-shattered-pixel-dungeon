package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;

public class Invisible extends CountInscription {
    {
        defaultTriggers = 16;
    }
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        if(damage >= defender.HP) {
            defender.damage(damage * 11 / 10, attacker);
            if (defender.isAlive()) {
                return 0;
            }
            else{
                new FlavourBuff(){
                    {
                        actPriority = VFX_PRIO;
                    }
                    @Override
                    public boolean act() {
                        Buff.affect(attacker, Invisibility.class, 6f + weapon.buffedLvl() * 1.2f);
                        detach();
                        return true;
                    }
                }.attachTo(attacker);

                consume(weapon, attacker);
            }
        }
        return damage;
    }

    @Override
    public void useUp(Weapon w, Char attacker) {
        new FlavourBuff(){
            {
                actPriority = VFX_PRIO;
            }
            @Override
            public boolean act() {
                Buff.affect(attacker, Invisibility.class, 50f + 8.5f * Math.min(w.buffedLvl(), 15));
                detach();
                return true;
            }
        }.attachTo(attacker);
        super.useUp(w, attacker);
    }
}
