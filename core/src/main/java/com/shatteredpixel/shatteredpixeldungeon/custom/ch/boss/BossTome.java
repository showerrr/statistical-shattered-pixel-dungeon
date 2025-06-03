
package com.shatteredpixel.shatteredpixeldungeon.custom.ch.boss;

import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.ChallengeItem;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

import java.util.ArrayList;

public class BossTome extends ChallengeItem {
    {
        unique = true;
        image = ItemSpriteSheet.ARTIFACT_SPELLBOOK;
        defaultAction = AC_SET;
    }

    private static final String AC_SET = "set";

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> action = super.actions(hero);
        action.add(AC_SET);
        return action;
    }

    @Override
    public void execute(Hero hero, String action) {
        if(action.equals(AC_SET)){
            GameScene.show(new SettingsWindow());
        }else {
            super.execute(hero, action);
        }
    }

    @Override
    public String desc() {
        StringBuilder sb = new StringBuilder(M.L(BossTome.class, "desc"));
        for(int i=0; i<32;++i){
            if((Statistics.boss_enhance & (1<<i)) > 0){
                sb.append('\n');
                sb.append(5*i+5);
            }
        }
        return sb.toString();
    }

    public static class SettingsWindow extends Window{
        private static final int WIDTH = 120;
        private static final int BOX_HEIGHT = 18;
        private static final int TTL_HEIGHT = 12;
        private static final int GAP= 2;

        private ArrayList<CheckBox> cbs;

        public SettingsWindow(){
            resize(WIDTH, 1);

            RenderedTextBlock rtb = PixelScene.renderTextBlock(M.L(BossTome.class, "title"), TTL_HEIGHT - GAP);
            rtb.setPos(WIDTH/2f - rtb.width()/2, GAP);
            PixelScene.align(rtb);
            rtb.hardlight(0x00FF00);
            add(rtb);

            float pos = TTL_HEIGHT + GAP;
            cbs = new ArrayList<>();
            for(int i = 0; i<5; ++i){
                CheckBox cb = new CheckBox(M.L(BossTome.class, "boss_"+String.valueOf(i+1)));
                cb.setRect(GAP, pos, WIDTH - GAP * 2, BOX_HEIGHT);
                add(cb);
                cbs.add(cb);
                pos += BOX_HEIGHT + GAP;
                cb.checked((Statistics.boss_enhance & (1<<i)) >0);
                cb.enable(Statistics.deepestFloor < (5+i*5));
            }
            resize(WIDTH, (int) Math.floor(pos));
        }

        @Override
        public void onBackPressed() {
            Statistics.boss_enhance = 0;
            for(int i=0, len = cbs.size(); i<len; ++i){
                if(cbs.get(i).checked()) {
                    Statistics.boss_enhance += 1<<i;
                }
            }
            super.onBackPressed();
        }
    }
}