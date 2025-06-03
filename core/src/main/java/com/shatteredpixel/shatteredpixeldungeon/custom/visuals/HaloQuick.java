package com.shatteredpixel.shatteredpixeldungeon.custom.visuals;

import com.badlogic.gdx.graphics.Pixmap;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.Game;
import com.watabou.noosa.Halo;
import com.watabou.noosa.Image;

public class HaloQuick extends Image {
    private static final Object CACHE_KEY = Halo.class;

    protected static final int RADIUS	= 128;

    protected float radius = RADIUS;
    protected float brightness = 1;

    public HaloQuick() {
        super();

        if (!TextureCache.contains( CACHE_KEY )) {
            Pixmap pixmap = TextureCache.create( CACHE_KEY, 2*RADIUS+1, 2*RADIUS+1 ).bitmap;

            pixmap.setColor( 0x00000000 );
            pixmap.fill();

            pixmap.setColor( 0xFFFFFF08 );
            for (int i = 0; i < RADIUS; i+=2) {
                pixmap.fillCircle(RADIUS, RADIUS, (RADIUS - i));
            }
        }

        texture( CACHE_KEY );
    }

    public HaloQuick(float radius, int color, float brightness, float fadeTime ) {

        this();

        hardlight( color );
        alpha( this.brightness = brightness );
        radius( radius );

        life = fadeTime;
        time = fadeTime;
    }

    public HaloQuick point(float x, float y ) {
        this.x = x - (width()/2f);
        this.y = y - (height()/2f);
        return this;
    }

    public void radius( float value ) {
        scale.set(  (this.radius = value) / RADIUS );
    }

    private float life;
    private float time;
    @Override
    public void update(){
        if((time -= Game.elapsed) < 0f){
            killAndErase();
        }else{
            am = time * (-1/life);
            aa = time * (+1/life);
        }
        super.update();
    }
    @Override
    public void draw() {
        Blending.setLightMode();
        super.draw();
        Blending.setNormalMode();
    }

}
