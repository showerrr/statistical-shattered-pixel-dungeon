package com.shatteredpixel.shatteredpixeldungeon.expansion.magic.holder;

import com.shatteredpixel.shatteredpixeldungeon.expansion.magic.baseclass.SpellBase;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Spell;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.Collection;

public class SpellRecord {

    public static ArrayList<SpellBase> spells = new ArrayList<>();

    public static void addSpell(SpellBase spell){
        boolean found = false;
        for(SpellBase sp : spells){
            if(sp.getClass().isInstance(spell)){
                sp.upgrade(1);
                found = true;
                break;
            }
        }
        if(!found) {
            spells.add(spell);
        }
    }

    public static SpellBase findSpell(SpellBase sp){
        for(SpellBase s: spells){
            if(s.getClass().isInstance(sp)){
                return s;
            }
        }
        return null;
    }

    public static ArrayList<SpellBase> getSpell(){
        return spells;
    }

    public static ArrayList<SpellBase> getSpell(SpellBase.Category category){
        ArrayList<SpellBase> spell = new ArrayList<>();
        for(SpellBase sp: spells){
            if(sp.spellCate == category){
                spell.add(sp);
            }
        }
        return spell;
    }

    public static void save(Bundle bundle){
        bundle.put("all_spells", spells);
    }

    public static void load(Bundle bundle){
        Collection<Bundlable> loaded = bundle.getCollection("all_spells");
        for(Bundlable b: loaded){
            spells.add((SpellBase) b);
        }

    }
}
