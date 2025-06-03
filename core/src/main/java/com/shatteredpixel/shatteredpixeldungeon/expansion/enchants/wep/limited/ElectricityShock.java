package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.RangeMap;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

public class ElectricityShock extends CountInscription {
    {
        defaultTriggers = 75;
    }
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        for(int i: RangeMap.manhattanRing(defender.pos, 0, weapon.buffedLvl() > 6 ? 1 : 0)){
            GameScene.add(Blob.seed(i, 5, Electricity.class));
        }
        consume(weapon, attacker);
        return damage;
    }

    @Override
    public void useUp(Weapon w, Char attacker) {
        Buff.affect(attacker, ElectricityShockImmune.class, 50f);
        for( Integer cell : RangeMap.centeredRect(attacker.pos, 2, 2)){
            GameScene.add(Blob.seed(cell, Math.min(20 + w.buffedLvl() * 3, 45), Electricity.class));
        }
        super.useUp(w, attacker);
    }

    public static class ElectricityShockImmune extends FlavourBuff{
        {
            immunities.add(Electricity.class);
        }
    }
}
