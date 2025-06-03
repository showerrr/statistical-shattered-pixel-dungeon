package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public abstract class Inscription implements Bundlable {

    protected Weapon weapon;

    protected void onGain(){}

    protected void onLose(){}

    public void attachToWeapon(Weapon w){
        weapon = w;
        if(weapon != null){
            weapon.inscription = this;
            onGain();
        }
    }

    public void detachFromWeapon(){
        if(weapon!=null){
            onLose();
            weapon.inscription = null;
        }
    }

    public String name(){
        return M.L(this, "name");
    }

    public String desc(){
        return M.L(this, "desc");
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {}

    @Override
    public void storeInBundle(Bundle bundle) {}

    public abstract int proc(Weapon weapon, Char attacker, Char defender, int damage);
}
