package com.shatteredpixel.shatteredpixeldungeon.expansion.mergeManagers.charmodifier;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;

public abstract class ModifierTemplate {
    protected boolean affectAtk = false;
    protected boolean affectDef = false;
    protected boolean affectAcc = false;
    protected boolean affectEva = false;
    protected boolean affectDmg = false;

    protected boolean atkProc = false;
    protected boolean defProc = false;

    protected boolean preBuff = false;
    protected boolean postBuff = false;

    public float attackModify(Char attacker, Char defender, float atk){return atk;}
    public int defenseModify(Char attacker, Char defender, int dr, int damage){return dr;}
    public float accuracyModify(Char attacker, Char defender, float accuracy){return accuracy;}
    public float evasionModify(Char attacker, Char defender, float evasion){return evasion;}
    public int damage(Char ch, int damage, Object src){return damage;}

    public int attackProc(Char attacker, Char defender, int damage){return damage;}
    public int defenseProc(Char attacker, Char defender, int damage){return damage;}

    public boolean preAddBuff(Char ch, Buff buff){return true;}
    public void postAddBuff(Char ch, Buff buff){}
}
