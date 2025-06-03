package com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.monsters;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.MvHHeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.MvHMobSprite;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.WndMvH;
import com.watabou.noosa.Game;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.PointF;

public class MvHBomb extends MvHMobSprite {

    public float fuse = 2f;

    public int[] NEIGHBOURS8 = new int[]{-20,0,20};

    public MvHBomb() {
        super();

        texture(Assets.Sprites.BOMB);

        TextureFilm frames = new TextureFilm(texture,10,13);

        idle = new Animation(5,true);
        idle.frames(frames,0,1);

        play(idle);

        HP = HT = 3000;
        cost = 150;
        coolDown = 50;
        firstCoolDown = 35;
    }

    @Override
    public void update() {
        super.update();

        if (WndMvH.wndMvH.pause) {
            return;
        }

        if ((fuse -= Game.elapsed) < 0) {

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
//            emitter.start(SmokeParticle.FACTORY,0,4);
            Sample.INSTANCE.play(Assets.Sounds.BLAST);
            gameDie();
//            gameDie();
        }
    }
}
