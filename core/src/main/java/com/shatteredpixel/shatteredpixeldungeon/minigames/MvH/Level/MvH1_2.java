package com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.level;

import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.heroes.MvHWarrior1;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.heroes.MvHWarrior2;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.monsters.MvHBomb;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.monsters.MvHDewcatcher;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.monsters.MvHElementalNewBornFire;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.MvHHeroSpawner;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.MvHHeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.WndMvH;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;

public class MvH1_2 extends WndMvH {

    {
        cards = new ArrayList<>(Arrays.asList(new Card(new MvHElementalNewBornFire()),new Card(new MvHDewcatcher())));
        addCards();
    }

    @Override
    public void addGrasses() {
        for (int i = 1;i < 4;i++) {
            for (int j = 0;j < 10;j++) {
                Grass grass = new Grass();
                grass.setRect(j * 20 + 15,i * 20 + 30,20,20);
                grasses.add(grass);
                add(grass);
            }
        }
    }

    @Override
    public void addHeroSpawner() {
        mvHHeroSpawner = new MvHHeroSpawner();
        mvHHeroSpawner.appearHero = new ArrayList<>(Arrays.asList(
                new MvHWarrior1(),
                new MvHWarrior2()
        ));

        mvHHeroSpawner.allWave = new ArrayList<>(Arrays.asList(
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.TIME,0,15f),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.TIME,0,1f,0),//before first zombie
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.ALL,1),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.ALL,1),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.ALL,1),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.ALL,2),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.ALL,2),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.ALL,3),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.ALL,3),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.ALL,4),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.DIE,4),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.TIME,0,2f,1),//before last wave
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.TIME,0,2f,2),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.DIE,6)
        ));

        mvHHeroSpawner.mustAppear.add(new MvHWarrior2());

        mvHHeroSpawner.x = 0;
        mvHHeroSpawner.y = 0;
        addToBack(mvHHeroSpawner);
    }

    @Override
    public void addHero(MvHHeroSprite newHeroSprite) {
        newHeroSprite.setMapToPos(10 + 1.5f * Random.Float(),Random.Int(3) + 1);
        WndMvH.heroSprites.add(newHeroSprite);
        WndMvH.wndMvH.addToFront(newHeroSprite);
    }

    @Override
    public void createToLoot(float x,float y) {
        lootCard loot = WndMvH.wndMvH.new lootCard(new MvHBomb());
        loot.setRect(x,y,20,20);
        WndMvH.wndMvH.addToFront(loot);
    }
}
