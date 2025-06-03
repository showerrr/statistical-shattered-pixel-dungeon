package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.GME;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;

public class DoubleAttack extends CountInscription {
    {
        defaultTriggers = 100;
    }

    private int extraAttack = 0;
    private boolean attacking = false;

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        if(!attacking){
            attacking = true;
            extraAttack = 2;
            attacker.attack(defender);
            consume(weapon, attacker);
        }else if(extraAttack > 0){
            extraAttack--;
            attacker.attack(defender);
        }else{
            attacking = false;
        }
        return GME.accurateRound(damage * (Math.min(weapon.buffedLvl() * 0.015f + 0.36f, 0.5f)));
    }

}
