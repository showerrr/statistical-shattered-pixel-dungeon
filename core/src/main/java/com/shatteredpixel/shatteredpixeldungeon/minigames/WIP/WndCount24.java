package com.shatteredpixel.shatteredpixeldungeon.minigames.wip;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.input.PointerEvent;
import com.watabou.input.ScrollEvent;
import com.watabou.noosa.Image;
import com.watabou.noosa.ScrollArea;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;

public class WndCount24 extends Window {

    public ArrayList<Integer> numbers;

    public Card card1;
    public Card card2;
    public Card card3;
    public Card card4;

    public ArrayList<Card> cards;
    public ArrayList<Integer> CardSlot;
    public ArrayList<Calculator> calculators;

    public enum Four {
        ADD,SUBTRACT,MULTIPLY,DIVIDE
    }

    public WndCount24() {
        fullReset();

        card1 = new Card(numbers.get(0));
        card1.setRect(0,100,20,32);
        addToFront(card1);
        cards.add(card1);

        card2 = new Card(numbers.get(1));
        card2.setRect(0,100,20,32);
        addToFront(card2);
        cards.add(card2);

        for (int i = 0;i < 2;i++) {
            Image cardSlot = new Image(Assets.Interfaces.CARDSLOT);
            cardSlot.frame(cardSlot.texture.uvRect(0, 0, 20, 32));
            cardSlot.x = i * 40;
            cardSlot.y = 0;
            add(cardSlot);
        }

        Calculator calculator1 = new Calculator(Four.ADD);
        calculator1.setRect(25,11,10,10);
        add(calculator1);
        calculators.add(calculator1);

        Result result = new Result(0);
        result.setRect(65,11,10,10);
        add(result);

        resize(120,120);
    }

    public void reset() {
        CardSlot = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0));
        calculators = new ArrayList<>();
        cards = new ArrayList<>();
    }

    public void fullReset() {
        numbers = new ArrayList<>(4);

        for (int i = 0;i < 4;i++) {
            int j = Random.Int(10) + 1;
            numbers.add(j);
        }

        reset();
    }

    public void attach(Card card) {
        if (card.left() > -10 && card.left() < 10 && card.top() > -10 && card.top() < 10 && CardSlot.get(0) == 0) {
            card.setPos(0,0);
            CardSlot.set(0,card.number);
//            board.used = true;

        }

        if (card.left() > 30 && card.left() < 50 && card.top() > -10 && card.top() < 10 && CardSlot.get(1) == 0) {
            card.setPos(40,0);
            CardSlot.set(1,card.number);
//            board.used = true;
        }
    }

    public boolean canCalculate(int i) {
        int a1 = CardSlot.get(2 * i);
        int a2 = CardSlot.get(2 * i + 1);

        if (a1 == 0 || a2 == 0) {
            return false;
        }

        if (calculators.get(i).four == Four.DIVIDE) {
            int a = a1 / a2;
            return a * a2 == a1;
        }

        return true;
    }

    public class Calculator extends StyledButton {

        public Four four;

        public Calculator(Four four) {
            super(Chrome.Type.GREY_BUTTON,"",8);

            this.four = four;

            changeIcon();
        }

        public void changeIcon() {
            switch (four) {
                case ADD:
                    text("+");
                    break;
                case SUBTRACT:
                    text("-");
                    break;
                case MULTIPLY:
                    text("*");
                    break;
                case DIVIDE:
                    text("/");
                    break;
            }
        }

        @Override
        public void onClick() {
            switch (four) {
                case ADD:
                    four = Four.SUBTRACT;
                    break;
                case SUBTRACT:
                    four = Four.MULTIPLY;
                    break;
                case MULTIPLY:
                    four = Four.DIVIDE;
                    break;
                case DIVIDE:
                    four = Four.ADD;
                    break;
            }

            changeIcon();
        }
    }



    public class Result extends IconButton {

        public int line;

        public Result(int line) {
            super();
            Image image = new Image(Assets.Interfaces.ICONS);
            image.frame(image.texture.uvRect(73,64,83,71));
            icon(image);

            this.line = line;
        }

        @Override
        public void onClick() {
            if (canCalculate(line)) {
                for (Card card : cards) {
                    if (card.top() == 32 * line && (card.left() == 0 || card.left() == 40)) {
                        card.used = true;
                    }
                }
            }
        }
    }

    public class Card extends Component {

        public Image image;

        public PointerController pointerController;

        public int number;

        public RenderedTextBlock txt;

        public boolean used = false;

        public Card(int number) {
            this.number = number;

            image = new Image(Assets.Interfaces.CARDFRONT);
            image.frame(image.texture.uvRect(0, 0, 20, 32));

            pointerController = new PointerController();
            pointerController.setComponent(this);

            txt = PixelScene.renderTextBlock(Integer.toString(number),7);
            txt.setHightlighting(true);
        }
        
        @Override
        public void layout() {
            image.x = x;
            image.y = y;
            add(image);

            pointerController.x = x;
            pointerController.y = y;
            pointerController.width = width;
            pointerController.height = height;
            add(pointerController);

            txt.setPos(x + 3,y + (height - txt.height()) / 4);
            PixelScene.align(txt);
            add( txt );
        }
    }

    public class PointerController extends ScrollArea {

        private float dragThreshold;
        public Card card;

        public PointerController() {
            super(0,0,0,0 );
            dragThreshold = PixelScene.defaultZoom;
        }

        public void setComponent(Card card) {
            this.card = card;
        }

        @Override
        protected void onScroll(ScrollEvent event) {
            if (card.used) {
                return;
            }

            PointF newPt = new PointF(lastPos);

            scroll(newPt);
            dragging = false;
        }

        @Override
        protected void onPointerUp( PointerEvent event ) {
            if (dragging) {
                dragging = false;
            }

            attach(card);
        }

        private boolean dragging = false;
        private PointF lastPos = new PointF();

        @Override
        protected void onDrag( PointerEvent event ) {
            if (card.used) {
                return;
            }

            if (dragging) {

                scroll(event.current);

            } else if (PointF.distance( event.current, event.start ) > dragThreshold) {

                dragging = true;
                lastPos.set(event.current);

            }
        }

        private void scroll( PointF current ){

            card.setPos(card.left() - PointF.diff(lastPos,current).x / PixelScene.defaultZoom,card.top() - PointF.diff(lastPos,current).y / PixelScene.defaultZoom);

            lastPos.set(current);
        }
    }
}
