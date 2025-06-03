package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;

public abstract class MobHard extends Mob {
    {
        immunities.add(AllyBuff.class);
    }
}
