package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.city;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.custom.buffs.AbsoluteBlindness;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.MobHard;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.RangeMap;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Shocking;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public abstract class ElementalH extends MobHard {

    {
        HP = HT = 66;
        defenseSkill = 20;

        EXP = 10;
        maxLvl = 20;

        flying = true;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 18, 24 );
    }

    @Override
    public int attackSkill( Char target ) {
        return 26;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 5);
    }

    protected int skillCD = 2;

    @Override
    protected boolean act() {
        if (state == HUNTING){
            skillCD--;
        }

        return super.act();
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        if (skillCD <= 0) {
            return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT ).collisionPos == enemy.pos;
        } else {
            return super.canAttack( enemy );
        }
    }

    protected boolean doAttack( Char enemy ) {

        if (Dungeon.level.adjacent( pos, enemy.pos ) || skillCD > 0) {

            return super.doAttack( enemy );

        } else {

            if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
                sprite.zap( enemy.pos );
                return false;
            } else {
                zap();
                return true;
            }
        }
    }

    @Override
    public int attackProc( Char enemy, int damage ) {
        damage = super.attackProc( enemy, damage );
        meleeProc( enemy, damage );

        return damage;
    }

    protected void zap() {
        spend( 1f );

        skillCD = setCD();

        if (hit( this, enemy, true )) {

            rangedProc( enemy );

        } else {
            enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
        }

        //skillCD = Random.NormalIntRange( 3, 5 );
    }

    public void onZapComplete() {
        zap();
        next();
    }

    @Override
    public boolean add( Buff buff ) {
        if (harmfulBuffs.contains( buff.getClass() )) {
            damage( Random.NormalIntRange( HT/3, HT/2 ), buff );
            return true;
        } else {
            return super.add( buff );
        }
    }

    protected abstract void meleeProc(Char enemy, int damage );
    protected abstract void rangedProc( Char enemy );

    protected abstract int setCD();

    protected ArrayList<Class<? extends Buff>> harmfulBuffs = new ArrayList<>();

    private static final String COOLDOWN = "skillCooldown";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( COOLDOWN, skillCD);
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        if (bundle.contains( COOLDOWN )){
            skillCD = bundle.getInt( COOLDOWN );
        }
    }

    public static Class<? extends ElementalH> random(){
        float seed = Random.Float();
        if(seed<0.35f) return FreezingElemental.class;
        else if(seed<0.7f) return BlazingElemental.class;
        else if(seed<0.95f) return LightningElemental.class;
        return UncertaintyElemental.class;
    }

    public static class FreezingElemental extends ElementalH{
        {
            spriteClass = ElementalHSprite.Frost.class;

            loot = new PotionOfFrost();
            lootChance = 1/4f;

            properties.add( Property.ICY );

            harmfulBuffs.add( Burning.class );
        }

        @Override
        protected void meleeProc(Char enemy, int damage) {
            if(enemy.buff(Frost.class)!=null){
                iceBreak(enemy);
            }
            else if (Random.Int( 3 ) == 0 || Dungeon.level.water[enemy.pos]) {
                new FlavourBuff(){
                    {actPriority = VFX_PRIO;}
                    public boolean act() {
                        Freezing.freeze(enemy.pos);
                        return super.act();
                    }
                }.attachTo(enemy);
                Splash.at( enemy.sprite.center(), sprite.blood(), 5);
            }
            Dungeon.level.setCellToWater(true, enemy.pos);
        }

        @Override
        protected void rangedProc(Char enemy) {
            if(enemy.buff(Frost.class)==null) {
                Freezing.freeze(enemy.pos);
                Dungeon.level.setCellToWater(true, enemy.pos);
                Splash.at( enemy.sprite.center(), sprite.blood(), 5);
            } else {
                iceBreak(enemy);
            }
        }

        protected void iceBreak(Char enemy){
            Buff.detach(enemy, Frost.class);
            enemy.damage(Math.max(enemy.HP/5, enemy.HT/8), this);
            enemy.sprite.burst(0x3325D4, 18);
            enemy.sprite.showStatus(0x2299FF, "!!!");
            int[] iceArea = RangeMap.C1(enemy.pos);
            for(int i: iceArea){
                GameScene.add(Blob.seed(i, 3, Freezing.class));
                Dungeon.level.setCellToWater(false, i);
            }
            if(enemy instanceof Hero && !enemy.isAlive()){
                Dungeon.fail(getClass());
                GLog.n(M.L(this, "ice_break_die"));
            }
        }

        @Override
        public boolean act(){
            if(Blob.volumeAt(pos, Freezing.class)>0 && isAlive()){
                HP = Math.min(HP + (HT-HP)/4, HT);
                sprite.emitter().burst(Speck.factory(Speck.HEALING), 2);
            }
            return super.act();
        }

        @Override
        public void die(Object cause){
            int[] iceArea=RangeMap.manhattanRing(pos, 0, 2);
            for(int i: iceArea){
                GameScene.add(Blob.seed(i, 4, Freezing.class));
                Dungeon.level.setCellToWater(false, i);
            }
            super.die(cause);
        }

        @Override
        protected int setCD() {
            return 2;
        }
    }

    public static class BlazingElemental extends ElementalH{

        {
            spriteClass = ElementalHSprite.Fire.class;

            loot = new PotionOfLiquidFlame();
            lootChance = 1/4f;

            properties.add( Property.FIERY );

            harmfulBuffs.add( Frost.class );
            harmfulBuffs.add( Chill.class );
        }

        @Override
        protected void meleeProc( Char enemy, int damage ) {
            if (Random.Int( 2 ) == 0 && !Dungeon.level.water[enemy.pos]) {
                Buff.affect( enemy, Burning.class ).reignite( enemy );
                Splash.at( enemy.sprite.center(), sprite.blood(), 5);
            }
            vaporize(PathFinder.NEIGHBOURS8[Random.Int(8)]+pos);
            vaporize(enemy.pos);
        }

        @Override
        protected void rangedProc( Char enemy ) {
            if (!Dungeon.level.water[enemy.pos]) {
                Buff.affect( enemy, Burning.class ).reignite( enemy, 4f );
            }
            Splash.at( enemy.sprite.center(), sprite.blood(), 5);

            Ballistica ba = new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT);
            for(int i: ba.path.subList(0, ba.dist)){
                GameScene.add(Blob.seed(i, 5, Fire.class));
                vaporize(i);
            }
        }

        protected void vaporize(int pos){
            if (Dungeon.level.map[pos] == Terrain.WATER){
                Level.set( pos, Terrain.EMPTY);
                GameScene.updateMap( pos );
                CellEmitter.get( pos ).burst( Speck.factory( Speck.STEAM ), 6 );
            }
        }

        @Override
        public boolean act(){
            if(Blob.volumeAt(pos, Fire.class)>0 && isAlive()){
                HP = Math.min(HP + (HT-HP)/4, HT);
                sprite.emitter().burst(Speck.factory(Speck.HEALING), 2);
            }
            return super.act();
        }

        @Override
        public void die(Object cause){
            int[] vap = RangeMap.manhattanRing(pos, 0, 2);
            for(int i: vap){
                vaporize(i);
            }

            int[] burn = RangeMap.manhattanRing(pos, 0, 1);
            for(int i:burn){
                GameScene.add(Blob.seed(i, 12, Fire.class));
            }

            super.die(cause);
        }

        @Override
        protected int setCD() {
            return 3;
        }
    }

    public static class LightningElemental extends ElementalH{

        {
            spriteClass = ElementalHSprite.Shock.class;

            loot = new ScrollOfRecharging();
            lootChance = 1/7f;

            properties.add( Property.ELECTRIC );

            immunities.add(Electricity.class);
        }

        @Override
        protected void meleeProc( Char enemy, int damage ) {
            ArrayList<Char> affected = new ArrayList<>();
            ArrayList<Lightning.Arc> arcs = new ArrayList<>();
            Shocking.arc( this, enemy, 0, affected, arcs );

            if (!Dungeon.level.water[enemy.pos]) {
                affected.remove( enemy );
            }

            for (Char ch : affected) {
                ch.damage( Math.round( damage * 0.6f ), this );
            }

            sprite.parent.addToFront( new Lightning( arcs, null ) );
            Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );

            if(HP*3<HT){
                Buff.affect(enemy, AbsoluteBlindness.class).addLeft(10f).storeVD(enemy.viewDistance);
            }
        }

        @Override
        protected void rangedProc( Char enemy ) {
            boolean blind = enemy.buff(Blindness.class) != null;
            boolean seeNothing = enemy.buff(AbsoluteBlindness.class) != null;
            if(seeNothing){
                Buff.affect(enemy, AbsoluteBlindness.class).addLeft(7f);
                Buff.detach(enemy, Blindness.class);
            } else if(blind){
                Buff.affect(enemy, AbsoluteBlindness.class).addLeft(7f).storeVD(enemy.viewDistance);
                Buff.detach(enemy, Blindness.class);
            }else {
                Buff.affect(enemy, Blindness.class, Blindness.DURATION);
            }
        }

        @Override
        public int defenseProc(Char enemy, int damage){

            if(Blob.volumeAt(pos, Electricity.class)>0){
                if(enemy!=null){
                    sprite.flash();
                    GameScene.add(Blob.seed(enemy.pos, 7, Electricity.class));
                }
            }else{
                int[] shock = RangeMap.manhattanRing(pos,0, 1);
                for(int i: shock){
                    GameScene.add(Blob.seed(i, 5, Electricity.class));
                }
            }

            return super.defenseProc(enemy, damage);
        }

        @Override
        protected int setCD() {
            return Random.IntRange(3,4);
        }

    }

    public static class UncertaintyElemental extends ElementalH{
        {
            spriteClass = ElementalHSprite.Chaos.class;

            loot = new ScrollOfTransmutation();
            lootChance = 1f;

            immunities.add(Electricity.class);
        }

        @Override
        protected void meleeProc( Char enemy, int damage ) {
            Ballistica aim = new Ballistica(pos, enemy.pos, Ballistica.STOP_TARGET);
            CursedWand.randomValidEffect(null, this, aim, false).effect(null, this, aim, false);

            if(this.isAlive() && enemy.isAlive()){
                aim = new Ballistica(pos, enemy.pos, Ballistica.STOP_TARGET);
                CursedWand.randomValidEffect(null, this, aim, false).effect(null, this, aim, false);
            }
        }

        @Override
        protected void rangedProc( Char enemy ) {
            CursedWand.cursedZap(null, this, new Ballistica(pos, enemy.pos, Ballistica.STOP_TARGET), new Callback() {
                @Override
                public void call() {
                }
            });

            if(this.isAlive() && enemy.isAlive()){
                CursedWand.cursedZap(null, this, new Ballistica(pos, enemy.pos, Ballistica.STOP_TARGET), new Callback() {
                    @Override
                    public void call() {
                        next();
                    }
                });
            }
        }

        @Override
        protected int setCD() {
            return 0;
        }
    }


    //Yeah it is rather annoying but we have no way to override the zap() directly, because ElementalSprite is virtual.
    //And what's more problematic is the sprite is tied with zap, meaning you have to switch different vfx by hand based on what is zapping...
    //Hope no more like this. Shaman had already annoyed me.
    public abstract static class ElementalHSprite extends MobSprite {

        protected int boltType;

        protected abstract int texOffset();

        private Emitter particles;
        protected abstract Emitter createEmitter();

        public ElementalHSprite() {
            super();

            int c = texOffset();

            texture( Assets.Sprites.ELEMENTAL );

            TextureFilm frames = new TextureFilm( texture, 12, 14 );

            idle = new Animation( 10, true );
            idle.frames( frames, c+0, c+1, c+2 );

            run = new Animation( 12, true );
            run.frames( frames, c+0, c+1, c+3 );

            attack = new Animation( 15, false );
            attack.frames( frames, c+4, c+5, c+6 );

            zap = attack.clone();

            die = new Animation( 15, false );
            die.frames( frames, c+7, c+8, c+9, c+10, c+11, c+12, c+13, c+12 );

            play( idle );
        }

        @Override
        public void link( Char ch ) {
            super.link( ch );

            if (particles == null) {
                particles = createEmitter();
            }
        }

        @Override
        public void update() {
            super.update();

            if (particles != null){
                particles.visible = visible;
            }
        }

        @Override
        public void die() {
            super.die();
            if (particles != null){
                particles.on = false;
            }
        }

        @Override
        public void kill() {
            super.kill();
            if (particles != null){
                particles.killAndErase();
            }
        }

        public void zap( int cell ) {

            turnTo( ch.pos , cell );
            play( zap );

            MagicMissile.boltFromChar( parent,
                    boltType,
                    this,
                    cell,
                    new Callback() {
                        @Override
                        public void call() {
                            ((ElementalH)ch).onZapComplete();
                        }
                    } );
            Sample.INSTANCE.play( Assets.Sounds.ZAP );
        }

        @Override
        public void onComplete( Animation anim ) {
            if (anim == zap) {
                idle();
            }
            super.onComplete( anim );
        }

        public static class Fire extends ElementalHSprite {

            {
                boltType = MagicMissile.FIRE;
            }

            @Override
            protected int texOffset() {
                return 0;
            }

            @Override
            protected Emitter createEmitter() {
                Emitter emitter = emitter();
                emitter.pour( FlameParticle.FACTORY, 0.06f );
                return emitter;
            }

            @Override
            public int blood() {
                return 0xFFFFBB33;
            }
        }

        public static class Frost extends ElementalHSprite {

            {
                boltType = MagicMissile.FROST;
            }

            @Override
            protected int texOffset() {
                return 28;
            }

            @Override
            protected Emitter createEmitter() {
                Emitter emitter = emitter();
                emitter.pour( MagicMissile.MagicParticle.FACTORY, 0.06f );
                return emitter;
            }

            @Override
            public int blood() {
                return 0xFF8EE3FF;
            }
        }

        public static class Shock extends ElementalHSprite {

            //different bolt, so overrides zap
            @Override
            public void zap( int cell ) {
                turnTo( ch.pos , cell );
                play( zap );

                ((ElementalH)ch).onZapComplete();
                parent.add( new Beam.LightRay(center(), DungeonTilemap.raisedTileCenterToWorld(cell)));
            }

            @Override
            protected int texOffset() {
                return 42;
            }

            @Override
            protected Emitter createEmitter() {
                Emitter emitter = emitter();
                emitter.pour( SparkParticle.STATIC, 0.06f );
                return emitter;
            }

            @Override
            public int blood() {
                return 0xFFFFFF85;
            }
        }

        public static class Chaos extends ElementalHSprite {

            {
                boltType = MagicMissile.RAINBOW;
            }

            @Override
            protected int texOffset() {
                return 56;
            }

            @Override
            protected Emitter createEmitter() {
                Emitter emitter = emitter();
                emitter.pour( RainbowParticle.BURST, 0.025f );
                return emitter;
            }

            @Override
            public int blood() {
                return 0xFFE3E3E3;
            }
        }
    }
}
