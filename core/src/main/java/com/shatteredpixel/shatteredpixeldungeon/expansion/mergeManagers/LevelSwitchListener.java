package com.shatteredpixel.shatteredpixeldungeon.expansion.mergeManagers;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.custom.buffs.GameTracker;

public class LevelSwitchListener {
    public static void onLevelSwitch(){
        GameTracker gmt = Dungeon.hero.buff(GameTracker.class);
        if(gmt != null){
            gmt.onNewLevel();
        }
    }
}
