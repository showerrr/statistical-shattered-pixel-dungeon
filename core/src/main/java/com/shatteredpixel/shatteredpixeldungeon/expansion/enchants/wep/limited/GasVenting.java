package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BlobImmunity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ToxicImbue;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.GME;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

public class GasVenting extends CountInscription {
    {
        defaultTriggers = 50;
    }

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        Ballistica ba = new Ballistica(attacker.pos, defender.pos, Ballistica.STOP_TARGET);
        for(int i: ba.subPath(1, ba.dist-1)){
            GameScene.add(Blob.seed(i, Math.min(20 + weapon.buffedLvl()*2, 40), ToxicGas.class));
        }
        GameScene.add(Blob.seed(defender.pos, Math.min(40 + weapon.buffedLvl() * 4, 80), ToxicGas.class));
        Buff.affect(attacker, BlobImmunity.class, 8f);

        consume(weapon, attacker);
        return GME.accurateRound(damage*.3f);
    }

    @Override
    public void useUp(Weapon w, Char attacker) {
        Buff.affect(attacker, ToxicImbue.class).set(Math.min(30f + w.buffedLvl() * 3f, 75f));
        super.useUp(w, attacker);
    }

    @Override
    protected void onGain() {
        super.onGain();
        weapon.RCH += 3;
    }

    @Override
    protected void onLose() {
        super.onLose();
        weapon.RCH -= 3;
    }
}
