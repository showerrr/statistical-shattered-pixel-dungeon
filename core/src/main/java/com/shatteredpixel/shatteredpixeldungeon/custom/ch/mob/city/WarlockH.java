package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.city;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Warlock;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.MobHard;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WarlockSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import static com.shatteredpixel.shatteredpixeldungeon.items.Item.updateQuickslot;

public class WarlockH extends MobHard implements Callback {
    {
        spriteClass = WarlockHSprite.class;

        HP = HT = 70;
        defenseSkill = 18;

        EXP = 11;
        maxLvl = 21;

        loot = Generator.Category.POTION;
        lootChance = 0.5f;

        properties.add(Property.UNDEAD);
    }
    private static final float TIME_TO_ZAP = 1f;

    public void onZapComplete() {
        zapThis();
        next();
    }

    @Override
    protected boolean doAttack( Char enemy ) {

        if (Dungeon.level.adjacent( pos, enemy.pos )) {

            return super.doAttack( enemy );

        } else {

            if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
                sprite.zap( enemy.pos );
                return false;
            } else {
                zapThis();
                return true;
            }
        }
    }

    private void zapThis() {
        spend( TIME_TO_ZAP );

        if (hit( this, enemy, true )) {
            //TODO would be nice for this to work on ghost/statues too
            if (enemy == Dungeon.hero && Random.Int( 2 ) == 0) {
                Buff.prolong(enemy, Degrade.class, Degrade.DURATION);
                Sample.INSTANCE.play(Assets.Sounds.DEBUFF);
            }

            if (enemy == Dungeon.hero && Random.Int( 2 ) == 0) {
                int drained = 0;
                for(Item i: Dungeon.hero.belongings){
                    if(i instanceof Wand){
                        if(Random.Int(50)==0) {((Wand) i).curCharges=-16;drained+=10;}
                        else{((Wand) i).curCharges -= 1;++drained;}
                    }
                }
                Sample.INSTANCE.play(Assets.Sounds.DEBUFF);
                enemy.sprite.emitter().burst( ShadowParticle.CURSE, 3+drained );
                updateQuickslot();
            }

            int dmg = Random.NormalIntRange( 12, 18 );
            enemy.damage( dmg, new DarkBolt() );

            if (enemy == Dungeon.hero && !enemy.isAlive()) {
                Dungeon.fail( getClass() );
                GLog.n( Messages.get(this, "bolt_kill") );
            }
        } else {
            enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
        }
    }

    private int hitsToDegrade = 0;
    @Override
    public int attackProc(Char enemy, int damage){

        ++hitsToDegrade;

        if(Random.Int(hitsToDegrade)>3 && enemy ==Dungeon.hero){

            Buff.prolong(enemy, Degrade.class, Degrade.DURATION/2f);
            Sample.INSTANCE.play(Assets.Sounds.DEBUFF);

            int drained = 0;
            for(Item i: Dungeon.hero.belongings){
                if(i instanceof Wand){
                    ((Wand) i).curCharges -= 1;
                    ++drained;
                }
            }
            enemy.sprite.emitter().burst( ShadowParticle.CURSE, 3+drained );
            updateQuickslot();

            hitsToDegrade = 0;
        }
        return super.attackProc(enemy, damage);
    }

    @Override
    public void storeInBundle(Bundle b){
        super.storeInBundle(b);
        b.put("hitsToDegrade", hitsToDegrade);
    }

    @Override
    public void restoreFromBundle(Bundle b){
        super.restoreFromBundle(b);
        hitsToDegrade = b.getInt("hitsToDegrade");
    }

    @Override
    public void rollToDropLoot(){
        if (Dungeon.hero.lvl <= maxLvl + 2){
            float chance = 0.2f;
            chance *= RingOfWealth.dropChanceMultiplier( Dungeon.hero );
            chance = Math.min(0.4f, chance);
            if(Random.Float()<chance){
                Dungeon.level.drop(new ScrollOfRecharging(), pos).sprite.drop();
            }
        }

        super.rollToDropLoot();

    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 12, 18 );
    }

    @Override
    public int attackSkill( Char target ) {
        return 25;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 8);
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
    }

    //used so resistances can differentiate between melee and magical attacks
    public static class DarkBolt{}

    @Override
    public void call() {
        next();
    }

    @Override
    public Item createLoot(){

        // 1/6 chance for healing, scaling to 0 over 8 drops
        if (Random.Int(3) == 0 && Random.Int(8) > Dungeon.LimitedDrops.WARLOCK_HP.count ){
            Dungeon.LimitedDrops.WARLOCK_HP.count++;
            return new PotionOfHealing();
        } else {
            Item i = Generator.randomUsingDefaults(Generator.Category.POTION);
            int healingTried = 0;
            while (i instanceof PotionOfHealing){
                healingTried++;
                i = Generator.randomUsingDefaults(Generator.Category.POTION);
            }

            //return the attempted healing potion drops to the pool
            if (healingTried > 0){
                for (int j = 0; j < Generator.Category.POTION.classes.length; j++){
                    if (Generator.Category.POTION.classes[j] == PotionOfHealing.class){
                        Generator.Category.POTION.probs[j] += healingTried;
                    }
                }
            }

            return i;
        }

    }

    public static class WarlockHSprite extends MobSprite {

        public WarlockHSprite() {
            super();

            texture( Assets.Sprites.WARLOCK );

            TextureFilm frames = new TextureFilm( texture, 12, 15 );

            idle = new MovieClip.Animation( 2, true );
            idle.frames( frames, 0, 0, 0, 1, 0, 0, 1, 1 );

            run = new MovieClip.Animation( 15, true );
            run.frames( frames, 0, 2, 3, 4 );

            attack = new MovieClip.Animation( 12, false );
            attack.frames( frames, 0, 5, 6 );

            zap = attack.clone();

            die = new MovieClip.Animation( 15, false );
            die.frames( frames, 0, 7, 8, 8, 9, 10 );

            play( idle );
        }

        public void zap( int cell ) {

            turnTo( ch.pos , cell );
            play( zap );

            MagicMissile.boltFromChar( parent,
                    MagicMissile.SHADOW,
                    this,
                    cell,
                    new Callback() {
                        @Override
                        public void call() {
                            ((WarlockH)ch).onZapComplete();
                        }
                    } );
            Sample.INSTANCE.play( Assets.Sounds.ZAP );
        }

        @Override
        public void onComplete( MovieClip.Animation anim ) {
            if (anim == zap) {
                idle();
            }
            super.onComplete( anim );
        }
    }

}
