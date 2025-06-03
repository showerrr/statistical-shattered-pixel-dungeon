package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class RaiseDead extends CountInscription {
    {
        defaultTriggers = 15;
    }
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        if(damage >= defender.HP){
            defender.sprite.centerEmitter().burst(ShadowParticle.UP, Random.IntRange(7, 15));
            defender.damage(damage * 11 / 10, attacker);
            if(defender.isAlive()) {return 0;}
            Wraith wraith = Wraith.spawnAt(defender.pos, Wraith.class);
            Buff.affect(wraith, Corruption.class);
            consume(weapon, attacker);
        }
        return damage;
    }

    @Override
    public void useUp(Weapon w, Char attacker) {

        for(int i: PathFinder.NEIGHBOURS4){
            Wraith wraith = Wraith.spawnAt(attacker.pos + i, Wraith.class);
            if(wraith != null){
                Buff.affect(wraith, Corruption.class);
                wraith.HT = wraith.HP = Dungeon.depth * 5 / 2 + 5;
            }
            new Flare(5, 32).show(attacker.sprite, 2f).color(0x222222, false);
        }

        super.useUp(w, attacker);
    }
}
