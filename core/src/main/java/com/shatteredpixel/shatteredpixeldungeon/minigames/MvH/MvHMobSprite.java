package com.shatteredpixel.shatteredpixeldungeon.minigames.mvh;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.noosa.tweeners.ScaleTweener;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class MvHMobSprite extends MvHCharSprite {

    private static final float FADE_TIME	= 3f;
    private static final float FALL_TIME	= 1f;

    public MvHHeroSprite enemy = null;
    public WndMvH.Grass grass;
    public int cost = 0;
    public float coolDown = 7.5f;
    public float firstCoolDown = 0f;

    public void setGrass(WndMvH.Grass grass) {
        this.grass = grass;
    }

    @Override
    public void update() {
        if (!gameMvH) {
            sleeping = ch != null && ch.isAlive() && ((Mob)ch).state == ((Mob)ch).SLEEPING;
        }

        if (gameMvH) {
            if (grass == null) {
                return;
            }
        }
        super.update();
    }

    @Override
    public void onComplete( Animation anim ) {

        super.onComplete( anim );

        if (anim == die && parent != null) {
            parent.add( new AlphaTweener( this, 0, FADE_TIME ) {
                @Override
                protected void onComplete() {
                    this.killAndErase();
                }
            } );
        }
    }

    public void fall() {

        origin.set( width / 2, height - DungeonTilemap.SIZE / 2 );
        angularSpeed = Random.Int( 2 ) == 0 ? -720 : 720;
        am = 1;

        hideEmo();

        if (health != null){
            health.killAndErase();
        }

        parent.add( new ScaleTweener( this, new PointF( 0, 0 ), FALL_TIME ) {
            @Override
            protected void onComplete() {
                this.killAndErase();
                parent.erase( this );
            }
            @Override
            protected void updateValues( float progress ) {
                super.updateValues( progress );
                y += 12 * Game.elapsed;
                am = 1 - progress;
            }
        } );
    }

    public void gameDie() {
        WndMvH.Grass grass = this.grass;
        if (grass != null) {
            grass.mobSprite = null;
        }
        killAndErase();
    }
}

