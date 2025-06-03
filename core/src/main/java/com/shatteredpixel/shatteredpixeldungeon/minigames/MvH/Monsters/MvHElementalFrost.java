package com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.monsters;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.WndMvH;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class MvHElementalFrost extends MvHElementalSprite.Frost {

    public MvHElementalFrost() {
        delay = 1.3f + Random.Float(0.2f);
        DELAY = 1.3f;
        HP = HT = 300;
        cost = 175;
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
                missile.reset(MagicMissile.FROST,this.center(),enemy.center(),new Callback() {
                    @Override
                    public void call() {
                        if (enemy != null) {
                            Sample.INSTANCE.play(Assets.Sounds.HIT);
                            enemy.flashSlightly();
                            enemy.HP -= 20;

                            if (enemy.gameBurning == 0) {
                                enemy.gameFrost = 10;
                            } else {
                                enemy.gameBurning = 0;
                            }
                            enemy.resetColor();
                        }
                    }
                });
            }

            delay = DELAY + Random.Float(0.2f);
        }
    }
}
