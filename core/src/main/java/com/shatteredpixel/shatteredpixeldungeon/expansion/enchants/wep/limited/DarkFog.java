package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SmokeScreen;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.RangeMap;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

public class DarkFog extends CountInscription {
    {
        defaultTriggers = 44;
    }
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        GameScene.add(Blob.seed(attacker.pos, Math.min(weapon.buffedLvl() * 3 + 30, 60), SmokeScreen.class));
        for(Integer cell: RangeMap.centeredRect(attacker.pos, 1, 1)){
            if(!Dungeon.level.solid[cell]) {
                GameScene.add(Blob.seed(attacker.pos, 20, SmokeScreen.class));
            }
        }
        consume(weapon, attacker);
        return damage;
    }

    @Override
    public void useUp(Weapon w, Char attacker) {
        Buff.affect(attacker, FogRelease.class).setLeft(100f + w.buffedLvl() * 10f);
        super.useUp(w, attacker);

    }

    public static class FogRelease extends Buff {
        private float left;
        @Override
        public boolean act() {
            spend(TICK);
            left -= 1f;
            if(left < 0.001f) detach();
            GameScene.add(Blob.seed(target.pos, 25, SmokeScreen.class));
            return true;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put("left_fog_turn", left);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            left = bundle.getFloat("left_fog_turn");
        }

        public void setLeft(float left) {
            this.left = left;
        }
    }
}
