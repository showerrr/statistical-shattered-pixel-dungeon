package com.shatteredpixel.shatteredpixeldungeon.custom.ch.boss;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BlobImmunity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Monk;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Warlock;
import com.shatteredpixel.shatteredpixeldungeon.custom.buffs.DeathCounter;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.timing.VirtualActor;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GhoulSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MonkSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WarlockSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class HDKSummon {
    public static class HDKStatue extends Mob{
        {
            HT = HP = 256;
            EXP = 0;
            maxLvl = -9999;
            defenseSkill = 0;
            viewDistance = 0;

            properties.add(Property.INORGANIC);
            properties.add(Property.IMMOVABLE);
            properties.add(Property.BOSS);

            spriteClass = StatueSprite.class;
        }

        @Override
        protected boolean act() {
            spend(TICK);
            return true;
        }

        @Override
        public int drRoll() {
            return Random.Int(20, 32);
        }

    }

    public static class HDKStatueP1 extends HDKStatue{
            @Override
            public void die(Object cause) {
            super.die(cause);
            boolean contain = false;
            for(Mob m: Dungeon.level.mobs.toArray(new Mob[0])){
                if(m instanceof HDKSummon.HDKStatue){
                    contain = true;
                    break;
                }
            }
            if(!contain){
                for(Mob m: Dungeon.level.mobs.toArray(new Mob[0])){
                    if(m instanceof NewHardDK){
                        Buff.detach(m, HDKBuff.NeutralBarrier.class);
                    }
                }
            }
        }
    }

    public static class HDKStatueP3 extends HDKStatue {
        @Override
        public void die(Object cause) {
            if(cause == DeathCounter.class){
                VirtualActor.delay(2f);
                MagicMissile.boltFromChar(
                    this.sprite.parent, MagicMissile.ELMO, this.sprite, Dungeon.hero.pos, new Callback() {
                        @Override
                        public void call() {
                            final Char ch = findChar(Dungeon.hero.pos);
                            if(ch!=null){
                                ch.sprite.showStatus(CharSprite.NEGATIVE, String.valueOf(ch.HP));
                                ch.HP = 0;
                                ch.die(NewHardDK.class);
                                if(ch == Dungeon.hero && !ch.isAlive()){
                                    GameScene.flash(0x101010, false);
                                    Dungeon.fail(NewHardDK.class);
                                    GLog.n( M.L(NewHardDK.class, "curse_kill") );
                                }
                            }
                        }
                    }
                );
                Sample.INSTANCE.play( Assets.Sounds.ZAP );
            }
            super.die(cause);
        }
    }

    public static class HDKGhoul extends Mob{
        {
            spriteClass = GhoulSprite.class;

            HP = HT = 50;
            defenseSkill = 22;

            maxLvl = -9999;

            state = HUNTING;

            properties.add(Property.UNDEAD);
        }

        @Override
        public int damageRoll() {
            return Random.NormalIntRange( 16, 25 );
        }

        @Override
        public int attackSkill( Char target ) {
            return 26;
        }

        @Override
        public int drRoll() {
            return Random.NormalIntRange(0, 4);
        }
    }

    public static class SpeedyMonk extends Monk{
        {
            baseSpeed = 1.5f;
            maxLvl = -9999;
            spriteClass = SpeedyMonkSprite.class;
            state = HUNTING;
            HT = HP = 50;
        }

        @Override
        protected boolean act() {
            focusCooldown = 9999f;
            return super.act();
        }

        @Override
        public float attackDelay() {
            return 1/3f;
        }

        @Override
        public int damageRoll() {
            return Random.NormalIntRange(12, 17);
        }

        public static class SpeedyMonkSprite extends MonkSprite{
            {
                tint(0x00FF00, 0.4f);
            }

            @Override
            public void resetColor() {
                super.resetColor();
                tint(0x00FF00, 0.4f);
            }
        }
    }

    public static class EvasiveMonk extends Monk{
        {
            maxLvl = -9999;
            defenseSkill = 45;
            state = HUNTING;
            HT = HP = 60;
        }
    }

    public static class TankMonk extends Monk{
        {
            maxLvl = -9999;
            defenseSkill = 20;
            spriteClass = TankMonkSprite.class;
            state = HUNTING;
            baseSpeed = 0.85f;
            HP = HT = 90;
        }

        @Override
        public int drRoll() {
            return Random.IntRange(4, 10);
        }

        @Override
        public float attackDelay() {
            return 1f;
        }

        @Override
        public int damageRoll() {
            return Random.NormalIntRange(22, 32);
        }

        public static class TankMonkSprite extends MonkSprite{
            {
                tint(0xFF0000, 0.4f);
            }

            @Override
            public void resetColor() {
                super.resetColor();
                tint(0xFF0000, 0.4f);
            }
        }
    }


    public static class CurseWarlock extends Warlock{
        {
            maxLvl = -9999;
            state = HUNTING;
            HT = HP = 60;
        }
        protected void castDebuff(Char enemy){
            switch (Random.Int(5)){
                case 0:
                    Buff.prolong(enemy, Weakness.class, 20f);
                    break;
                case 1:
                    Buff.prolong(enemy, Blindness.class, 6f);
                    break;
                case 2:
                    Buff.prolong(enemy, Vulnerable.class, 12f);
                    break;
                case 3:
                    Buff.prolong(enemy, Hex.class, 20f);
                    break;
                case 4:
                    Buff.prolong(enemy, Vertigo.class, 3f);
                    break;
            }
        }

        @Override
        protected void zap() {

            spend( TICK );

            if (hit( this, enemy, true )) {
                //TODO would be nice for this to work on ghost/statues too
                if (enemy == Dungeon.hero) {
                    if(Random.Int(3) == 0){
                        Buff.prolong(enemy, Degrade.class, 15f);
                        Sample.INSTANCE.play( Assets.Sounds.DEBUFF );
                    }else if(Random.Int(2) == 0){
                        for(Item i: Dungeon.hero.belongings){
                            if(i instanceof Wand){
                                ((Wand) i).curCharges -= 1;
                            }
                        }
                        Sample.INSTANCE.play( Assets.Sounds.DEBUFF );
                    }else if(Random.Int(3) < 2){
                        castDebuff(enemy);
                        Sample.INSTANCE.play( Assets.Sounds.DEBUFF );
                    }

                }else if(Random.Int(2)==0){
                    castDebuff(enemy);
                }

                int dmg = Random.NormalIntRange( 9, 13 );
                enemy.damage( dmg, this );

                if (enemy == Dungeon.hero && !enemy.isAlive()) {
                    Badges.validateDeathFromEnemyMagic();
                    Dungeon.fail( getClass() );
                    GLog.n( Messages.get(this, "bolt_kill") );
                }
            } else {
                enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
            }
        }
    }

    public static class DPSWarlock extends Warlock{
        {
            baseSpeed = 0.6f;
            spriteClass = DPSWarlockSprite.class;
            maxLvl = -9999;
            state = HUNTING;
            HT = HP = 80;
        }
        @Override
        public void damage(int dmg, Object src) {
            if(src instanceof Wand){
                dmg = 0;
                Buff.affect(this, Barrier.class).incShield(1);
                Buff.detach(this, Barrier.class);
            }
            super.damage(dmg, src);
        }

        @Override
        protected void zap() {

            spend( TICK * 2 );
            if (hit( this, enemy, true )) {
                //TODO would be nice for this to work on ghost/statues too

                int dmg = Random.NormalIntRange( 28, 45 );
                enemy.damage( dmg, this );

                if (enemy == Dungeon.hero && !enemy.isAlive()) {
                    Badges.validateDeathFromEnemyMagic();
                    Dungeon.fail( getClass() );
                    GLog.n( Messages.get(this, "bolt_kill") );
                }
            } else {
                enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
            }
        }

        public static class DPSWarlockSprite extends WarlockSprite{
            {
                tint(0xFF0000, 0.4f);
            }

            @Override
            public void resetColor() {
                super.resetColor();
                tint(0xFF0000, 0.4f);
            }
        }
    }

    public static class HealingWarlock extends Warlock{
        {
            spriteClass = HealingWarlockSprite.class;
            maxLvl = -9999;
            state = HUNTING;
            HT = HP = 55;
        }

        @Override
        protected boolean act() {
            Buff.affect(this, BlobImmunity.class, 100f);
            return super.act();
        }

        @Override
        protected void zap() {

            spend( TICK );

            if (hit( this, enemy, true )) {
                //TODO would be nice for this to work on ghost/statues too

                int dmg = Random.NormalIntRange( 5, 9 );
                enemy.damage( dmg, this );

                if (enemy == Dungeon.hero && !enemy.isAlive()) {
                    Badges.validateDeathFromEnemyMagic();
                    Dungeon.fail( getClass() );
                    GLog.n( Messages.get(this, "bolt_kill") );
                }

                healChar(15);

            } else {
                enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
            }
        }

        @Override
        public int attackProc(Char enemy, int damage) {
            healChar(5);
            return super.attackProc(enemy, damage);
        }

        protected void healChar(int amount){
            Mob healingTarget = null;
            for(Mob m: Dungeon.level.mobs.toArray(new Mob[0])){
                if(m instanceof NewHardDK && m.HT - m.HP > 5){
                    healingTarget = m;
                    break;
                }
            }
            if(healingTarget == null){
                for(Mob m: Dungeon.level.mobs.toArray(new Mob[0])){
                    if(m.HP < HT - 5 && m != this && Dungeon.level.distance(this.pos, m.pos) < 5){
                        healingTarget = m;
                    }
                }
            }
            if(healingTarget != null){
                healingTarget.HP = Math.min(healingTarget.HP + amount, healingTarget.HT);
                healingTarget.sprite.showStatus(0x00FF00, String.valueOf(amount));
            }
        }

        public static class HealingWarlockSprite extends WarlockSprite{
            {
                tint(0x00FF00, 0.4f);
            }

            @Override
            public void resetColor() {
                super.resetColor();
                tint(0x00FF00, 0.4f);
            }
        }
    }
}
