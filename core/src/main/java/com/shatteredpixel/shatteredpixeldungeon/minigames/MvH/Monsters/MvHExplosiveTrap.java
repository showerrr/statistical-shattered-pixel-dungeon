package com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.monsters;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.MvHHeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.MvHMobSprite;
import com.shatteredpixel.shatteredpixeldungeon.minigames.mvh.WndMvH;
import com.watabou.noosa.Game;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;

public class MvHExplosiveTrap extends MvHMobSprite {

    public Animation idle2;
    public Animation idle3;

    public float prepare = 15f;
    public float flash = 1f;

    public boolean hasFlash = false;
    public boolean exploding = false;

    public MvHExplosiveTrap() {
        super();

        texture(Assets.Environment.TERRAIN_FEATURES);

        TextureFilm frames = new TextureFilm(texture,16,16);

        idle = new Animation(1,false);
        idle.frames(frames,72);

        idle2 = new Animation(5,true);
        idle2.frames(frames,72,65);

        idle3 = new Animation(1,false);
        idle3.frames(frames,65);

        play(idle3);

        HP = HT = 300;
        cost = 25;
        coolDown = 30;
        firstCoolDown = 20;
    }

    @Override
    public void update() {
        super.update();

        if (WndMvH.wndMvH.pause) {
            return;
        }

        if (prepare > 0) {
            play(idle);
            prepare -= Game.elapsed;
        } else {
            flash -= Game.elapsed;
        }

        if (!hasFlash && prepare < 0 && flash > 0) {
            play(idle2);
            hasFlash = true;
        }

        if (hasFlash && flash < 0) {
            play(idle3);

            for (MvHHeroSprite hero : WndMvH.heroSprites) {
                if (hero.x > x && hero.x < x + 20 && hero.y + hero.height() / 2 < y + height() && hero.y + hero.height() / 2 > y) {
                    exploding = true;
                    break;
                }
            }

            if (exploding) {
                Emitter emitter = new Emitter();
                WndMvH.wndMvH.add(emitter);

                emitter.pos(this.center());
                emitter.burst(BlastParticle.FACTORY,30);

                for (MvHHeroSprite hero : WndMvH.heroSprites) {
                    if (hero.x > x && hero.x < x + 25 && hero.y + hero.height() / 2 < y + height() && hero.y + hero.height() / 2 > y) {
                        hero.HP -= 1800;
                    }
                }

                Sample.INSTANCE.play(Assets.Sounds.BLAST);
                gameDie();
            }
        }
    }
}
