package com.shatteredpixel.shatteredpixeldungeon.custom.ch.boss;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.GME;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.LloydsBeacon;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlink;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Shuriken;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TenguSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.watabou.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

public class TenguHard extends Boss{
    {
        spriteClass = TenguSprite.class;

        initProperty();
        initBaseStatus(6, 13, 20, 14, 200, 2, 3);
        initStatus(44);

        HUNTING = new Hunting();

        flying = true; //doesn't literally fly, but he is fleet-of-foot enough to avoid hazards

        viewDistance = 12;
    }

    private int comboSinceJump = 0;

    @Override
    public boolean act(){
        if(((HardTenguLevel)(Dungeon.level)).state() == HardTenguLevel.State.FIGHT_ARENA) {
            if(HP <= 80) {
                if (buff(BackgroundBeamCounter.class) == null) {
                    Buff.affect(this, BackgroundBeamCounter.class).setDensity(9, 9, 1, 1);
                    yell(Messages.get(this, "more_interesting"));
                }
            }
        }
        return super.act();
    }

    @Override
    protected void onAdd() {
        //when he's removed and re-added to the fight, his time is always set to now.
        if (cooldown() > TICK) {
            spend(-cooldown());
            spendToWhole();
        }
        super.onAdd();
    }

    @Override
    public int damageRoll() {
        return super.damageRoll();
    }

    @Override
    public int attackSkill( Char target ) {
        if (Dungeon.level.adjacent(pos, target.pos)){
            return super.attackSkill(target)/2;
        } else {
            return super.attackSkill(target);
        }
    }

    @Override
    public int drRoll() {
        return super.drRoll();
    }

    boolean loading = false;

    //Tengu is immune to debuffs and damage when removed from the level
    @Override
    public boolean add(Buff buff) {
        if (Actor.chars().contains(this) || buff instanceof Doom || loading){
            return super.add(buff);
        }
        return false;
    }

    @Override
    public void damage(int dmg, Object src) {
        if (!Dungeon.level.mobs.contains(this)){
            return;
        }

        if(src instanceof Buff || src instanceof Blob || src instanceof Wand || src instanceof MissileWeapon){
            dmg = Math.round(dmg*0.75f);
        }

        HardTenguLevel.State state = ((HardTenguLevel)Dungeon.level).state();

        int hpBracket = 20;

        int beforeHitHP = HP;
        super.damage(dmg, src);
        dmg = beforeHitHP - HP;

        //tengu cannot be hit through multiple brackets at a time
        if ((beforeHitHP/hpBracket - HP/hpBracket) >= 2){
            HP = hpBracket * ((beforeHitHP/hpBracket)-1) + 1;
        }

        LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
        if (lock != null) {
            int multiple = state == HardTenguLevel.State.FIGHT_START ? 1 : 4;
            lock.addTime(dmg*multiple);
        }

        //phase 2 of the fight is over
        if (HP == 0 && state == HardTenguLevel.State.FIGHT_ARENA) {
            //let full attack action complete first
            Actor.add(new Actor() {

                {
                    actPriority = VFX_PRIO;
                }

                @Override
                protected boolean act() {
                    Actor.remove(this);
                    ((HardTenguLevel)Dungeon.level).progress();
                    return true;
                }
            });
            return;
        }

        //phase 1 of the fight is over
        if (state ==HardTenguLevel.State.FIGHT_START && HP <= 140){
            HP = 140;
            yell(Messages.get(this, "interesting"));
            ((HardTenguLevel)Dungeon.level).progress();
            BossHealthBar.bleed(true);

            //if tengu has lost a certain amount of hp, jump
        } else if (beforeHitHP / hpBracket != HP / hpBracket) {
            //let full attack action complete first
            Actor.add(new Actor() {

                {
                    actPriority = VFX_PRIO;
                }

                @Override
                protected boolean act() {
                    Actor.remove(this);
                    jump();
                    return true;
                }
            });
            return;
        }
    }


    @Override
    public boolean isAlive() {
        return HP > 0 || Dungeon.level.mobs.contains(this); //Tengu has special death rules, see prisonbosslevel.progress()
    }

