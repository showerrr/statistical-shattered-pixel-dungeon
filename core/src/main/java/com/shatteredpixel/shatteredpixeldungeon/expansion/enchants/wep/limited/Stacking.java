package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.GME;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.watabou.utils.Bundle;

public abstract class Stacking extends CountInscription {
    protected int id = 0;
    protected int stacks = 0;

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("stack_ch_id", id);
        bundle.put("cur_stacks", stacks);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        id = bundle.getInt("stack_ch_id");
        stacks = bundle.getInt("cur_stacks");
    }

    public static class DamageStacking extends Stacking{
        {
            defaultTriggers = 125;
        }
        @Override
        public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
            if(defender.id() == id){
                ++stacks;
                damage = GME.accurateRound(damage + weapon.buffedLvl() * 1.25f * stacks);
                consume(weapon, attacker);
            }else{
                id = defender.id();
                stacks = 0;
            }
            return damage;
        }
    }

    public static class PoisonStacking extends Stacking{
        {
            defaultTriggers = 110;
        }
        @Override
        public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
            if(defender.id() == id){
                ++stacks;
                Buff.affect(defender, Poison.class).extend(3f + stacks + weapon.buffedLvl() * .25f * stacks);
                consume(weapon, attacker);
            }else{
                id = defender.id();
                Buff.affect(defender, Poison.class).extend(3f);
                stacks = 0;
            }
            return damage;
        }
    }
}
