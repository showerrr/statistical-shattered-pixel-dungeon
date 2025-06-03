package com.shatteredpixel.shatteredpixeldungeon.minigames.mvh;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class MvHHeroSpawner extends Image {

    public ArrayList<MvHHeroSprite> appearHero = new ArrayList<>();
    public ArrayList<MvHHeroSprite> mustAppear = new ArrayList<>();

    public ArrayList<Wave> allWave = new ArrayList<>();

    public Wave thisWave;
    public ArrayList<MvHHeroSprite> thisWaveHero;

    public int wave;

    public float duration;

    public boolean lastWave = false;

    public boolean canNextWave = true;

    public float left = 6f;

    public float length;
    public float maxLength;

    public ColorBlock greyBlock;
    public ColorBlock redBlock;

    public ArrayList<Image> flags = new ArrayList<>();

    public enum Condition {
        DIE,
        TIME,
        HALF,
        ALL
    }

    public MvHHeroSpawner() {
        super();

        visible = false;

        length = 0;
        maxLength = 0;

        greyBlock = new ColorBlock(34,5,0xFF333333);
        greyBlock.x = 178;
        greyBlock.y = 138;

        redBlock = new ColorBlock(30,1,0xFFEE0000);
        redBlock.scale.x = 0;
        redBlock.x = 210;
        redBlock.y = 140;
    }

    public class Wave {

        public int level;
        public ArrayList<MvHHeroSprite> arrayList;
        public float duration;
        public Condition condition;
        public int alert = -1;

        public Wave(Condition condition,int level) {
            this.level = level;

            if (level != 0) {
                arrayList = new ArrayList<>();
                do {
                    MvHHeroSprite heroSprite = Reflection.newInstance(appearHero.get(Random.Int(appearHero.size() - 1)).getClass());
                    if (heroSprite.level <= level) {
                        level -= heroSprite.level;
                        arrayList.add(heroSprite);
                    }
                } while (level > 0);
            }

            this.duration = Random.Float(6f) + 25f;
            this.condition = condition;
        }

        public Wave(Condition condition,int level,float duration) {
            this.level = level;

            if (level != 0) {
                arrayList = new ArrayList<>();
                do {
                    MvHHeroSprite heroSprite = Reflection.newInstance(appearHero.get(Random.Int(appearHero.size() - 1)).getClass());
                    if (heroSprite.level <= level) {
                        level -= heroSprite.level;
                        arrayList.add(heroSprite);
                    }
                } while (level > 0);
            }

            this.duration = duration;
            this.condition = condition;
        }

        public Wave(Condition condition,int level,int alert) {
            this.level = level;

            if (level != 0) {
                arrayList = new ArrayList<>();
                do {
                    MvHHeroSprite heroSprite = Reflection.newInstance(appearHero.get(Random.Int(appearHero.size() - 1)).getClass());
                    if (heroSprite.level <= level) {
                        level -= heroSprite.level;
                        arrayList.add(heroSprite);
                    }
                } while (level > 0);
            }

            this.duration = Random.Float(6f) + 25f;
            this.condition = condition;
            this.alert = alert;
        }

        public Wave(Condition condition,int level,float duration,int alert) {
            this.level = level;

            if (level != 0) {
                arrayList = new ArrayList<>();
                do {
                    MvHHeroSprite heroSprite = Reflection.newInstance(appearHero.get(Random.Int(appearHero.size() - 1)).getClass());
                    if (heroSprite.level <= level) {
                        level -= heroSprite.level;
                        arrayList.add(heroSprite);
                    }
                } while (level > 0);
            }

            this.duration = duration;
            this.condition = condition;
            this.alert = alert;
        }

        public void spawn() {
            thisWaveHero = new ArrayList<>();

            for (MvHHeroSprite heroSprite : arrayList) {
                thisWaveHero.add(heroSprite);
                WndMvH.wndMvH.addHero(heroSprite);
            }
        }
    }



    public void nextWave() {
        wave++;

        if (!lastWave) {
            thisWave = allWave.get(wave);

            if (thisWave.alert != -1) {
                switch (thisWave.alert) {
                    case 0:
                        addFlagAndBlock();
                        Sample.INSTANCE.play(Assets.Sounds.ALERT);
                        break;
                    case 1:
//                        GameScene.bossSlain();
                        Sample.INSTANCE.play(Assets.Sounds.ALERT);
                        break;
                    case 2:
//                        GameScene.bossSlain();
                        Sample.INSTANCE.play(Assets.Sounds.ALERT);
                        break;
                }
            }

            if (thisWave.level != 0) {
                length++;
                redBlock.scale.x = 30 * length / maxLength;
                redBlock.x = 210 - 30 * length / maxLength;
            }

            if (thisWave.arrayList != null) {
                thisWave.spawn();
            }

            if (thisWave.duration > 0) {
                duration = thisWave.duration;
            }

            canNextWave = false;
        }

        if (wave == allWave.size() - 1) {
            lastWave = true;
            if (mustAppear.size() > 0) {
                for (MvHHeroSprite heroSprite : mustAppear) {
                    thisWaveHero.add(heroSprite);
                    WndMvH.wndMvH.addHero(heroSprite);
                }
            }
        }
    }

    public void addFlagAndBlock() {
        WndMvH.wndMvH.add(greyBlock);
        WndMvH.wndMvH.add(redBlock);

        for (Wave wave : allWave) {
            if (wave.level != 0) {
                maxLength++;
            }
        }
        int i = 0;
        for (Wave wave : allWave) {
            if (wave.alert == 1) {
                Image flag = new Image(Assets.Sprites.ITEMS);
                flag.frame(flag.texture.uvRect(48,48,59,62));
                flag.scale.set(0.5f);
                flag.x = 210 - 30f * i / maxLength - flag.width();
                flag.y = 140 - flag.height();
                flags.add(flag);
            }
            if (wave.level != 0) {
                i++;
            }
        }
        for (Image flag : flags) {
            WndMvH.wndMvH.add(flag);
        }
    }

    @Override
    public void update() {
        if (WndMvH.wndMvH.pause) {
            return;
        }

        if (thisWave == null) {
            wave = -1;
            nextWave();
        }

        if (!canNextWave) {
            if ((left -= Game.elapsed) < 0) {
                canNextWave = true;
                left = 6f;
            }
        }

        if (lastWave && WndMvH.heroSprites.size() == 0) {
            GameScene.bossSlain();
            WndMvH.wndMvH.pause = true;
            return;
        }

        if (canNextWave) {
            if ((thisWave.condition == Condition.DIE || thisWave.condition == Condition.ALL)) {
                if (WndMvH.heroSprites.size() == 0) {
                    nextWave();
                }
            }

            if ((thisWave.condition == Condition.HALF || thisWave.condition == Condition.ALL)) {
                int totalHP = 0;
                int totalHT = 0;
                for (MvHHeroSprite heroSprite : thisWaveHero) {
                    totalHT += heroSprite.HT;
                    totalHP += Math.max(heroSprite.HP,0);
                }
                if (totalHP * 2 < totalHT) {
                    nextWave();
                }
            }

            if (thisWave.condition == Condition.TIME || thisWave.condition == Condition.ALL) {
                if ((duration -= Game.elapsed) < 0) {
                    nextWave();
                    canNextWave = true;
                }
            }
        }
    }
}
