package com.shatteredpixel.shatteredpixeldungeon.expansion.magic.baseclass;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public abstract class SpellBase implements Bundlable{
    public float baseManaCost = 0f;
    public Category spellCate = Category.NONE;
    public int targetProperty;

    public int level;
    public int maxLevel;

    public abstract void castSpell(Char caster);

    public abstract void visualEffect(Char caster, int targetCell);

    public abstract void onHit(Char caster, int targetCell, Char victim);

    public abstract void dealDamage(Char caster, Char victim);

    public abstract void onDeath(Char caster, Char victim);

    public String name(){
        return M.L(this.getClass(), "name");
    }

    public String desc(){
        return M.L(this.getClass(), descKey());
    }

    public String descKey(){
        return "desc_" + level;
    }

    public float manaCost(){
        return baseManaCost;
    }

    public void upgrade(int lvl){
        level = Math.min(level + lvl, maxLevel);
    }

    public void degrade(int lvl){
        level -= lvl;
    }

    //Consider: Can one spell have more than one element?
    public enum Category{
        FIRE,
        ICE,
        LIGHTNING,
        SHADOW,
        NONE,
        PASSIVE
    }

    public static final int T_NO = 0x01;
    public static final int T_NO_AUTO_AIM = 0x02;
    public static final int T_ANY = 0x04;
    public static final int T_ONLY_FRIENDLY = 0x08;
    public static final int T_NO_SELF = 0x10;
    public static final int T_NO_FRIENDLY = 0x20;
    public static final int T_NO_BOSS = 0x40;

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put("spell_level", level);
    }
}
