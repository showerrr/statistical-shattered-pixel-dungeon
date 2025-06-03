package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.hall;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.MobHard;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
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
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.Collections;
import java.util.List;

public class  EyeH extends MobHard {

    {
        spriteClass = EyeHSprite.class;

        HP = HT = 112;
        defenseSkill = 24;
        viewDistance = Light.DISTANCE + 1;

        EXP = 14;
        maxLvl = 27;

        flying = true;

        HUNTING = new Hunting();

        loot = new Dewdrop();
        lootChance = 1f;

        properties.add(Property.DEMONIC);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(20, 30);
    }

    @Override
    public int attackSkill( Char target ) {
        return 10000000;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 10);
    }

    protected Ballistica beam;
    protected int beamTarget = -1;
    protected int beamCooldown;
    public boolean beamCharged;

    @Override
    protected boolean canAttack( Char enemy ) {

        if (beamCooldown == 0) {
            Ballistica aim = new Ballistica(pos, enemy.pos, Ballistica.STOP_SOLID);

            if (enemy.invisible == 0 && !isCharmedBy(enemy) && fieldOfView[enemy.pos] && aim.subPath(1, aim.dist).contains(enemy.pos)){
                beam = aim;
                beamTarget = aim.collisionPos;
                return true;
            } else
                //if the beam is charged, it has to attack, will aim at previous location of target.
                return beamCharged;
        } else
            return super.canAttack(enemy);
    }

    @Override
    protected boolean act() {
        if (beamCharged && state != HUNTING){
            beamCharged = false;
            sprite.idle();
        }
        if (beam == null && beamTarget != -1) {
            beam = new Ballistica(pos, beamTarget, Ballistica.STOP_SOLID);
            sprite.turnTo(pos, beamTarget);
        }
        if (beamCooldown > 0)
            beamCooldown--;
        return super.act();
    }

    @Override
    protected boolean doAttack( Char enemy ) {

        if (beamCooldown > 0) {
            return super.doAttack(enemy);
        } else if (!beamCharged){
            ((EyeHSprite)sprite).charge( enemy.pos );
            spend( attackDelay()*2f );
            beamCharged = true;
            return true;
        } else {

            spend( attackDelay() );

            beam = new Ballistica(pos, beamTarget, Ballistica.STOP_SOLID);
            if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[beam.collisionPos] ) {
                sprite.zap( beam.collisionPos );
                return false;
            } else {
                sprite.idle();
                deathGaze();
                return true;
            }
        }

    }

    @Override
    public void damage(int dmg, Object src) {
        if (beamCharged) dmg /= 4;
        super.damage(dmg, src);
    }

    //used so resistances can differentiate between melee and magical attacks
    public static class DeathGaze{}

    public void deathGaze(){
        if (!beamCharged || beamCooldown > 0 || beam == null)
            return;

        beamCharged = false;
        beamCooldown = Random.IntRange(3, 5);

        boolean terrainAffected = false;

        for (int pos : beam.subPath(1, beam.dist)) {

            if (Dungeon.level.flamable[pos]) {

                Dungeon.level.destroy( pos );
                GameScene.updateMap( pos );
                terrainAffected = true;

            }

            Char ch = Actor.findChar( pos );
            if (ch == null) {
                continue;
            }

            if (hit( this, ch, true )) {

                if (ch == Dungeon.hero){
                    disintergrate((Hero) ch);
                    disintergrate((Hero) ch);
                }

                ch.damage( Random.NormalIntRange( 25, 40 ), new DeathGaze() );

                if (Dungeon.level.heroFOV[pos]) {
                    ch.sprite.flash();
                    CellEmitter.center( pos ).burst( PurpleParticle.BURST, Random.IntRange( 1, 2 ) );
                }

                if (!ch.isAlive() && ch == Dungeon.hero) {
                    Dungeon.fail( getClass() );
                    GLog.n( M.L(this, "deathgaze_kill") );
                }
            } else {
                ch.sprite.showStatus( CharSprite.NEUTRAL,  ch.defenseVerb() );
            }
        }

        createImage();

        if (terrainAffected) {
            Dungeon.observe();
        }

        beam = null;
        beamTarget = -1;
    }

    protected void disintergrate(Hero hero){
        Item item = hero.belongings.randomUnequipped();
        Bag bag = hero.belongings.backpack;
        //bags do not protect
        int tries = 0;
        while(++tries < 5) {
            if (item instanceof Bag) {
                bag = (Bag) item;
                item = Random.element(bag.items);
            }
            if (item == null || item.level() > 0 || item.unique) continue;

            if (!item.stackable) {
                item.detachAll(bag);
                GLog.w(M.L(this, "one", item.name()));
                break;
            } else {
                int n = Random.NormalIntRange(1, (item.quantity() + 1) / 2);
                for (int i = 1; i <= n; i++)
                    item.detach(bag);
                GLog.w(M.L(this, "some", item.name()));
                break;
            }
        }
    }

    protected void createImage(){
        int p = -1;
        List<Integer> route = beam.subPath(1, beam.dist);
        Collections.reverse(route);

        for(int i: route){
            if(findChar(i)==null&&!Dungeon.level.solid[i]){
                p = i; break;
            }
        }
        if(p != -1) {
            EyeImage image = new EyeImage();
            GameScene.add(image, 2f);
            ScrollOfTeleportation.appear(image, p);
            image.state = image.HUNTING;
        }
    }

    //generates an average of 1 dew, 0.25 seeds, and 0.25 stones
    @Override
    public Item createLoot() {
        Item loot;
        switch(Random.Int(4)){
            case 0: case 1: default:
                loot = new Dewdrop();
                int ofs;
                do {
                    ofs = PathFinder.NEIGHBOURS8[Random.Int(8)];
                } while (Dungeon.level.solid[pos + ofs] && !Dungeon.level.passable[pos + ofs]);
                Dungeon.level.drop( new Dewdrop(), pos + ofs ).sprite.drop( pos );
                break;
            case 2:
                loot = Generator.random(Generator.Category.SEED);
                break;
            case 3:
                loot = Generator.random(Generator.Category.STONE);
                break;
        }
        return loot;
    }

    @Override
    public void rollToDropLoot(){
        if (Dungeon.hero.lvl <= maxLvl + 2){
            float chance = 0.04f;
            chance *= RingOfWealth.dropChanceMultiplier( Dungeon.hero );
            chance = Math.min(0.1f, chance);
            if(Random.Float()<chance){
                WandOfDisintegration w=new WandOfDisintegration();
                w.level(Random.chances(new float[]{0.3f - 2f*chance, 0.4f + chance, 0.2f+chance/2f, 0.1f + chance/2f}));
                Dungeon.level.drop(w, pos).sprite.drop();
            }
        }

        super.rollToDropLoot();

    }

    private static final String BEAM_TARGET     = "beamTarget";
    private static final String BEAM_COOLDOWN   = "beamCooldown";
    private static final String BEAM_CHARGED    = "beamCharged";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put( BEAM_TARGET, beamTarget);
        bundle.put( BEAM_COOLDOWN, beamCooldown );
        bundle.put( BEAM_CHARGED, beamCharged );
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(BEAM_TARGET))
            beamTarget = bundle.getInt(BEAM_TARGET);
        beamCooldown = bundle.getInt(BEAM_COOLDOWN);
        beamCharged = bundle.getBoolean(BEAM_CHARGED);
    }

    {
        resistances.add( WandOfDisintegration.class );
    }

    {
        immunities.add( Terror.class );
        immunities.add( DeathGaze.class );
    }

    protected void summonRipper(){
        RipperH rh = new RipperH();
        rh.pos = Dungeon.level.randomRespawnCell(rh);
        if(!Dungeon.level.insideMap(rh.pos)){
            rh.pos = this.pos;
        }
        GameScene.add(rh);
    }

    @Override
    public void die(Object cause){
        super.die(cause);
        summonRipper();
    }


    private class Hunting extends Mob.Hunting{
        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            //even if enemy isn't seen, attack them if the beam is charged
            if (beamCharged && enemy != null && canAttack(enemy)) {
                enemySeen = enemyInFOV;
                return doAttack(enemy);
            }
            return super.act(enemyInFOV, justAlerted);
        }
    }


    public static class EyeHSprite extends MobSprite {

        private int zapPos;

        private Animation charging;
        private Emitter chargeParticles;

        public EyeHSprite() {
            super();

            texture( Assets.Sprites.EYE );

            TextureFilm frames = new TextureFilm( texture, 16, 18 );

            idle = new Animation( 8, true );
            idle.frames( frames, 0, 1, 2 );

            charging = new Animation( 12, true);
            charging.frames( frames, 3, 4 );

            run = new Animation( 12, true );
            run.frames( frames, 5, 6 );

            attack = new Animation( 8, false );
            attack.frames( frames, 4, 3 );
            zap = attack.clone();

            die = new Animation( 8, false );
            die.frames( frames, 7, 8, 9 );

            play( idle );
        }

        @Override
        public void link(Char ch) {
            super.link(ch);

            chargeParticles = centerEmitter();
            chargeParticles.autoKill = false;
            chargeParticles.pour(MagicMissile.MagicParticle.ATTRACTING, 0.05f);
            chargeParticles.on = false;

            if (((EyeH)ch).beamCharged) play(charging);
        }

        @Override
        public void update() {
            super.update();
            if (chargeParticles != null){
                chargeParticles.pos( center() );
                chargeParticles.visible = visible;
            }
        }

        @Override
        public void die() {
            super.die();
            if (chargeParticles != null){
                chargeParticles.on = false;
            }
        }

        @Override
        public void kill() {
            super.kill();
            if (chargeParticles != null){
                chargeParticles.killAndErase();
            }
        }

        public void charge( int pos ){
            turnTo(ch.pos, pos);
            play(charging);
            if (visible) Sample.INSTANCE.play( Assets.Sounds.CHARGEUP );
        }

        @Override
        public void play(Animation anim) {
            if (chargeParticles != null) chargeParticles.on = anim == charging;
            super.play(anim);
        }

        @Override
        public void zap( int pos ) {
            zapPos = pos;
            super.zap( pos );
        }

        @Override
        public void onComplete( Animation anim ) {
            super.onComplete( anim );

            if (anim == zap) {
                idle();
                if (Actor.findChar(zapPos) != null){
                    parent.add(new Beam.DeathRay(center(), Actor.findChar(zapPos).sprite.center()));
                } else {
                    parent.add(new Beam.DeathRay(center(), DungeonTilemap.raisedTileCenterToWorld(zapPos)));
                }
                ((EyeH)ch).deathGaze();
                ch.next();
            } else if (anim == die){
                chargeParticles.killAndErase();
            }
        }
    }

}
