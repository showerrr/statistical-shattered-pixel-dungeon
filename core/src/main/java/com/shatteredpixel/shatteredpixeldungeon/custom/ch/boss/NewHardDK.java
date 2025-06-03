package com.shatteredpixel.shatteredpixeldungeon.custom.ch.boss;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AdrenalineSurge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BlobImmunity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Stamina;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bee;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Ghoul;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Golem;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Monk;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Warlock;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.custom.buffs.AttributeModifier;
import com.shatteredpixel.shatteredpixeldungeon.custom.buffs.DeathCounter;
import com.shatteredpixel.shatteredpixeldungeon.custom.buffs.IgnoreArmor;
import com.shatteredpixel.shatteredpixeldungeon.custom.buffs.PositiveBuffProhibition;
import com.shatteredpixel.shatteredpixeldungeon.custom.buffs.ThornsShield;
import com.shatteredpixel.shatteredpixeldungeon.custom.buffs.ZeroAttack;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.GME;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.timing.RepeatedCallback;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.timing.VirtualActor;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KingsCrown;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Stone;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfCleansing;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gloves;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.KingSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.HashSet;

public class NewHardDK extends Boss{
    {
        initProperty();
        initBaseStatus(22, 42, 40, 26, 250, 6, 16);
        initStatus(300);

        spriteClass = KingSprite.class;
        properties.add(Property.UNDEAD);
        viewDistance = 99; // king is able to see all the map
        state = HUNTING;

    }

    {
        immunities.add( Terror.class );
        immunities.add( Amok.class );
        immunities.add( Charm.class );
        immunities.add( Sleep.class );
        immunities.add( Vertigo.class );
        immunities.add( Frost.class );
        immunities.add( Paralysis.class );
    }

    private int phase = 0;
    private float summonCD = 1f;
    private boolean shouldEmpower = false;
    private int empowered = 0;
    private boolean need_tele = false;
    private int p3_statue = 0;
    private boolean sacrifice_prepared = false;
    private int sacrifice_executed = 0;
    private float sacrifice_cd = 75f;


    @Override
    protected boolean act() {
        for(Mob m: Dungeon.level.mobs.toArray(new Mob[0])){
            if(m.state == m.SLEEPING){
                if(m != this){
                    m.damage(0, this);
                }
            }
        }

        if(phase == 0 || phase == 1) {
            if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()) {
                fieldOfView = new boolean[Dungeon.level.length()];
            }
            Dungeon.level.updateFieldOfView(this, fieldOfView);

            enemy = chooseEnemy();

            if (enemy == null) enemy = Dungeon.hero;

            enemySeen = enemy != null && enemy.isAlive() && fieldOfView[enemy.pos] && enemy.invisible <= 0;
        }

        if (phase == 0){
            if (Dungeon.hero.viewDistance >= Dungeon.level.distance(pos, Dungeon.hero.pos)) {
                Dungeon.observe();
            }
            if (Dungeon.level.heroFOV[pos]) {
                notice();
            }
        }

        if(phase == 0){
            spend(TICK);
            return true;
        }else if(phase == 1){
            spend(TICK);
            summonCD -= 1f;
            actSummon();
            if(buff(HDKBuff.NeutralBarrier.class)  == null){
                shiftPhase();
            }
            return true;
        }else if(phase == 2){
            if(need_tele){
                need_tele = false;
                teleportSubject();
                spend(TICK);
                return true;
            }
            actSummon();
            summonCD -= TICK;
            return super.act();
        }else if(phase == 3){
            shouldEmpower = getStatueCount() < p3_statue;
            if(shouldEmpower && paralysed <= 0){
                p3_statue = getStatueCount();
                if(empowered == 0){
                    actEmpower(5);
                }else if(empowered == 1){
                    actEmpower(3);
                }else if(empowered == 2){
                    actEmpower(9);
                }else if(empowered >= 3){
                    actEmpower(15);
                }
                return true;
            }
            if(need_tele){
                need_tele = false;
                teleportSubject();
                spend(TICK/2f);
                return true;
            }
            actSummon();
            summonCD -= TICK;
            return super.act();
        }else if(phase == 4){
            if(paralysed > 0){
                return super.act();
            }
            if(sacrifice_prepared){
                actSacrifice();
                return true;
            }else if(sacrifice_cd < 0f){
                actSacrificePreparation();
                return true;
            }
            actSummon();
            summonCD -= 1f + sacrifice_executed * 0.25f;
            return super.act();
        }

