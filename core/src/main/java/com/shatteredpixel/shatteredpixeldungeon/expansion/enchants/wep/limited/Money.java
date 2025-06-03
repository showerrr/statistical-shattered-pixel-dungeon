package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;

public class Money extends CountInscription {
    {
        defaultTriggers = 100;
    }

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        if(attacker instanceof Hero) {
            int maxConsume = Dungeon.gold / 40;
            int dmgBonus = 20 + Dungeon.depth;
            dmgBonus = Math.min(maxConsume / 3, dmgBonus);
            Dungeon.gold -= dmgBonus * 3;
            damage += dmgBonus;
            consume(weapon, attacker);
        }
        return damage;
    }
}
