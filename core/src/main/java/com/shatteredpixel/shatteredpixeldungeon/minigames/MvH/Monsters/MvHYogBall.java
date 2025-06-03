package com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.monsters;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.MvHHeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.MvHMobSprite;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.WndMvH;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.PosTweener;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class MvHYogBall extends MvHMobSprite {

    public PosTweener tweener;

    private ArrayList<MvHHeroSprite> hit = new ArrayList<>();

    public MvHYogBall() {
        super();

        texture(Assets.Sprites.YOG);

        TextureFilm frames = new TextureFilm( texture, 20, 19 );

        idle = new Animation(5,true);
        idle.frames(frames,0,1,2,1);

        run = new Animation(12,true);
        run.frames(frames,0);

        attack = new Animation(12,false);
        attack.frames(frames, 0 );

        die = new Animation(10,false);
        die.frames(frames,0,7,8,9);

        play(idle);

        delay = DELAY = 1f;
        HP = HT = 999;
        cost = 0;
        coolDown = 0;
    }

    public void place() {

        originToCenter();
        PointF pointF = new PointF(grass.left() + grass.width() / 2 - width() / 2,grass.top() + grass.height() / 2 - height() / 2);
        point(pointF);

        PointF d = new PointF(x + 1000,y);
        speed.set(d).normalize().scale(50f);
        angularSpeed = 360;

        tweener = new PosTweener(this,d,20f) {
            @Override
            public void update() {
                if (WndMvH.wndMvH.pause) {
                    return;
                }
                super.update();
            }
        };
        WndMvH.wndMvH.add(tweener);

        WndMvH.wndMvH.add(this);
    }

    public void roll() {
        int i = 0;

        for (MvHHeroSprite heroSprite : WndMvH.heroSprites) {
            if (Math.min(y + height() - 2,heroSprite.y + heroSprite.height() / 2 - 2) > Math.max(y + 2,heroSprite.y + 2)
                    && Math.min(x + width() - 2,heroSprite.x + heroSprite.width() - 2) > Math.max(x + 2,heroSprite.x + 2)) {

                if (!hit.contains(heroSprite)) {
                    heroSprite.HP -= 500;
                    Sample.INSTANCE.play(Assets.Sounds.HIT);

                    if (Random.Int(2) == 0) {
                        i = 1;
                    } else {
                        i = -1;
                    }

                    hit.add(heroSprite);
                }
            }
        }

        if (x > 250 && !WndMvH.wndMvH.pause) {
            hit = null;
            tweener.killAndErase();
            killAndErase();
        }

        if (y < 30) {
            i = 1;
        }

        if (y > 111) {
            i = -1;
        }

        if (i != 0) {
            WndMvH.wndMvH.remove(tweener);
            tweener = new PosTweener(this,new PointF(x + 1000,y + 1000 * i),20f) {
                @Override
                public void update() {
                    if (WndMvH.wndMvH.pause) {
                        return;
                    }
                    super.update();
                }
            };
            WndMvH.wndMvH.add(tweener);
        }

    }

    @Override
    public void update() {
        super.update();

        if (WndMvH.wndMvH.pause) {
            return;
        }

        roll();
    }
}
