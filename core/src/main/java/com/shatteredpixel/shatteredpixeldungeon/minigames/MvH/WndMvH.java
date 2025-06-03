package com.shatteredpixel.shatteredpixeldungeon.minigames.mvh;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.minigames.TownGamer;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class WndMvH extends Window {

    public static WndMvH wndMvH;

    public static ArrayList<MvHHeroSprite> heroSprites;
    public static ArrayList<Grass> grasses;

    public boolean pause = false;

    public static MvHMobSprite select = null;
    public static Card selectCard = null;

    public static int water;

    public BlueBlock blueBlock;
    public StyledButton txt;

    public boolean night;

    public Pickaxe pickaxe;

    public static ArrayList<Card> cards = new ArrayList<>();

    public MvHHeroSpawner mvHHeroSpawner;

    public WndMvH() {

        Music.INSTANCE.pause();
        Music.INSTANCE.play(Assets.Music.PVH,true);
//        SPDSettings.landscape(true);

        wndMvH = this;
        heroSprites = new ArrayList<>();
        grasses = new ArrayList<>();
        water = 50;
        night = false;

        addGrasses();
        addLines();
        addUi();
//        addCards();
        addDewSpawner();
        addHeroSpawner();

        resize(250,150);
    }

    public void addGrasses() {
        for (int i = 0;i < 5;i++) {
            for (int j = 0;j < 10;j++) {
                Grass grass = new Grass();
                grass.setRect(j * 20 + 15,i * 20 + 30,20,20);
                grasses.add(grass);
                add(grass);
            }
        }
    }

    public void addLines() {
        for (int i = 0;i < 6;i++) {
            ColorBlock colorBlock = new ColorBlock(10 * 20,1,0xFF111111);
            colorBlock.x = 0 + 15;
            colorBlock.y = i * 20 + 30;
            add(colorBlock);
        }

        for (int j = 0;j < 11;j++) {
            ColorBlock colorBlock = new ColorBlock(1,5 * 20,0xFF111111);
            colorBlock.x = j * 20 + 15;
            colorBlock.y = 0 + 30;
            add(colorBlock);
        }
    }

    public void addUi() {
        WaterImage waterImage = new WaterImage();
        waterImage.setRect(2,0,20,20);
        add(waterImage);

        blueBlock = new BlueBlock();
        blueBlock.x = 4;
        blueBlock.scale.y = 4 * Math.min((water / 1000f),1f);
        blueBlock.y = 10 - blueBlock.scale.y;
        add(blueBlock);

        txt = new StyledButton(Chrome.Type.GREY_BUTTON,Integer.toString(water),7);
        txt.setRect(0,18,20,10);
        add(txt);

        pickaxe = new Pickaxe();
        pickaxe.setRect(210,0,20,20);
        add(pickaxe);
    }

    public void addCards() {
        int i = 1;

        for (Card card : cards) {
            card.setRect(20 * i,0,20,20);
            add(card);
            i++;
        }
    }

    public void addDewSpawner() {
        DewSpawner dewSpawner = new DewSpawner();
        dewSpawner.x = 0;
        dewSpawner.y = 0;
        addToBack(dewSpawner);
    }

    public void addHeroSpawner() {
        mvHHeroSpawner = new MvHHeroSpawner();
        mvHHeroSpawner.x = 0;
        mvHHeroSpawner.y = 0;
        addToBack(mvHHeroSpawner);
    }

    public void addHero(MvHHeroSprite newHeroSprite) {
        newHeroSprite.setMapToPos(10 + 1.5f * Random.Float(), Random.Int(5));
        WndMvH.heroSprites.add(newHeroSprite);
        WndMvH.wndMvH.addToFront(newHeroSprite);
    }

    public void createToLoot(float x,float y) {
    }

    public void gameOver() {
        pause = true;
    }

    public class Grass extends IconButton {

        public MvHMobSprite mobSprite;

        public Grass() {
            super();

            Image image = new Image(Assets.Environment.TILES_SEWERS);
            image.frame(image.texture.uvRect(32,0,48,16));
            image.scale.set(1.25f);
            icon(image);
        }

        @Override
        public void onClick() {
            if (WndMvH.wndMvH.pause) {
                return;
            }

            if (mobSprite != null) {
                if (pickaxe.press) {
                    mobSprite.gameDie();
                    pickaxe.press = false;
                }

            } else if (select != null && selectCard != null && water >= select.cost) {
                MvHMobSprite newSprite = Reflection.newInstance(select.getClass());

                newSprite.setGrass(this);
                setMobSprite(newSprite);

                selectCard.cooldown = selectCard.mobSprite.coolDown;
                water -= select.cost;
                txt.text(Integer.toString(water));
                blueBlock.changeHeight();

                select = null;
                selectCard = null;
            }
        }

        @Override
        public void onPointerDown() {
            for (Grass grass : grasses) {
                if (grass.x == x || grass.y == y) {
                    grass.icon.brightness(1.5f);
                }
            }
            Sample.INSTANCE.play(Assets.Sounds.CLICK,2);
        }

        @Override
        public void onPointerUp() {
            for (Grass grass : grasses) {
                if (grass.x == x || grass.y == y) {
                    grass.icon.resetColor();
                }
            }
        }

        public void setMobSprite(MvHMobSprite mobSprite) {
            this.mobSprite = mobSprite;
            mobSprite.x = x + width() / 2 - mobSprite.width() / 2;
            mobSprite.y = y + height() / 2 - mobSprite.height() / 2;
            addToFront(mobSprite);
        }

        public void chooseEnemy() {
            mobSprite.enemy = null;
            MvHHeroSprite newEnemy = null;

            if (heroSprites.size() == 0) {
                return;
            }

            for (MvHHeroSprite hero : heroSprites) {
                if (hero.x > mobSprite.x && hero.x < 215 && hero.y + hero.height() / 2 < y + height() && hero.y + hero.height() / 2 > y) {
                    if (newEnemy == null) {
                        newEnemy = hero;
                    } else if (hero.x < newEnemy.x) {
                        newEnemy = hero;
                    }
                }
            }

            if (newEnemy != null) {
                mobSprite.enemy = newEnemy;
            }
        }
    }

    public class Card extends Component {

        public MvHMobSprite mobSprite;
        public IconButton card;
        public RenderedTextBlock txt;

        public ColorBlock colorBlock1;
        public ColorBlock colorBlock2;
        public ColorBlock colorBlock3;
        public ColorBlock colorBlock4;
        public ColorBlock colorBlock5;
        public ColorBlock colorBlock6;
        public ColorBlock colorBlock7;
        public ColorBlock colorBlock8;

        public ColorBlock coolDownBlock;

        public float cooldown;

        public Card(MvHMobSprite mobSprite) {
            super();

            Image image = new Image(Assets.Interfaces.CARDFRONT);
            image.frame(0, 0, 20, 32);
            image.scale.set(1f,0.625f);

            card = new IconButton(image) {
                @Override
                public void onClick() {
                    super.onClick();

                    if (mobSprite == null || WndMvH.wndMvH.pause) {
                        return;
                    }

                    pickaxe.press = false;
                    if (select == mobSprite) {
                        select = null;
                        selectCard = null;
                    } else {
                        select = mobSprite;
                        selectCard = Card.this;
                    }
                }
            };
            card.enable(cooldown <= 0 && water >= mobSprite.cost);

            this.mobSprite = mobSprite;

            colorBlock1 = new ColorBlock(6,1,0xFFFFFF00);
            colorBlock2 = new ColorBlock(6,1,0xFFFFFF00);
            colorBlock3 = new ColorBlock(1,6,0xFFFFFF00);
            colorBlock4 = new ColorBlock(1,6,0xFFFFFF00);
            colorBlock5 = new ColorBlock(6,1,0xFFFFFF00);
            colorBlock6 = new ColorBlock(6,1,0xFFFFFF00);
            colorBlock7 = new ColorBlock(1,6,0xFFFFFF00);
            colorBlock8 = new ColorBlock(1,6,0xFFFFFF00);

            txt = PixelScene.renderTextBlock(Integer.toString(mobSprite.cost),5);
            txt.setHightlighting(true);

            coolDownBlock = new ColorBlock(20f,20f,0xAA222222);

            cooldown = mobSprite.firstCoolDown;
        }

        @Override
        protected void layout() {
            super.layout();

            card.setRect(x,y,20,20);
            add(card);

            mobSprite.x = x + card.width() / 2 - mobSprite.width() / 2;
            mobSprite.y = y + card.height() / 2 - mobSprite.height() / 2;
            add(mobSprite);

            colorBlock1.x = x + 2;
            colorBlock1.y = y + 2;

            colorBlock2.x = x + width() - colorBlock2.width() - 3;
            colorBlock2.y = y + 2;

            colorBlock3.x = x + width() - 3;
            colorBlock3.y = y + 2;

            colorBlock4.x = x + width() - 3;
            colorBlock4.y = y + height() - colorBlock4.height() - 3;

            colorBlock5.x = x + width() - colorBlock5.width() - 3;
            colorBlock5.y = y + height() - 3;

            colorBlock6.x = x + 2;
            colorBlock6.y = y + height() - 3;

            colorBlock7.x = x + 2;
            colorBlock7.y = y + height() - colorBlock7.height() - 3;

            colorBlock8.x = x + 2;
            colorBlock8.y = y + 2;

            colorBlock1.visible = false;
            colorBlock2.visible = false;
            colorBlock3.visible = false;
            colorBlock4.visible = false;
            colorBlock5.visible = false;
            colorBlock6.visible = false;
            colorBlock7.visible = false;
            colorBlock8.visible = false;

            add(colorBlock1);
            add(colorBlock2);
            add(colorBlock3);
            add(colorBlock4);
            add(colorBlock5);
            add(colorBlock6);
            add(colorBlock7);
            add(colorBlock8);

            txt.setPos(x + width() - 2 - txt.width(),y + height() - 2 - txt.height());
            PixelScene.align(txt);
            addToFront(txt);

            coolDownBlock.x = x;
            coolDownBlock.y = y;
            coolDownBlock.scale.y = -1.0f;
            addToFront(coolDownBlock);
        }

        @Override
        public void update() {

            if (WndMvH.wndMvH.pause) {
                return;
            }

            if (cooldown >= 0) {
                cooldown -= Game.elapsed;
            }
            coolDownBlock.size(width(),height() * cooldown / mobSprite.coolDown);

            card.enable(cooldown <= 0 && water >= mobSprite.cost);

            if (select == mobSprite) {
                colorBlock1.visible = true;
                colorBlock2.visible = true;
                colorBlock3.visible = true;
                colorBlock4.visible = true;
                colorBlock5.visible = true;
                colorBlock6.visible = true;
                colorBlock7.visible = true;
                colorBlock8.visible = true;
            } else {
                colorBlock1.visible = false;
                colorBlock2.visible = false;
                colorBlock3.visible = false;
                colorBlock4.visible = false;
                colorBlock5.visible = false;
                colorBlock6.visible = false;
                colorBlock7.visible = false;
                colorBlock8.visible = false;
            }
        }
    }

    public class WaterImage extends Component {

        public Image waterSkin;
        public RenderedTextBlock txt;

        public WaterImage() {
            waterSkin = new Image(Assets.Sprites.ITEMS);
            waterSkin.frame(waterSkin.texture.uvRect(96,480,108,492));
        }

        public void layout() {
            waterSkin.x = x;
            waterSkin.y = y;
            add(waterSkin);
        }
    }

    public class BlueBlock extends ColorBlock {
        public BlueBlock() {
            super(8,4,0xFF0000FF);

            alpha(0.6f);
        }

        public void changeHeight() {
            scale.y = 4 * Math.min((water / 1000f),1f);
            y = 10 - blueBlock.scale.y;
        }
    }

    public class Dew extends IconButton {

        public float delay = 0.6f;

        public int right = 1;

        public float originX;
        public float originY;

        public float speedX = 0.8f;
        public float speedY = 1f;

        public boolean canMove = true;

        public Dew() {
            Image image = new Image(Assets.Sprites.ITEMS);
            image.frame(image.texture.uvRect(48,16,58,26));
            icon(image);

            if (Random.Int(2) == 0) {
                right = -1;
            }

            setSize(10,10);
        }

        public void move() {
            if (WndMvH.wndMvH.pause) {
                return;
            }

            icon.x += speedX * right;
            hotArea.x += speedX * right;

            icon.y -= speedY;
            hotArea.y -= speedY;

            if (icon.y > originY) {
                icon.y = Math.min(icon.y,originY + 3);
                hotArea.y = Math.min(hotArea.y,originY + 3);
                canMove = false;
            }

            speedY -= 0.172f;
        }

        @Override
        public void update() {
            super.update();

            if (WndMvH.wndMvH.pause) {
                return;
            }

            delay -= Game.elapsed;

            if (delay > 0 && canMove) {
                move();
            }

            if (delay < -20) {
                destroy();
            }

        }

        @Override
        public void onClick() {
            if (WndMvH.wndMvH.pause) {
                return;
            }

            Sample.INSTANCE.play(Assets.Sounds.DEWDROP);
            water += 25;
            txt.text(Integer.toString(water));
            blueBlock.changeHeight();
            destroy();
        }

        @Override
        public Component setPos(float x,float y) {
            originX = x;
            originY = y;
            return super.setPos(x,y);
        }
    }

    public class FreeDew extends IconButton {

        public FreeDew() {
            Image image = new Image(Assets.Sprites.ITEMS);
            image.frame(image.texture.uvRect(48,16,58,26));
            icon(image);

            setSize(10,10);
        }

        @Override
        public void onClick() {
            if (WndMvH.wndMvH.pause) {
                return;
            }

            Sample.INSTANCE.play(Assets.Sounds.DEWDROP);
            water += 25;
            txt.text(Integer.toString(water));
            blueBlock.changeHeight();
            destroy();
        }

        public void move() {
            icon.y += 0.15;
            hotArea.y += 0.15;
            if (icon.y > 140) {
                destroy();
            }
        }

        @Override
        public void update() {
            super.update();
            if (WndMvH.wndMvH.pause) {
                return;
            }
            move();
        }
    }

    public class Pickaxe extends IconButton {

        public boolean press;

        public Pickaxe() {
            Image image = new Image(Assets.Sprites.ITEMS);
            image.frame(image.texture.uvRect(64,448,78,462));
            icon(image);

            press = false;
        }

        @Override
        public void onClick() {
            if (WndMvH.wndMvH.pause) {
                return;
            }

            if (!press) {
                select = null;
                selectCard = null;
                press = true;
            } else {
                press = false;
            }
        }

        @Override
        public void update() {
            super.update();

            if (WndMvH.wndMvH.pause) {
                return;
            }

            if (press) {
                icon.brightness(2f);
            } else {
                icon.brightness(1f);
            }
        }
    }

    public class Menu extends IconButton {

    }

    public class DewSpawner extends Image {

        public float time = 4.25f + Random.Float(2.75f);

        public int haveSpawned = 0;

        @Override
        public void update() {
            if (WndMvH.wndMvH.pause) {
                return;
            }

            if (night) {
                return;
            }

            if ((time -= Game.elapsed) < 0) {
                FreeDew freeDew = new FreeDew();
                freeDew.setPos(Random.Float(15,195),30);
                addToFront(freeDew);

                haveSpawned++;
                time = 4.25f + Random.Float(2.75f) + 0.15f * haveSpawned;
            }
        }
    }

    public class lootCard extends Component {

        public MvHMobSprite mobSprite;
        public IconButton card;

        public lootCard(MvHMobSprite mobSprite) {
            super();

            Image image = new Image(Assets.Interfaces.CARDFRONT);
            image.frame(0, 0, 20, 32);
            image.scale.set(1f,0.625f);

            card = new IconButton(image) {
                @Override
                public void onClick() {
                    hide();
                    GameScene.show(new TownGamer.WndAdventure());
                }
            };

            this.mobSprite = mobSprite;
        }

        @Override
        protected void layout() {
            super.layout();

            card.setRect(x, y, 20, 20);
            add(card);

            mobSprite.x = x + card.width() / 2 - mobSprite.width() / 2;
            mobSprite.y = y + card.height() / 2 - mobSprite.height() / 2;
            add(mobSprite);
        }

        @Override
        public void update() {
            super.update();
        }
    }

    @Override
    public void onBackPressed() {
        if (!pause) {
            pause = true;
            GameScene.show(new WndOptions(Messages.get(WndMvH.class,"pause"),Messages.get(WndMvH.class,"exit"),Messages.get(WndMvH.class,"confirm"),Messages.get(WndMvH.class,"cancel")) {
                @Override
                public void onSelect(int index) {
                    pause = false;
                    if (index == 0) {
                        WndMvH.this.hide();
                        hide();
                    } else {
                        hide();
                    }
                }

                @Override
                public void onBackPressed() {
                }
            });
        } else {
            GameScene.show(new WndOptions(Messages.get(WndMvH.class,"pause"),Messages.get(WndMvH.class,"exit"),Messages.get(WndMvH.class,"confirm"),Messages.get(WndMvH.class,"cancel")) {
                @Override
                public void onSelect(int index) {
                    if (index == 0) {
                        WndMvH.this.hide();
                        hide();
                    } else {
                        hide();
                    }
                }

                @Override
                public void onBackPressed() {
                }
            });
        }
    }

    @Override
    public void destroy() {
        Music.INSTANCE.pause();
        super.destroy();
    }
}