package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.prison;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM100;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.MobHard;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.timing.VirtualActor;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DM100Sprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class DM100H extends MobHard implements Callback {
    private static final float TIME_TO_ZAP	= 1f;
    {
        EXP = 7;
        HT = HP = 21;

        spriteClass = DM100HSprite.class;

        defenseSkill = 8;

        maxLvl = 13;

        loot = Generator.Category.SCROLL;
        lootChance = 0.2f;

        properties.add(Property.ELECTRIC);
        properties.add(Property.INORGANIC);
    }

    private float cd = 4f;

    @Override
    public boolean act() {
        boolean prepare = buff(LightningPrediction.class) != null;
        if(paralysed<=0) {
            if (cd <= 0) {
                if (enemy != null && enemySeen) {
                    if (!prepare) {
                        Buff.affect(this, LightningPrediction.class).set(enemy).countUp(5f);
                    }
                    cd = 8f;
                    spend(TICK);
                    return true;
                }
            }
        }
        if(prepare){
            if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
                fieldOfView = new boolean[Dungeon.level.length()];
            }
            Dungeon.level.updateFieldOfView( this, fieldOfView );
            spend(TICK);
            return true;
        }else if(state!=SLEEPING){
            cd -= TICK;
        }

        return super.act();

    }

    @Override
    public void damage(int damage, Object src){
        if(buff(LightningPrediction.class)!=null){
            damage = 1;
        }
        super.damage(damage, src);
    }

    @Override
    protected boolean canAttack(Char enemy){
        boolean can = new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
        if(!can && buff(LightningPrediction.class)==null){
            cd -= 1f;
        }
        return can;

    }

    @Override
    public void storeInBundle(Bundle b){
        super.storeInBundle(b);
        b.put("skillCD", cd);
    }

    @Override
    public void restoreFromBundle(Bundle b){
        super.restoreFromBundle(b);
        cd = b.getFloat("skillCD");
    }

    @Override
    public void rollToDropLoot(){
        if (Dungeon.hero.lvl <= maxLvl + 2){
            float chance = 0.02f;
            chance *= RingOfWealth.dropChanceMultiplier( Dungeon.hero );
            chance = Math.max(chance, 0.03f);
            if(Random.Float()<chance){
                WandOfLightning wandOfLightning = new WandOfLightning();
                wandOfLightning.level(Random.chances(new float[]{0.6f-chance*2, 0.3f + chance, 0.1f+chance}));
                Dungeon.level.drop(wandOfLightning, pos).sprite.drop();
            }
        }

        super.rollToDropLoot();

    }

    @Override
    public void die(Object cause) {
        super.die(cause);
    }

    public static class SkyLightning{}

    public static class LightningPrediction extends CounterBuff{
        //by default
        Char aim = Dungeon.hero;
        WarnWave wave;

        Emitter charge;

        @Override
        public boolean act(){
            countDown(TICK);
            if(aim==null || !aim.isAlive()) detach();
            else if(!target.fieldOfView[aim.pos]) detach();
            else if(count()<=0f) {
                lightningStrike();
                detach();
            }else{
                updateWave();
            }
            spend(TICK);
            return true;
        }

        protected void lightningStrike(){
            VirtualActor.delay(0.5f, ()->{
                float x = aim.sprite.center().x;
                float y = aim.sprite.center().y;
                aim.sprite.parent.add(new Lightning(aim.sprite.center(), new PointF( x, y-300f),null));
                aim.sprite.parent.add(new Lightning(new PointF(x-5f, y), new PointF( x-5f, y-300f),null));
                aim.sprite.parent.add(new Lightning(new PointF(x+5f, y), new PointF( x+5f, y-300f),null));
                Sample.INSTANCE.play( Assets.Sounds.LIGHTNING, 1.5f);
                boolean alive = aim.isAlive();

                aim.damage(Random.Int(25, 35), new SkyLightning());
                aim.sprite.centerEmitter().burst( SparkParticle.FACTORY, 32 );
                aim.sprite.flash();

                if(aim == Dungeon.hero){
                    Camera.main.shake(5f, 0.8f);
                    if(alive && !aim.isAlive()){
                        Dungeon.fail(SkyLightning.class);
                        GLog.n(M.L(SkyLightning.class, "ondeath"));
                    }
                }
            });
        }

        public LightningPrediction set(Char ch){
            aim = ch;
            wave = new WarnWave(ch.sprite).setScale(2.5f, -2.3f).setSpeed(1f);
            ch.sprite.parent.add(wave);
            return this;
        }

        protected void updateWave(){
            if(wave == null){
                wave = new WarnWave(aim.sprite);
                aim.sprite.parent.add(wave);
            }
            wave.setSpeed(GameMath.gate(1f, 2f - count() / 4f, 2.25f)).setScale(GameMath.gate(1f, 1.2f + count() * 0.3f, 3f), -GameMath.gate(1f, 1.2f + count() * 0.3f, 3f)+0.1f);
        }

        @Override
        public void detach(){
            if(wave != null){
                wave.killWave();
            }
            if(target.sprite != null) {
                target.sprite.centerEmitter().burst(SparkParticle.FACTORY, count() <= 0 ? 25 : 10);
            }
            super.detach();
        }

        @Override
        public void fx(boolean on){
            if(on) {
                charge = target.sprite.emitter();
                charge.autoKill = false;
                charge.pour(SparkParticle.STATIC, 0.05f);
                //charge.on = false;
            }else{
                if(charge != null) {
                    charge.on = false;
                    charge = null;
                }

            }
        }

    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 2, 8 );
    }

    @Override
    public int attackSkill( Char target ) {
        return 11;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 4);
    }

    //used so resistances can differentiate between melee and magical attacks
    public static class LightningBolt{}

    @Override
    protected boolean doAttack( Char enemy ) {

        if (Dungeon.level.distance( pos, enemy.pos ) <= 1) {

            return super.doAttack( enemy );

        } else {

            spend( TIME_TO_ZAP );

            if (hit( this, enemy, true )) {
                int dmg = Random.NormalIntRange(3, 10);
                dmg = Math.round(dmg * AscensionChallenge.statModifier(this));
                enemy.damage( dmg, new DM100H.LightningBolt() );

                if (enemy.sprite.visible) {
                    enemy.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
                    enemy.sprite.flash();
                }

                if (enemy == Dungeon.hero) {

                    Camera.main.shake( 2, 0.3f );

                    if (!enemy.isAlive()) {
                        Badges.validateDeathFromEnemyMagic();
                        Dungeon.fail( getClass() );
                        GLog.n( Messages.get(this, "zap_kill") );
                    }
                }
            } else {
                enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
            }

            if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
                sprite.zap( enemy.pos );
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public void call() {
        next();
    }

    public static class WarnWave extends Image {

        private static final float TIME_TO_FADE = 1f;

        private float time = 0f;

        private float speed = 1f;

        private CharSprite sprite;

        private float baseScale = 1f;
        private float alterScale = 1.5f;

        public WarnWave(CharSprite sp){
            super(Effects.get(Effects.Type.RIPPLE));
            origin.set(width / 2, height / 2);
            sprite = sp;
        }

        public WarnWave setPhase(float phase){
            time = TIME_TO_FADE * (1f - phase + (int) phase);
            return this;
        }

        public WarnWave setSpeed(float speedScale){
            speed = speedScale;
            return this;
        }

        public WarnWave setScale(float base, float scaling){
            baseScale = base;
            alterScale = scaling;
            return this;
        }

        public void reset(int pos) {
            revive();

            x = (pos % Dungeon.level.width()) * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - width) / 2;
            y = (pos / Dungeon.level.width()) * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - height) / 2;

            time = TIME_TO_FADE/speed;
        }

        @Override
        public void update() {
            super.update();

            x = sprite.center().x-this.width/2f;
            y = sprite.center().y-this.height/2f;

            if ((time -= Game.elapsed) <= 0) {
                time += TIME_TO_FADE/speed;
            }

            float p = time / TIME_TO_FADE;
            alpha(1f);
            //grow large by default. alterScale < 0 means shrinking.
            scale.y = scale.x = (1-p)*(1-p)*alterScale + baseScale;

        }
/*
        public static void blast(int pos) {
            Group parent = Dungeon.hero.sprite.parent;
            WarnWave b = (WarnWave) parent.recycle(WarnWave.class);
            parent.bringToFront(b);
            b.reset(pos);
        }
*/
        public void killWave(){
            kill();
        }
    }


    public static class DM100HSprite extends MobSprite {

        public DM100HSprite () {
            super();

            texture( Assets.Sprites.DM100 );

            TextureFilm frames = new TextureFilm( texture, 16, 14 );

            idle = new MovieClip.Animation( 1, true );
            idle.frames( frames, 0, 1 );

            run = new MovieClip.Animation( 12, true );
            run.frames( frames, 6, 7, 8, 9 );

            attack = new MovieClip.Animation( 12, false );
            attack.frames( frames, 2, 3, 4, 0 );

            zap = new MovieClip.Animation( 8, false );
            zap.frames( frames, 5, 5, 1 );

            die = new MovieClip.Animation( 12, false );
            die.frames( frames, 10, 11, 12, 13, 14, 15 );

            play( idle );
        }

        public void zap( int pos ) {

            Char enemy = Actor.findChar(pos);

            //shoot lightning from eye, not sprite center.
            PointF origin = center();
            if (flipHorizontal){
                origin.y -= 6*scale.y;
                origin.x -= 1*scale.x;
            } else {
                origin.y -= 8*scale.y;
                origin.x += 1*scale.x;
            }
            if (enemy != null) {
                parent.add(new Lightning(origin, enemy.sprite.destinationCenter(), (DM100H) ch));
            } else {
                parent.add(new Lightning(origin, pos, (DM100H) ch));
            }
            Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );

            turnTo( ch.pos, pos );
            flash();
            play( zap );
        }

        @Override
        public void die() {
            emitter().burst( Speck.factory( Speck.WOOL ), 5 );
            super.die();
        }

        @Override
        public void onComplete( MovieClip.Animation anim ) {
            if (anim == zap) {
                idle();
            }
            super.onComplete( anim );
        }

        @Override
        public int blood() {
            return 0xFFFFFF88;
        }
    }
}
