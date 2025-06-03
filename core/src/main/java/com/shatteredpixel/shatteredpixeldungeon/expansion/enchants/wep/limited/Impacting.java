package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.GME;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;

public class Impacting extends CountInscription {

    {
        defaultTriggers = 50;
    }

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        float speed = Math.max(attacker.speed(), 0.8f);
        float amplifier = 1f + 2f * (1f-(float)Math.exp(-(speed-1f)/3f));
        if(amplifier > 1.5f){
            defender.sprite.centerEmitter().burst(Speck.factory(Speck.STAR), 6 + GME.accurateRound((amplifier - 1.5f)*8f));
        }
        consume(weapon, attacker);
        return GME.accurateRound(damage * amplifier);
    }

    @Override
    protected void onGain() {
        super.onGain();
        weapon.DLY *= 1.5;
    }

    @Override
    protected void onLose() {
        super.onLose();
        weapon.DLY /= 1.5;
    }
}
