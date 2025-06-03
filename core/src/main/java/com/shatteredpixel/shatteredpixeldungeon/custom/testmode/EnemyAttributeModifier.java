package com.shatteredpixel.shatteredpixeldungeon.custom.testmode;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.custom.buffs.AttributeModifier;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class EnemyAttributeModifier extends TestItem{
    {
        image = ItemSpriteSheet.ARTIFACT_CHALICE1;
        defaultAction = AC_ADJUST;
    }

    public float[] add = new float[]{0f, 0f, 0f, 0f, 0f, 0f};
    public float[] mul = new float[]{1f, 1f, 1f, 1f, 1f, 1f};

    private static final String AC_ADJUST = "adjust";
    private static final String AC_LOCK = "lock";
    private static final String AC_APPLY = "apply";
    private static final String AC_CANCEL = "cancel";
    private static final String AC_CLEAR = "clear";

    private boolean locked = false;
    private boolean applied = false;

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> action = super.actions(hero);
        if(!locked) {
            action.add(AC_LOCK);
            action.add(AC_ADJUST);
            if(!applied) {
                action.add(AC_APPLY);
            }else{
                action.add(AC_CANCEL);
            }
            action.add(AC_CLEAR);
        }
        return action;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if(action.equals(AC_ADJUST)){
            if(applied){
                GameScene.show(new WndAttributeSettings(){
                    @Override
                    public void onBackPressed() {
                        super.onBackPressed();
                        execute(hero, AC_APPLY);
                    }
                });
            }else{
                GameScene.show(new WndAttributeSettings());
            }
        }else if(action.equals(AC_APPLY)){
            ModificationTracker mt = Buff.affect(curUser, ModificationTracker.class);
            mt.set(mul, add);
            mt.updateAttribute();
            applied = true;
            GLog.i(M.L(this, "activated"));
            hero.spendAndNext(Actor.TICK);
        }else if(action.equals(AC_CANCEL)){
            Buff.detach(curUser, ModificationTracker.class);
            applied = false;
            GLog.i(M.L(this, "cancelled"));
            hero.spendAndNext(Actor.TICK);
        }else if(action.equals(AC_LOCK)){
            GameScene.show(new WndOptions(M.L(this, "op_title"), M.L(this, "op_desc"), M.L(this, "op_con"), M.L(this, "op_neg")){
                @Override
                protected void onSelect(int index) {
                    if(index == 0){
                        locked = true;
                        execute(curUser, AC_APPLY);
                        GLog.i(M.L(this, "lock_on"));
                    }
                    super.onSelect(index);
                }
            });
        }else if(action.equals(AC_CLEAR)){
            if(!applied) {
                for (int i = 0; i < 6; ++i) {
                    add[i] = 0f;
                    mul[i] = 1f;
                }
                GLog.i(M.L(this, "cleared"));
            }
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("is_option_locked", locked);
        bundle.put("is_applied", applied);
        bundle.put("mul_array", mul);
        bundle.put("add_array", add);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        locked = bundle.getBoolean("is_option_locked");
        applied = bundle.getBoolean("is_applied");
        mul = bundle.getFloatArray("mul_array");
        add = bundle.getFloatArray("add_array");
    }

    @Override
    public String desc() {
        String desc = M.L(this, "desc", mul[0], add[0], mul[1], add[1], mul[2], add[2], mul[3], add[3],mul[4], add[4], mul[5], add[5]);
        String appendix = M.L(this, locked ? "has_locked" : (applied ? "applied" : "not_applied"));
        return desc + appendix;
    }

    private class WndAttributeSettings extends Window{
        public static final int WIDTH  = 120;
        public static final int HEIGHT = 140;
        public static final int BTN_HEIGHT = 18;
        public static final int GAP = 2;
        public static final int TEXT_SIZE = 6;
        public float[] attributes_add = new float[]{0f, 0f, 0f, 0f, 0f, 0f};
        public float[] attributes_mul = new float[]{1f, 1f, 1f, 1f, 1f, 1f};
        private ArrayList<RedButton> mulList = new ArrayList<>(6);
        private ArrayList<RedButton> addList = new ArrayList<>(6);
        private boolean needUpdate = false;
        public WndAttributeSettings(){
            System.arraycopy(add, 0, attributes_add, 0, 6);
            System.arraycopy(mul, 0, attributes_mul, 0, 6);
            float pos = 0;
            resize(WIDTH, HEIGHT);
            for(int i=0;i<6;++i){
                final int j = i;
                RenderedTextBlock rtb = PixelScene.renderTextBlock(TEXT_SIZE);
                rtb.text(M.L(WndAttributeSettings.class, "attribute_name_"+String.valueOf(i)));
                rtb.maxWidth(WIDTH / 3);
                add(rtb);
                rtb.setPos(GAP, pos + GAP/2f + BTN_HEIGHT/2f - TEXT_SIZE/2f);

                RedButton buttonMul = new RedButton(M.L(WndAttributeSettings.class, "button_mul_text", attributes_mul[i]), 6){
                    @Override
                    protected void onClick() {
                        super.onClick();
                        WndTextInput input = new WndTextInput(M.L(WndAttributeSettings.class,"set_mul_title"), M.L(WndAttributeSettings.class,"set_mul_body"), "", 40, false, M.L(WndAttributeSettings.class,"input_ok"), M.L(WndAttributeSettings.class,"input_no")){
                            @Override
                            public void onSelect(boolean positive, String text) {
                                if(positive){
                                    float mul = 1f;
                                    try{
                                        mul = Float.parseFloat(text);
                                    }catch (NumberFormatException e){
                                        mul = 1f;
                                    }
                                    mul = Math.max(0.0001f, mul);
                                    mul = Math.min(10000f, mul);
                                   attributes_mul[j] = mul;
                                   needUpdate = true;
                                }
                                super.onSelect(positive, text);
                            }
                        };
                        GameScene.show(input);
                    }
                };
                add(buttonMul);
                buttonMul.setRect(WIDTH/3f + GAP/2f, pos + GAP/2f, WIDTH/3f - GAP, BTN_HEIGHT);
                mulList.add(buttonMul);

                RedButton buttonAdd = new RedButton(M.L(WndAttributeSettings.class, "button_add_text", attributes_add[i]), 6){
                    @Override
                    protected void onClick() {
                        super.onClick();
                        WndTextInput input = new WndTextInput(M.L(WndAttributeSettings.class,"set_add_title"), M.L(WndAttributeSettings.class,"set_add_body"), "", 40, false, M.L(WndAttributeSettings.class,"input_ok"), M.L(WndAttributeSettings.class,"input_no")){
                            @Override
                            public void onSelect(boolean positive, String text) {
                                if(positive){
                                    float add = 0f;
                                    try{
                                        add = Float.parseFloat(text);
                                    }catch (NumberFormatException e){
                                        add = 0f;
                                    }
                                    add = Math.max(-1000000f, add);
                                    add = Math.min(1000000f, add);
                                    attributes_add[j] = add;
                                    needUpdate = true;
                                }
                                super.onSelect(positive, text);
                            }
                        };
                        GameScene.show(input);
                    }
                };
                add(buttonAdd);
                buttonAdd.setRect(2*WIDTH/3f + GAP/2f, pos + GAP/2f, WIDTH/3f - GAP, BTN_HEIGHT);
                addList.add(buttonAdd);

                pos += GAP + BTN_HEIGHT;
            }

            RedButton confirm = new RedButton(M.L(WndAttributeSettings.class, "settings_ok")){
                @Override
                protected void onClick() {
                    super.onClick();
                    onBackPressed();
                }
            };
            add(confirm);
            confirm.setRect(GAP/2f, pos + GAP, WIDTH - GAP, BTN_HEIGHT);
        }

        @Override
        public void onBackPressed() {
            System.arraycopy(attributes_add, 0, add, 0, 6);
            System.arraycopy(attributes_mul, 0, mul, 0, 6);
            super.onBackPressed();
        }

        @Override
        public synchronized void update() {
            super.update();
            if(needUpdate) {
                updateText();
                needUpdate = false;
            }
        }

        private void updateText(){
            for(int i=0; i<6; ++i){
                mulList.get(i).text(M.L(WndAttributeSettings.class, "button_mul_text", attributes_mul[i]));
                addList.get(i).text(M.L(WndAttributeSettings.class, "button_add_text", attributes_add[i]));
            }
        }
    }

    public static class ModificationTracker extends Buff{
        {
            actPriority = VFX_PRIO;
            revivePersists = true;
        }
        public float[] attributes_add = new float[]{0f, 0f, 0f, 0f, 0f, 0f};
        public float[] attributes_mul = new float[]{1f, 1f, 1f, 1f, 1f, 1f};

        public void set(float[] mul, float[] add){
            System.arraycopy(add, 0, attributes_add, 0, 6);
            System.arraycopy(mul, 0, attributes_mul, 0, 6);
        }

        @Override
        public boolean act() {
            spend(TICK);
            if(!Dungeon.bossLevel()) {
                for (Mob m : Dungeon.level.mobs.toArray(new Mob[0])) {
                    if (m.buff(AttributeModifier.class) == null) {
                        Buff.affect(m, AttributeModifier.class, 99999999f).setAll(attributes_mul, attributes_add);
                    }
                }
            }
            return true;
        }

        public void updateAttribute(){
            //detach first, avoid merge
            for(Mob m : Dungeon.level.mobs.toArray(new Mob[0])){
                Buff.detach(m, AttributeModifier.class);
            }
            //let it act
            spend(-TICK*2);
        }

        @Override
        public void detach() {
            for(Mob m : Dungeon.level.mobs.toArray(new Mob[0])){
                Buff.detach(m, AttributeModifier.class);
            }
            super.detach();
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put("mul_array",attributes_mul);
            bundle.put("add_array", attributes_add);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            attributes_mul = bundle.getFloatArray("mul_array");
            attributes_add = bundle.getFloatArray("add_array");
        }
    }
}
