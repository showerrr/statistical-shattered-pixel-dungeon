package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.GME;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.RangeMap;
import com.shatteredpixel.shatteredpixeldungeon.custom.visuals.effects.SpreadWave;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;

public class RangedSlash extends CountInscription {
    {
        defaultTriggers = 40;
    }
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        int dist = weapon.reachFactor(attacker);
        for(int i: RangeMap.centeredRect(attacker.pos, dist, dist)){
            Char ch = Actor.findChar(i);
            if(ch != null) {
                if (ch != attacker && ch.alignment != Char.Alignment.ALLY) {
                    ch.damage(GME.accurateRound(damage * .45f + .025f * Math.min(weapon.buffedLvl(), 16)), attacker);
                }
            }
        }
        SpreadWave.blast(attacker.sprite.center(), dist * .8f, .25f, 0xCCCCCC, null);
        consume(weapon, attacker);
        return damage;
    }
}
