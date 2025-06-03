package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.Inscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.watabou.utils.Bundle;

public class TierChange extends Inscription {
    protected int tierChange;
    private int prevTier;
    //No proc effect
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        return damage;
    }

    @Override
    protected void onGain() {
        super.onGain();
        if(weapon instanceof MeleeWeapon){
            prevTier = ((MeleeWeapon) weapon).tier;
            ((MeleeWeapon) weapon).tier += tierChange;
            ((MeleeWeapon) weapon).tier = Math.max(0, ((MeleeWeapon) weapon).tier);
        }
    }

    @Override
    protected void onLose() {
        super.onLose();
        if(weapon instanceof MeleeWeapon){
            ((MeleeWeapon) weapon).tier = prevTier;
        }
    }

    @Override
    public String desc() {
        return M.L(TierChange.class, "desc", tierChange);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("prevTier", prevTier);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        prevTier = bundle.getInt("prevTier");
    }

    public static class TierUpOne extends TierChange{
        {
            tierChange = 1;
        }
    }

    public static class TierUpTwo extends TierChange{
        {
            tierChange = 2;
        }
    }

    public static class TierUpThree extends TierChange{
        {
            tierChange = 3;
        }
    }

    public static class TierDownOne extends TierChange{
        {
            tierChange = -1;
        }
    }

    public static class TierDownTwo extends TierChange{
        {
            tierChange = -2;
        }
    }

    public static class TierDownThree extends TierChange{
        {
            tierChange = -3;
        }
    }
}
