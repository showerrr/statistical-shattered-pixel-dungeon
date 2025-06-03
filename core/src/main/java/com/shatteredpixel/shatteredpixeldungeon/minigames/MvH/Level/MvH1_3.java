package com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.level;

import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.heroes.MvHWarrior1;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.heroes.MvHWarrior2;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.heroes.MvHWarrior3;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.monsters.MvHBomb;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.monsters.MvHDewcatcher;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.monsters.MvHElementalNewBornFire;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.monsters.MvHYog;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.MvHHeroSpawner;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.WndMvH;

import java.util.ArrayList;
import java.util.Arrays;

public class MvH1_3 extends WndMvH {

    {
        cards = new ArrayList<>(
                Arrays.asList(
                        new Card(new MvHElementalNewBornFire()),
                        new Card(new MvHDewcatcher()),
                        new Card(new MvHBomb())));
        addCards();
    }

    @Override
    public void addHeroSpawner() {
        mvHHeroSpawner = new MvHHeroSpawner();
        mvHHeroSpawner.appearHero = new ArrayList<>(Arrays.asList(
                new MvHWarrior1(),
                new MvHWarrior2(),
                new MvHWarrior3()
        ));

        mvHHeroSpawner.allWave = new ArrayList<>(Arrays.asList(
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.TIME,0,15f),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.TIME,0,1f,0),//before first zombie
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.ALL,1),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.ALL,1),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.ALL,2),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.ALL,2),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.ALL,3),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.ALL,3),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.ALL,4),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.ALL,4),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.DIE,5),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.TIME,0,2f,1),//before last wave
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.TIME,0,2f,2),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.DIE,8)
        ));

        mvHHeroSpawner.mustAppear.add(new MvHWarrior3());

        mvHHeroSpawner.x = 0;
        mvHHeroSpawner.y = 0;
        addToBack(mvHHeroSpawner);
    }

    @Override
    public void createToLoot(float x,float y) {
        lootCard loot = WndMvH.wndMvH.new lootCard(new MvHYog());
        loot.setRect(x,y,20,20);
        WndMvH.wndMvH.addToFront(loot);
    }
}
