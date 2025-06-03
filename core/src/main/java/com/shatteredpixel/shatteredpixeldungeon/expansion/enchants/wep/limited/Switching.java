package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;

public class Switching extends CountInscription {
    {
        defaultTriggers = 333;
    }

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        if(!(attacker.properties().contains(Char.Property.IMMOVABLE) || defender.properties().contains(Char.Property.IMMOVABLE))) {
            int tempPos = defender.pos;
            ScrollOfTeleportation.appear(defender, attacker.pos);
            ScrollOfTeleportation.appear(attacker, tempPos);
        }
        consume(weapon, attacker);

        return damage/2;
    }

    @Override
    protected void onGain() {
        super.onGain();
        weapon.RCH <<= 1;
        weapon.RCH += 3;
    }

    @Override
    protected void onLose() {
        super.onLose();
        weapon.RCH -= 3;
        weapon.RCH >>= 1;
    }
}
