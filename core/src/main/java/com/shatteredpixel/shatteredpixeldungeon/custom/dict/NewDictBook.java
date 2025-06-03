package com.shatteredpixel.shatteredpixeldungeon.custom.dict;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.ChallengeItem;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.TextInput;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collections;

public class NewDictBook extends ChallengeItem {

    {
        image = ItemSpriteSheet.GUIDE_PAGE;
        defaultAction = AC_READ;
        unique = true;
    }

    private static final String AC_READ = "read";

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> action = super.actions(hero);
        action.add(AC_READ);
        return action;
    }

    @Override
    public void execute(Hero hero, String action) {
        if(action.equals(AC_READ)){
            GameScene.show(new NewDictBook.WndNewDict());
        }else {
            super.execute(hero, action);
        }
    }

    public static class WndNewDict extends Window {

        protected TextInput textBox;
        public ArrayList<DictButton> button = new ArrayList<>();
        public String lastSearch;

        public float GAP = 2f;
        public int WIDTH = 120;

        public WndNewDict() {

            lastSearch = "";

            int textSize = (int) PixelScene.uiCamera.zoom * 9;
            textBox = new TextInput(Chrome.get(Chrome.Type.TOAST_WHITE),false,textSize)
//            {
//                @Override
//                public void enterPressed() {
//                    //triggers positive action on enter pressed, only with non-multiline though.
//                    onSelect(true, getText());
//                    hide();
//                }
//            };
            ;
            textBox.setText("");
            lastSearch = textBox.getText();
            textBox.setMaxLength(10);
            add(textBox);
            textBox.setRect(GAP,GAP,WIDTH - 2 * GAP,16);

            for (int i = 0;i < 8;i++) {
                DictButton dictButton = new DictButton();
                dictButton.setRect(GAP + i % 2 * 59,16 + 2 * GAP + (int)(i / 2) * 30,57,28);
                button.add(dictButton);
                add(dictButton);
            }

            resize(WIDTH,140);
            textBox.setRect(GAP,textBox.top(),WIDTH - 2 * GAP,16);
        }

        @Override
        public void update() {
            super.update();

            if (!(textBox.getText() == null || textBox.getText().equals(lastSearch))) {
                lastSearch = textBox.getText();
                Wishing.checkSimilarity(textBox.getText());
                for (DictButton button1 : button) {
                    button1.set(null);
                }
//                GLog.i(lastSearch);
                if (Wishing.hashMap.size() > 0 && Wishing.hashMap.size() <= 8) {
                    ArrayList<Float> arrayList = new ArrayList<>();
                    arrayList.addAll(Wishing.hashMap.keySet());
                    Collections.sort(arrayList);

                    int i = Wishing.hashMap.size() - 1;
                    for (Float f : arrayList) {
//                        GLog.i(Float.toString(f));
                        button.get(i).set(Wishing.hashMap.get(f));
                        i--;
                    }
                }
            }
        }

        @Override
        public void offset(int xOffset, int yOffset) {
            super.offset(xOffset, yOffset);
            if (textBox != null){
                textBox.setRect(textBox.left(), textBox.top(), textBox.width(), textBox.height());
            }
        }

//        @Override
//        public void onBackPressed() {
//            GameScene.show(new WndOptions("退出","确定要关闭图鉴么","关闭","不了") {
//                @Override
//                public void onSelect(int index) {
//                    if (index == 0) {
//                        WndNewDict.this.hide();
//                        hide();
//                    } else {
//                        hide();
//                    }
//                }
//
//                @Override
//                public void onBackPressed() {
//                }
//            });
//        }
    }

    private static class DictButton extends StyledButton {

        public Object o;
        public DictButton(Chrome.Type type, String label, int size) {
            super(type,label,size);
        }

        public DictButton() {
            this(Chrome.Type.GREY_BUTTON,"",7);
        }

        public void set(Class<?> c) {
            if (c == null) {
                text(" ");
                icon(null);
                o = null;
                return;
            }
            o = Reflection.newInstance(c);
            if (o instanceof Item) {
                icon(new ItemSprite(((Item) o).image));
            }

            text(M.L(c,"name"));
        }

        @Override
        public void onClick() {
            if (o != null && o instanceof Weapon) {
                GameScene.show(new WndWeaponInfo((Weapon) o));
            }
            super.onClick();
        }

    }

    public static class WndItemInfo extends Window {

        public float pos;

        public WndItemInfo(Item item) {

            IconTitle iconTitle = new IconTitle(item);
            iconTitle.setRect(0,0,120,0);
            add(iconTitle);

            pos = iconTitle.bottom() + 5;

            resize(120,140);
        }
    }

    public static class WndWeaponInfo extends WndItemInfo {
        public WndWeaponInfo(Weapon weapon) {
            super(weapon);

            int tier = 0;
            if (weapon instanceof MeleeWeapon) {
                tier = ((MeleeWeapon) weapon).tier;
            } else if (weapon instanceof MissileWeapon) {
                tier = ((MissileWeapon) weapon).tier;
            }

            RenderedTextBlock baseTier = PixelScene.renderTextBlock("阶数:" + "_" + tier + "_",7);
            baseTier.setPos(3,pos);
            add(baseTier);

            RenderedTextBlock baseAtk = PixelScene.renderTextBlock("基础伤害:" + "_" + weapon.min() + "~" + weapon.max() + "_",7);
            baseAtk.setPos(45,pos);
            add(baseAtk);

            RenderedTextBlock baseStr = PixelScene.renderTextBlock("力量需求:" + "_" + weapon.STRReq() + "_",7);
            baseStr.setPos(3,baseTier.bottom() + 3);
            add(baseStr);

            RenderedTextBlock baseUgd = PixelScene.renderTextBlock("伤害成长:" + "_" + (weapon.min(1) - weapon.min()) + "~" +
                    (weapon.max(1) - weapon.max()) + "_",7);
            baseUgd.setPos(45,baseAtk.bottom() + 3);
            add(baseUgd);


        }
    }
}
