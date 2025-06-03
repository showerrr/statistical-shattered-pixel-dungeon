package com.shatteredpixel.shatteredpixeldungeon.minigames;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.input.PointerEvent;
import com.watabou.input.ScrollEvent;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ScrollArea;
import com.watabou.utils.PointF;

public class WndDebugging extends Window {

    public Ball ball;

    public PointerController pointerController;

    public int i = 0;
    public boolean in = false;

    public WndDebugging() {

        ball = new Ball();
        ball.x = 60;
        ball.y = 105;
        add(ball);

        Board board = new Board();
        board.x = 50;
        board.y = 115;
        board.size(20,5);
        add(board);

        for (int i = 0;i < 5;i++) {
            for (int j = 0;j < 5;j++) {
                Brick brick = new Brick();
                brick.x = i * 25;
                brick.y = j * 15;
                brick.size(15,8);
                add(brick);
            }
        }

        pointerController = new PointerController();
        pointerController.setComponent(board);
        pointerController.width = 120;
        pointerController.height = 150;
        pointerController.x = 0;
        pointerController.y = 0;
        add(pointerController);

        ColorBlock line = new ColorBlock(120,1,0xFF000000);
        line.x = 0;
        line.y = 120;
        add(line);

        resize(120,150);
    }

    public class Ball extends Image {

        public Ball() {
            super();

            Image image = new Image(Assets.Sprites.ITEMS);
            image.frame(image.texture.uvRect(48,16,58,26));
            image.scale.set(0.5f);
            copy(image);

            speed.set(25,-25);
        }

        public void update() {
            super.update();
            if (y > 115) {
                GLog.n("GAMEOVER");
            }
            if ((x <= 0 && speed.x < 0) || (x >= 115 && speed.x > 0)) {
                speed.x *= -1;
            }
            if ((y <= 0 && speed.y < 0) || (y >= 115 && speed.y > 0)) {
                speed.y *= -1;
            }
        }

    }

    public class Board extends NinePatch {

        public boolean crash = false;

        public Board() {
            super(Assets.Interfaces.CHROME,38,6,6,6,2);
        }

        @Override
        public void update() {
            super.update();

            if (!crash) {
                if (intersects(this,ball)) {
                    float[] a = intersection(this,ball);
                    if ((a[2] == y || a[3] == y + height())) {
                        ball.speed.y *= -1;
                    }

                    if ((a[0] == x || a[1] == x + height())) {
                        ball.speed.x *= -1;
                    }

                }
            }
            crash = intersects(this,ball);

            if (x <= 0) {
                x = 0;
                speed.x = 0;
            }

            if (x >= 100) {
                x = 100;
                speed.x = 0;
            }

            if (!pointerController.dragging && speed.x != 0) {
                if (speed.x > 0) {
                    speed.x -= 2;
                }
                if (speed.x < 0) {
                    speed.x += 2;
                }
                if (speed.x > -2 && speed.x < 2) {
                    speed.x = 0;
                }
            }
        }
    }

    public class Brick extends Board {

        @Override
        public void update() {
            if (!visible) {
                return;
            }

            updateMotion();

            if (!crash) {
                if (intersects(this,ball)) {
                    float[] a = intersection(this,ball);
                    if ((a[2] == y || a[3] == y + height())) {
                        ball.speed.y *= -1;
                    }

                    if ((a[0] == x || a[1] == x + height())) {
                        ball.speed.x *= -1;
                    }

                    this.visible = false;
                }
            }

            crash = intersects(this,ball);
        }
    }

    public class PointerController extends ScrollArea {

        private final float dragThreshold;
        public Board board;

        public PointerController() {
            super(0,0,0,0 );
            dragThreshold = PixelScene.defaultZoom;
        }

        public void setComponent(Board board) {
            this.board = board;
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
            }
        }

        private boolean dragging = false;
        private final PointF lastPos = new PointF();

        @Override
        protected void onDrag( PointerEvent event ) {

            if (dragging) {

                scroll(event.current);

            } else if (PointF.distance( event.current, event.start ) > dragThreshold) {

                dragging = true;
                lastPos.set(event.current);

            }
        }

        private void scroll( PointF current ){

//            board.speed.x -= PointF.diff(lastPos,current).x / PixelScene.defaultZoom * 1.5;
            if (lastPos.x > current.x) {
                board.speed.x = -30;
            } else if (lastPos.x < current.x) {
                board.speed.x = 30;
            }


            lastPos.set(current);
        }
    }

    public boolean intersects(NinePatch ninePatch,Image r) {
        float tw = ninePatch.width();
        float th = ninePatch.height();
        float rw = r.width();
        float rh = r.height();
        if (rw > 0 && rh > 0 && tw > 0 && th > 0) {
            float tx = ninePatch.x;
            float ty = ninePatch.y;
            float rx = r.x;
            float ry = r.y;
            rw += rx;
            rh += ry;
            tw += tx;
            th += ty;
            return (rw < rx || rw > tx) && (rh < ry || rh > ty) && (tw < tx || tw > rx) && (th < ty || th > ry);
        } else {
            return false;
        }
    }

    public float[] intersection(NinePatch ninePatch,Image r) {
        float tx1 = ninePatch.x;
        float ty1 = ninePatch.y;
        float rx1 = r.x;
        float ry1 = r.y;
        float tx2 = tx1 + ninePatch.width();
        float ty2 = ty1 + ninePatch.height();
        float rx2 = rx1 + r.width();
        float ry2 = ry1 + r.height();

        if (tx1 < rx1) {
            tx1 = rx1;
        }

        if (ty1 < ry1) {
            ty1 = ry1;
        }

        if (tx2 > rx2) {
            tx2 = rx2;
        }

        if (ty2 > ry2) {
            ty2 = ry2;
        }

        float[] a = new float[4];
        a[0] = tx1;
        a[1] = tx2;
        a[2] = ty1;
        a[3] = ty2;

        return a;
    }
}
