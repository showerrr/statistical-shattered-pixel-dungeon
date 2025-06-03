package com.shatteredpixel.shatteredpixeldungeon.custom.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.boss.HDKBuff;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

public class DeathCounter extends CounterBuff {
    {
        type = buffType.NEUTRAL;
        announced = true;
    }

    @Override
    public int icon() {
        return BuffIndicator.TIME;
    }

    @Override
    public boolean act() {
        countDown(1);
        if(count() <= 0){
            detach();
            target.die(DeathCounter.class);
        }else {
            target.sprite.showStatus(0xFF0000, String.valueOf(count()));
        }
        spend(TICK);
        return true;
    }

    @Override
    public String desc() {
        return M.L(this, "desc", count());
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }
}
