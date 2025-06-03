package com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.level;

import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.heroes.MvHWarrior1;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.heroes.MvHWarrior2;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.heroes.MvHWarrior3;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.monsters.MvHExplosiveTrap;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.monsters.MvHYogBall;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.monsters.MvHYogBomb;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.MvHHeroSpawner;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.MvHMobSprite;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.WndMvH;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;

public class MvH1_4 extends WndMvH {

    public ArrayList<Card> yogCard = new ArrayList<>(Arrays.asList(null,null,null,null,null,null,null,null));

    public MvH1_4() {
        super();

        night = true;

        ColorBlock colorBlock = new ColorBlock(1,110,0xFFBB0000);
        colorBlock.x = 75;
        colorBlock.y = 25;
        add(colorBlock);

        addYogSpawner();
//        cards = new ArrayList<>(
//                Arrays.asList(
//                        new Card(new MvHYogBall()),
//                        new Card(new MvHYogBomb())));
//        addCards();
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
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.TIME,0,3f),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.TIME,0,1f,0),//before first zombie
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.ALL,3),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.ALL,3),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.ALL,3),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.ALL,6),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.ALL,6),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.ALL,6),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.ALL,9),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.ALL,9),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.DIE,9),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.TIME,0,2f,1),//before last wave
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.TIME,0,2f,2),
                mvHHeroSpawner.new Wave(MvHHeroSpawner.Condition.DIE,16)
        ));

        mvHHeroSpawner.mustAppear.add(new MvHWarrior3());

        mvHHeroSpawner.x = 0;
        mvHHeroSpawner.y = 0;
        addToBack(mvHHeroSpawner);
    }

    public class YogGrass extends Grass {

        @Override
        public void onClick() {
            if (WndMvH.wndMvH.pause) {
                return;
            }

            if (x > 75) {
                return;
            }

            if (mobSprite != null) {
                if (pickaxe.press) {
                    mobSprite.gameDie();
                    pickaxe.press = false;
                }

            } else if (select != null && selectCard != null && water >= select.cost) {
                MvHMobSprite newSprite = Reflection.newInstance(select.getClass());

                newSprite.setGrass(this);

                ((MvHYogBall)newSprite).place();

                water -= select.cost;
                txt.text(Integer.toString(water));
                blueBlock.changeHeight();

                select = null;
                for (int i = 0;i < yogCard.size();i++) {
                    if (yogCard.get(i) == selectCard) {
                        yogCard.set(i,null);
                    }
                }
                selectCard.destroy();
                selectCard = null;
            }
        }
    }

    @Override
    public void addGrasses() {
        for (int i = 0;i < 5;i++) {
            for (int j = 0;j < 10;j++) {
                YogGrass grass = new YogGrass();
                grass.setRect(j * 20 + 15,i * 20 + 30,20,20);
                grasses.add(grass);
                add(grass);
            }
        }
    }

    public void addYogSpawner() {
        YogSpawner yogSpawner = new YogSpawner();
        yogSpawner.x = 0;
        yogSpawner.y = 0;
        add(yogSpawner);
    }

    public class YogSpawner extends Image {

        public float time = 3f;

        public int number = 0;

        @Override
        public void update() {
            if (WndMvH.wndMvH.pause) {
                return;
            }

            if ((time -= Game.elapsed) < 0) {

                if (yogCard.size() == 8 && !yogCard.contains(null)) {
                    return;
                }

                Card card;
                number++;

                if (Random.Float() <= Math.max(1f - number * 0.04f,0.6f)) {
                    card = new Card(new MvHYogBall());
                } else {
                    card = new Card(new MvHYogBomb());
                }

                for (int i = 0;i < yogCard.size();i++) {
                    if (yogCard.get(i) == null) {
                        yogCard.set(i,card);
                        card.setRect(20 * i + 20,0,20,20);
                        add(card);
                        break;
                    }
                }

                time = 9f;
            }
        }
    }

    @Override
    public void createToLoot(float x,float y) {
        lootCard loot = WndMvH.wndMvH.new lootCard(new MvHExplosiveTrap());
        loot.setRect(x,y,20,20);
        WndMvH.wndMvH.addToFront(loot);
    }
}
