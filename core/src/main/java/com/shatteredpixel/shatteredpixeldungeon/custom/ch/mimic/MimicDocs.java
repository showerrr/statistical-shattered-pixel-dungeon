package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mimic;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.ChallengeItem;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;

public class MimicDocs extends ChallengeItem {
    {
        image = ItemSpriteSheet.ALCH_PAGE;
        defaultAction = AC_READ;
    }

    private static final String AC_READ = "read";

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> action = super.actions(hero);
        action.add(AC_READ);
        return action;
    }

    @Override
    public void execute(Hero hero, String action) {
        if(action.equals(AC_READ)){
            GameScene.show(new WndMimicDocs());
        }else {
            super.execute(hero, action);
        }
    }

    public static class WndMimicDocs extends Window{
        private static final int WIDTH = 120;
        private static final int HEIGHT = 160;

        private int baseLvl;
        private int abilityLvl;
        private RenderedTextBlock desc;
        private RedButton baseButton;
        private RedButton abilityButton;
        private ScrollPane sp;

        public WndMimicDocs(){
            super();
            resize(WIDTH, HEIGHT);
            baseButton = new RedButton("", 8){
                @Override
                protected void onClick() {
                    super.onClick();
                    baseLvl += 1;
                    if(baseLvl>7) baseLvl = 1;
                    updateText();
                }
            };
            abilityButton = new RedButton("", 8){
                @Override
                protected void onClick() {
                    super.onClick();
                    abilityLvl += 1;
                    if(abilityLvl>3) abilityLvl = 1;
                    updateText();
                }
            };
            add(baseButton);
            add(abilityButton);
            baseButton.setRect(0, 0, WIDTH/2f-1, 12);
            abilityButton.setRect(WIDTH/2f+1, 0, WIDTH/2f-1, 12);

            sp = new ScrollPane(new Component());
            add(sp);
            sp.setRect(0, 15, WIDTH, HEIGHT - 15);
            Component content = sp.content();
            desc = PixelScene.renderTextBlock(6);
            desc.maxWidth(WIDTH);
            content.add(desc);

            baseLvl=1;
            abilityLvl=1;

            resize(WIDTH, HEIGHT);

            updateText();
        }

        private void updateText(){
            baseButton.text(M.L(MimicDocs.class, "base_button", baseLvl));
            abilityButton.text(M.L(MimicDocs.class, "ability_button", abilityLvl));

            StringBuilder sb = new StringBuilder();
            sb.append(  M.L(MimicDocs.class, "general"));
            sb.append(M.NNL(MimicDocs.class, "base"));
            sb.append(M.NNL(MimicDocs.class, "base_health", healthModFactor(baseLvl)));
            sb.append(M.NNL(MimicDocs.class, "base_atk", attackModFactor(baseLvl)));
            sb.append(M.NNL(MimicDocs.class, "base_acc", accuracyModFactor(baseLvl)));
            sb.append(M.NNL(MimicDocs.class, "base_eva", evasionModFactor(baseLvl)));
            sb.append(M.NNL(MimicDocs.class, "base_mov", moveSpeedFactor(baseLvl)));
            sb.append(M.NNL(MimicDocs.class, "base_ksp", attackSpeedFactor(baseLvl)));
            sb.append(M.NNL(MimicDocs.class, "res"));
            sb.append(M.NNL(MimicDocs.class, "res_melee", meleeResistanceFactor(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "res_missile", missileResistanceFactor(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "res_magic", magicalResistanceFactor(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "atk"));
            sb.append(M.NNL(MimicDocs.class, "atk_berserk", berserkFactor(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "atk_suppress", suppressFactor(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "atk_defkill", defenseKillerFactor(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "atk_combo", comboFactor(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "atk_root", rootFactor(abilityLvl), rootDuration(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "atk_curse", curseAttackFactor(abilityLvl), curseDuration(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "atk_freeze", freezingAttackFactor(abilityLvl), freezingDuration(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "atk_fire", fireFactor(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "def"));
            sb.append(M.NNL(MimicDocs.class, "def_copy", defenseCopyFactor(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "def_combores", comboResistanceFactor(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "def_max", maxDamageFactor(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "def_min", minDamageFactor(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "def_vampire", vampireFactor(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "def_pushback", pushBackFactor(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "def_per", periodicShieldFactor(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "def_immune", negativeImmuneFactor(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "trk"));
            sb.append(M.NNL(MimicDocs.class, "trk_range", attackRangeFactor(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "trk_disappear", disappearFactor(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "trk_degrade", degradeFactor(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "trk_alert", alertFactor(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "trk_summon", summonFactor(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "trk_scan", scanFactor(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "trk_chargeeat", chargeEatFactor(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "trk_hitback", hitBackFactor(abilityLvl)));
            sb.append(M.NNL(MimicDocs.class, "power_base", basicModPower(baseLvl)));
            sb.append(M.NNL(MimicDocs.class, "power_ability", specialModPower(abilityLvl)));

            desc.text(sb.toString());

            sp.content().setSize(WIDTH, desc.height() + 2);
        }

        private float healthModFactor(int modlevel){
            return 1f + modlevel/16f + modlevel*modlevel / 112f;
        }
        private float attackModFactor(int modlevel){
            return 1f + modlevel/14f + modlevel*modlevel / 98f;
        }
        private float accuracyModFactor(int modlevel){
            return 1f + 0.2f * (2<<(modlevel-1));
        }
        private float evasionModFactor(int modlevel){
            return 1f + 0.08f * (2<<(modlevel-1));
        }
        private float moveSpeedFactor(int modlevel){
            return 1f + modlevel/14f + modlevel*modlevel / 98f;
        }
        private float attackSpeedFactor(int modlevel){
            return 1f + modlevel/14f + modlevel*modlevel / 98f;
        }
        private float meleeResistanceFactor(int modlevel) {
            return 0.21f*modlevel;
        }
        private float missileResistanceFactor(int modlevel) {
            return 0.26f*modlevel;
        }
        private float magicalResistanceFactor(int modlevel){
            return 0.31f*modlevel;
        }
        private float berserkFactor(int modlevel){
            return modlevel*0.33f;
        }
        private float suppressFactor(int modlevel){
            return modlevel*0.25f;
        }
        private float defenseKillerFactor(int modlevel){
            return 0.4f*modlevel-0.2f;
        }
        private float comboFactor(int modlevel){
            return modlevel*0.1f;
        }
        private float rootFactor(int modlevel){
            return 0.4f + 0.12f*modlevel;
        }
        private float rootDuration(int modlevel){
            return 2 * modlevel;
        }
        private float curseAttackFactor(int modlevel){
            return 1f/(Math.max(1, 5-modlevel));
        }
        private float curseDuration(int modlevel){
            return 2<<modlevel;
        }
        private float freezingAttackFactor(int modlevel){
            return 0.4f + 0.12f*modlevel;
        }
        private float freezingDuration(int modlevel){
            return modlevel * 2.5f - 1f;
        }
        private float fireFactor(int modlevel){
            return 1f/(Math.max(1, 5-modlevel));
        }
        private float defenseCopyFactor(int modlevel){
            return 0.4f*modlevel;
        }
        private float comboResistanceFactor(int modlevel){
            if(modlevel < 1) return 0f;
            return 1f - modlevel / (2.5f + modlevel);
        }
        private float maxDamageFactor(int modlevel){
            return 1f/(modlevel*2+1);
        }
        private float minDamageFactor(int modlevel){
            return 0.08f*modlevel-0.01f*modlevel*modlevel;
        }
        private float vampireFactor(int modlevel){
            return 0.17f*modlevel;
        }
        private float pushBackFactor(int modlevel){
            return 1 + modlevel;
        }
        private float periodicShieldFactor(int modlevel){
            return 6-modlevel;
        }
        private float negativeImmuneFactor(int modlevel){ return modlevel/3f;}
        private float attackRangeFactor(int modlevel){
            switch (modlevel){
                case 3: return 4;
                case 2: return 3;
                case 1: return 2;
                case 0: default: return 1;
            }
        }
        private float disappearFactor(int modlevel) {
            return 17 - modlevel * 4;
        }
        private float degradeFactor(int modlevel){
            return 1<<modlevel;
        }
        private float alertFactor(int modlevel){
            return 1f/(7 - 2*modlevel);
        }
        private float summonFactor(int modlevel){
            return modlevel;
        }
        private float scanFactor(int modlevel){
            return 6*modlevel-5;
        }
        private float chargeEatFactor(int modlevel){
            return modlevel;
        }
        private float hitBackFactor(int modlevel){
            return 2 * modlevel;
        }
        private float basicModPower(int level){
            if(level>0 && level<8){
                return 1f + level/17f + level*level / 119f;
            }
            return 1f;
        }
        private float specialModPower(int level){
            switch (level){
                case 0: default: return 1f;
                case 1: return 1.2f;
                case 2: return 1.4f;
                case 3: return 1.7f;
            }
        }
    }
}
