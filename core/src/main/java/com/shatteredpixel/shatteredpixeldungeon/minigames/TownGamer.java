package com.shatteredpixel.shatteredpixeldungeon.minigames;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.level.MvH1_1;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.level.MvH1_2;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.level.MvH1_3;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.level.MvH1_4;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.level.MvH1_5;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.level.MvH1_6;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.level.MvH1_7;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ShopkeeperSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.OptionSlider;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.utils.Callback;

public class TownGamer extends NPC {

    {
        spriteClass = ShopkeeperSprite.class;

        properties.add(Property.IMMOVABLE);
    }

    @Override
    public boolean interact(Char c) {

        if (c != Dungeon.hero) {
            return false;
        }

        Game.runOnRenderThread(new Callback() {
            @Override
            public void call() {

                GameScene.show(new WndOptions(new ShopkeeperSprite(),Messages.get(TownGamer.class,"gamer"),Messages.get(TownGamer.class,"play"),
                        Messages.get(TownGamer.class,"blackjack"),Messages.get(TownGamer.class,"starving_warrior"),Messages.get(TownGamer.class,"hungry_warrior"),
                        Messages.get(TownGamer.class,"minesweeper"),Messages.get(TownGamer.class,"mvh"),Messages.get(TownGamer.class,"no")) {
                    @Override
                    protected void onSelect(int index) {
                        if (index == 0) {
                            BlackJack.gameStart();
                            GameScene.show(new WndBlackJack(false));
                        }

                        if (index == 1) {
                            GameScene.show(new WndOptions(Messages.get(TownGamer.class,"difficulty"),Messages.get(TownGamer.class,"explain1"),
                                    Messages.get(TownGamer.class,"easy"),Messages.get(TownGamer.class,"normal"),
                                    Messages.get(TownGamer.class,"hard"),Messages.get(TownGamer.class,"no")) {
                                @Override
                                protected void onSelect(int index) {
                                    switch (index) {
                                        case 0:
                                            WndStarvingWarrior.speed = 1f;
                                            GameScene.show(new WndStarvingWarrior());
                                            break;
                                        case 1:
                                            WndStarvingWarrior.speed = 0.5f;
                                            GameScene.show(new WndStarvingWarrior());
                                            break;
                                        case 2:
                                            WndStarvingWarrior.speed = 0.25f;
                                            GameScene.show(new WndStarvingWarrior());
                                            break;
                                        case 3:
                                            hide();
                                            break;
                                    }
                                }
                            });
                        }

                        if (index == 2) {
                            GameScene.show(new WndHungryWarrior());
                        }

                        if (index == 3) {
                            GameScene.show(new WndOptions(Messages.get(TownGamer.class,"difficulty"),Messages.get(TownGamer.class,"explain2"),
                                    Messages.get(TownGamer.class,"easy"),Messages.get(TownGamer.class,"normal"),
                                    Messages.get(TownGamer.class,"hard"),Messages.get(TownGamer.class,"custom"),Messages.get(TownGamer.class,"no")) {
                                @Override
                                public void onSelect(int index) {
                                    switch (index) {
                                        case 0:
                                            WndMineSweeper.row = 8;
                                            WndMineSweeper.column = 8;
                                            WndMineSweeper.mineNumber = 10;
                                            GameScene.show(new WndMineSweeper());
                                            break;
                                        case 1:
                                            WndMineSweeper.row = 16;
                                            WndMineSweeper.column = 16;
                                            WndMineSweeper.mineNumber = 40;
                                            GameScene.show(new WndMineSweeper());
                                            break;
                                        case 2:
                                            WndMineSweeper.row = 16;
                                            WndMineSweeper.column = 30;
                                            WndMineSweeper.mineNumber = 99;
                                            GameScene.show(new WndMineSweeper());
                                            break;
                                        case 3:
                                            GameScene.show(new WndMine());
                                            break;
                                        case 4:
                                            hide();
                                            break;
                                    }
                                }
                            });
                        }

                        if (index == 4) {
                            Boolean landscape = SPDSettings.landscape();
                            if (landscape == null){
                                landscape = Game.width > Game.height;
                            }
                            if (!landscape) {
                                GameScene.show(new WndOptions("确认","怪物大战英雄需要横屏才能运行，是否改为横屏？","确认","取消") {
                                    @Override
                                    public void onSelect(int index) {
                                        switch (index) {
                                            case 0:
                                                SPDSettings.landscape(true);
                                                hide();
                                                break;
                                            case 1:
                                            default:
                                                hide();
                                        }
                                    }
                                });
                            } else {
                                GameScene.show(new WndAdventure());
                            }
                        }

                        if (index == 5) {
//                            GameScene.show(new WndDebugging());
                            hide();
                        }

                    }
                });

            }
        });

        return true;
    }

