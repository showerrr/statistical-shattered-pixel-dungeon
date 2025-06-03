package com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.heroes;

import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.MvHHeroSprite;

public class MvHWarrior1 extends MvHHeroSprite {

    public boolean haveUpdate = false;

    public MvHWarrior1() {
        super(true,1,0);
        HT = HP = 180;
        DELAY = delay = 0.5f;
        gameSpeed = 2f;
        level = 1;
        flipHorizontal = true;
        runSlowly();
    }

    @Override
    public void update() {
        if (!haveUpdate && HP < 180 && HP > 0) {
            tier = 1;
            updateArmor();
            haveUpdate = true;
        }

        super.update();
    }
}