        return super.act();


    }

    private boolean actSummon(){
        if(paralysed > 0) return false;

        if(phase == 1){
            if(getSummoningAlive() < 2) {
                summonCD -= 3f;
            }
            if(summonCD > 0){ return false;}
            summonCD += 20f + getStatueCount() * 4f;
            summonSingle(3, 0);
            if(Random.Int(2) == 0) {
                summonSingle(7, Random.IntRange(1, 6));
            }else{
                summonSingle(3, 0);
            }
        }else if(phase == 2){
            if(getSummoningAlive() < 3){
                summonCD -= 7f;
            }
            if(summonCD > 0){ return false;}
            summonCD += 40f;
            summonSingle(1, 0);
            summonSingle(3, Random.IntRange(1, 6));
        }else if(phase == 3){
            if(getSummoningAlive() < 4){
                summonCD -= 9f;
            }
            if(summonCD > 0){ return false;}
            summonCD += 24f + getStatueCount() * 6f;
            if(Random.Int(2) == 0) {
                summonSingle(3, 0);
            }else {
                summonSingle(4, Random.IntRange(1, 6));
            }
            summonSingle(4, Random.IntRange(1, 6));

            need_tele = true;
        }else if(phase == 4){
            if(getSummoningAlive() < 4){
                summonCD -=11f;
            }
            if(summonCD > 0){ return false;}
            summonCD += 40f;
            summonSingle(2, 0);
            summonSingle(4, Random.IntRange(1, 3));
            summonSingle(6, Random.IntRange(4, 6));
        }
        return true;
    }

    private void actSacrificePreparation(){
        Buff.detach(this, HDKBuff.HDKSummoning.class);
        Buff.affect(this, Barrier.class).setShield(50);
        backToStay(2);
        new Flare(5, 32).show(this.sprite, 3f).color(0x444444);
        Buff.affect(this, ThornsShield.class, 10f);
        spend(TICK * 7);
        sacrifice_prepared = true;
        yell(M.L(this, "prepare_to_sacrifice"));
        if(sacrifice_executed == 0){
            GLog.w(M.L(this, "hint_phase_4"));
        }
    }

    private void actSacrifice(){
        int sacrificed = 0;
        Buff.detach(this, Barrier.class);
        for(Mob m:Dungeon.level.mobs.toArray(new Mob[0])){
            if(isSummonedMob(m)){
                ++sacrificed;
                CellEmitter.get(m.pos).burst(ShadowParticle.UP, 12);
                m.die(this);
            }
        }


        int power = Math.max(sacrificed - 1, 0);
        power = Math.min(power, 10);

        this.HP = Math.min(this.HP + 50, this.HT);
        sprite.showStatus(CharSprite.POSITIVE, String.valueOf(50));

        RepeatedCallback.executeChain(0.5f, power, ()->{
            this.HP = Math.min(this.HP + 15, this.HT);
            sprite.showStatus(CharSprite.POSITIVE, String.valueOf(15));
        });
        if(power > 1) {
            RepeatedCallback.executeChain(new float[]{1f}, ()->{
                PotionOfCleansing.cleanse(this);
                Buff.affect(this, BlobImmunity.class, 20f);
                Buff.affect(this, BlobImmunity.class, 20f);
            });
        }
        if(power > 2){
            RepeatedCallback.executeChain(new float[]{1.5f}, ()->{
                MagicMissile.boltFromChar(sprite.parent, MagicMissile.ELMO, sprite, Dungeon.hero.pos, ()-> Buff.affect(Dungeon.hero, ZeroAttack.class, 15f));
            });
        }
        if(power > 3){
            RepeatedCallback.executeChain(new float[]{2f}, ()->{
                MagicMissile.boltFromChar(sprite.parent, MagicMissile.ELMO, sprite, Dungeon.hero.pos, ()->{
                    Buff.affect(Dungeon.hero, Blindness.class, 10f);
                    Buff.affect(Dungeon.hero, Vertigo.class, 15f);
                });
            });
        }
        if(power > 4){
            RepeatedCallback.executeChain(new float[]{2.5f}, ()->{
                MagicMissile.boltFromChar(sprite.parent, MagicMissile.ELMO, sprite, Dungeon.hero.pos, ()->{
                    for(Item item: Dungeon.hero.belongings){
                        if(item instanceof Wand){
                            {((Wand) item).curCharges -= 10;}
                        }
                    }
                    Dungeon.hero.sprite.showStatus(CharSprite.NEGATIVE, String.valueOf(-10));
                });
                Buff.affect(this, Stamina.class, 20f);
            });
        }
        if(power > 5){
            RepeatedCallback.executeChain(new float[]{3f}, ()->{
                Buff.affect(this, Adrenaline.class, 15f);
            });
        }
        if(power > 6){
            RepeatedCallback.executeChain(new float[]{3.5f}, ()->{
                MagicMissile.boltFromChar(sprite.parent, MagicMissile.ELMO, sprite, Dungeon.hero.pos, ()-> Buff.affect(Dungeon.hero, PositiveBuffProhibition.class, 15f));
            });
        }
        if(power > 7){
            RepeatedCallback.executeChain(new float[]{4f}, ()->{
                MagicMissile.boltFromChar(sprite.parent, MagicMissile.ELMO, sprite, Dungeon.hero.pos, ()-> Buff.affect(Dungeon.hero, Slow.class, 20f));
            });
        }
        if(power > 8){
            RepeatedCallback.executeChain(new float[]{4.5f}, ()->{
                MagicMissile.boltFromChar(sprite.parent, MagicMissile.ELMO, sprite, Dungeon.hero.pos, ()-> {
                    Buff.affect(Dungeon.hero, PositiveBuffProhibition.class, 666f);
                    Buff.affect(Dungeon.hero, ZeroAttack.class, 666f);
                });
            });
        }
        if(power > 9){
            RepeatedCallback.executeChain(new float[]{5f}, ()->{
                MagicMissile.boltFromChar(sprite.parent, MagicMissile.ELMO, sprite, Dungeon.hero.pos, ()->{
                    Dungeon.hero.HP = 1;
                    GameScene.flash(0x80ff0000);
                });
            });
        }

        VirtualActor.delay(power * 0.5f + 0.5f);

        ++sacrifice_executed;
        sacrifice_prepared = false;
        sacrifice_cd += 75f;
        summonSingle(3, 1);
        summonSingle(3, 2);
        summonSingle(3, 3);
        summonSingle(3, 4);
        summonSingle(3, 5);
        summonSingle(3, 6);
        spend(TICK*3);

        if(sacrifice_executed < 3) {
            yell(M.L(this, "sacrifice_finished", Dungeon.hero.name()));
        }
        if(sacrifice_executed >= 3){
            Buff.affect(this, DeathCounter.class).countUp(20);
            yell(M.L(this, "why_this", Dungeon.hero.name()));
            sacrifice_cd = 99999f;
        }
    }

    private int getSummoningAlive(){
        int count = 0;
        for(Mob m: Dungeon.level.mobs.toArray(new Mob[0])){
            if(isSummonedMob(m) && m.alignment == Alignment.ENEMY){
                ++count;
            }
        }
        for(Buff b: buffs()){
            if(b instanceof HDKBuff.HDKSummoning){
                ++count;
            }
        }
        return count;
    }

    private int getStatueCount(){
        int count = 0;
        for(Mob m: Dungeon.level.mobs.toArray(new Mob[0])){
            if(m instanceof HDKSummon.HDKStatue){
                ++count;
            }
        }
        return count;
    }

    @Override
    public void notice() {
        super.notice();
        if (!BossHealthBar.isAssigned()) {
            BossHealthBar.assignBoss(this);
            for (Char ch : Actor.chars()){
                if (ch instanceof DriedRose.GhostHero){
                    ((DriedRose.GhostHero) ch).sayBoss();
                }
            }
            if (phase == 0) {
                yell(M.L(this, "notice"));
                shiftPhase();
            }
        }

    }

    private void actEmpower(int cate){
        if(paralysed > 0) return;
        backToStay(2);
        for(Mob m: Dungeon.level.mobs.toArray(new Mob[0])){
            if(isSummonedMob(m)){
                    if((cate & 1) > 0) {
                        Buff.affect(m, IgnoreArmor.class, 100f);
                    }
                    if((cate & 2) > 0) {
                        Buff.affect(m, BlobImmunity.class, 10f);
                        PotionOfCleansing.cleanse(m);
                    }
                    if((cate & 4) > 0) {
                        Buff.affect(m, Healing.class).setHeal(m.HT * 3 / 10, 1f, 1);
                    }
                    if((cate & 8) > 0){
                        Buff.affect(m, Haste.class, 6f);
                }
            }
        }
        new Flare(5, 32).color(0xFFFF00, true).show(this.sprite, 1f);
        ++empowered;
        shouldEmpower = false;
        spend(TICK);
        yell(M.L(this, "empower"));
    }

    private void shiftPhase(){
        if(phase == 0){
            phase = 1;
            Buff.detach(this, Barrier.class);
            if(HP < HT){
                HP = HT;
                sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
            }
            summonStatue(1);
            Buff.affect(this, HDKBuff.NeutralBarrier.class).setShield(99999999);
            GLog.w(M.L(this, "hint_phase_1"));
        }else if(phase == 1){
            phase = 2;
            PotionOfCleansing.cleanse(this);
            Buff.affect(this, BlobImmunity.class, 30f);
            for(Mob m: Dungeon.level.mobs.toArray(new Mob[0])){
                if(m instanceof HDKSummon.HDKStatue){
                    m.die(null);
                }
            }
            Buff.affect(this, HDKBuff.MustPhysicalAtk.class);
            yell(M.L(this, "phase_1_done"));
            GLog.w(M.L(this, "hint_phase_2"));
            for(Mob m:Dungeon.level.mobs.toArray(new Mob[0])){
                if(m instanceof HDKSummon.HDKStatue){
                    m.die(NewHardDK.class);
                }
            }
        }else if(phase == 2){
            phase = 3;
            p3_statue = 4;
            backToStay(1);
            summonStatue(2);
            PotionOfCleansing.cleanse(this);
            Buff.affect(this, BlobImmunity.class, 30f);
            Buff.affect(this, Healing.class).setHeal(this.HT, .1f, 1);
            Buff.affect(this, IgnoreArmor.class, 9999f);
            for(Mob m:Dungeon.level.mobs.toArray(new Mob[0])){
                if(isSummonedMob(m)){
                    m.die(null);
                }
            }
            VirtualActor.delay(5.1f);
            RepeatedCallback.executeChain(0.05f, 100, ()->{
                HP = Math.min(HT, HP + 3);
                if(Random.Int(5) == 0){
                    sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
                }
            });
            spend(TICK*2);
            yell(M.L(this, "phase_2_done"));
            GLog.w(M.L(this, "hint_phase_3"));
        }else if(phase == 3){
            for(Mob m:Dungeon.level.mobs.toArray(new Mob[0])){
                if(isSummonedMob(m)){
                    m.die(null);
                }
            }
            Buff.detach(this, HDKBuff.HDKSummoning.class);
            phase = 4;
            for(Mob m:Dungeon.level.mobs.toArray(new Mob[0])){
                if(m instanceof HDKSummon.HDKStatue){
                    m.die(NewHardDK.class);
                }
            }
            backToStay(2);
            VirtualActor.delay(5.1f);
            RepeatedCallback.executeChain(0.05f, 100, ()->{
                HP = Math.min(HT, HP + 3);
                if(Random.Int(4) == 0){
                    sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
                }
            });
            PotionOfCleansing.cleanse(this);
            Buff.affect(this, BlobImmunity.class, 30f);
            Buff.affect(this, Healing.class).setHeal(this.HT, .1f, 1);
            Buff.affect(this, IgnoreArmor.class, 9999f);
            spend(TICK*2);
            summonSingle(3, 1);
            summonSingle(3, 2);
            summonSingle(3, 3);
            summonSingle(3, 4);
            summonSingle(3, 5);
            summonSingle(3, 6);
            yell(M.L(this, "phase_3_done", Dungeon.hero.name()));
        }
    }

    private void backToStay(int stay){
        int stayPos = -1;
        if(Dungeon.level instanceof NewHDKLevel){
            stayPos = ((NewHDKLevel)Dungeon.level).getKingStay(stay);
        }
        if(stayPos != -1){
            Char ch = Actor.findChar(stayPos);
            if(ch == null){
                ScrollOfTeleportation.appear(this, stayPos);
            }else{
                ScrollOfTeleportation.appear(ch, this.pos);
                ScrollOfTeleportation.appear(this, stayPos);
            }
        }
    }

    private boolean summonSingle(int delay, int type){
        int spos = -1;
        if(Dungeon.level instanceof NewHDKLevel){
            spos = ((NewHDKLevel)Dungeon.level).getSummoningPos();
        }else{
            return false;
        }
        HDKBuff.HDKSummoning s = new HDKBuff.HDKSummoning();
        Class<? extends Mob> summoncls;
        switch (type){
            case 0 : default:
                summoncls = HDKSummon.HDKGhoul.class;
                break;
            case 1:
                summoncls = HDKSummon.SpeedyMonk.class;
                break;
            case 2:
                summoncls = HDKSummon.EvasiveMonk.class;
                break;
            case 3:
                summoncls = HDKSummon.TankMonk.class;
                break;
            case 4:
                summoncls = HDKSummon.CurseWarlock.class;
                break;
            case 5:
                summoncls = HDKSummon.DPSWarlock.class;
                break;
            case 6:
                summoncls = HDKSummon.HealingWarlock.class;
                break;
        }
        if(spos <= 0) return false;
        s.pos = spos;
        s.summon = summoncls;
        s.delay = delay;
        s.attachTo(this);
        return true;
    }

    private void summonStatue(int settings){
        int[] position;
        if(Dungeon.level instanceof NewHDKLevel){
            position = ((NewHDKLevel)Dungeon.level).getStatuePos();
            if(settings == 1){
                for(int i=0;i<4;++i){
                    HDKSummon.HDKStatue hdks = new HDKSummon.HDKStatueP1();
                    hdks.pos = position[i];
                    hdks.state = HUNTING;
                    GameScene.add(hdks);
                    Buff.affect(hdks, Light.class, 10000f);
                    new Flare(5, 32 ).show(hdks.sprite, 3f).color(0xFFFF00);
                }
            }else if(settings == 2){
                for(int i=0;i<4;++i){
                    int j = Random.Int( i, 4 );
                    if (j != i) {
                        int t = position[i];
                        position[i] = position[j];
                        position[j] = t;
                    }
                }
                for(int i=0;i<4;++i){
                    HDKSummon.HDKStatue hdks = new HDKSummon.HDKStatueP3();
                    hdks.pos = position[i];
                    hdks.state = HUNTING;
                    GameScene.add(hdks);
                    Buff.affect(hdks, Light.class, 10000f);
                    new Flare(5, 32 ).show(hdks.sprite, 3f).color(0xFFFF00);
                    Buff.affect(hdks, DeathCounter.class).countUp(50 + i * 20);
                }
            }
        }

    }

    private boolean teleportSubject(){
        if (enemy == null) return false;

        if(paralysed > 0) return false;

        Mob furthest = null;

        for (Mob m : getSubjects()){
            if (furthest == null || Dungeon.level.distance(pos, furthest.pos) < Dungeon.level.distance(pos, m.pos)){
                furthest = m;
            }
        }

        if (furthest != null){

            float bestDist;
            int bestPos = pos;

            Ballistica trajectory = new Ballistica(enemy.pos, pos, Ballistica.STOP_TARGET);
            int targetCell = trajectory.path.get(trajectory.dist+1);
            //if the position opposite the direction of the hero is open, go there
            if (Actor.findChar(targetCell) == null && !Dungeon.level.solid[targetCell]){
                bestPos = targetCell;

                //Otherwise go to the neighbour cell that's open and is furthest
            } else {
                bestDist = Dungeon.level.trueDistance(pos, enemy.pos);

                for (int i : PathFinder.NEIGHBOURS8){
                    if (Actor.findChar(pos+i) == null
                        && !Dungeon.level.solid[pos+i]
                        && Dungeon.level.trueDistance(pos+i, enemy.pos) > bestDist){
                        bestPos = pos+i;
                        bestDist = Dungeon.level.trueDistance(pos+i, enemy.pos);
                    }
                }
            }

            Actor.add(new Pushing(this, pos, bestPos));
            pos = bestPos;

            //find closest cell that's adjacent to enemy, place subject there
            bestDist = Dungeon.level.trueDistance(enemy.pos, pos);
            bestPos = enemy.pos;
            for (int i : PathFinder.NEIGHBOURS8){
                if (Actor.findChar(enemy.pos+i) == null
                    && !Dungeon.level.solid[enemy.pos+i]
                    && Dungeon.level.trueDistance(enemy.pos+i, pos) < bestDist){
                    bestPos = enemy.pos+i;
                    bestDist = Dungeon.level.trueDistance(enemy.pos+i, pos);
                }
            }

            if (bestPos != enemy.pos) ScrollOfTeleportation.appear(furthest, bestPos);
            yell(Messages.get(this, "teleport_" + Random.IntRange(1, 2)));
            return true;
        }
        return false;
    }

    private HashSet<Mob> getSubjects(){
        HashSet<Mob> subjects = new HashSet<>();
        for (Mob m : Dungeon.level.mobs){
            if (m.alignment == alignment && (isSummonedMob(m))){
                subjects.add(m);
            }
        }
        return subjects;
    }

    private boolean isSummonedMob(Mob m){
        return isSummonedMob(m.getClass());
    }

    private boolean isSummonedMob(Class c){
        return HDKSummon.HDKGhoul.class.isAssignableFrom(c) || Warlock.class.isAssignableFrom(c) || Monk.class.isAssignableFrom(c);
    }

    private boolean damageFromMagic(Object cause){
        return cause instanceof Wand || cause instanceof Buff || cause instanceof Blob || cause instanceof Scroll || cause instanceof Stone || cause instanceof Bomb;
    }

    private boolean damageFromMissile(Char enemy){
        if (enemy instanceof Hero) {
            if (((Hero) enemy).belongings.thrownWeapon instanceof MissileWeapon) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int defenseProc(Char enemy, int damage) {

        if(phase == 2) {
            if(damageFromMissile(enemy)) {
                if (buff(HDKBuff.MustPhysicalAtk.class) == null) {
                    Buff.affect(this, HDKBuff.MustPhysicalAtk.class);
                } else{
                    damage = 0;
                    sprite.add(CharSprite.State.SHIELDED);
                    sprite.remove(CharSprite.State.SHIELDED);
                }
            }else if (enemy instanceof Hero) {
                Buff.detach(this, HDKBuff.MustPhysicalAtk.class);
            }
        }

        return super.defenseProc(enemy, damage);
    }

    @Override
    public void damage(int dmg, Object src) {
        if(!BossHealthBar.isAssigned()){
            BossHealthBar.assignBoss(this);
        }

        if(dmg > HT / 4){
            dmg = (dmg - HT / 4) / 4 + HT / 4;
        }

        if(phase == 2){
            if(damageFromMagic(src)) {
                if (buff(HDKBuff.MustPhysicalAtk.class) == null) {
                    Buff.affect(this, HDKBuff.MustPhysicalAtk.class);
                } else{
                    dmg = 0;
                    sprite.add(CharSprite.State.SHIELDED);
                    sprite.remove(CharSprite.State.SHIELDED);
                }
            }
        }

        if(phase == 3){
            dmg = GME.accurateRound(dmg * (1f / (1<<getStatueCount())));
        }

        if(phase <= 0){
            dmg = 0;
            notice();
        }
        int beforeHP = HP;
        super.damage(dmg, src);
        int lostHP = - HP + beforeHP;

        summonCD -= lostHP / 4f;

        LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
        if (lock != null) {
            lock.addTime(dmg);
        }

        if(phase == 4 && !sacrifice_prepared){
            sacrifice_cd -= lostHP / 2.1f;
            if(sacrifice_cd < 0){
                spend(-TICK);
            }
        }

        if(HP <= 0 && isAlive()){
            shiftPhase();
        }

        if(phase == 2 && lostHP >= HT / 10){
            need_tele = true;
        }
    }

    @Override
    public boolean isAlive() {
        return !(HP <= 0 && phase >= 4);
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        GameScene.bossSlain();
        Dungeon.level.unseal();
        for(Mob m: Dungeon.level.mobs.toArray(new Mob[0])){
            if(m.alignment == Alignment.ENEMY){
                m.die(null);
            }
        }
        IronKey key = new IronKey();
        key.depth = 20;
        key.quantity(4);
        Dungeon.level.drop(key, pos).sprite.drop();
        GoldenKey gkey = new GoldenKey();
        gkey.depth = 20;
        gkey.quantity(3);
        Dungeon.level.drop(gkey, pos).sprite.drop();
        Dungeon.level.drop(new KingsCrown(), pos).sprite.drop();
        yell(M.L(this, "defeated"));
        Buff.detach(Dungeon.hero, ZeroAttack.class);
        Buff.detach(Dungeon.hero, PositiveBuffProhibition.class);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("phase_king", phase);
        bundle.put("summoncd_king", summonCD);
        bundle.put("should_empower", shouldEmpower);
        bundle.put("empowered_times", empowered);
        bundle.put("sacrifice_is_prepared", sacrifice_prepared);
        bundle.put("sacrifice_has_executed", sacrifice_executed);
        bundle.put("sacrifice_cd", sacrifice_cd);
        bundle.put("p3_statue_last", p3_statue);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        phase = bundle.getInt("phase_king");
        summonCD = bundle.getFloat("summoncd_king");
        shouldEmpower = bundle.getBoolean("should_empower");
        empowered = bundle.getInt("empowered_times");
        sacrifice_prepared = bundle.getBoolean("sacrifice_is_prepared");
        sacrifice_executed = bundle.getInt("sacrifice_has_executed");
        sacrifice_cd = bundle.getFloat("sacrifice_cd");
        p3_statue = bundle.getInt("p3_statue_last");
    }
}
