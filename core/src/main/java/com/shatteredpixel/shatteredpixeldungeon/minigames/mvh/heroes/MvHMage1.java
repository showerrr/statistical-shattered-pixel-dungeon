package com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.heroes;

import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.MvHHeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.WndMvH;
import com.watabou.noosa.Game;

public class MvHMage1 extends MvHHeroSprite {

    public float coolDown = 10f;

    public MvHMage1() {
        super(true,1,1);
        HT = HP = 280;
        DELAY = delay = 0.5f;
        gameSpeed = 2f;
        level = 2;
        flipHorizontal = true;
        runSlowly();
    }

    @Override
    public void update() {
        if (!WndMvH.wndMvH.pause) {
            if ((delay -= Game.elapsed) < 0) {
                if (coolDown > 0) {
                    coolDown --;
                    if (coolDown < 0) {
                        coolDown = 0;
                    }
                }
            }
        }

        super.update();
    }

    @Override
    public void doAttack() {
        if (coolDown == 0 && !haveAmulet) {
            blink(x - 21,y);
            coolDown = 40f;
        } else {
            super.doAttack();
        }
    }
}
