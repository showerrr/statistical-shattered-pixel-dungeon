package com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.heroes;

import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.MvHHeroSprite;

public class MvHWarrior4 extends MvHHeroSprite {

    public boolean haveUpdate = false;

    public MvHWarrior4() {
        super(true,4,0);
        HT = HP = 270;
        DELAY = delay = 0.5f;
        gameSpeed = 2f;
        level = 5;
        flipHorizontal = true;
        runSlowly();
    }

    @Override
    public void update() {
        if (!haveUpdate && HP < 190 && HP > 0) {
            tier = 0;
            updateArmor();
            haveUpdate = true;
        }

        super.update();
    }
}