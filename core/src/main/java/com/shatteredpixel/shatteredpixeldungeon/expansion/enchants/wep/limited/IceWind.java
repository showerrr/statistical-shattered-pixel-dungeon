package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BlobImmunity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.RangeMap;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Random;

public class IceWind extends CountInscription {
    {
        defaultTriggers = 40;
    }
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        if(Random.Int(5) < 2){
            int dist = 1 + Math.min(3, weapon.buffedLvl() / 4);
            for(int i: RangeMap.manhattanRing(attacker.pos, 1, dist)){
                GameScene.add(Blob.seed(i, 5, Freezing.class));
            }
            Buff.affect(attacker, BlobImmunity.class, 6f);
            consume(weapon, attacker);
        }
        return damage;
    }
}
