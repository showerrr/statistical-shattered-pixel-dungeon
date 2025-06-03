package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;

public class Armory extends CountInscription {
    {
        defaultTriggers = 100;
    }
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        damage = (int) (attacker.drRoll() * (1.1f + Math.min(weapon.buffedLvl() * 0.06f, 0.3f)));
        damage -= defender.drRoll();
        consume(weapon, attacker);
        return damage;
    }
}
