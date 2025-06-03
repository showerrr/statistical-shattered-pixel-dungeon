package com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.monsters;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.WndMvH;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class MvHElementalFire extends MvHElementalSprite.Fire {

    public MvHElementalFire() {
        delay = 1.3f + Random.Float(0.2f);
        DELAY = 1.3f;
        HP = HT = 300;
        cost = 150;
        coolDown = 7.5f;
        firstCoolDown = 0;
    }

    @Override
    public void update() {
        super.update();

        if (WndMvH.wndMvH.pause) {
            return;
        }

        if ((delay -= Game.elapsed) < 0) {

            grass.chooseEnemy();

            if (enemy != null) {
                play(zap);

                MagicMissile missile = ((MagicMissile)parent.recycle(MagicMissile.class));
                WndMvH.wndMvH.addToFront(missile);
                missile.reset(MagicMissile.FIRE,this.center(),enemy.center(),new Callback() {
                    @Override
                    public void call() {
                        if (enemy != null) {
                            Sample.INSTANCE.play(Assets.Sounds.HIT);
                            enemy.HP -= 15;

                            if (enemy.gameFrost == 0) {
                                enemy.gameBurning = 10;
                            } else {
                                enemy.gameFrost = 0;
                            }
                            enemy.resetColor();
                        }
                    }
                });

//                Sample.INSTANCE.play(Assets.Sounds.ZAP);
            }

            delay = DELAY + Random.Float(0.2f);
        }
    }
}
