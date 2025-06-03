package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.cave;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Brute;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.MobHard;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.HitBack;
import com.shatteredpixel.shatteredpixeldungeon.custom.visuals.MissileSpriteCustom;
import com.shatteredpixel.shatteredpixeldungeon.custom.visuals.effects.SpreadWave;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Bolas;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Tomahawk;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BruteSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

//should make a template for ai like this, or a ranged attack action class.
public class BruteH extends MobHard {
    {
        spriteClass = BruteSprite.class;

        HP = HT = 40;
        defenseSkill = 15;

        EXP = 10;
        maxLvl = 17;

        loot = Gold.class;
        lootChance = 0.5f;
    }

    protected int revived = 0;
    protected boolean raging = false;
    protected int thrownLeft = 2;
    protected boolean rangedAttack = false;

    @Override
    public boolean act(){
        if(buff(BruteRevive.class)!=null){
            spend(TICK);
            return true;
        }
        return super.act();
    }

    @Override
    public void die(Object cause){
        if(cause== Chasm.class) revived = 1;
        if(revived > 0){
            super.die(cause);
        }else{
            revive();
        }
    }

    protected void revive(){
        ++revived;
        HP = HT/2;
        Buff.affect(this, BruteRevive.class, 3f);
        Buff.affect(this, Barrier.class).setShield(65535);
        sprite.showStatus(CharSprite.NEGATIVE, "!!!");
        raging = false;
    }

    protected void enrage(){
        Buff.affect(this, BruteRage.class).setShield(revived>0 ? HT/2 : HT * 3 / 4);
        if (Dungeon.level.heroFOV[pos]) {
            sprite.showStatus( CharSprite.NEGATIVE, Messages.get(Brute.class, "enraged") );
        }
        spend( TICK );
        raging = true;
    }

    @Override
    public synchronized boolean isAlive() {
        if (HP > 0){
            return true;
        } else {
            if (!raging){
                enrage();
            }
            return !buffs(BruteRage.class).isEmpty();
        }
    }

    @Override
    public void storeInBundle(Bundle b){
        super.storeInBundle(b);
        b.put("revived", revived);
        b.put("isEnraged", raging);
        b.put("missileLeft", thrownLeft);
    }

    @Override
    public void restoreFromBundle(Bundle b){
        super.restoreFromBundle(b);
        revived = b.getInt("revived");
        raging = b.getBoolean("isEnraged");
        thrownLeft = b.getInt("missileLeft");
    }

    @Override
    public int damageRoll() {
        int dmg =  (buff(BruteRage.class) != null ?
                Random.NormalIntRange( 15, 40 ) :
                Random.NormalIntRange( 5, 25 ));
        return Math.round(dmg*(rangedAttack?0.75f:1f)*(revived>0?1.2f:1f));
    }

    @Override
    public int attackSkill( Char target ) {
        return 20*(rangedAttack?2:1);
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 8);
    }

    @Override
    protected boolean canAttack(Char enemy){
        if(thrownLeft>0 && revived>0){
            Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);
            if(!Dungeon.level.adjacent( pos, enemy.pos ) && attack.collisionPos == enemy.pos){
                rangedAttack = true;
                return true;
            }
        }
        rangedAttack  = false;
        return super.canAttack(enemy);
    }

    @Override
    protected boolean doAttack(Char enemy){
        if(rangedAttack && thrownLeft>0){
            thrownLeft--;
            spend( attackDelay() );

            Actor.addDelayed(new Actor() {
                                 @Override
                                 public boolean act(){
                                     final Actor toRemove = this;
                                     ((MissileSpriteCustom)sprite.parent.recycle(MissileSpriteCustom.class)).reset(
                                             pos, enemy.pos, thrownLeft>=1?new Bolas():new Tomahawk(), 1.25f, 0.8f,
                                             new Callback() {
                                                 @Override
                                                 public void call() {
                                                     onAttackComplete();
                                                     Actor.remove(toRemove);
                                                     toRemove.next();
                                                 }
                                             }
                                     );
                                     return false;
                                 }
                             },
                    -1);
            next();
            return true;
        }
        return super.doAttack(enemy);
    }

    @Override
    public int attackProc(Char enemy, int damage){
        if(rangedAttack){
            if(thrownLeft >=1){
                Buff.affect(enemy, Cripple.class, Cripple.DURATION);
            }else if(thrownLeft == 0){
                Buff.affect(enemy, Bleeding.class).set(damage*0.8f);
            }
        }
        return super.attackProc(enemy, damage);
    }

    @Override
    public void rollToDropLoot(){
        if (Dungeon.hero.lvl <= maxLvl + 2){
            float chance = 0.02f;
            chance *= RingOfWealth.dropChanceMultiplier( Dungeon.hero );
            chance = Math.min(0.03f, chance);
            if(Random.Float()<chance){
                Ankh a = new Ankh();
                a.bless();
                Dungeon.level.drop(a, pos).sprite.drop();
            }
        }

        super.rollToDropLoot();

    }


    public static class BruteRage extends ShieldBuff {

        {
            type = buffType.POSITIVE;
        }

        @Override
        public boolean act() {

            if (target.HP > 0){
                detach();
                return true;
            }

            absorbDamage( 2 );

            if (shielding() <= 0){
                target.die(null);
            }

            spend( TICK );

            return true;
        }

        @Override
        public int icon () {
            return BuffIndicator.FURY;
        }

        @Override
        public String toString () {
            return Messages.get(Brute.BruteRage.class, "name");
        }

        @Override
        public String desc() {
            return Messages.get(Brute.BruteRage.class, "desc", shielding());
        }

        {
            immunities.add(Terror.class);
        }
    }

    public static class BruteRevive extends FlavourBuff{

        @Override
        public void detach(){

            Actor.addDelayed(new Actor() {
                                 @Override
                                 protected boolean act() {
                                     final Actor toRemove = this;
                                     SpreadWave.blast(target.sprite.center(), 3.5f, 0.25f, 0xA030A0, new Callback() {
                                         @Override
                                         public void call() {
                                             for (Char ch : Actor.chars()) {
                                                 if (ch.alignment != Alignment.ENEMY) {
                                                     float dist = Dungeon.level.trueDistance(ch.pos, target.pos);
                                                     if (dist < 3.5f) {
                                                         int back = Math.round((3.5f - dist) * (3.5f - dist) / 2f + (4.5f - dist));
                                                         WandOfBlastWave.throwChar(ch, HitBack.hitBack(target, ch), back, true, false, BruteH.class);
                                                     }
                                                 }
                                             }
                                             Buff.detach(target, Barrier.class);
                                             Actor.remove(toRemove);
                                             toRemove.next();

                                         }
                                     });
                                     return false;
                                 }
                             }, -1);

            super.detach();
            Sample.INSTANCE.play(Assets.Sounds.BLAST);
            Camera.main.shake(3f, 1f);
        }
    }

}
