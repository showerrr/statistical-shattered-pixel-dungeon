package com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.monsters;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.MvHHeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.WndMvH;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.PointF;

public class MvHYogBomb extends MvHYogBall {

    public int[] NEIGHBOURS8 = new int[]{-20,0,20};

    public boolean bomb = false;

    {
        ra = 0.8f;
    }

    public void roll() {

        for (MvHHeroSprite heroSprite : WndMvH.heroSprites) {
            if (Math.min(y + height() - 2,heroSprite.y + heroSprite.height() / 2 - 2) > Math.max(y + 2,heroSprite.y + 2)
                    && Math.min(x + width() - 2,heroSprite.x + heroSprite.width() - 2) > Math.max(x + 2,heroSprite.x + 2)) {
                bomb = true;
            }
        }

        if (bomb) {
            Emitter emitter = new Emitter();
            WndMvH.wndMvH.add(emitter);

            emitter.pos(this.center());
            emitter.burst(BlastParticle.FACTORY,30);

            for (int i : NEIGHBOURS8) {
                for (int j : NEIGHBOURS8) {
                    if (!(i == 0 && j == 0)) {
                        Emitter emitter1 = new Emitter();
                        WndMvH.wndMvH.add(emitter1);

                        emitter1.pos(center().clone().offset(i,j));
                        emitter1.burst(SmokeParticle.FACTORY,4);
                    }
                }
            }

            for (MvHHeroSprite heroSprite : WndMvH.heroSprites) {
                float distance = PointF.distance(this.center(),heroSprite.center());
                if (distance <= 30) {
                    heroSprite.HP -= 1800;
                }
            }

            Sample.INSTANCE.play(Assets.Sounds.BLAST);
            gameDie();
        }


        if (x > 250 && !WndMvH.wndMvH.pause) {
            tweener.killAndErase();
            killAndErase();
        }

    }
}
