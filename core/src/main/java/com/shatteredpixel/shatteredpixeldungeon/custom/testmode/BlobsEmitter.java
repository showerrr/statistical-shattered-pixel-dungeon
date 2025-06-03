package com.shatteredpixel.shatteredpixeldungeon.custom.testmode;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Alchemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blizzard;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ConfusionGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Foliage;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.GooWarn;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Inferno;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Regrowth;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SmokeScreen;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StenchGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StormCloud;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WaterOfAwareness;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WaterOfHealth;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SacrificialParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SnowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.WebParticle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GooSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;

public class BlobsEmitter extends TestItem {

    {
        image = ItemSpriteSheet.CHAOTIC_CENSER;
        defaultAction = AC_PLACE;
    }

    private static ArrayList<Class<? extends Blob>> blobs = new ArrayList<>(Arrays.asList(
            Alchemy.class, Blizzard.class, ConfusionGas.class, CorrosiveGas.class,
            Electricity.class, Fire.class, Foliage.class, Freezing.class, GooWarn.class,
            Inferno.class, ParalyticGas.class, Regrowth.class, SacrificialFire.class,
            SmokeScreen.class, StenchGas.class, StormCloud.class, ToxicGas.class,
            WaterOfAwareness.class, WaterOfHealth.class, Web.class
    ));
//    private static ArrayList<Image> blobImages = new ArrayList<>(Arrays.asList(
//            new Speck().image(Speck.HEALING)
//    ));

    private static ArrayList<Image> blobImages = new ArrayList<>();
    static {
        blobImages.add(new Speck().image(Speck.BUBBLE));
        blobImages.add(new Speck().image(Speck.BLIZZARD));
        blobImages.add(new Speck().image(Speck.CONFUSION));
        blobImages.add(new Speck().image(Speck.CORROSION));
        blobImages.add(new SparkParticle());
        blobImages.add(new FlameParticle());
        blobImages.add(new ShaftParticle());
        blobImages.add(new SnowParticle());
        blobImages.add(new GooSprite.GooParticle());
        blobImages.add(new Speck().image(Speck.INFERNO));
        blobImages.add(new Speck().image(Speck.PARALYSIS));
        blobImages.add(new LeafParticle());
        blobImages.add(new SacrificialParticle());
        blobImages.add(new Speck().image(Speck.SMOKE));
        blobImages.add(new Speck().image(Speck.STENCH));
        blobImages.add(new Speck().image(Speck.STORM));
        blobImages.add(new Speck().image(Speck.TOXIC));
        blobImages.add(new Speck().image(Speck.QUESTION));
        blobImages.add(new Speck().image(Speck.HEALING));
        blobImages.add(new WebParticle());
    }

    private static int index = 0;
    private RenderedTextBlock name;
    private RenderedTextBlock desc;

    private static final String AC_PLACE = "place";
    private static final String AC_SET = "set";

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_PLACE);
        actions.add(AC_SET);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_PLACE)) {
            GameScene.selectCell(new CellSelector.Listener() {
                @Override
                public void onSelect(Integer cell) {
                    if (cell!= null) {
                        GameScene.add(Blob.seed(cell, 2,blobs.get(index)));
//                        GLog.i(blobs.get(index).getSimpleName());
                    }
                }
                @Override
                public String prompt() {
                   return M.L(BlobsEmitter.class,"prompt");
                }
            });

        } else if (action.equals(AC_SET)) {
            GameScene.show(new SettingsWindow());
        }
    }

    public class SettingsWindow extends Window {
        public SettingsWindow() {
            BlobButton button;
            int x, y;

            for (int i = 0;i < blobs.size();i++) {
                button = new BlobButton(i);
                x = i % 6 * 18 + 2;
                y = i / 6 * 18 + 2;
                button.setRect(x,y,16,16);
                add(button);
            }

            name = PixelScene.renderTextBlock("炼金气泡",9);
            name.setPos(5,84);
            name.hardlight(Window.TITLE_COLOR);
            add(name);

            desc = PixelScene.renderTextBlock("炼药锅气泡特效",7);
            desc.maxWidth(100);
            desc.setPos(5,name.bottom() + 2);
            add(desc);

            resize(110,(int) (desc.top() + desc.height() + 10));
        }

        private class BlobButton extends IconButton {

            private int blobIndex;

            public BlobButton(int blobIndex) {
                this.blobIndex = blobIndex;
//            icon(blobImages.get(blobIndex));
                Image image1 = new Image();
                if (blobImages.size() > blobIndex) {
                    image1.copy(blobImages.get(blobIndex));
                } else {
                    image1.copy(blobImages.get(0));
                }
                icon(image1);
            }

            @Override
            public void onClick() {
                super.onClick();
                index = blobIndex;
                name.text(blobs.get(index).getSimpleName());
                name.text(M.L(BlobsEmitter.class,blobs.get(index).getSimpleName()));
                if (index == 0) {
                    desc.text("炼药锅气泡特效");
                } else if (index == 11) {
                    desc.text("草地再生特效");
                } else {
                    desc.text(M.L(blobs.get(index),"desc"));
                }

                if (parent instanceof SettingsWindow && ((SettingsWindow)parent).height < (desc.top() + desc.height() + 10)) {
                    resize(110,(int) (desc.top() + desc.height() + 10));
                }
            }
        }
    }
    

}