    @Override
    public void die( Object cause ) {

        if (Dungeon.hero.subClass == HeroSubClass.NONE) {
            Dungeon.level.drop( new TengusMask(), pos ).sprite.drop();
        }

        Shuriken sh = new Shuriken();
        sh.level(3);
        Dungeon.level.drop(sh.identify(), pos).sprite.drop();
        Dungeon.level.drop(new Bomb().quantity(Random.Int(3, 6)), pos ).sprite.drop();
        Dungeon.level.drop(new StoneOfBlink().quantity(3), pos).sprite.drop();

        GameScene.bossSlain();
        super.die( cause );

        Badges.validateBossSlain();

        LloydsBeacon beacon = Dungeon.hero.belongings.getItem(LloydsBeacon.class);
        if (beacon != null) {
            beacon.upgrade();
        }

        yell( Messages.get(this, "defeated") );
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        return new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos;
    }

    //tengu's attack is always visible
    @Override
    protected boolean doAttack(Char enemy) {
        sprite.attack( enemy.pos );
        spend( attackDelay() );
        return false;
    }

    @Override
    public int attackProc(Char enemy, int damage){
        comboSinceJump ++;
        if(comboSinceJump == 7) GLog.w(Messages.get(this, "throw_warning"));
        if(comboSinceJump > 7){
            switch (comboSinceJump % 3){
                case 0: default:
                    Buff.affect(enemy, Poison.class).extend(comboSinceJump/1.5f);
                    break;
                case 1:
                    Buff.affect(enemy, Chill.class, comboSinceJump/1.5f);
                    break;
                case 2:
                    Ballistica trajectory = new Ballistica(pos, enemy.pos, Ballistica.STOP_TARGET);
                    //trim it to just be the part that goes past them
                    trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size()-1), Ballistica.PROJECTILE);
                    //knock them back along that ballistica
                    WandOfBlastWave.throwChar(enemy, trajectory, comboSinceJump/3, true, true, TenguHard.class);
            }
        }
        return damage;
    }

    private void jump() {
        comboSinceJump = 0;
        //in case tengu hasn't had a chance to act yet
        if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
            fieldOfView = new boolean[Dungeon.level.length()];
            Dungeon.level.updateFieldOfView( this, fieldOfView );
        }

        if (enemy == null) enemy = chooseEnemy();
        if (enemy == null) return;

        int newPos;
        if (Dungeon.level instanceof HardTenguLevel){
            HardTenguLevel level = (HardTenguLevel) Dungeon.level;

            //if we're in phase 1, want to warp around within the room
            if (level.state() == HardTenguLevel.State.FIGHT_START) {

                level.cleanTenguCell();

                int tries = 100;
                do {
                    newPos = ((HardTenguLevel)Dungeon.level).randomTenguCellPos();
                    tries--;
                } while ( tries > 0 && (level.trueDistance(newPos, enemy.pos) <= 3.5f
                        || level.trueDistance(newPos, Dungeon.hero.pos) <= 3.5f
                        || Actor.findChar(newPos) != null));

                if (tries <= 0) newPos = pos;

                if (level.heroFOV[pos]) CellEmitter.get( pos ).burst( Speck.factory( Speck.WOOL ), 6 );

                sprite.move( pos, newPos );
                move( newPos );

                if (level.heroFOV[newPos]) CellEmitter.get( newPos ).burst( Speck.factory( Speck.WOOL ), 6 );
                Sample.INSTANCE.play( Assets.Sounds.PUFF );

                float fill = 0.9f - 1f*((HP-HT/2f)/HT);
                level.placeTrapsInTenguCell(fill);

                //otherwise, jump in a larger possible area, as the room is bigger
            } else {

                int tries = 100;
                do {
                    newPos = Random.Int(level.length());
                    tries--;
                } while (  tries > 0 &&
                        (level.solid[newPos] ||
                                level.distance(newPos, enemy.pos) < 5 ||
                                level.distance(newPos, enemy.pos) > 7 ||
                                level.distance(newPos, Dungeon.hero.pos) < 5 ||
                                level.distance(newPos, Dungeon.hero.pos) > 7 ||
                                level.distance(newPos, pos) < 5 ||
                                Actor.findChar(newPos) != null ||
                                Dungeon.level.heaps.get(newPos) != null));

                if (tries <= 0) newPos = pos;

                if (level.heroFOV[pos]) CellEmitter.get( pos ).burst( Speck.factory( Speck.WOOL ), 6 );

                sprite.move( pos, newPos );
                move( newPos );

                if (arenaJumps < 4) arenaJumps++;

                if (level.heroFOV[newPos]) CellEmitter.get( newPos ).burst( Speck.factory( Speck.WOOL ), 6 );
                Sample.INSTANCE.play( Assets.Sounds.PUFF );

            }

            //if we're on another type of level
        } else {
            Level level = Dungeon.level;

            newPos = level.randomRespawnCell( this );

            if (level.heroFOV[pos]) CellEmitter.get( pos ).burst( Speck.factory( Speck.WOOL ), 6 );

            sprite.move( pos, newPos );
            move( newPos );

            if (level.heroFOV[newPos]) CellEmitter.get( newPos ).burst( Speck.factory( Speck.WOOL ), 6 );
            Sample.INSTANCE.play( Assets.Sounds.PUFF );

        }

    }

    @Override
    public void notice() {
        super.notice();
        if (!BossHealthBar.isAssigned()) {
            BossHealthBar.assignBoss(this);
            if (HP <= 80) BossHealthBar.bleed(true);
            if (HP == HT) {
                yell(Messages.get(this, "notice_gotcha", Dungeon.hero.name()));
                for (Char ch : Actor.chars()){
                    if (ch instanceof DriedRose.GhostHero){
                        ((DriedRose.GhostHero) ch).sayBoss();
                    }
                }
            } else {
                yell(Messages.get(this, "notice_have", Dungeon.hero.name()));
            }
        }
    }

    {
        immunities.add( Blindness.class );
        immunities.add( Terror.class );
    }

    private static final String LAST_ABILITY     = "last_ability";
    private static final String ABILITIES_USED   = "abilities_used";
    private static final String ARENA_JUMPS      = "arena_jumps";
    private static final String ABILITY_COOLDOWN = "ability_cooldown";
    private static final String COMBO = "combosincejump";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put( LAST_ABILITY, lastAbility );
        bundle.put( ABILITIES_USED, abilitiesUsed );
        bundle.put( ARENA_JUMPS, arenaJumps );
        bundle.put( ABILITY_COOLDOWN, abilityCooldown );
        bundle.put( COMBO, comboSinceJump );
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        loading = true;
        super.restoreFromBundle(bundle);
        loading = false;
        lastAbility = bundle.getInt( LAST_ABILITY );
        abilitiesUsed = bundle.getInt( ABILITIES_USED );
        arenaJumps = bundle.getInt( ARENA_JUMPS );
        abilityCooldown = bundle.getInt( ABILITY_COOLDOWN );
        comboSinceJump = bundle.getInt( COMBO );

        BossHealthBar.assignBoss(this);
        if (HP <= 80) BossHealthBar.bleed(true);
    }

    //don't bother bundling this, as its purely cosmetic
    private boolean yelledCoward = false;

    //tengu is always hunting
    private class Hunting extends Mob.Hunting{

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {

            enemySeen = enemyInFOV;
            if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy )) {

                if (canUseAbility()){
                    useAbility();
                    return true;
                }

                return doAttack( enemy );

            } else {

                if (enemyInFOV) {
                    target = enemy.pos;
                } else {
                    chooseEnemy();
                    if (enemy == null){
                        //if nothing else can be targeted, target hero
                        enemy = Dungeon.hero;
                    }
                    target = enemy.pos;
                }

                //if not charmed, attempt to use an ability, even if the enemy can't be seen
                if (canUseAbility()){
                    useAbility();
                    return true;
                }

                spend( TICK );
                return true;

            }
        }
    }

    //*****************************************************************************************
    //***** Tengu abilities. These are expressed in game logic as buffs, blobs, and items *****
    //*****************************************************************************************

    //so that mobs can also use this
    private static Char throwingChar;

    private int lastAbility = -1;
    private int abilitiesUsed = 0;
    private int arenaJumps = 0;
    private int towerThrown = 0;

    //starts at 2, so one turn and then first ability
    private int abilityCooldown = -1;

    private static final int BOMB_ABILITY    = 0;
    private static final int FIRE_ABILITY    = 1;
    private static final int SHOCKER_ABILITY = 2;

    //expects to be called once per turn;
    public boolean canUseAbility(){
        //only arena area can use
        if(((HardTenguLevel)(Dungeon.level)).state() != HardTenguLevel.State.FIGHT_ARENA) return false;

        if(abilityCooldown < 0){
            abilityCooldown += 3f + 3f*HP/HT;
            return true;
        }else{
            abilityCooldown -= 1f;
            return false;
        }
    }

    public boolean useAbility(){
        boolean abilityUsed = false;
        int abilityToUse = -1;

        if(abilitiesUsed == 0){
            abilityToUse = SHOCKER_ABILITY;
            abilityUsed = throwBeamTower(TenguHard.this, enemy);
            if(abilityUsed) towerThrown++;
            lastAbility = abilityToUse;
            if(!abilityUsed){
                abilityToUse = BOMB_ABILITY;
                abilityUsed = throwBomb(TenguHard.this, enemy);
                lastAbility = abilityToUse;
            }
        }

        while (!abilityUsed ){
            do {
                abilityToUse = Random.chances(new float[]{6f, 3f, towerWeight()});
            }while(lastAbility == FIRE_ABILITY && abilityToUse == FIRE_ABILITY);

            switch (abilityToUse){
                case BOMB_ABILITY : default:
                    abilityUsed = throwBomb(TenguHard.this, enemy);
                    break;

                    case FIRE_ABILITY:
                        abilityUsed = throwFire(TenguHard.this, enemy);
                    break;

                    case SHOCKER_ABILITY:
                        abilityUsed = throwBeamTower(TenguHard.this, enemy);
                        if(abilityUsed) towerThrown++;
                    break;
            }

        }

        spend(TICK);

        lastAbility = abilityToUse;
        abilitiesUsed++;
        return lastAbility == FIRE_ABILITY;
    }

    private float towerWeight(){
        if(towerThrown >= 6) return 0f;
        if(abilitiesUsed > towerThrown * towerThrown){
            return 15f;
        }else if(HP <= HT * 3 / 10 && towerThrown < 5){
            return 15f;
        }else{
            return 0f;
        }
    }

    //******************
    //***Bomb Ability***
    //******************

    //returns true if bomb was thrown
    public static boolean throwBomb(final Char thrower, final Char target){

        int targetCell = -1;

        //Targets closest cell which is adjacent to target, and at least 3 tiles away
        for (int i : PathFinder.NEIGHBOURS8){
            int cell = target.pos + i;
            if ( !Dungeon.level.solid[cell] ){
                if (targetCell == -1 ||
                        Dungeon.level.trueDistance(cell, thrower.pos) < Dungeon.level.trueDistance(targetCell, thrower.pos)){
                    targetCell = cell;
                }
            }
        }

        if (targetCell == -1){
            return false;
        }

        final int finalTargetCell = targetCell;
        throwingChar = thrower;
        final BombAbility.BombItem item = new BombAbility.BombItem();
        thrower.sprite.zap(finalTargetCell);
        ((MissileSprite) thrower.sprite.parent.recycle(MissileSprite.class)).
                reset(thrower.sprite,
                        finalTargetCell,
                        item,
                        new Callback() {
                            @Override
                            public void call() {
                                item.onThrow(finalTargetCell);
                                thrower.next();
                            }
                        });
        return true;
    }

    public static class BombAbility extends Buff {

        public int bombPos = -1;
        private int timer = 3;

        private ArrayList<Emitter> smokeEmitters = new ArrayList<>();

        protected void bombProc(){
            PathFinder.buildDistanceMap( bombPos, BArray.not( Dungeon.level.solid, null ), 2 );
            for (int cell = 0; cell < PathFinder.distance.length; cell++) {

                if (PathFinder.distance[cell] < Integer.MAX_VALUE) {
                    Char ch = Actor.findChar(cell);
                    if (ch != null && !(ch instanceof TenguHard)) {
                        int dmg = Random.NormalIntRange(5 + Dungeon.depth, 10 + Dungeon.depth * 2);
                        dmg -= ch.drRoll();

                        if (dmg > 0) {
                            ch.damage(dmg, Bomb.class);
                        }

                        if (ch == Dungeon.hero && !ch.isAlive()) {
                            Dungeon.fail(TenguHard.class);
                        }
                    }

                    Heap h = Dungeon.level.heaps.get(cell);
                    if (h != null) {
                        for (Item i : h.items.toArray(new Item[0])) {
                            if (i instanceof BombItem) {
                                h.remove(i);
                            }
                        }
                    }
                }

            }
        }

        @Override
        public boolean act() {

            if (smokeEmitters.isEmpty()){
                fx(true);
            }

            PointF p = DungeonTilemap.raisedTileCenterToWorld(bombPos);
            if (timer == 3) {
                FloatingText.show(p.x, p.y, bombPos, "3...", CharSprite.NEUTRAL);
            } else if (timer == 2){
                FloatingText.show(p.x, p.y, bombPos, "2...", CharSprite.WARNING);
            } else if (timer == 1){
                FloatingText.show(p.x, p.y, bombPos, "1...", CharSprite.NEGATIVE);
            } else {
                bombProc();
                Sample.INSTANCE.play(Assets.Sounds.BLAST);
                detach();
                return true;
            }

            timer--;
            spend(TICK);
            return true;
        }

        @Override
        public void fx(boolean on) {
            if (on && bombPos != -1){
                PathFinder.buildDistanceMap( bombPos, BArray.not( Dungeon.level.solid, null ), 2 );
                for (int i = 0; i < PathFinder.distance.length; i++) {
                    if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                        Emitter e = CellEmitter.get(i);
                        e.pour( SmokeParticle.FACTORY, 0.25f );
                        smokeEmitters.add(e);
                    }
                }
            } else if (!on) {
                for (Emitter e : smokeEmitters){
                    e.burst(BlastParticle.FACTORY, 2);
                }
            }
        }

        private static final String BOMB_POS = "bomb_pos";
        private static final String TIMER = "timer";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put( BOMB_POS, bombPos );
            bundle.put( TIMER, timer );
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            bombPos = bundle.getInt( BOMB_POS );
            timer = bundle.getInt( TIMER );
        }

        public static class BombItem extends Item {

            {
                dropsDownHeap = true;
                unique = true;

                image = ItemSpriteSheet.TENGU_BOMB;
            }

            @Override
            public boolean doPickUp( Hero hero, int pos) {
                GLog.w( Messages.get(this, "cant_pickup") );
                return false;
            }

            @Override
            protected void onThrow(int cell) {
                super.onThrow(cell);
                if (throwingChar != null){
                    Buff.append(throwingChar, BombAbility.class).bombPos = cell;
                    throwingChar = null;
                } else {
                    Buff.append(curUser, BombAbility.class).bombPos = cell;
                }
            }

            @Override
            public Emitter emitter() {
                Emitter emitter = new Emitter();
                emitter.pos(7.5f, 3.5f);
                emitter.fillTarget = false;
                emitter.pour(SmokeParticle.SPEW, 0.05f);
                return emitter;
            }
        }
    }

    //******************
    //***Fire Ability***
    //******************

    public static boolean throwFire(final Char thrower, final Char target){

        Ballistica aim = new Ballistica(thrower.pos, target.pos, Ballistica.WONT_STOP);

        for (int i = 0; i < PathFinder.CIRCLE8.length; i++){
            if (aim.sourcePos+PathFinder.CIRCLE8[i] == aim.path.get(1)){
                thrower.sprite.zap(target.pos);
                Buff.append(thrower, FireAbility.class).direction = i;

                thrower.sprite.emitter().start(Speck.factory(Speck.STEAM), .03f, 10);
                return true;
            }
        }

        return false;
    }

    public static class FireAbility extends Buff {

        public int direction;
        private int[] curCells;

        HashSet<Integer> toCells = new HashSet<>();

        @Override
        public boolean act() {

            toCells.clear();

            if (curCells == null){
                curCells = new int[1];
                curCells[0] = target.pos;
                spreadFromCell( curCells[0] );

            } else {
                for (Integer c : curCells) {
                    if (FireBlob.volumeAt(c, FireBlob.class) > 0) spreadFromCell(c);
                }
            }

            for (Integer c : curCells){
                toCells.remove(c);
            }

            if (toCells.isEmpty()){
                detach();
            } else {
                curCells = new int[toCells.size()];
                int i = 0;
                for (Integer c : toCells){
                    GameScene.add(Blob.seed(c, 2, FireBlob.class));
                    curCells[i] = c;
                    i++;
                }
            }

            spend(TICK);
            return true;
        }

        private void spreadFromCell( int cell ){
            if (!Dungeon.level.solid[cell + PathFinder.CIRCLE8[left(direction)]]){
                toCells.add(cell + PathFinder.CIRCLE8[left(direction)]);
            }
            if (!Dungeon.level.solid[cell + PathFinder.CIRCLE8[direction]]){
                toCells.add(cell + PathFinder.CIRCLE8[direction]);
            }
            if (!Dungeon.level.solid[cell + PathFinder.CIRCLE8[right(direction)]]){
                toCells.add(cell + PathFinder.CIRCLE8[right(direction)]);
            }
        }

        private int left(int direction){
            return direction == 0 ? 7 : direction-1;
        }

        private int right(int direction){
            return direction == 7 ? 0 : direction+1;
        }

        private static final String DIRECTION = "direction";
        private static final String CUR_CELLS = "cur_cells";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put( DIRECTION, direction );
            bundle.put( CUR_CELLS, curCells );
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            direction = bundle.getInt( DIRECTION );
            curCells = bundle.getIntArray( CUR_CELLS );
        }

        public static class FireBlob extends Blob {

            {
                actPriority = BUFF_PRIO - 1;
                alwaysVisible = true;
            }

            @Override
            protected void evolve() {

                boolean observe = false;
                boolean burned = false;

                int cell;
                for (int i = area.left; i < area.right; i++){
                    for (int j = area.top; j < area.bottom; j++){
                        cell = i + j* Dungeon.level.width();
                        off[cell] = cur[cell] > 0 ? cur[cell] - 1 : 0;

                        if (off[cell] > 0) {
                            volume += off[cell];
                        }

                        if (cur[cell] > 0 && off[cell] == 0){

                            Char ch = Actor.findChar( cell );
                            if (ch != null && !ch.isImmune(Fire.class) && !(ch instanceof TenguHard)) {
                                Buff.affect( ch, Burning.class ).reignite( ch );
                            }

                            if (Dungeon.level.flamable[cell]){
                                Dungeon.level.destroy( cell );

                                observe = true;
                                GameScene.updateMap( cell );
                            }

                            burned = true;
                            CellEmitter.get(cell).start(FlameParticle.FACTORY, 0.03f, 10);
                        }
                    }
                }

                if (observe) {
                    Dungeon.observe();
                }

                if (burned){
                    Sample.INSTANCE.play(Assets.Sounds.BURNING);
                }
            }

            @Override
            public void use(BlobEmitter emitter) {
                super.use(emitter);

                emitter.pour( Speck.factory( Speck.STEAM ), 0.2f );
            }

            @Override
            public String tileDesc() {
                return Messages.get(this, "desc");
            }
        }
    }

    //*********************
    //***Shocker Ability***
    //*********************

    //returns true if shocker was thrown
    public static boolean throwBeamTower(final Char thrower, final Char target){

        int targetCell = -1;

        //Targets closest cell which is adjacent to target, and not adjacent to thrower or another shocker
        for (int i : GME.NEIGHBOURS20()){
            int cell = target.pos + i;
            if (Dungeon.level.distance(cell, thrower.pos) >= 2 && !Dungeon.level.solid[cell]){
                boolean validTarget = true;
                for (BeamTowerAbility s : thrower.buffs(BeamTowerAbility.class)){
                    if (Dungeon.level.distance(cell, s.towerPos) < 4){
                        validTarget = false;
                        break;
                    }
                }
                if (validTarget && Dungeon.level.trueDistance(cell, thrower.pos) < Dungeon.level.trueDistance(targetCell, thrower.pos)){
                    targetCell = cell;
                }
            }
        }

        if (targetCell == -1){
            return false;
        }

        final int finalTargetCell = targetCell;
        throwingChar = thrower;
        final BeamTowerAbility.BeamTowerItem item = new BeamTowerAbility.BeamTowerItem();
        thrower.sprite.zap(finalTargetCell);
        ((MissileSprite) thrower.sprite.parent.recycle(MissileSprite.class)).
                reset(thrower.sprite,
                        finalTargetCell,
                        item,
                        new Callback() {
                            @Override
                            public void call() {
                                item.onThrow(finalTargetCell);
                                thrower.next();
                            }
                        });
        return true;
    }

    public static class BeamTowerAbility extends Buff {

        public int towerPos;
        private int stateLoop = 0;

        private void beamProc(Ballistica b){
            for(int j: b.path){
                if(j==b.sourcePos) continue;
                Char ch = findChar(j);
                if(ch != null){
                    if(ch.alignment != Alignment.ENEMY){
                        ch.damage(Random.IntRange(6, 10), TenguHard.class);
                        Buff.affect(ch, Cripple.class, 2f);
                        if (ch == Dungeon.hero && !ch.isAlive()) {
                            Dungeon.fail(getClass());
                        }
                    }
                }
            }
        }

        @Override
        public boolean act() {
            PointF p = DungeonTilemap.raisedTileCenterToWorld(towerPos);
/*
            if(target instanceof Boss && target.HP <= target.HT * 2 /10) {
                if(stateLoop%3==0){
                    stateLoop++;
                }else if(stateLoop%3 == 1){
                    FloatingText.show(p.x, p.y, "*", 0xFF77FF);
                    stateLoop++;
                }else{
                    int[] tile = PathFinder.NEIGHBOURS8;
                    for(int i=0;i<8;++i){
                        Ballistica b = new Ballistica(towerPos, towerPos + tile[i], Ballistica.STOP_SOLID);
                        target.sprite.parent.add(new Beam.DeathRay(DungeonTilemap.raisedTileCenterToWorld(b.sourcePos), DungeonTilemap.raisedTileCenterToWorld(b.collisionPos)));
                        beamProc(b);
                    }
                }
            }
            else{

 */
                if (stateLoop == 1) {
                    stateLoop++;
                    FloatingText.show(p.x, p.y, "+", 0x5580FF);
                } else if (stateLoop == 2) {
                    int w = Dungeon.level.width();
                    int[] tile = {w, -w, 1, -1};
                    for (int i = 0; i < 4; ++i) {
                        Ballistica b = new Ballistica(towerPos, towerPos + tile[i], Ballistica.STOP_SOLID);
                        target.sprite.parent.add(new Beam.DeathRay(DungeonTilemap.raisedTileCenterToWorld(b.sourcePos), DungeonTilemap.raisedTileCenterToWorld(b.collisionPos)));
                        beamProc(b);
                    }
                    stateLoop++;
                } else if (stateLoop == 4) {
                    stateLoop++;
                    FloatingText.show(p.x, p.y, "x", 0xFF8055);
                } else if (stateLoop == 5) {
                    int w = Dungeon.level.width();
                    int[] tile = {w + 1, w - 1, -w + 1, -w - 1};
                    for (int i = 0; i < 4; ++i) {
                        Ballistica b = new Ballistica(towerPos, towerPos + tile[i], Ballistica.STOP_SOLID);
                        target.sprite.parent.add(new Beam.DeathRay(DungeonTilemap.raisedTileCenterToWorld(b.sourcePos), DungeonTilemap.raisedTileCenterToWorld(b.collisionPos)));
                        beamProc(b);
                    }
                    stateLoop = 0;
                } else if (stateLoop == 0 || stateLoop == 3) {
                    stateLoop++;
                } else {
                    stateLoop = 0;
                }
           // }

            spend(TICK);
            return true;
        }

        private static final String SHOCKER_POS = "shocker_pos";
        private static final String SHOCKING_ORDINALS = "shocking_ordinals";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put( SHOCKER_POS, towerPos);
            bundle.put(SHOCKING_ORDINALS, stateLoop);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            towerPos = bundle.getInt( SHOCKER_POS );
            stateLoop = bundle.getInt( SHOCKING_ORDINALS );
        }

        public static class BeamTowerItem extends Item {

            {
                dropsDownHeap = true;
                unique = true;

                image = ItemSpriteSheet.RETURN_BEACON;
            }

            @Override
            public boolean doPickUp( Hero hero, int pos ) {
                GLog.w( Messages.get(this, "cant_pickup") );
                return false;
            }

            @Override
            protected void onThrow(int cell) {
                super.onThrow(cell);
                if (throwingChar != null){
                    Buff.append(throwingChar, BeamTowerAbility.class).towerPos = cell;
                    throwingChar = null;
                } else {
                    Buff.append(curUser, BeamTowerAbility.class).towerPos = cell;
                }
            }
        }

    }

}
