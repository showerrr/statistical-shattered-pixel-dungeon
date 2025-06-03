package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;

import java.util.LinkedHashMap;
import java.util.Map;

public class Polluting extends CountInscription {
    {
        defaultTriggers = 75;
    }
    private static final LinkedHashMap<Class<? extends FlavourBuff>, Float> toAdd = new LinkedHashMap<>();
    private static int length;
    static {
        toAdd.put(Cripple.class, 10f);
        toAdd.put(Blindness.class, 10f);
        toAdd.put(Weakness.class, 20f);
        toAdd.put(Hex.class, 20f);
        toAdd.put(Vertigo.class, 10f);
        toAdd.put(Amok.class, 10f);
        toAdd.put(Chill.class, 10f);
        toAdd.put(Roots.class, 5f);
        toAdd.put(Paralysis.class, 2f);
        toAdd.put(Vulnerable.class, 10f);
        toAdd.put(Slow.class, 4f);
        length = toAdd.size();
    }
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        int add = 1 + Math.min(weapon.buffedLvl() / 4, 2);
        float durationFactor = Math.min(1f + weapon.buffedLvl() * .05f, 1.75f);
        for(int i=0;i<add;++i){
            Map.Entry<Class<? extends FlavourBuff>, Float> entry = toAdd.entrySet().iterator().next();
            Buff.affect(defender, entry.getKey(), entry.getValue() * durationFactor);
            toAdd.remove(entry.getKey());
            toAdd.put(entry.getKey(), entry.getValue());
        }
        consume(weapon, attacker);
        return damage;
    }
}
