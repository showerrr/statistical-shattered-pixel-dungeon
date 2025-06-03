package com.shatteredpixel.shatteredpixeldungeon.expansion.mergeManagers.charmodifier.mobchallenge;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.custom.buffs.IgnoreArmor;
import com.shatteredpixel.shatteredpixeldungeon.custom.buffs.PositiveBuffProhibition;
import com.shatteredpixel.shatteredpixeldungeon.custom.buffs.ThornsShield;
import com.shatteredpixel.shatteredpixeldungeon.custom.buffs.ZeroAttack;
import com.shatteredpixel.shatteredpixeldungeon.custom.buffs.ZeroDefense;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.boss.HDKItem;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.GME;
import com.shatteredpixel.shatteredpixeldungeon.expansion.mergeManagers.charmodifier.ModifierTemplate;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class ExChallenge extends ModifierTemplate {
    {
        affectAtk = true;
        affectDef = true;
        affectDmg = true;
        postBuff = true;
    }

    @Override
    public float attackModify(Char attacker, Char defender, float atk) {
        if(attacker.buff(ZeroAttack.class) != null){
            atk = 0;
        }
        return atk;
    }

    @Override
    public int defenseModify(Char attacker, Char defender, int dr, int damage) {
        if(attacker.buff(IgnoreArmor.class) != null) {
            dr = 0;
        }
        if(defender.buff(ZeroDefense.class)!=null){
            dr = 0;
        }
        return dr;
    }

    @Override
    public int damage(Char ch, int damage, Object src) {

        HDKItem.KingAmulet.DamageCheater damageCheater = ch.buff(HDKItem.KingAmulet.DamageCheater.class);
        if (damageCheater != null) {
            damage = damageCheater.onDamage(ch, damage);
        }

        if(ch.buff(ThornsShield.class) != null){
            if(src instanceof Char){
                ((Char) src).damage(GME.accurateRound(damage* Random.Float(.7f, .8f)), this);
            }else{
                damage = 0;
            }
            if(src == Dungeon.hero && !((Hero) src).isAlive()){
                Dungeon.fail(ThornsShield.class);
                GLog.n(M.L(ThornsShield.class, "ondeath"));
            }
        }

        return damage;
    }

    @Override
    public void postAddBuff(Char ch, Buff buff) {
        if(ch.buff(PositiveBuffProhibition.class) != null) {
            if (!buff.revivePersists && buff.type == Buff.buffType.POSITIVE) {
                new FlavourBuff() {
                    {
                        actPriority = VFX_PRIO;
                    }

                    @Override
                    public void detach() {
                        buff.detach();
                        super.detach();
                    }
                }.attachTo(ch);
            }
        }
    }
}
