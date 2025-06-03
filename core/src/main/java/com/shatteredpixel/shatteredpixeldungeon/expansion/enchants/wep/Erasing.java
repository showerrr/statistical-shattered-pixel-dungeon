package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.Inscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

//erase inscription
public class Erasing extends Inscription {
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        return 0;
    }

    @Override
    public void attachToWeapon(Weapon w) {
        GLog.i(M.L(Erasing.class, "washed_away", w.name()));
        detachFromWeapon();
    }
}
