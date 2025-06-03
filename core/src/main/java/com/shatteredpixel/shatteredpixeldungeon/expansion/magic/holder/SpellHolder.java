package com.shatteredpixel.shatteredpixeldungeon.expansion.magic.holder;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.ChallengeItem;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.expansion.magic.baseclass.Mana;
import com.shatteredpixel.shatteredpixeldungeon.expansion.magic.baseclass.SpellBase;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SpellHolder extends ChallengeItem {
    {
        image = ItemSpriteSheet.MASTERY;
    }

    private static final String AC_OPEN = "open";

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_OPEN);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if(action.equals(AC_OPEN)){
            if(hero.buff(Mana.class) != null){
                GameScene.show(new WndSpellList());
            }
        }
    }

    public static class WndSpellList extends Window {
        private static final int WIDTH = 120;
        private static final int HEIGHT = 160;
        private static final int NUM_BUTTON = 6;
        private static final int BTN_SIZE = 16;

        private ScrollPane pane;
        private ArrayList<RenderedTextBlock> statTextBlock = new ArrayList<>(4);
        private ArrayList<IconButton> imageButtons = new ArrayList<>(NUM_BUTTON);
        private HashMap<SpellBase, RenderedTextBlock> spellAndText = new HashMap<>();
        private static final int[] buttonImage = new int[]{ItemSpriteSheet.WAND_FIREBOLT, ItemSpriteSheet.WAND_FROST, ItemSpriteSheet.WAND_LIGHTNING,
                ItemSpriteSheet.WAND_CORRUPTION, ItemSpriteSheet.WAND_MAGIC_MISSILE, ItemSpriteSheet.RING_RUBY, ItemSpriteSheet.TOKEN};
        private int selected = 0;

        public WndSpellList(){
            super();

            resize(WIDTH, HEIGHT);

            for(int i = 0; i < NUM_BUTTON; ++i){
                final int j = i;
                IconButton ib = new IconButton(){
                    @Override
                    protected void onClick() {
                        super.onClick();
                        selected = j;
                        updateStat();
                        buildSpellTextBlock();
                    }
                };

                Image im =  new Image(Assets.Sprites.ITEMS);
                im.frame(ItemSpriteSheet.film.get(buttonImage[i]));
                im.scale.set(1.0f);
                ib.icon(im);

                imageButtons.add(ib);
            }

            float pos = 2f;
            for(int i = 0; i < NUM_BUTTON; ++i){
                add(imageButtons.get(i));
                imageButtons.get(i).setRect((WIDTH - NUM_BUTTON*BTN_SIZE)/2f+BTN_SIZE*i, pos, BTN_SIZE, BTN_SIZE);
            }

            pos += BTN_SIZE + 2;

            for(int i = 0; i < 4; ++i){
                statTextBlock.add(PixelScene.renderTextBlock(6));
                add(statTextBlock.get(i));
                statTextBlock.get(i).maxWidth(WIDTH - 4);
                statTextBlock.get(i).setPos(2, pos);
                pos += 8;
            }

            pane = new ScrollPane(new Component()){
                @Override
                public void onClick(float x, float y) {
                    super.onClick(x, y);
                    for(Map.Entry<SpellBase, RenderedTextBlock> sat: spellAndText.entrySet()){
                        RenderedTextBlock text = sat.getValue();
                        if(x > text.left() && x < text.right() && y > text.top() && y < text.bottom()){
                            GameScene.show(new WndCastSpell(sat.getKey(), null));
                            break;
                        }
                    }
                }
            };
            add(pane);
            pane.setRect(1, pos + 2, WIDTH - 2, HEIGHT - pos - 2);

            updateStat();

            buildSpellTextBlock();
        }

        public void updateStat(){
            Mana mana = curUser.buff(Mana.class);
            if(mana != null) {
                SpellBase.Category cate = getSpellCate(selected);
                statTextBlock.get(0).text(M.L(SpellHolder.class, "cur_mana", mana.curMana, mana.RMaxMana(), mana.RManaRegen()));
                statTextBlock.get(1).text(M.L(SpellHolder.class, "cur_spell_power", mana.RSpellPower(cate), mana.RSpellPower(cate) - mana.spellPower));
                statTextBlock.get(2).text(M.L(SpellHolder.class, "cur_spell_damage", mana.RSpellDamage(cate), mana.RSpellDamage(cate) - mana.spellDamage));
                statTextBlock.get(3).text(M.L(SpellHolder.class, "cur_spell_reduction", mana.RCostReduce(cate), mana.RCostReduce(cate) - mana.costReduction));
            }
        }

        public void buildSpellTextBlock(){
            float pane_pos = 1;
            spellAndText.clear();
            ArrayList<SpellBase> curSpell = SpellRecord.getSpell(getSpellCate(selected));
            for(SpellBase sp: curSpell){
                RenderedTextBlock spellText = PixelScene.renderTextBlock(6);
                pane.content().add(spellText);
                spellText.maxWidth(WIDTH - 2);
                spellText.text(sp.name() + "\n" + sp.desc());
                spellText.setPos(0, pane_pos);
                pane_pos = spellText.bottom() + 6;
                spellAndText.put(sp, spellText);
            }
        }

        public SpellBase.Category getSpellCate(int selection){
            switch (selection){
                case 0: return SpellBase.Category.FIRE;
                case 1: return SpellBase.Category.ICE;
                case 2: return SpellBase.Category.LIGHTNING;
                case 3: return SpellBase.Category.SHADOW;
                case 4: default: return SpellBase.Category.NONE;
                case 5: return SpellBase.Category.PASSIVE;
            }
        }

        public class WndCastSpell extends Window{
            private static final int WIDTH = 120;
            private static final int MAX_HEIGHT = 160;
            public WndCastSpell(SpellBase spell){
                new WndCastSpell(spell, null);
            }

            public WndCastSpell(SpellBase spell, QuickSpellCaster qsc){
                resize(WIDTH, MAX_HEIGHT);

                RenderedTextBlock title = PixelScene.renderTextBlock(10);
                title.maxWidth(WIDTH - 4);
                title.text(spell.name());
                add(title);
                title.setPos(2, 2);
                title.hardlight(TITLE_COLOR);

                RenderedTextBlock info = PixelScene.renderTextBlock(6);
                info.maxWidth(WIDTH - 2);
                info.text(spell.desc());
                add(info);
                info.setPos(1, title.bottom() + 6);

                RenderedTextBlock manaChange = PixelScene.renderTextBlock(6);
                manaChange.maxWidth(WIDTH - 2);
                Mana mana = curUser.buff(Mana.class);
                if(mana != null) {
                    manaChange.text(M.L(SpellHolder.class, "cast_spell_mana_change", mana.curMana, mana.curMana - spell.manaCost()));
                }
                add(manaChange);
                manaChange.setPos(1, info.bottom() + 6);

                RedButton btnCast = new RedButton(M.L(SpellHolder.class, "cast_spell_button")){
                    @Override
                    protected void onClick() {
                        super.onClick();
                        spell.castSpell(curUser);
                        WndCastSpell.this.hide();
                        WndSpellList.this.hide();
                    }
                };
                add(btnCast);
                btnCast.setRect(1, manaChange.bottom() + 4, WIDTH - 2, 18);

                IconButton setQuick = new IconButton(){
                    @Override
                    protected void onClick() {
                        super.onClick();
                        if(qsc != null){
                            qsc.setQuickSpell(spell);
                        }else{
                            GameScene.selectItem(new WndBag.ItemSelector() {
                                @Override
                                public String textPrompt() {
                                    return M.L(SpellHolder.class, "select_quick_spell_item");
                                }

                                @Override
                                public boolean itemSelectable(Item item) {
                                    return item instanceof QuickSpellCaster;
                                }

                                @Override
                                public void onSelect(Item item) {
                                    if(item != null && item instanceof QuickSpellCaster){
                                        ((QuickSpellCaster) item).setQuickSpell(spell);
                                    }
                                }
                            });
                        }
                    }
                };
                Image im =  new Image(Assets.Sprites.ITEMS);
                im.frame(ItemSpriteSheet.film.get(ItemSpriteSheet.RECYCLE));
                im.scale.set(1.0f);
                setQuick.icon(im);
                add(setQuick);
                setQuick.setRect(WIDTH - 16, 1, 16, 16);

                resize(WIDTH, (int) (btnCast.bottom() + 1));
            }
        }
    }

}
