package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FireImbue;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.BallisticaFloat;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.GME;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Random;

public class FireBeam extends CountInscription {
    {
        defaultTriggers = 75;
    }
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        Ballistica ba = new Ballistica(attacker.pos, defender.pos, BallisticaFloat.STOP_SOLID);
        for(int i: ba.subPath(1, ba.dist)){
            GameScene.add(Blob.seed(i, Math.min(2 + weapon.buffedLvl() / 3, 6), Fire.class));
            Char ch = Actor.findChar(i);
            if(ch != null && ch.buff(Burning.class)!=null){
                ch.damage(GME.accurateRound(Random.Float(1f, 1.25f+0.1f*weapon.buffedLvl())*(Dungeon.depth + 3)), Burning.class);
                ch.sprite.emitter().start(ElmoParticle.FACTORY, 0.075f, 10);
            }
        }
        consume(weapon, attacker);
        return damage;
    }

    @Override
    public void useUp(Weapon w, Char attacker) {
        Buff.affect(attacker, FireImbue.class).set(Math.min(30f + w.buffedLvl() * 3f, 75f));
        super.useUp(w, attacker);
    }
}
