package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mimic;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;

public class MimicParticle extends PixelParticle.Shrinking {
    public int color;

    public static final Emitter.Factory FACTORY = new Emitter.Factory() {
        @Override
        public boolean lightMode() {
            return true;
        }
        @Override
        public void emit( Emitter emitter, int index, float x, float y) {
            ((MimicParticle)emitter.recycle( MimicParticle.class )).reset( x, y);
        }
    };

    public MimicParticle() {
        super();

        color( 0x2266AA );
        lifespan = 0.6f;

        acc.set( 0, -80 );
    }

    public void reset( float x, float y ) {
        revive();

        this.x = x;
        this.y = y;

        left = lifespan;

        size = 4;
        speed.set( 0 );
    }

    @Override
    public void update() {
        super.update();
        float p = left / lifespan;
        am = p > 0.8f ? (1 - p) * 5 : 1;
    }
}
