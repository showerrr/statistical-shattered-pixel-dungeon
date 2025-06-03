package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mimic;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.particles.Emitter;

public class MimicStatusShower extends Buff {
    Emitter stateFire;

    {
        this.announced = false;
    }

    @Override
    public boolean act() {
        spend(TICK);
        return true;
    }

    @Override
    public void fx(boolean on) {
        if (on) {
            if (target instanceof MimicForChallenge)
                stateFire = GameScene.emitter();
            stateFire.pos(target.sprite);
            double logPower = Math.log(((MimicForChallenge) target).showPower()) / 0.693;
            stateFire.pour(MimicParticle.FACTORY, (float) (1.5f / (logPower * logPower + 1f)));
        } else {
            if (stateFire != null) {
                stateFire.on = false;
                stateFire = null;
            }
        }
    }
}
