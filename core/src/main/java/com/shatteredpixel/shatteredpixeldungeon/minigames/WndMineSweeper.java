package com.shatteredpixel.shatteredpixeldungeon.minigames;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.effects.BannerSprites;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.WndMvH;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Banner;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashMap;

public class WndMineSweeper extends Window {

    public int[][] map;

    public static int row = 16;//一行多少个
    public static int column = 16;//一列多少个

    public static int mineNumber = 40;

    public boolean hasPressed;
    public int firstNumberR = 0;
    public int firstNumberC = 0;

    public static int[] NEIGHBOURS = new int[]{-1,0,+1};

    public HashMap<Integer,Block> hashMap;
    public static ArrayList<Image> mines;

    public int[][] ZERO;

    public boolean gameOver;

    public IconButton dig;
    public IconButton mark;
    public IconButton unknown;

    public int type;
    public ColorBlock choose;

    public Image counter;
    public RenderedTextBlock Counter;
    public int counterNumber;

    public WndMineSweeper() {

        reset();

        for (int i = 0;i < row;i++) {
            for (int j = 0;j < column;j++) {
                Block block = new Block();
                block.X = i;
                block.Y = j;
                block.setRect(i * 10, j * 10,10,10);
                hashMap.put(100 * i + j,block);
                add(block);
            }
        }

        Image digImage = new Image(Assets.Sprites.ITEM_ICONS);
        digImage.frame(digImage.texture.uvRect(0,16,7,23));
        digImage.scale.set(2f);
        dig = new IconButton(digImage) {
            @Override
            public void onClick() {
                type = 0;
                choose.x = this.x;
            }
        };
        dig.setRect(0,column * 10 + 2,row * 10 / 6,row * 10 / 6);
        add(dig);

        Image flagImage = new Image(Assets.Sprites.ITEM_ICONS);
        flagImage.frame(flagImage.texture.uvRect(0,0,7,7));
        flagImage.scale.set(2f);
        mark = new IconButton(flagImage) {
            @Override
            public void onClick() {
                type = 1;
                choose.x = this.x;
            }
        };
        mark.setRect(row * 10 / 6,column * 10 + 2,row * 10 / 6,row * 10 / 6);
        add(mark);

        Image unknownImage = new Image(Assets.Sprites.ITEM_ICONS);
        unknownImage.frame(unknownImage.texture.uvRect(8,16,12,23));
        unknownImage.scale.set(2f);
        unknown = new IconButton(unknownImage) {
            @Override
            public void onClick() {
                type = 2;
                choose.x = this.x;
            }
        };
        unknown.setRect(2 * (row * 10 / 6),column * 10 + 2,row * 10 / 6,row * 10 / 6);
        add(unknown);

        choose = new ColorBlock(row * 10 / 6,2,0xFFFFFF00);
        choose.x = 0;
        choose.y = column * 10;
        add(choose);

        counter = new Image(Assets.Environment.TERRAIN_FEATURES);
        counter.frame(counter.texture.uvRect(112,96,127,111));
        counter.scale.set(0.8f);
        counter.x = 3 * (row * 10 / 6);
        counter.y = column * 10 + row * 10 / 6 / 2 - counter.height() / 2;
        add(counter);

        Counter = PixelScene.renderTextBlock("x" + counterNumber,6);
        Counter.setPos(counter.x + counter.width(),counter.y + counter.height() / 2 - Counter.height() / 2);
        add(Counter);

        resize(row * 10,column * 10 + row * 10 / 6);
    }

    public void reset() {
        map = new int[row][column];
        ZERO = new int[row][column];
        hashMap = new HashMap();
        mines = new ArrayList<>();
        hasPressed = false;
        gameOver = false;
        counterNumber = mineNumber;
        type = 0;
    }

