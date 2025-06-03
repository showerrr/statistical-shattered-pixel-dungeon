package com.shatteredpixel.shatteredpixeldungeon.minigames;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.MvHHeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.input.PointerEvent;
import com.watabou.input.ScrollEvent;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ScrollArea;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class WndHungryWarrior extends Window {

    public MvHHeroSprite hero;

    public PointerController pointerController;

    public static float Scale;

    public boolean gameOver = false;

    public static int star = 0;

    public WndHungryWarrior() {

        Scale = 1f;
        star = 0;

        hero= new MvHHeroSprite(0);
        hero.x = 0;
        hero.y = 140 - hero.height;
        add(hero);

        ThrowItem throwItem = new ThrowItem();
        throwItem.x = 0;
        throwItem.y = 0;
        throwItem.visible = false;
        add(throwItem);

        pointerController = new PointerController();
        pointerController.width = 100;
        pointerController.height = 140;
        add(pointerController);

        resize(100,140);
    }

    public boolean canEat(Image image) {
        if ((image.y + image.height()) < hero.y || (image.y + image.height() > 135)) {
            return false;
        }
        return Math.max(image.x, hero.x) < Math.min((image.x + image.width()), (hero.x + hero.width()));
    }

    public void justEatIt() {

        Scale += 0.1f;

        if (Scale >= 2) {
            Scale = 1;
            star ++;
            star = Math.min(star,5);
            hero.tier = star;
            hero.updateArmor();
        }

        hero.scale.set(Scale);
        hero.x = Math.min(hero.x,100 - hero.width());
        hero.y = 140 - hero.height();
    }

    public class Apple extends Image {

        public float time = 0.05f;

        public Apple() {
            super(Assets.Sprites.ITEMS);
            frame(texture.uvRect(192,432,201,443));
        }

        @Override
        public void update() {
            if ((time -= Game.elapsed) < 0 && !gameOver) {
                y += 1.5;
                time = 0.05f;
            }

            if (y + height() >= 140) {
                killAndErase();
                return;
            }

            if (canEat(this)) {
                justEatIt();
                killAndErase();
            }
        }
    }

    public class Bomb extends Image {

        public float time = 0.05f;

        public Bomb() {
            super(Assets.Sprites.ITEMS);

            int j;
            j = Random.Int(12);

            frame(texture.uvRect(16 * j,64,10 + 16 * j,77));
        }

        @Override
        public void update() {
            if ((time -= Game.elapsed) < 0 && !gameOver) {
                y += 1.5;
                time = 0.05f;
            }

            if (y + height() >= 135) {
                killAndErase();
                return;
            }

            if (canEat(this)) {
                gameOver();
                killAndErase();
            }
        }
    }

    public class ThrowItem extends Image {

        public float time = 0.05f;

        public int i = 0;

        @Override
        public void update() {
            if (!gameOver) {
                if ((time -= Game.elapsed) < 0) {

                    if (Random.Float() < (0.2 + 0.1 * star)) {
                        if (Random.Float() < 0.3f) {
                            Bomb bomb = new Bomb();
                            bomb.x = Random.Int(0,(int)(100 - bomb.width()));
                            bomb.y = 0;
                            add(bomb);
                            i++;
                        } else {
                            Apple apple = new Apple();
                            apple.x = Random.Int(0,(int)(100 - apple.width()));
                            apple.y = 0;
                            add(apple);
                        }
                    }

                    time = 0.3f;
                }
            }
        }
    }

    public void gameOver() {
        gameOver = true;
        GLog.newLine();
        GLog.n("GAMEOVER");
    }

    public class PointerController extends ScrollArea {

        private float dragThreshold;

        public PointerController() {
            super(0,0,0,0 );
            dragThreshold = PixelScene.defaultZoom * 8;
        }

        @Override
        protected void onScroll(ScrollEvent event) {
            PointF newPt = new PointF(lastPos);

            scroll(newPt);
            dragging = false;
        }

        @Override
        protected void onPointerUp( PointerEvent event ) {
            if (dragging) {
                dragging = false;
                hero.idle();
            }
        }

        private boolean dragging = false;
        private PointF lastPos = new PointF();

        @Override
        protected void onDrag( PointerEvent event ) {
            if (dragging) {

                scroll(event.current);

            } else if (PointF.distance( event.current, event.start ) > dragThreshold) {

                dragging = true;
                lastPos.set( event.current );

            }
        }

        private void scroll( PointF current ){

            if (gameOver) {
                return;
            }

            hero.run();

            if (PointF.diff(lastPos, current).x > 0) {
                hero.flipHorizontal = true;
            }

            if (PointF.diff(lastPos, current).x < 0) {
                hero.flipHorizontal = false;
            }

            hero.x -= PointF.diff(lastPos, current).x / PixelScene.defaultZoom / Scale;

            hero.x = Math.max(hero.x,0);
            hero.x = Math.min(hero.x,100 - hero.width());

            lastPos.set( current );
        }
    }

    @Override
    public void onBackPressed() {
        if (gameOver) {
            super.onBackPressed();
        } else {

        }
    }
}
