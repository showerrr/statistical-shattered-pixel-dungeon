package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;

public class EnchRepeating extends CountInscription {
    {
        defaultTriggers = 30;
    }
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        if(weapon.enchantment != null){
            int trig = 2 + Math.min(weapon.buffedLvl() / 5, 2);
            for(int i=0; i<trig; ++i){
                damage = weapon.enchantment.proc(weapon, attacker, defender, damage);
            }
            consume(weapon, attacker);
        }
        return damage;
    }

    @Override
    public void useUp(Weapon w, Char attacker) {

        if(weapon.enchantment != null) {
            for (Char ch : Actor.chars()) {
                if (ch != attacker && ch.alignment != Char.Alignment.ALLY && attacker.fieldOfView[ch.pos]) {
                    for(int i=0;i<3;++i){
                        ch.damage(weapon.enchantment.proc(w, attacker, ch, 1), attacker);
                    }
                }
            }
        }
        super.useUp(w, attacker);
    }
}
