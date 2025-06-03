package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfDread;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.watabou.utils.Random;

public class Terrifying extends CountInscription {
    {
        defaultTriggers = 15;
    }
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        if(damage >= defender.HP){
            defender.sprite.centerEmitter().burst(ShadowParticle.UP, Random.IntRange(7, 15));
            defender.damage(damage * 11 / 10, attacker);
            //This means defender haven't die (usually), interrupt
            if(defender.isAlive()) {return 0;}
            consume(weapon, attacker);

            float maxDist = Math.min(3f + weapon.buffedLvl() * 0.2f, 6f);
            for(Char ch: Actor.chars()){
                if(ch != attacker && ch != defender){
                    if(Dungeon.level.trueDistance(ch.pos, attacker.pos) < maxDist){
                        Buff.affect(ch, Terror.class, 12f).object = attacker.id();
                        Buff.affect(ch, Cripple.class, 12f);
                    }
                }
            }

            new Flare(5, 32).show(attacker.sprite, 2.5f).color(0x444444, false);

            consume(weapon, attacker);
        }
        return damage;
    }

    @Override
    public void useUp(Weapon w, Char attacker) {
        new ScrollOfDread().doRead();
        super.useUp(w, attacker);
    }
}
