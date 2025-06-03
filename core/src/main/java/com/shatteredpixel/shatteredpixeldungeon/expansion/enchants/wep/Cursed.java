package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.Inscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class Cursed extends Inscription {
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        damage = Math.round((damage + (defender.drRoll() + defender.drRoll()) * 0.55f)*3.4f);
        Buff.affect(attacker, CursedAttack.class).addCurse(damage/2.4f);
        return damage;
    }

    public static class CursedAttack extends Buff {
        {
            announced  = true;
            //act after buffs to check death more effectively
            actPriority = BUFF_PRIO - 10;
        }

        private float left;
        @Override
        public boolean act() {
            spend(TICK);
            left -= 1f;
            if(left <=0){
                detach();
            }
            if(target.HP < Math.floor(target.HT * .80)){
                target.HP = 0;
                if(target == Dungeon.hero){
                    if(!Dungeon.hero.isAlive()) {
                        GameScene.flash(0x101010, false);
                        Dungeon.hero.die(Cursed.class);
                        Dungeon.fail(Cursed.class);
                        GLog.n(M.L(Cursed.class, "on_death"));
                    }
                }else if(!target.isAlive()){
                    target.die(Cursed.class);
                }
            }
            return true;
        }

        public CursedAttack addCurse(float more){
            left += more;
            return this;
        }

        @Override
        public int icon() {
            return BuffIndicator.SACRIFICE;
        }

        @Override
        public String heroMessage() {
            return "";
        }

        @Override
        public String desc() {
            return M.L(this, "desc", dispTurns(left + 1f));
        }

        @Override
        public String toString() {
            return M.L(this, "name");
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put("left_curse", left);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            left = bundle.getFloat("left_curse");
        }
    }
}
