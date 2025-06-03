package com.shatteredpixel.shatteredpixeldungeon.custom.ch.boss;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.GME;
import com.shatteredpixel.shatteredpixeldungeon.custom.visuals.MissileSpriteCustom;
import com.shatteredpixel.shatteredpixeldungeon.custom.visuals.effects.BeamCustom;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.LloydsBeacon;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DM300Sprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class DM300Hard extends Boss{
    {
        spriteClass = DM300Sprite.class;

        initProperty();
        properties.add(Property.INORGANIC);
        initBaseStatus(16, 22, 28, 16, 400, 4, 8);
        initStatus(76);

        viewDistance = 18;
    }

    {
        immunities.add(Sleep.class);

        resistances.add(Terror.class);
        resistances.add(Charm.class);
        resistances.add(Vertigo.class);
        resistances.add(Cripple.class);
        resistances.add(Chill.class);
        resistances.add(Frost.class);
        resistances.add(Roots.class);
        resistances.add(Slow.class);

        immunities.add(Paralysis.class);
    }

    //0~7 phases. if health < threshold[phase], then go on.
    private static final int[] healthThreshold = new int[]{399, 330, 270, 210, 160, 120, 80, 40, -1000000};

    private int phase = 0;

    private float summonCD = 60f;

    private int lastTargeting = -1;

    @Override
    public String info(){
        return Messages.get(this, "desc", phase, HP - healthThreshold[phase]);
    }

    @Override
    public float speed(){
        return super.speed() * (0.6f + phase*0.05f);
    }

    protected void goOnPhase(){
        phase++;
        CellEmitter.center(pos).burst(BlastParticle.FACTORY, 30);
        Sample.INSTANCE.play( Assets.Sounds.BLAST );
        if(phase % 2 == 0){
            destroyAll();
            ArrayList<Integer> places = new ArrayList<>();
            places.add(5*Dungeon.level.width()+4);
            places.add(6*Dungeon.level.width()-5);
            places.add(17*Dungeon.level.width()+4);
            places.add(18*Dungeon.level.width()-5);
            Random.shuffle(places);
            for(int i=0;i<Math.min(phase/2-1, 3);++i){
                summonCaster(Random.Int(4), places.get(i),false);
            }
        }else{
            destroyAll();
            for(int i=0;i<phase/2;++i){
                summonCaster(Random.Int(6), findRandomPlaceForCaster(), false);
            }
        }

        activateAll();

        lastTargeting = -1;
        Buff.affect(this, RageAndFire.class, 1f*phase + 5f);

        yell(Messages.get(this, "damaged"));
    }

    @Override
    public boolean act(){
        if(paralysed>0){
            spend(TICK);
            summonCD -= 1/speed();
            return true;
        }
        if(buff(RageAndFire.class)!=null){
            //if target is locked, fire, target = -1
            if(lastTargeting != -1){
                //no spend, execute next act
                fireProc(lastTargeting);
                return true;
                //else try to lock target
            }else if(findTargetLocation()) {
                //if success, spend and ready to fire
                return true;
            }//else, just act
        }
        if(summonCD<0f){
            summonCD += Math.max(60f - phase * 2f, 40f);
            summonCaster(Random.Int(4), findRandomPlaceForCaster(), phase>5);
        }
        summonCD -= 1/speed();
        return super.act();
    }

    @Override
    public void move(int step, boolean traveling) {

        super.move(step, traveling);

        Camera.main.shake(  1, 0.25f );

        if (Dungeon.level.map[step] == Terrain.INACTIVE_TRAP && state == HUNTING) {

            if (Dungeon.level.heroFOV[step]) {
                if (buff(Barrier.class) == null) {
                    GLog.w(Messages.get(this, "shield"));
                }
                sprite.emitter().start(SparkParticle.STATIC, 0.05f, 20);
            }

            Buff.affect(this, Barrier.class).setShield( 20 );

            summonCD -= 24f;

        }
    }

    @Override
    public void notice() {
        super.notice();
        if (!BossHealthBar.isAssigned()) {
            BossHealthBar.assignBoss(this);
            yell(Messages.get(this, "notice"));
            for (Char ch : Actor.chars()){
                if (ch instanceof DriedRose.GhostHero){
                    ((DriedRose.GhostHero) ch).sayBoss();
                }
            }
        }
    }

    @Override
    public void damage(int damage, Object src){
        if (!BossHealthBar.isAssigned()) {
            BossHealthBar.assignBoss(this);
            yell(Messages.get(this, "notice"));
        }
        if(buff(RageAndFire.class)!=null) damage = Math.round(damage*0.1f);

        int preHP = HP;
        super.damage(damage, src);
        int postHP = HP;
        if(preHP>healthThreshold[phase] && postHP<=healthThreshold[phase]){
            HP = healthThreshold[phase];
            goOnPhase();
        }

        if(phase>4) BossHealthBar.bleed(true);
        LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
        if (lock != null) lock.addTime(damage*2);
    }

    @Override
    public void die(Object src){

        super.die(src);

        yell(Messages.get(this, "die"));

        for(Mob m: Dungeon.level.mobs.toArray(new Mob[0])){
            if(m instanceof SpellCaster){
                m.die(this);
                Dungeon.level.mobs.remove(m);
            }
        }

        Dungeon.level.drop(new SkeletonKey(Dungeon.depth), pos).sprite.drop();
        GameScene.bossSlain();
        Badges.validateBossSlain();
        LloydsBeacon beacon = Dungeon.hero.belongings.getItem(LloydsBeacon.class);
        if (beacon != null) {
            beacon.upgrade();
        }

        Dungeon.level.drop(new Gold().quantity(Random.Int(1800, 2400)), pos).sprite.drop();
        Dungeon.level.drop(new MetalShard().quantity(Random.Int(4, 7)), pos).sprite.drop();
        Dungeon.level.drop(new ScrollOfMagicMapping().quantity(2).identify(), pos).sprite.drop();
        Dungeon.level.drop(new ScrollOfUpgrade().identify(), pos).sprite.drop();
    }

    @Override
    protected boolean canAttack(Char enemy){
        if(enemy!=null && enemySeen){
            if(Dungeon.level.distance(pos, enemy.pos)<3) return true;
        }
        return false;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put("phaseDM", phase);
        bundle.put("summonCD", summonCD);
        bundle.put("lastTargetingDM", lastTargeting);
        super.storeInBundle(bundle);

    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        phase = bundle.getInt("phaseDM");
        summonCD = bundle.getFloat("summonCD");
        lastTargeting = bundle.getInt("lastTargetingDM");

            BossHealthBar.assignBoss(this);
            if (phase>4) BossHealthBar.bleed(true);

    }

//caster ability logic

    private static final int FROST = 0;
    private static final int EXPLODE = 1;
    private static final int LIGHT = 2;
    private static final int BOUNCE = 3;

    protected void fallingRockVisual(int pos){
        Camera.main.shake(0.4f, 2f);
        CellEmitter.get( pos - Dungeon.level.width() ).start(Speck.factory(Speck.ROCK), 0.08f, 10);
    }

    protected void activateVisual(int pos){
        CellEmitter.get( pos ).start(Speck.factory(Speck.STAR), 0.14f, 8);
    }

    protected void summonCaster(int category, int pos, boolean activate){
        if(pos != -1){
            SpellCaster caster;
            switch (category){
                case FROST:
                    caster = new SpellCaster.FrostCaster();
                    break;
                case EXPLODE:
                    caster = new SpellCaster.ExplosionCaster();
                    break;
                case LIGHT:
                    caster = new SpellCaster.LightCaster();
                    break;
                case BOUNCE: default:
                    caster = new SpellCaster.BounceCaster();
            }
            caster.pos = pos;
            GameScene.add(caster, Random.Float(2f, 8f));
            Dungeon.level.mobs.add(caster);
            fallingRockVisual(pos);
            if(activate) caster.activate();
            Dungeon.level.passable[pos] = false;
        }
    }

    protected int findRandomPlaceForCaster(){

        int[] ceil = GME.rectBuilder(pos, 4, 4);

        //shuffle
        for (int i=0; i < ceil.length - 1; i++) {
            int j = Random.Int( i, ceil.length );
            if (j != i) {
                int t = ceil[i];
                ceil[i] = ceil[j];
                ceil[j] = t;
            }
        }

        boolean valid;
        for(int i: ceil){
            valid = true;
            for(int j: PathFinder.NEIGHBOURS4){
                if(findChar(j+i)!=null){
                    valid = false;break;
                }
            }
            if(!valid) continue;
            if(findChar(i) == null && !Dungeon.level.solid[i] && !(Dungeon.level.map[i]==Terrain.INACTIVE_TRAP)){

                //caster.spriteHardlight();
                return i;
            }
        }

        return -1;
    }

    protected void activateAll(){
        for(Mob m: Dungeon.level.mobs.toArray(new Mob[0])){
            if(m instanceof SpellCaster){
                if(m.alignment == Alignment.NEUTRAL) {
                    ((SpellCaster) m).activate();
                    activateVisual(m.pos);
                }
            }
        }
    }

    protected void destroyAll(){
        for(Mob m: Dungeon.level.mobs.toArray(new Mob[0])){
            if(m instanceof SpellCaster){
                if(m.alignment == Alignment.NEUTRAL) continue;
                Ballistica beam = new Ballistica(m.pos, Dungeon.hero.pos, Ballistica.WONT_STOP);
                m.sprite.parent.add(new BeamCustom(
                        DungeonTilemap.raisedTileCenterToWorld(m.pos),
                        DungeonTilemap.tileCenterToWorld(beam.collisionPos),
                        Effects.Type.DEATH_RAY).setLifespan(0.9f));
                for(int i: beam.path){
                    Char ch = findChar(i);
                    if(ch!=null){
                        if(ch.alignment != Alignment.ENEMY){
                            SpellCaster.zapDamage(ch, 20, 30, 0.85f, m);
                        }
                    }
                }
                m.die(this);
                Dungeon.level.mobs.remove(m);
            }
        }
    }

    //the first num is all nums, and the second is activated nums.
    protected int[] aliveCasters(){
        int[] count = new int[]{0, 0};
        for(Mob m: Dungeon.level.mobs.toArray(new Mob[0])) {
            if (m instanceof SpellCaster) {
                ++count[0];
                if(m.alignment != Alignment.NEUTRAL){
                    ++count[1];
                }
            }
        }
        return count;
    }

    public static class RageAndFire extends FlavourBuff{
        Emitter charge;
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

    protected void fireProc(int targetCell){
        Ballistica ballistica = new Ballistica(pos, targetCell, Ballistica.PROJECTILE);
        ((MissileSpriteCustom)sprite.parent.recycle(MissileSpriteCustom.class)).reset(
                sprite, ballistica.collisionPos, new Bomb(), 10f, 2.0f,
                new Callback() {
                    @Override
                    public void call() {
                        int[] cells = GME.NEIGHBOURS5();
                        for(int i: cells){
                            int c = i+ballistica.collisionPos;
                            Char ch = findChar(c);
                            if(ch!=null){
                                if(ch.alignment != Alignment.ENEMY){
                                    int damage = Random.Int(14, 24);
                                    damage -= ch.drRoll();
                                    ch.damage(damage, this);
                                    if(ch == Dungeon.hero && !ch.isAlive()){
                                        Dungeon.fail(this.getClass());
                                    }
                                }
                            }
                            CellEmitter.center(c).burst(BlastParticle.FACTORY, 15);
                        }

                    }
                }
        );
        lastTargeting = -1;
    }

    protected boolean findTargetLocation(){
        if(enemy!=null && enemySeen){
            lastTargeting = enemy.pos;
        }else{
            lastTargeting = Dungeon.hero.pos;
        }
        if(canHit(lastTargeting)) {
            sprite.parent.addToBack(new TargetedCell(lastTargeting, 0xFF0000));
            spend(TICK);
            return true;
        }else{
            lastTargeting = -1;
            return false;
        }
    }

    protected boolean canHit(int targetPos){
        Ballistica ba = new Ballistica(pos, targetPos, Ballistica.PROJECTILE);
        return Dungeon.level.distance(ba.collisionPos, targetPos) <= 1;
    }

    @Override
    public boolean isAlive(){
        return HP>0 || healthThreshold[phase]>0;
    }
}