    public void addMines() {
        int m = mineNumber;

        do {
            int i = Random.Int(row);
            int j = Random.Int(column);
            if (!(i == firstNumberR && j == firstNumberC) && map[i][j] != -1) {
                map[i][j] = -1;
                m--;

                Image mine = new Image(Assets.Environment.TERRAIN_FEATURES);
                mine.frame(mine.texture.uvRect(112,96,127,111));
                mine.scale.set(0.65f);
                Block block = hashMap.get(100 * i + j);
                mine.x = block.left() + 5 - mine.width() / 2;
                mine.y = block.top() + 5 - mine.height() / 2;
                mines.add(mine);
                add(mine);
                bringToFront(block);
            }
        } while (m > 0);

        for (int i = 0;i < row;i++) {
            for (int j = 0;j < column;j++) {

                if (map[i][j] != -1) {
                    int k = haveMine(i,j);
                    map[i][j] = k;

                    if (k > 0) {
                        Block block = hashMap.get(100 * i + j);
                        RenderedTextBlock txt = PixelScene.renderTextBlock(Integer.toString(k),7);

                        switch (k) {
                            case 1:
                                txt.hardlight(0xFF0000FF);
                                break;
                            case 2:
                                txt.hardlight(0xFF00FF00);
                                break;
                            case 3:
                                txt.hardlight(0xCC0000);
                                break;
                            default:
                                txt.hardlight(0xFFFFFFFF);
                                break;
                        }

                        txt.setPos(block.left() + 5 - txt.width() / 2,block.top() + 5 - txt.height() / 2);
                        add(txt);
                        bringToFront(block);
                    }
                }

            }
        }
    }

    public int haveMine(int i,int j) {
        int k = 0;
        for (int a : NEIGHBOURS) {
            for (int b : NEIGHBOURS) {
                if (a == 0 && b == 0) {
                    continue;
                }
                if ((a + i) >= 0 && (a + i) < row && (b + j) >= 0 && (b + j) < column && map[a + i][b + j] == -1) {
                    k++;
                }
            }
        }
        return k;
    }

    public void check() {
        boolean win = true;

        for (int i = 0;i < row;i++) {
            for (int j = 0;j < column;j++) {
                if (map[i][j] >= 0 && hashMap.get(i * 100 + j).visible) {
                    win = false;
                    break;
                }
            }
        }

        if (win) {
            win();
        }
    }

    public void win() {
        gameOver = true;

        for (Image mine : mines) {
            bringToFront(mine);
        }

        RedButton restart = new RedButton(Messages.get(this,"onemoregame"),6) {
            @Override
            public void onClick() {
                hide();
                GameScene.show(new WndMineSweeper());
            }
        };
        restart.setRect(0,column * 10 + 2,row * 5,row * 10 / 6);
        addToFront(restart);

        RedButton exit = new RedButton(Messages.get(this,"exit"),6) {
            @Override
            public void onClick() {
                hide();
            }
        };
        exit.setRect(row * 5,column * 10 + 2,row * 5,row * 10 / 6);
        addToFront(exit);

        GameScene.bossSlain();
        GLog.n("YOU WIN");
    }

    public void gameOver() {
        gameOver = true;

        for (Image mine : mines) {
            bringToFront(mine);
        }

        RedButton restart = new RedButton("再来一把",6) {
            @Override
            public void onClick() {
                hide();
                GameScene.show(new WndMineSweeper());
            }
        };
        restart.setRect(0,column * 10 + 2,row * 5,row * 10 / 6);
        addToFront(restart);

        RedButton exit = new RedButton("退出",6) {
            @Override
            public void onClick() {
                hide();
            }
        };
        exit.setRect(row * 5,column * 10 + 2,row * 5,row * 10 / 6);
        addToFront(exit);

//        GameScene.gameOver();
        Banner gameOver = new Banner( BannerSprites.get( BannerSprites.Type.GAME_OVER ) );
        gameOver.show( 0x000000, 2f );
        gameOver.camera = PixelScene.uiCamera;
        float offset = Camera.main.centerOffset.y;
        gameOver.x = PixelScene.align(PixelScene.uiCamera,(PixelScene.uiCamera.width - gameOver.width) / 2 );
        gameOver.y = PixelScene.align(PixelScene.uiCamera,(PixelScene.uiCamera.height - gameOver.height) / 2 - gameOver.height/2 - 16 - offset);
        addToFront(gameOver);
        GLog.n("GAMEOVER");
    }