    public static class WndAdventure extends WndOptions {

        public WndAdventure() {
            super(Messages.get(TownGamer.class,"adventure"),Messages.get(TownGamer.class,"in_development"),
                    "1-1","1-2","1-3","1-4","1-5","1-6","1-7",Messages.get(TownGamer.class,"no"));
        }
        @Override
        public void onSelect(int index) {

            switch (index) {
                case 0:
                    GameScene.show(new MvH1_1());
                    break;
                case 1:
                    GameScene.show(new MvH1_2());
                    break;
                case 2:
                    GameScene.show(new MvH1_3());
                    break;
                case 3:
                    GameScene.show(new MvH1_4());
                    break;
                case 4:
                    GameScene.show(new MvH1_5());
                    break;
                case 5:
                    GameScene.show(new MvH1_6());
                    break;
                case 6:
                    GameScene.show(new MvH1_7());
                    break;
                default:
                    hide();
                    break;
            }
        }
    }

    public class WndMine extends Window {

        public int ROW;
        public int COLUMN;
        public int MINE;

        public RenderedTextBlock txt;

        public WndMine() {
            ROW = WndMineSweeper.row;
            COLUMN = WndMineSweeper.column;
            MINE = 1;

//            Messages.get(TownGamer.class,"");
            OptionSlider row = new OptionSlider(Messages.get(TownGamer.class,"row"),"4","16",4,16) {
                @Override
                protected void onChange() {
                    ROW = getSelectedValue();
                    txt.text(Messages.get(TownGamer.class,"row") + ROW + "," + Messages.get(TownGamer.class,"column") + COLUMN + "," + Messages.get(TownGamer.class,"mine") + MINE + "%");
                }
            };
            row.setRect(0,0,120,20);
            add(row);

            OptionSlider column = new OptionSlider(Messages.get(TownGamer.class,"column"),"4","30",4,30) {
                @Override
                protected void onChange() {
                    COLUMN = getSelectedValue();
                    txt.text(Messages.get(TownGamer.class,"row") + ROW + "," + Messages.get(TownGamer.class,"column") + COLUMN + "," + Messages.get(TownGamer.class,"mine") + MINE + "%");
                }
            };
            column.setRect(0,20,120,20);
            add(column);

            OptionSlider mine = new OptionSlider(Messages.get(TownGamer.class,"mine"),"1%","50%",1,50) {
                @Override
                protected void onChange() {
                    MINE = getSelectedValue();
                    txt.text(Messages.get(TownGamer.class,"row") + ROW + "," + Messages.get(TownGamer.class,"column") + COLUMN + "," + Messages.get(TownGamer.class,"mine") + MINE + "%");
                }
            };
            mine.setRect(0,40,120,20);
            add(mine);

            txt = PixelScene.renderTextBlock(Messages.get(TownGamer.class,"row") + ROW + "," + Messages.get(TownGamer.class,"column") + COLUMN + "," + Messages.get(TownGamer.class,"mine") + MINE + "%",6);
            txt.setPos(0,62);
            add(txt);

            RedButton confirm = new RedButton(Messages.get(TownGamer.class,"confirm")) {
                @Override
                public void onClick() {
                    WndMineSweeper.row = ROW;
                    WndMineSweeper.column = COLUMN;
                    WndMineSweeper.mineNumber = Math.max((int)(ROW * COLUMN * MINE / 100),1);
                    hide();
                    GameScene.show(new WndMineSweeper());
                }
            };
            confirm.setRect(0,txt.bottom(),60,20);
            add(confirm);

            RedButton cancel = new RedButton(Messages.get(TownGamer.class,"cancel")) {
                @Override
                public void onClick() {
                    hide();
                }
            };
            cancel.setRect(60,txt.bottom(),60,20);
            add(cancel);

            resize(120,(int)confirm.bottom());
        }
    }
}
