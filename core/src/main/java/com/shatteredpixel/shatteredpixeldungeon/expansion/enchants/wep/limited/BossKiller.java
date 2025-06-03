package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.GME;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;

public class BossKiller extends CountInscription {
    {
        defaultTriggers = 50;
    }
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        if(defender.properties().contains(Char.Property.BOSS) || defender.properties().contains(Char.Property.MINIBOSS)){
            damage = GME.accurateRound(damage * (Math.min(1.5f + weapon.buffedLvl() * 0.075f, 2.3f)));
            defender.sprite.centerEmitter().burst(Speck.factory(Speck.STAR), 8);
            consume(weapon, attacker);
        }
        return damage;
    }
}
