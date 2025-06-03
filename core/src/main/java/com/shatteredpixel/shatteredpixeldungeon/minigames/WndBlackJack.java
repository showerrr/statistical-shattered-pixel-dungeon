package com.shatteredpixel.shatteredpixeldungeon.minigames;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class WndBlackJack extends Window {

    public WndBlackJack(boolean endGame) {

        float pos = 0f;

        CardFront cardImage0 = new CardFront(BlackJack.PCcards.get(0));
        cardImage0.setRect(pos,50,20,32);
        add(cardImage0);
        pos += 22;

        CardFront cardImage1 = new CardFront(BlackJack.PCcards.get(1));
        cardImage1.setRect(pos,50,20,32);
        add(cardImage1);
        pos += 22;

        if (BlackJack.PCcards.size() >= 3) {
            CardFront cardImage2 = new CardFront(BlackJack.PCcards.get(2));
            cardImage2.setRect(pos, 50, 20, 32);
            add(cardImage2);
            pos += 22;
        }

        if (BlackJack.PCcards.size() >= 4) {
            CardFront cardImage3 = new CardFront(BlackJack.PCcards.get(3));
            cardImage3.setRect(pos,50,20,32);
            add(cardImage3);
            pos += 22;
        }

        if (BlackJack.PCcards.size() >= 5) {
            CardFront cardImage4 = new CardFront(BlackJack.PCcards.get(4));
            cardImage4.setRect(pos,50,20,32);
            add(cardImage4);
            pos += 22;
        }

        pos = 88f;

        if (!endGame) {

            CardBack cardImage5 = new CardBack();
            cardImage5.setRect(pos,0,20,32);
            add(cardImage5);
            pos -= 22;

            CardBack cardImage6 = new CardBack();
            cardImage6.setRect(pos,0,20,32);
            add(cardImage6);
            pos -= 22;

            if (BlackJack.AIcards.size() >= 3) {
                CardBack cardImage7 = new CardBack();
                cardImage7.setRect(pos,0,20,32);
                add(cardImage7);
                pos -= 22;
            }

            if (BlackJack.AIcards.size() >= 4) {
                CardBack cardImage8 = new CardBack();
                cardImage8.setRect(pos,0,20,32);
                add(cardImage8);
                pos -= 22;
            }

            if (BlackJack.AIcards.size() >= 5) {
                CardBack cardImage9 = new CardBack();
                cardImage9.setRect(pos,0,20,32);
                add(cardImage9);
                pos -= 22;
            }

            String PCaction = Messages.get(this,"pcdraw");
            RenderedTextBlock PCact = PixelScene.renderTextBlock(PCaction,7);
            PCact.setRect(0,40, PCact.width(), 10);
            if (BlackJack.PCdraw)add(PCact);

            String AIaction = Messages.get(this,"aidraw");
            RenderedTextBlock AIact = PixelScene.renderTextBlock(AIaction,7);
            AIact.setRect(110-AIact.width(),40, AIact.width(), 10);
            if (BlackJack.AIdraw)add(AIact);

            String yes = BlackJack.PCnum == 0 ? Messages.get(this,"toomany") : Messages.get(this,"drawmore");
            RedButton btnDraw = new RedButton(yes) {
                @Override
                protected void onClick() {
                    BlackJack.PCdraw = true;
                    hide();
                    BlackJack.nextTurn();
                }
            };
            btnDraw.enable( !(BlackJack.PCcards.size() == 5) && BlackJack.PCnum != 0);
            btnDraw.setRect(0,90,55,20);
            add(btnDraw);

            String no = BlackJack.PCnum >= 21 ? Messages.get(this,"enough") : Messages.get(this,"nomore");
            RedButton btnPass = new RedButton(no) {
                @Override
                protected void onClick() {
                    BlackJack.PCdraw = false;
                    hide();
                    BlackJack.nextTurn();
                }
            };

            btnPass.setRect(55,90,55,20);
            add(btnPass);

        } else {

            CardFront cardImage5f = new CardFront(BlackJack.AIcards.get(0));
            cardImage5f.setRect(pos,0,20,32);
            add(cardImage5f);
            pos -= 22;

            CardFront cardImage6f = new CardFront(BlackJack.AIcards.get(1));
            cardImage6f.setRect(pos,0,20,32);
            add(cardImage6f);
            pos -= 22;

            if (BlackJack.AIcards.size() >= 3) {
                CardFront cardImage7f = new CardFront(BlackJack.AIcards.get(2));
                cardImage7f.setRect(pos,0,20,32);
                add(cardImage7f);
                pos -= 22;
            }

            if (BlackJack.AIcards.size() >= 4) {
                CardFront cardImage8f = new CardFront(BlackJack.AIcards.get(3));
                cardImage8f.setRect(pos,0,20,32);
                add(cardImage8f);
                pos -= 22;
            }

            if (BlackJack.AIcards.size() >= 5) {
                CardFront cardImage9f = new CardFront(BlackJack.AIcards.get(4));
                cardImage9f.setRect(pos,0,20,32);
                add(cardImage9f);
                pos -= 22;
            }

            String msg;
            if (BlackJack.PCnum > BlackJack.AInum) {
                msg = Messages.get(this,"win");
            } else if (BlackJack.PCnum < BlackJack.AInum) {
                msg = Messages.get(this,"lose");
            } else {
                msg = Messages.get(this,"tie");
            }

            RenderedTextBlock result = PixelScene.renderTextBlock(msg,7);
            result.setRect(0,40, result.width(), 10);
            result.setHightlighting(true);
            add(result);

            RedButton btnAgain = new RedButton(Messages.get(this,"onemoregame")) {
                @Override
                protected void onClick() {
                    hide();
                    BlackJack.gameStart();
                    GameScene.show(new WndBlackJack(false));
                }
            };
            btnAgain.setRect(0,90,55,20);
            add(btnAgain);

            RedButton btnClose = new RedButton(Messages.get(this,"nomoregame")) {
                @Override
                protected void onClick() {
                    hide();
                }
            };

            btnClose.setRect(55,90,55,20);
            add(btnClose);
        }


        resize(110,110);
    }

    private class CardFront extends Component {

        Image icon;
        RenderedTextBlock txt;

        public CardFront(int i) {

            icon = new Image( Assets.Interfaces.CARDFRONT );
            icon.frame(0, 0, 20, 32);

            String num;
            if (i == 1) {
                num = "A";
            } else if (i == 11) {
                num = "J";
            } else if (i == 12) {
                num = "Q";
            } else if (i == 13) {
                num = "K";
            } else {
                num = Integer.toString(i);
            }

            txt = PixelScene.renderTextBlock( num, 7 );
            txt.setHightlighting(true);
        }

        @Override
        protected void layout() {
            super.layout();

            icon.x = this.x;
            icon.y = this.y;
            add( icon );

            txt.setPos(icon.x + 3, icon.y + (icon.height - txt.height()) / 4);
            PixelScene.align(txt);
            add( txt );
        }
    }

    private class CardBack extends Component {

        Image icon;

        public CardBack() {

            icon = new Image( Assets.Interfaces.CARDBACK );
            icon.frame(0, 0, 20, 32);

        }

        @Override
        protected void layout() {
            super.layout();

            icon.x = this.x;
            icon.y = this.y;
            add( icon );

        }
    }
}
