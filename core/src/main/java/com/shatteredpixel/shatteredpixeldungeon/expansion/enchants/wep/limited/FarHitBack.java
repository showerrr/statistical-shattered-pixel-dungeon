package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.GME;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.HitBack;
import com.shatteredpixel.shatteredpixeldungeon.custom.visuals.effects.SpreadWave;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;

public class FarHitBack extends CountInscription {
    {
        defaultTriggers = 120;
    }
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        int opposite = attacker.pos + (attacker.pos - defender.pos);
        Ballistica trajectory = new Ballistica(attacker.pos, opposite, Ballistica.MAGIC_BOLT);
        WandOfBlastWave.throwChar(attacker, trajectory, 2, true, true, attacker.getClass());

        trajectory = new Ballistica(attacker.pos, defender.pos, Ballistica.STOP_TARGET);
        trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size()-1), Ballistica.PROJECTILE);
        WandOfBlastWave.throwChar(defender, trajectory, Math.min(7 + weapon.buffedLvl()*2/3, 14), true, true, attacker.getClass());

        consume(weapon, attacker);
        return GME.accurateRound(damage / 2f);
    }

    @Override
    public void useUp(Weapon w, Char attacker) {

        float maxDist = Math.min(7f + weapon.buffedLvl()*0.5f, 12f);
        for(Char ch : Actor.chars()){
            if(!ch.properties().contains(Char.Property.IMMOVABLE) && ch != attacker){
                float distance = Dungeon.level.trueDistance(attacker.pos, ch.pos);
                if(distance < maxDist){
                    WandOfBlastWave.throwChar(ch, HitBack.hitBack(attacker, ch), GME.accurateRound(2f * (maxDist - distance)) ,true, true, attacker.getClass());
                }
            }
        }
        SpreadWave.blast(attacker.sprite.center(), maxDist, 1f, 0xA0A0A0, null);

        super.useUp(w, attacker);
    }
}
