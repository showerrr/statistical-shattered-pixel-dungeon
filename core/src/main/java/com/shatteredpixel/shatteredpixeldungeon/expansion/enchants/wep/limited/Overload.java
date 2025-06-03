package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.GME;
import com.shatteredpixel.shatteredpixeldungeon.custom.visuals.HaloQuick;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Overload extends CountInscription {
    {
        defaultTriggers = 66;
    }
    private boolean buffed = false;

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        consume(weapon, attacker);
        return damage;
    }

    @Override
    protected void onGain() {
        super.onGain();
        if(!buffed) {
            weapon.level(weapon.level() * 2 + 4);
            buffed = true;
        }
    }

    @Override
    protected void onLose() {
        super.onLose();
        weapon.level(0);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("has_buffed", buffed);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        buffed = bundle.getBoolean("has_buffed");
    }

    @Override
    public void useUp(Weapon w, Char attacker) {
        Camera.main.shake(5f, 1f);
        HaloQuick halo = new HaloQuick(5f * DungeonTilemap.SIZE, 0xFF4427, 1f, 1f);
        halo.center(attacker.sprite.center());
        attacker.sprite.parent.add(halo);
        CellEmitter.center(attacker.pos).burst(BlastParticle.FACTORY, 50);
        Sample.INSTANCE.play(Assets.Sounds.BLAST, Random.Float(1.0f, 1.2f));
        for(Mob m: Dungeon.level.mobs.toArray(new Mob[0])){
            float dist = Dungeon.level.trueDistance(attacker.pos, m.pos);
            if(dist < 5f) {
                m.damage(GME.accurateRound((w.damageRoll(attacker) + w.damageRoll(attacker)) / 2f * (5f-dist) / 5f * (1f + 0.1f * w.buffedLvl())), attacker);
            }
        }
        attacker.damage(attacker.HP * 3 / 5 - 1, attacker);

        super.useUp(w, attacker);
    }
}
