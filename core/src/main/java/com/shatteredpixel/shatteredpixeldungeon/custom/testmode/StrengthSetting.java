package com.shatteredpixel.shatteredpixeldungeon.custom.testmode;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.OptionSlider;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import java.util.ArrayList;

public class StrengthSetting extends TestItem {

    {
        image = ItemSpriteSheet.SEED_STARFLOWER;
    }

    private static final String AC_SET = "set";

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_SET);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_SET)) {
            GameScene.show(new SettingsWindow());
        }
    }

    public static class SettingsWindow extends Window {
        private OptionSlider str;
        private OptionSlider level;

        private RenderedTextBlock text;
        private RedButton confirm;
        private RedButton cancel;

        public SettingsWindow() {
            super();
            str = new OptionSlider("Strength","1","30",1,30) {
                @Override
                protected void onChange() {
                    text.text("力量: " + str.getSelectedValue() + "，等级: " + level.getSelectedValue());
                }
            };
            str.setSelectedValue(Dungeon.hero.STR);
            str.setRect(0,0,120,24);
            add(str);

            level = new OptionSlider("Level","1","30",1,30) {
                @Override
                protected void onChange() {
                    text.text("力量: " + str.getSelectedValue() + "，等级: " + level.getSelectedValue());
                }
            };
            level.setSelectedValue(Dungeon.hero.lvl);
            level.setRect(0,str.bottom() + 5,120,24);
            add(level);

            text = PixelScene.renderTextBlock("力量: " + Dungeon.hero.STR + "，等级: " + Dungeon.hero.lvl,6);
            text.setPos((120 - text.width()) / 2,level.bottom() + 5);
            add(text);

            confirm = new RedButton("设置") {
                @Override
                public void onClick() {
                    super.onClick();
                    Dungeon.hero.STR = str.getSelectedValue();
                    Dungeon.hero.lvl = level.getSelectedValue();
                    Dungeon.hero.updateHT(true);
                    GLog.i("设置完成！");
                    hide();
                }
            };
            confirm.setRect(0,text.bottom() + 5,50,24);
            add(confirm);

            cancel = new RedButton("取消") {
                @Override
                public void onClick() {
                    super.onClick();
                    hide();
                }
            };
            cancel.setRect(confirm.right() + 5,confirm.top(),50,24);
            add(cancel);

            resize(120, (int) (confirm.bottom() + 5));

        }

    }
}
