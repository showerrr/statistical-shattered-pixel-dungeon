package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.city;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Golem;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.MobHard;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.RangeMap;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GolemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.utils.BArray;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

public class GolemH extends MobHard {

    {
        EXP = 15;

        spriteClass = GolemHSprite.class;

        HP = HT = 120;
        defenseSkill = 15;

        maxLvl = 22;

        loot = Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR);
        lootChance = 0.125f; //initially, see lootChance()

        properties.add(Property.INORGANIC);
        properties.add(Property.LARGE);

        WANDERING = new GolemH.Wandering();
        HUNTING = new GolemH.Hunting();
    }

    private int damageToElemental = 3;

    @Override
    public int defenseProc(Char enemy, int damage){
        damageToElemental--;
        if (damageToElemental <= 0) {
            summonElemental();
        }
        return super.defenseProc(enemy, damage);
    }

    @Override
    public void damage(int damage, Object src) {
        if (src instanceof Wand) {
            damageToElemental--;
            if (damageToElemental <= 0) {
                summonElemental();
            }
        }
        super.damage(damage, src);
    }

    protected void summonElemental(){
        damageToElemental += 3;
        int summonPos = -1;
        int[] range = RangeMap.manhattanRing(pos, 1, 2);
        for (int i : range) {
            if (!Dungeon.level.solid[i] && findChar(i) == null) {
                summonPos = i;
                break;
            }
        }
        if (summonPos == -1) summonPos = Dungeon.level.randomRespawnCell(this);
        ElementalH ele = Reflection.newInstance(ElementalH.random());
        ele.pos = summonPos;
        ele.HP = ele.HT / 2;
        ele.maxLvl = -100;
        ele.state = ele.HUNTING;
        GameScene.add(ele);
        sprite.emitter().start(ElmoParticle.FACTORY, 0.01f, 18);
    }

    private static final String TELEPORTING = "teleporting";
    private static final String SELF_COOLDOWN = "self_cooldown";
    private static final String ENEMY_COOLDOWN = "enemy_cooldown";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TELEPORTING, teleporting);
        bundle.put(SELF_COOLDOWN, selfTeleCooldown);
        bundle.put(ENEMY_COOLDOWN, enemyTeleCooldown);
        bundle.put("element", damageToElemental);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        teleporting = bundle.getBoolean( TELEPORTING );
        selfTeleCooldown = bundle.getInt( SELF_COOLDOWN );
        enemyTeleCooldown = bundle.getInt( ENEMY_COOLDOWN );
        damageToElemental = bundle.getInt("element");
    }

    @Override
    public int distance(Char enemy){
        int dist = super.distance(enemy);
        return  Math.max(dist, 1);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 25, 30 );
    }

    @Override
    public int attackSkill( Char target ) {
        return 28;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 12);
    }

    @Override
    public float lootChance() {
        //each drop makes future drops 1/2 as likely
        // so loot chance looks like: 1/8, 1/16, 1/32, 1/64, etc.
        return super.lootChance() * (float)Math.pow(1/2f, Dungeon.LimitedDrops.GOLEM_EQUIP.count);
    }

    @Override
    public void rollToDropLoot() {
        Imp.Quest.process( this );
        super.rollToDropLoot();
    }

    public Item createLoot() {
        Dungeon.LimitedDrops.GOLEM_EQUIP.count++;
        //uses probability tables for demon halls
        if (loot == Generator.Category.WEAPON){
            return Generator.randomWeapon(5);
        } else {
            return Generator.randomArmor(5);
        }
    }

    private boolean teleporting = false;
    private int selfTeleCooldown = 0;
    private int enemyTeleCooldown = 0;



    @Override
    protected boolean act() {
        selfTeleCooldown--;
        enemyTeleCooldown--;
        if (teleporting){
            ((GolemHSprite)sprite).teleParticles(false);
            if (Actor.findChar(target) == null && Dungeon.level.openSpace[target]) {
                ScrollOfTeleportation.appear(this, target);
                selfTeleCooldown = 30;
            } else {
                target = Dungeon.level.randomDestination(this);
            }
            teleporting = false;
            spend(TICK);
            return true;
        }
        return super.act();
    }

    public void onZapComplete(){
        teleportEnemy();
        next();
    }

    public void teleportEnemy(){
        spend(TICK);

        int bestPos = enemy.pos;
        for (int i : PathFinder.NEIGHBOURS8){
            if (Dungeon.level.passable[pos + i]
                    && Actor.findChar(pos+i) == null
                    && Dungeon.level.trueDistance(pos+i, enemy.pos) > Dungeon.level.trueDistance(bestPos, enemy.pos)){
                bestPos = pos+i;
            }
        }

        if (enemy.buff(MagicImmune.class) != null){
            bestPos = enemy.pos;
        }

        if (bestPos != enemy.pos){
            ScrollOfTeleportation.appear(enemy, bestPos);
            if (enemy instanceof Hero){
                ((Hero) enemy).interrupt();
                Dungeon.observe();
                GameScene.updateFog();
            }
        }

        enemyTeleCooldown = 20;
    }

    private boolean canTele(int target){
        if (enemyTeleCooldown > 0) return false;
        PathFinder.buildDistanceMap(target, BArray.not(Dungeon.level.solid, null), Dungeon.level.distance(pos, target)+1);
        //zaps can go around blocking terrain, but not through it
        if (PathFinder.distance[pos] == Integer.MAX_VALUE){
            return false;
        }
        return true;
    }

    private class Wandering extends Mob.Wandering{

        @Override
        protected boolean continueWandering() {
            enemySeen = false;

            int oldPos = pos;
            if (target != -1 && getCloser( target )) {
                spend( 1 / speed() );
                return moveSprite( oldPos, pos );
            } else if (!Dungeon.bossLevel() && target != -1 && target != pos && selfTeleCooldown <= 0) {
                ((GolemHSprite)sprite).teleParticles(true);
                teleporting = true;
                spend( 2*TICK );
            } else {
                target = Dungeon.level.randomDestination( GolemH.this );
                spend( TICK );
            }

            return true;
        }
    }

    private class Hunting extends Mob.Hunting{

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (!enemyInFOV || canAttack(enemy)) {
                return super.act(enemyInFOV, justAlerted);
            } else {
                enemySeen = true;
                target = enemy.pos;

                int oldPos = pos;

                if (distance(enemy) >= 1 && Random.Int(100/distance(enemy)) == 0
                        && !Char.hasProp(enemy, Property.IMMOVABLE) && canTele(target)){
                    if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
                        sprite.zap( enemy.pos );
                        return false;
                    } else {
                        teleportEnemy();
                        return true;
                    }

                } else if (getCloser( target )) {
                    spend( 1 / speed() );
                    return moveSprite( oldPos,  pos );

                } else if (!Char.hasProp(enemy, Property.IMMOVABLE) && canTele(target)) {
                    if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
                        sprite.zap( enemy.pos );
                        return false;
                    } else {
                        teleportEnemy();
                        return true;
                    }

                } else {
                    spend( TICK );
                    return true;
                }

            }
        }
    }

    public static class GolemHSprite extends MobSprite {

        private Emitter teleParticles;

        public GolemHSprite() {
            super();

            texture( Assets.Sprites.GOLEM );

            TextureFilm frames = new TextureFilm( texture, 17, 19 );

            idle = new MovieClip.Animation( 4, true );
            idle.frames( frames, 0, 1 );

            run = new MovieClip.Animation( 12, true );
            run.frames( frames, 2, 3, 4, 5 );

            attack = new MovieClip.Animation( 10, false );
            attack.frames( frames, 6, 7, 8 );

            zap = attack.clone();

            die = new MovieClip.Animation( 15, false );
            die.frames( frames, 9, 10, 11, 12, 13 );

            play( idle );
        }

        @Override
        public void link(Char ch) {
            super.link(ch);

            teleParticles = emitter();
            teleParticles.autoKill = false;
            teleParticles.pour(ElmoParticle.FACTORY, 0.05f);
            teleParticles.on = false;
        }

        @Override
        public void update() {
            super.update();
            if (teleParticles != null){
                teleParticles.pos( this );
                teleParticles.visible = visible;
            }
        }

        @Override
        public void kill() {
            super.kill();

            if (teleParticles != null) {
                teleParticles.on = false;
            }
        }

        public void teleParticles(boolean value){
            if (teleParticles != null) teleParticles.on = value;
        }

        @Override
        public synchronized void play(MovieClip.Animation anim, boolean force) {
            if (teleParticles != null) teleParticles.on = false;
            super.play(anim, force);
        }

        @Override
        public int blood() {
            return 0xFF80706c;
        }

        public void zap( int cell ) {

            turnTo( ch.pos , cell );
            play( zap );

            MagicMissile.boltFromChar( parent,
                    MagicMissile.ELMO,
                    this,
                    cell,
                    new Callback() {
                        @Override
                        public void call() {
                            ((GolemH)ch).onZapComplete();
                        }
                    } );
            Sample.INSTANCE.play( Assets.Sounds.ZAP );
        }

        private boolean died = false;

        @Override
        public void onComplete( MovieClip.Animation anim ) {
            if (anim == die && !died) {
                died = true;
                emitter().burst( ElmoParticle.FACTORY, 4 );
            }
            if (anim == zap) {
                idle();
            }
            super.onComplete( anim );
        }
    }
}
