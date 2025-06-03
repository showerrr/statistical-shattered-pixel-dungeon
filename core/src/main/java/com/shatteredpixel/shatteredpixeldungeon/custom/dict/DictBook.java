package com.shatteredpixel.shatteredpixeldungeon.custom.dict;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.ChallengeItem;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;

public class DictBook extends ChallengeItem {
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
            GameScene.show(new WndDict());
        }else {
            super.execute(hero, action);
        }
    }

    public static class WndDict extends Window{
        public static final int WIDTH_P = 126;
        public static final int HEIGHT_P = 180;

        public static final int WIDTH_L = 200;
        public static final int HEIGHT_L = 130;

        public WndDict(){
            super();
            int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;
            int height = PixelScene.landscape() ? HEIGHT_L : HEIGHT_P;
            resize(width, height);

            DictTab dt = new DictTab();
            add(dt);
            dt.setRect(0,0,width,height);
            dt.updateList();
        }
    }

    public static class DictTab extends Component{
        private static final int ITEM_HEIGHT = 18;

        private RedButton[] itemButtons;
        private static final int NUM_BUTTONS = 10;

        private static int currentItemIdx = 0;

        //sprite locations
        private static final int ARMORS_IDX = 0;
        private static final int WEAPONS_IDX = 1;
        private static final int WANDS_IDX = 2;
        private static final int RINGS_IDX = 3;
        private static final int ARTIFACTS_IDX = 4;
        private static final int ALCHEMY_IDX = 5;
        private static final int TRINKETS_IDX = 6;
        private static final int MOB_IDX = 7;
        private static final int UNCLASSIFIED_IDX = 8;
        private static final int DOCUMENTS_IDX = 9;

        private static final int spriteIndexes[] = {2, 1, 4, 5, 6, 13,0, 0, 0, 0};

        private ScrollPane list;

        private ArrayList<DictButton> items = new ArrayList<>();

        @Override
        protected void createChildren() {
            itemButtons = new RedButton[NUM_BUTTONS];
            for (int i = 0; i < NUM_BUTTONS; i++) {
                final int idx = i;
                itemButtons[i] = new RedButton("") {
                    @Override
                    protected void onClick() {
                        currentItemIdx = idx;
                        updateList();
                    }
                };
                itemButtons[i].icon(new ItemSprite(ItemSpriteSheet.SOMETHING + spriteIndexes[i], null));
                add(itemButtons[i]);
            }

            list = new ScrollPane(new Component()) {
                @Override
                public void onClick(float x, float y) {
                    int size = items.size();
                    for (int i = 0; i < size; i++) {
                        if (items.get(i).onClick(x, y)) {
                            break;
                        }
                    }
                }
            };
            add(list);
        }

        @Override
        protected void layout() {
            super.layout();

            int perRow = NUM_BUTTONS;
            float buttonWidth = width() / perRow;

            for (int i = 0; i < NUM_BUTTONS; i++) {
                itemButtons[i].setRect((i % perRow) * (buttonWidth), (i / perRow) * (ITEM_HEIGHT),
                        buttonWidth, ITEM_HEIGHT);
                PixelScene.align(itemButtons[i]);
            }

            list.setRect(0, itemButtons[NUM_BUTTONS - 1].bottom() + 1, width,
                    height - itemButtons[NUM_BUTTONS - 1].bottom() - 1);
        }

        private void updateList() {

            items.clear();

            for (int i = 0; i < NUM_BUTTONS; i++) {
                if (i == currentItemIdx) {
                    itemButtons[i].icon().color(Window.TITLE_COLOR);
                } else {
                    itemButtons[i].icon().resetColor();
                }
            }

            Component content = list.content();
            content.clear();
            list.scrollTo(0, 0);

            ArrayList<String> keys;
            ArrayList<Integer> imageSheets;
            if (currentItemIdx == ARMORS_IDX) {
                keys = new ArrayList<>(DictionaryJournal.ARMORS.keyList());
                imageSheets = new ArrayList<>(DictionaryJournal.ARMORS.imageList());
            } else if (currentItemIdx == ARTIFACTS_IDX) {
                keys = new ArrayList<>(DictionaryJournal.ARTIFACTS.keyList());
                imageSheets = new ArrayList<>(DictionaryJournal.ARTIFACTS.imageList());
            } else if (currentItemIdx == WEAPONS_IDX) {
                keys = new ArrayList<>(DictionaryJournal.WEAPONS.keyList());
                imageSheets = new ArrayList<>(DictionaryJournal.WEAPONS.imageList());
            } else if (currentItemIdx == RINGS_IDX) {
                keys = new ArrayList<>(DictionaryJournal.RINGS.keyList());
                imageSheets = new ArrayList<>(DictionaryJournal.RINGS.imageList());
            }  else if (currentItemIdx == TRINKETS_IDX) {
                keys = new ArrayList<>(DictionaryJournal.TRINKETS.keyList());
                imageSheets = new ArrayList<>(DictionaryJournal.TRINKETS.imageList());
            } else if (currentItemIdx == ALCHEMY_IDX) {
                keys = new ArrayList<>(DictionaryJournal.ALCHEMY.keyList());
                imageSheets = new ArrayList<>(DictionaryJournal.ALCHEMY.imageList());
            } else if (currentItemIdx == MOB_IDX) {
                keys = new ArrayList<>(DictionaryJournal.MOBS.keyList());
                imageSheets = new ArrayList<>(DictionaryJournal.MOBS.imageList());
            } else if (currentItemIdx == UNCLASSIFIED_IDX) {
                keys = new ArrayList<>(DictionaryJournal.UNCLASSIFIED.keyList());
                imageSheets = new ArrayList<>(DictionaryJournal.UNCLASSIFIED.imageList());
            } else if (currentItemIdx == WANDS_IDX) {
                keys = new ArrayList<>(DictionaryJournal.WANDS.keyList());
                imageSheets = new ArrayList<>(DictionaryJournal.WANDS.imageList());
            } else if (currentItemIdx == DOCUMENTS_IDX) {
                keys = new ArrayList<>(DictionaryJournal.DOCUMENTS.keyList());
                imageSheets = new ArrayList<>(DictionaryJournal.DOCUMENTS.imageList());
            } else {
                keys = new ArrayList<>();
                imageSheets = new ArrayList<>();
            }

            float pos = 0;
            for (int i = 0; i < keys.size(); i++) {
                DictButton item = new DictButton(keys.get(i), imageSheets.get(i));
                item.setRect(0, pos, width, ITEM_HEIGHT);
                content.add(item);
                items.add(item);

                pos += item.height();
            }

            content.setSize(width, pos);
            list.setSize(list.width(), list.height());
        }

        private static class DictButton extends ListItem {

            private String k;

            public DictButton(String key, int imageSheet) {
                super(DictSpriteSheet.createImage(imageSheet), M.TL(Dict.class, key ));
                this.k = key;
            }

            public boolean onClick(float x, float y) {
                if (inside(x, y)) {
                    GameScene.show(new WndScrollTitledMessage(new Image(icon), M.TL(Dict.class, k ), M.L(Dict.class, k + "_d"), 152));
                    return true;
                }
                return false;
            }
        }
    }


    private static class ListItem extends Component {

        protected RenderedTextBlock label;
        protected BitmapText depth;
        protected ColorBlock line;
        protected Image icon;

        public ListItem(Image icon, String text) {
            this(icon, text, -1);
        }

        public ListItem(Image icon, String text, int d) {
            super();

            this.icon.copy(icon);

            label.text(text);

            if (d >= 0) {
                depth.text(Integer.toString(d));
                depth.measure();

                if (d == Dungeon.depth) {
                    label.hardlight(Window.TITLE_COLOR);
                    depth.hardlight(Window.TITLE_COLOR);
                }
            }
        }

        @Override
        protected void createChildren() {
            label = PixelScene.renderTextBlock(7);
            add(label);

            icon = new Image();
            add(icon);

            depth = new BitmapText(PixelScene.pixelFont);
            add(depth);

            line = new ColorBlock(1, 1, 0xFF222222);
            add(line);

        }

        @Override
        protected void layout() {

            icon.y = y + 1 + (height() - 1 - icon.height()) / 2f;
            icon.x = x + (16 - icon.width()) / 2f;
            PixelScene.align(icon);

            depth.x = icon.x + (icon.width - depth.width()) / 2f;
            depth.y = icon.y + (icon.height - depth.height()) / 2f + 1;
            PixelScene.align(depth);

            line.size(width, 1);
            line.x = 0;
            line.y = y;

            label.maxWidth((int) (width - 16 - 1));
            label.setPos(17, y + 1 + (height() - label.height()) / 2f);
            PixelScene.align(label);
        }
    }
}