    public class Block extends StyledButton {

        public int X;
        public int Y;

        public Image Mark;
        public boolean haveFlag = false;
        public boolean haveUnknown = false;

        public Block() {
            super(Chrome.Type.GREY_BUTTON,"",1);
        }

        @Override
        public void onClick() {
            if (gameOver || !visible) {
                return;
            }

            if (type == 1) {
                if (haveFlag) {
                    counterNumber++;
                    Counter.text("x" + counterNumber);

                    Mark.killAndErase();
                    haveFlag = false;
                } else {
                    if (haveUnknown) {
                        Mark.killAndErase();
                        haveUnknown = false;
                    }

                    Mark = new Image(Assets.Sprites.ITEM_ICONS);
                    Mark.frame(Mark.texture.uvRect(0,0,7,7));
                    Mark.x = x + 5 - Mark.width() / 2;
                    Mark.y = y + 5 - Mark.height() / 2;
                    add(Mark);
                    haveFlag = true;

                    counterNumber--;
                    Counter.text("x" + counterNumber);
                }
                return;
            }

            if (type == 2) {
                if (haveUnknown) {
                    Mark.killAndErase();
                    haveUnknown = false;
                } else {
                    if (haveFlag) {
                        counterNumber++;
                        Counter.text("x" + counterNumber);

                        Mark.killAndErase();
                        haveFlag = false;
                    }

                    Mark = new Image(Assets.Sprites.ITEM_ICONS);
                    Mark.frame(Mark.texture.uvRect(8,16,12,23));
                    Mark.x = x + 5 - Mark.width() / 2;
                    Mark.y = y + 5 - Mark.height() / 2;
                    add(Mark);
                    haveUnknown = true;
                }

                return;
            }

            if ((haveFlag || haveUnknown) && type == 0) {
                return;
            }

            if (!hasPressed) {
                firstNumberR = X;
                firstNumberC = Y;
                addMines();
                hasPressed = true;
            }

            visible = false;

            if (map[X][Y] == -1) {
                gameOver();

            } else if (map[X][Y] == 0) {
                if (ZERO[X][Y] != -1) {
                    ZERO[X][Y] = 1;
                }

                invisible();
            }

            Sample.INSTANCE.play(Assets.Sounds.SECRET);

            check();
        }
    }

    public void invisible() {
        boolean again = false;

        for (int i = 0;i < row;i++) {
            for (int j = 0;j < column;j++) {
                if (ZERO[i][j] == 1) {

                    for (int a : NEIGHBOURS) {
                        for (int b : NEIGHBOURS) {
                            if (a == 0 && b == 0) {
                                continue;
                            }

                            if ((a + i) >= 0 && (a + i) < row && (b + j) >= 0 && (b + j) < column) {
                                Block block1 = hashMap.get(100 * (a + i) + (b + j));
                                block1.visible = false;
                                if (map[a + i][b + j] == 0 && ZERO[a + i][b + j] >= 0) {
                                    ZERO[a + i][b + j] = 1;
                                    again = true;
                                }
                            }
                        }
                    }

                    ZERO[i][j] = -1;
                }
            }
        }

        if (again) {
            invisible();
        }
    }

    @Override
    public void onBackPressed() {
        GameScene.show(new WndOptions(Messages.get(WndMvH.class,"pause"),Messages.get(WndMvH.class,"exit"),Messages.get(WndMvH.class,"confirm"),Messages.get(WndMvH.class,"cancel")) {
            @Override
            public void onSelect(int index) {
                if (index == 0) {
                    WndMineSweeper.this.hide();
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
