package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.GME;
import com.shatteredpixel.shatteredpixeldungeon.effects.Wound;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.watabou.utils.Random;

public class Assassination extends CountInscription {
    {
        defaultTriggers = 40;
    }
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        if (attacker instanceof Hero) {
            if (defender instanceof Mob && ((Mob) defender).surprisedBy(attacker)) {
                Wound.hit(defender);
                consume(weapon, attacker);
                return GME.accurateRound(damage * Random.Float(1.4f, 1.7f));
            }
        }
        return damage;
    }

    @Override
    public void useUp(Weapon w, Char attacker) {
        if(attacker instanceof Hero)
        for(Char ch : Actor.chars()){
            if(ch.alignment == Char.Alignment.ENEMY){
                if(ch instanceof Mob && attacker.fieldOfView[ch.pos] && Dungeon.level.distance(attacker.pos, ch.pos) < 10){
                    if(((Mob) ch).surprisedBy(attacker)){
                        Wound.hit(ch);
                        ch.damage(ch.HP - 1, attacker);
                    }
                }
            }
        }
        super.useUp(w, attacker);
    }
}
