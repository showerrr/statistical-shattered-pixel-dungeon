package com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.monsters;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.MvHMobSprite;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.WndMvH;
import com.watabou.noosa.TextureFilm;

public class MvHYog extends MvHMobSprite {

    public int state = 1;

    public Animation idle2;
    public Animation idle3;

    public MvHYog() {
        super();

        texture(Assets.Sprites.YOG);

        TextureFilm frames = new TextureFilm( texture, 20, 19 );

        idle = new Animation(1,true);
        idle.frames(frames,0,1,2,2,2,2,1,0,0,0);

        idle2 = new Animation(1,false);
        idle2.frames(frames,7);

        idle3 = new Animation(1,false);
        idle3.frames(frames,8);

        play(idle);

        delay = DELAY = 1f;
        HP = HT = 4000;
        cost = 50;
        coolDown = 30;
        firstCoolDown = 20;
    }

    @Override
    public void update() {
        super.update();

        if (WndMvH.wndMvH.pause) {
            return;
        }

        if (state != 2 && HP < HT * 2 / 3) {
            play(idle2);
            state = 2;
        }

        if (state != 3 && HP < HT / 3) {
            play(idle3);
            state = 3;
        }
    }
}
