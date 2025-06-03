package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.cave;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.MobHard;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PoisonParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorrosion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfPrismaticLight;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public abstract class ShamanH extends MobHard {

    {
        HP = HT = 35;
        defenseSkill = 15;

        EXP = 8;
        maxLvl = 16;

        loot = Generator.Category.WAND;
        lootChance = 0.1f; //initially, see rollToDropLoot
    }
    //avoid interfering with existing zaps.
    protected int zapCate = 0;

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 5, 10 );
    }

    @Override
    public int attackSkill( Char target ) {
        return Dungeon.level.adjacent(pos, target.pos)?18:1000;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 6);
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
    }

    @Override
    public void rollToDropLoot() {
        //each drop makes future drops 1/3 as likely
        // so loot chance looks like: 1/33, 1/100, 1/300, 1/900, etc.
        lootChance *= Math.pow(1/3f, Dungeon.LimitedDrops.SHAMAN_WAND.count);
        super.rollToDropLoot();
    }

    @Override
    public Item createLoot() {
        Dungeon.LimitedDrops.SHAMAN_WAND.count++;
        return super.createLoot();
    }

    protected boolean doAttack(Char enemy ) {

        if (Dungeon.level.adjacent( pos, enemy.pos )) {

            return super.doAttack( enemy );

        } else {
            zapCate = decideWhichToZap();

            if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
                sprite.zap( enemy.pos );
                executeFx();
                return false;
            } else {
                zap();
                return true;
            }
        }
    }

    public static class ShamanMagic{}

    protected void zap() {
        spend(1f);

        if (hit(this, enemy, true)) {

            int dmg = executeProc();
            enemy.damage(dmg, new ShamanMagic());

            if (!enemy.isAlive() && enemy == Dungeon.hero) {
                Dungeon.fail(getClass());
                GLog.n(Messages.get(this, "bolt_kill"));
            }
        } else {
            enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
        }
    }

    public void onZapComplete() {
        zap();
        next();
    }

    @Override
    public String description() {
        return super.description() + "\n\n" + Messages.get(this, "spell_desc");
    }


    //a manager for different zaps.

    public int executeProc(){
        switch (zapCate){
            case 0: default:
                return nerfZapProc(this, enemy.pos);
            case 1:
                return nerfBlastProc(this, enemy.pos);
            case 2:
                return iceZapProc(this, enemy.pos);
            case 3:
                return lightingZapProc(this, enemy.pos);
            case 4:
                return poisonZapProc(this, enemy.pos);
            case 5:
                return teleportZapProc(this, enemy.pos);

        }
    }
    //proc is already contained in fx.
    public void executeFx(){
        switch (zapCate){
            case 0: default:
                nerfZapFx(this, enemy.pos);
                break;
            case 1:
                nerfBlastFx(this, enemy.pos);
                break;
            case 2:
                iceZapFx(this, enemy.pos);
                break;
            case 3:
                lightingZapFx(this, enemy.pos);
                break;
            case 4:
                poisonZapFx(this,enemy.pos);
                break;
            case 5:
                teleportZapFx(this, enemy.pos);
        }
    }

    //sets zapCate
    protected abstract int decideWhichToZap();

    //lib for different zaps.
    //no need to return damage.
    public void nerfZapFx(Char from, int target){
        MagicMissile.boltFromChar(
                from.sprite.parent, MagicMissile.SHADOW, from.sprite, target, new Callback() {
                    @Override
                    public void call() {
                        onZapComplete();
                    }
                }
        );
        Sample.INSTANCE.play( Assets.Sounds.ZAP );
    }
    //return damage
    public int nerfZapProc(Char from, int target){
        Char to = findChar(target);
        if(to==null) return 0;

        ArrayList<Class<? extends FlavourBuff>> toDebuff = new ArrayList<>(5);
        toDebuff.add(Hex.class); toDebuff.add(Vulnerable.class); toDebuff.add(Weakness.class);
        toDebuff.add(Blindness.class); toDebuff.add(Cripple.class);

        Buff.affect(to, toDebuff.get(Random.Int(toDebuff.size())), 12f);

        return Random.NormalIntRange(5, 11);
    }

    public void nerfBlastFx(Char from, int target){
        MagicMissile.boltFromChar(
                from.sprite.parent, MagicMissile.ELMO, from.sprite, target, new Callback() {
                    @Override
                    public void call() {
                        onZapComplete();
                        final Char ch = findChar(target);
                        if(ch!=null){
                            ch.sprite.emitter().start( ShadowParticle.UP, 0.05f, 10 );
                        }
                    }
                }
        );
        Sample.INSTANCE.play( Assets.Sounds.ZAP );
    }

    public int nerfBlastProc(Char from, int target){
        Char to = findChar(target);
        if(to==null) return 0;

        ArrayList<Class<? extends Buff>> toBlast = new ArrayList<>(5);
        toBlast.add(Hex.class); toBlast.add(Vulnerable.class); toBlast.add(Weakness.class);
        toBlast.add(Blindness.class); toBlast.add(Cripple.class);

        int detached = 0;
        for(Class<? extends Buff> b: toBlast){
            for(Buff hasBuff: to.buffs()){
                if(hasBuff.getClass() == b){
                    Buff.detach(to, b);
                    detached++;
                    break;
                }
            }
        }

        return Math.round(Random.NormalIntRange(6, 12)*(1f+detached*0.5f));
    }

    public void iceZapFx(Char from, int target){
        MagicMissile.boltFromChar(
                from.sprite.parent, MagicMissile.FROST, from.sprite, target, new Callback() {
                    @Override
                    public void call() {
                        onZapComplete();
                        final Char ch = findChar(target);
                        if(ch!=null){
                            ch.sprite.burst( 0xFF99CCFF, 4 );
                        }
                    }
                }
        );
        Sample.INSTANCE.play( Assets.Sounds.ZAP );
    }

    public int iceZapProc(Char from, int target){
        Char to = findChar(target);
        if(to==null) return 0;

        Chill chill = to.buff(Chill.class);
        float multiplier = 1f;
        if(chill != null){
            multiplier *= 1f + 0.5f*chill.cooldown()/(chill.cooldown() + 3f);
        }
        Buff.affect(to, Chill.class, Dungeon.level.water[target]?5f:3f);
        multiplier *= Dungeon.level.water[target]? 1.35f:1f;
        return Math.round(multiplier*Random.NormalIntRange(6,12));
    }

    public void lightingZapFx(Char from, int target){
        from.sprite.parent.add(new Lightning(DungeonTilemap.raisedTileCenterToWorld(from.pos), DungeonTilemap.raisedTileCenterToWorld(target), new Callback() {
            @Override
            public void call() {
                final Char ch = findChar(target);
                if(ch!=null){
                    ch.sprite.centerEmitter().burst( SparkParticle.FACTORY, 8 );
                }
                onZapComplete();
            }
        }));
        if(findChar(target) == Dungeon.hero) {
            Camera.main.shake(2, 0.3f);
        }
        Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
    }

    public int lightingZapProc(Char from, int target){
        return Math.round(Random.Int(8, 14)*(Dungeon.level.water[target]?1.6f:1f));
    }

    public void poisonZapFx(Char from, int target){
        MagicMissile.boltFromChar(
                from.sprite.parent, MagicMissile.CORROSION, from.sprite, target, new Callback() {
                    @Override
                    public void call() {
                        onZapComplete();
                        final Char ch = findChar(target);
                        if(ch!=null){
                            ch.sprite.centerEmitter().burst( PoisonParticle.SPLASH, 3 );
                        }
                    }
                }
        );
        Sample.INSTANCE.play( Assets.Sounds.ZAP );
    }

    public int poisonZapProc(Char from, int target){
        Char to = findChar(target);
        if(to==null) return 0;
        Buff.affect(to, Poison.class).extend(6f);
        return Random.NormalIntRange(1, 2);
    }

    public void teleportZapFx(Char from, int target){
        MagicMissile.boltFromChar(
                from.sprite.parent, MagicMissile.BEACON, from.sprite, target, new Callback() {
                    @Override
                    public void call() {
                        onZapComplete();
                        final Char ch = findChar(target);
                        if(ch!=null){
                            ScrollOfTeleportation.teleportChar(ch);
                        }
                    }
                }
        );
        Sample.INSTANCE.play( Assets.Sounds.ZAP );
    }

    public int teleportZapProc(Char from, int target){
        return 0;
    }

    public static Class<? extends ShamanH> random(){
        float seed = Random.Float();
        if(seed<0.35f) return ControlShaman.class;
        else if(seed<0.7f) return ElementalShaman.class;
        else return TrickShaman.class;
    }

    public static class ControlShaman extends ShamanH{
        {
            spriteClass = ShamanHSprite.Purple.class;
        }

        @Override
        protected int decideWhichToZap() {
            if(enemy == null){return 0;}
            ArrayList<Class<? extends Buff>> toBlast = new ArrayList<>(5);
            toBlast.add(Hex.class); toBlast.add(Vulnerable.class); toBlast.add(Weakness.class);
            toBlast.add(Blindness.class); toBlast.add(Cripple.class);
            int has = 0;
            for(Class<? extends Buff> b: toBlast){
                for(Buff hasBuff: enemy.buffs()){
                    if(hasBuff.getClass() == b){
                        ++has;
                        break;
                    }
                }
            }
            return Random.chances(new float[]{9f, has*4f});
        }

        @Override
        public void rollToDropLoot(){
            if (Dungeon.hero.lvl <= maxLvl + 2){
                float chance = 0.02f;
                chance *= RingOfWealth.dropChanceMultiplier( Dungeon.hero );
                chance = Math.min(0.05f, chance);
                if(Random.Float()<chance){
                    WandOfPrismaticLight w = new WandOfPrismaticLight();
                    w.level(Random.chances(new float[]{0.55f - 2f*chance, 0.3f + chance, 0.15f+chance}));
                    Dungeon.level.drop(w, pos).sprite.drop();
                }
            }

            super.rollToDropLoot();

        }
    }

    public static class ElementalShaman extends ShamanH{
        {
            spriteClass = ShamanHSprite.Red.class;
        }

        private int ice = 0;
        private int lightning = 0;

        @Override
        protected int decideWhichToZap() {
            int zap =  Random.chances(new float[]{1.3f*lightning +1f, 0.9f*ice+1f}) + 2;
            if(zap==2) ++ice;
            if(zap==3) ++lightning;
            if(ice*lightning > 24){ice = 0; lightning = 0;}
            return zap;
        }

        @Override
        public void rollToDropLoot(){
            if (Dungeon.hero.lvl <= maxLvl + 2){
                float chance = 0.02f;
                chance *= RingOfWealth.dropChanceMultiplier( Dungeon.hero );
                chance = Math.min(0.05f, chance);
                if(Random.Float()<chance){
                    WandOfFrost w = new WandOfFrost();
                    w.level(Random.chances(new float[]{0.55f - 2f*chance, 0.3f + chance, 0.15f+chance}));
                    Dungeon.level.drop(w, pos).sprite.drop();
                }
            }

            super.rollToDropLoot();

        }
    }

    public static class TrickShaman extends ShamanH{
        {
            spriteClass = ShamanHSprite.Blue.class;
        }
        private int poisonZap = 0;

        @Override
        protected int decideWhichToZap() {
            int zap = Random.chances(new float[]{5f, 10f*(Math.max(poisonZap-2, 0))}) + 4;
            ++poisonZap;
            if(zap==5) poisonZap=0;
            return zap;
        }

        @Override
        public void rollToDropLoot(){
            if (Dungeon.hero.lvl <= maxLvl + 2){
                float chance = 0.02f;
                chance *= RingOfWealth.dropChanceMultiplier( Dungeon.hero );
                chance = Math.min(0.05f, chance);
                if(Random.Float()<chance){
                    WandOfCorrosion w = new WandOfCorrosion();
                    w.level(Random.chances(new float[]{0.55f - 2f*chance, 0.3f + chance, 0.15f+chance}));
                    Dungeon.level.drop(w, pos).sprite.drop();
                }
            }
            super.rollToDropLoot();
        }
    }

    public static abstract class ShamanHSprite extends MobSprite {
        public ShamanHSprite() {
            super();

            int c = texOffset();

            texture( Assets.Sprites.SHAMAN );

            TextureFilm frames = new TextureFilm( texture, 12, 15 );

            idle = new Animation( 2, true );
            idle.frames( frames, c+0, c+0, c+0, c+1, c+0, c+0, c+1, c+1 );

            run = new Animation( 12, true );
            run.frames( frames, c+4, c+5, c+6, c+7 );

            attack = new Animation( 12, false );
            attack.frames( frames, c+2, c+3, c+0 );

            zap = attack.clone();

            die = new Animation( 12, false );
            die.frames( frames, c+8, c+9, c+10 );

            play( idle );
        }

        public void zap( int cell ) {

            turnTo( ch.pos , cell );
            play( zap );

        }

        protected abstract int texOffset();

        public static class Red extends ShamanHSprite {
            @Override
            protected int texOffset() {
                return 0;
            }
        }

        public static class Blue extends ShamanHSprite {
            @Override
            protected int texOffset() {
                return 21;
            }
        }

        public static class Purple extends ShamanHSprite {
            @Override
            protected int texOffset() {
                return 42;
            }
        }
    }
}
