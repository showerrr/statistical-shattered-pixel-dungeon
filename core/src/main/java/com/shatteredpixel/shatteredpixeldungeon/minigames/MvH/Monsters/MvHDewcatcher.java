package com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.monsters;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.MvHMobSprite;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.WndMvH;
import com.watabou.noosa.Game;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Random;

public class MvHDewcatcher extends MvHMobSprite {

    public boolean highlight = false;

    public MvHDewcatcher() {
        super();

        texture(Assets.Environment.TERRAIN_FEATURES);

        TextureFilm frames = new TextureFilm(texture,16,16);

        idle = new Animation(1,false);
        idle.frames(frames,125);

        play(idle);

        delay = Random.Float(4) + 4;
        DELAY = 23f;
        HP = HT = 300;
        cost = 50;
        coolDown = 7.5f;
        firstCoolDown = 0f;
    }

    public void update() {
        super.update();

        if (WndMvH.wndMvH.pause) {
            return;
        }

        if (!highlight && delay < 2) {
            ga = 0.5f;
            flashTime = 3f;
            highlight = true;
        }

        if (highlight && delay < 22) {
            highlight = false;
        }

        if ((delay -= Game.elapsed) < 0) {


            WndMvH.Dew dew = WndMvH.wndMvH.new Dew();

            dew.setPos(x + width() / 2,y + width() / 3);
            WndMvH.wndMvH.addToFront(dew);

            delay = DELAY + Random.Float(2);
        }
    }
}
