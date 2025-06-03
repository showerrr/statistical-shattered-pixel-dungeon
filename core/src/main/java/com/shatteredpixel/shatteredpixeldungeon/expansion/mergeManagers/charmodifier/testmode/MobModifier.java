package com.shatteredpixel.shatteredpixeldungeon.expansion.mergeManagers.charmodifier.testmode;


import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.custom.buffs.AttributeModifier;
import com.shatteredpixel.shatteredpixeldungeon.expansion.mergeManagers.charmodifier.ModifierTemplate;

public class MobModifier extends ModifierTemplate {

    {
        affectAtk = true;
        affectDef = true;
        affectAcc = true;
        affectEva = true;
        affectDmg = true;
    }

    @Override
    public float attackModify(Char attacker, Char defender, float atk) {
        AttributeModifier modifier = attacker.buff(AttributeModifier.class);
        if (modifier != null) {
            atk = modifier.affectAtk(atk);
        }
        return atk;
    }

    @Override
    public int defenseModify(Char attacker, Char defender, int dr, int damage) {
        AttributeModifier modifier = defender.buff(AttributeModifier.class);
        if (modifier != null) {
            dr = modifier.affectDef(dr);
        }
        return dr;
    }

    @Override
    public float accuracyModify(Char attacker, Char defender, float accuracy){
        AttributeModifier modifier = attacker.buff(AttributeModifier.class);
        if(modifier != null){
            accuracy = modifier.affectAcc(accuracy);
        }
        return accuracy;
    }

    @Override
    public float evasionModify(Char attacker, Char defender, float evasion){
        AttributeModifier modifier = defender.buff(AttributeModifier.class);
        if(modifier != null){
            evasion= modifier.affectEva(evasion);
        }
        return evasion;
    }

    @Override
    public int damage(Char ch, int damage, Object src) {
        AttributeModifier modifier = ch.buff(AttributeModifier.class);
        if(modifier != null){
            damage = modifier.affectDmg(damage);
        }
        return damage;
    }
}
