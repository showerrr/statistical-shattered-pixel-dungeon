package com.shatteredpixel.shatteredpixeldungeon.expansion.magic.holder;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.ChallengeItem;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.expansion.magic.baseclass.SpellBase;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class QuickSpellCaster extends ChallengeItem {
    {
        image = ItemSpriteSheet.WAND_HOLDER;
        defaultAction = AC_CAST;
    }

    private static final String AC_CAST = "cast";

    private SpellBase quickSpell;

    public void setQuickSpell(SpellBase sp){
        quickSpell = sp;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> action = super.actions(hero);
        action.add(AC_CAST);
        return action;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if(action.equals(AC_CAST)){
            quickSpell = SpellRecord.findSpell(quickSpell);
            if(quickSpell != null){
                quickSpell.castSpell(hero);
            }else{
                GameScene.show(new SpellHolder.WndSpellList());
            }
            hero.next();
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("quick_spell", quickSpell);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        quickSpell = (SpellBase) bundle.get("quick_spell");
        quickSpell = SpellRecord.findSpell(quickSpell);
    }
}
