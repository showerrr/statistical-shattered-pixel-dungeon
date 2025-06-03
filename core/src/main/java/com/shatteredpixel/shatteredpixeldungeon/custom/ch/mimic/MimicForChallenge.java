package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mimic;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Tengu;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Stone;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TeleportationTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MimicForChallenge extends Mimic {
    {
        immunities.add(Grim.class);
        immunities.add(AllyBuff.class);
        //immunities.add(Corruption.class);
        resistances.add(ScrollOfRetribution.class);
        resistances.add(Paralysis.class);
        resistances.add(Terror.class);
        resistances.add(Amok.class);
        resistances.add(ScrollOfPsionicBlast.class);
        resistances.add(Dread.class);
        flying=true;
    }

    protected float FloatLevel;
    private static final String LEVEL = "level_float";
    private static final String BASIC_MOD_FACTOR = "b_mod";
    private static final String TYPE_FACTOR = "type_f";
    private static final String ATTACK_FACTOR = "attack_factor";
    private static final String DEFENSE_FACTOR = "defense_factor";
    private static final String TRICK_FACTOR = "trick_factor";

    public static MimicForChallenge spawnAt( int pos, Item item ){
        return spawnAt( pos, Arrays.asList(item), MimicForChallenge.class);
    }

    public static MimicForChallenge spawnAt( int pos, Item item, Class mimicType ){
        return spawnAt( pos, Arrays.asList(item), mimicType);
    }

    public static MimicForChallenge spawnAt( int pos, List<Item> items ) {
        return spawnAt( pos, items, MimicForChallenge.class);
    }
    public static MimicForChallenge spawnAt(int pos, List<Item> items, Class mimicType ) {

        MimicForChallenge m;

        if(mimicType == GoldenMimicForChallenge.class){
            m = new GoldenMimicForChallenge();
        }else if(mimicType == CrystalMimicForChallenge.class){
            m = new CrystalMimicForChallenge();
        }else{
            m = new MimicForChallenge();
        }

        m.items = new ArrayList<>( items );

        m.setFloatLevel( Dungeon.depth );
        m.pos = pos;

        //generate an extra reward for killing the mimic
        m.generatePrize(false);

        return m;
    }

    public int type = 0;
    private static final int T_CHAOS = 0;
    private static final int T_ATTACK = 1;
    private static final int T_DEFEND = 2;
    private static final int T_TRICK = 3;

    //BASIC attribute modification for mimic. Spawn one, occasionally two.
    //Mod level is between 0 and 7.
    private int basicModFactor = 0;
    private static final int BASE_HEALTH_MOVE = 0;
    private static final int BASE_ATTACK_MOVE = 3;
    private static final int BASE_EVASION_MOVE = 6;
    private static final int BASE_ACCURACY_MOVE = 9;
    private static final int BASE_MOVE_SPEED_MOVE = 12;
    private static final int BASE_ATTACK_SPEED_MOVE = 15;

    protected void createType(){
        type = Random.chances(new float[]{40f, 25f, 15f, 20f});
    }

    protected int basicModLevel(){
        return Random.chances(new float[]{15,13,11,7})+Math.min(4, Math.round(FloatLevel /6));
    }

    protected int basicPerkModLevel(){
        return Random.chances(new float[]{5,5,4,4})+Math.min(4, Math.round(FloatLevel /6));
    }

    private int maxBasicTags(){
        return 1;
    }

    private void createBasicModFactor(){
        int maxMods = 1;

        int typeAbilityLevel = basicPerkModLevel();

        int created=0;
        switch (type){
            case T_ATTACK:
                basicModFactor += typeAbilityLevel<<BASE_ATTACK_MOVE;
                created += 1<<(BASE_ATTACK_MOVE/3);
                break;
            case T_DEFEND:
                basicModFactor += typeAbilityLevel<<BASE_HEALTH_MOVE;
                created += 1<<(BASE_HEALTH_MOVE/3);
                break;
            case T_TRICK:
                basicModFactor += typeAbilityLevel<<BASE_MOVE_SPEED_MOVE;
                created += 1<<(BASE_MOVE_SPEED_MOVE/3);
                break;
            case T_CHAOS: default: maxMods=maxBasicTags();
        }
        if(created>0){
            if(maxBasicTags()>1) {
                int sel;
                do {
                    sel = Random.Int(6);
                } while (((created >> sel) & 1) > 0);
                basicModFactor += basicModLevel() << (sel * 3);
            }
        }else {
            int modded = 0;
            int maxRoll = 6;
            //before resistances
            for (int i = 0; i < 6; ++i) {
                if (Random.Float() < (float) (maxMods - modded) / (maxRoll - i)) {
                    int modLevel = (Random.Int(2)==0) ? basicPerkModLevel():basicModLevel();
                    basicModFactor += modLevel << (3 * i);
                    modded++;
                }
            }
        }
    }

    //6 basic arguments
    protected float HealthModFactor(){
        int modlevel = (basicModFactor>>BASE_HEALTH_MOVE) & 0x7;
        return 1f + modlevel/16f + modlevel*modlevel / 112f;
    }
    protected float AttackModFactor(){
        int modlevel = (basicModFactor>>BASE_ATTACK_MOVE) & 0x7;
        return 1f + modlevel/14f + modlevel*modlevel / 98f;
    }
    protected float AccuracyModFactor(){
        int modlevel = (basicModFactor>>BASE_ACCURACY_MOVE) & 0x7;
        return 1f + 0.2f * (2<<(modlevel-1));
    }
    protected float EvasionModFactor(){
        int modlevel = (basicModFactor>>BASE_EVASION_MOVE) & 0x7;
        return 1f + 0.08f * (2<<(modlevel-1));
    }
    protected float MoveSpeedFactor(){
        int modlevel = (basicModFactor>>BASE_MOVE_SPEED_MOVE) & 0x7;
        return 1f + modlevel/14f + modlevel*modlevel / 98f;
    }
    protected float AttackSpeedFactor(){
        int modlevel = (basicModFactor>>BASE_ATTACK_SPEED_MOVE) & 0x7;
        return 1f + modlevel/14f + modlevel*modlevel / 98f;
    }

    protected int attackMod = 0;
    protected int defendMod = 0;
    protected int trickMod = 0;
    protected int resistMod = 0;

    private static final int RES_MELEE = 0;
    private static final int RES_MISSILE = 2;
    private static final int RES_MAGIC = 4;

    private static final int ATK_ARMOR_PIERCE = 0;
    private static final int ATK_BERSERK = 2;
    private static final int ATK_SUPPRESS = 4;
    private static final int ATK_COMBO = 6;
    private static final int ATK_ROOT = 8;
    private static final int ATK_CURSE = 10;
    private static final int ATK_FROZEN = 12;
    private static final int ATK_FIRE = 14;

    private static final int DEF_HIGH_DAMAGE = 0;
    private static final int DEF_LOW_DAMAGE = 2;
    private static final int DEF_DEFENSE_COPY = 4;
    private static final int DEF_COMBO_RESIST = 6;
    private static final int DEF_VAMPIRE = 8;
    private static final int DEF_PUSH_BACK = 10;
    private static final int DEF_PERIODIC_SHIELD = 12;
    private static final int DEF_NEGATIVE_IMMUNE = 14;

    private static final int TRK_RANGE = 0;
    private static final int TRK_DISAPPEAR = 2;
    private static final int TRK_DEGRADE = 4;
    private static final int TRK_ALERT = 6;
    private static final int TRK_SUMMON = 8;
    private static final int TRK_SCAN = 10;
    private static final int TRK_CHARGE_EATER = 12;
    private static final int TRK_THROW = 14;

    private int maxSpecialTags(){
        if(FloatLevel <5) return 1;
        else if(FloatLevel <10) return 2;
        else return 3;
    }
    //We are much more offensive on distributing high-level perks to improve interest and difficulty, and provide richer reward in early run.
    private void createSpecialModFactor(){

        int reslevel = Random.chances(new float[]{45f - FloatLevel, 30f - FloatLevel /2, 23f + FloatLevel /2, 12f + FloatLevel});
        switch(Random.Int(3)){
            case 0: resistMod += reslevel << RES_MELEE; break;
            case 1: resistMod += reslevel << RES_MISSILE; break;
            case 2: resistMod += reslevel << RES_MAGIC; break;
        }

        if(type != T_CHAOS) {
            int attackLevel = perkLevel();
            final int[] selected = new int[]{Random.Int(8), Random.Int(8), Random.Int(8)};
            int defenseLevel = perkLevel();
            int trickLevel = perkLevel();

            int another;
            switch (type) {
                case T_ATTACK:
                    do {
                        another = Random.Int(8);
                    } while (selected[0] == another);
                    attackMod += attackLevel << (2 * selected[0]);
                    if(maxSpecialTags()>1) attackMod += uniquePerkLevel() << (2*another);
                    if(maxSpecialTags()>2) defendMod += defenseLevel << (2 * selected[1]);

                    break;
                case T_DEFEND:
                    do {
                        another = Random.Int(8);
                    } while (selected[1] == another);
                    defendMod += uniquePerkLevel() << (2*another);
                    if(maxSpecialTags()>1) defendMod += defenseLevel << (2 * selected[1]);
                    if(maxSpecialTags()>2) trickMod += trickLevel << (2 * selected[2]);
                    break;
                case T_TRICK: default:
                    do {
                        another = Random.Int(8);
                    } while (selected[2] == another);
                    trickMod += trickLevel << (2 * selected[2]);
                    if(maxSpecialTags()>1) trickMod += uniquePerkLevel() << (2*another);
                    if(maxSpecialTags()>2) attackMod += attackLevel << (2 * selected[0]);
            }
        }else{
            final int total = maxSpecialTags();
            int modded = 0;
            for(int i=0;i<24;++i){
                if(Random.Float()<(float)(total-modded)/(24-i)){
                    int perkLevel = (Random.Int(3)>0)? perkLevel():uniquePerkLevel();
                    if(i<8){
                        attackMod += perkLevel <<(2*i);
                    }else if(i<16){
                        defendMod += perkLevel <<(2*i-16);
                    }else{
                        trickMod += perkLevel <<(2*i-32);
                    }
                    modded++;
                }
            }
        }
    }

    protected int perkLevel(){
        return Random.chances(new float[]{50f - FloatLevel, 40f - FloatLevel / 2, 30f + FloatLevel / 2, 20f + FloatLevel});
    }

    protected int uniquePerkLevel(){
        return Random.chances(new float[]{30f - FloatLevel, 30f - FloatLevel / 2, 30f + FloatLevel / 2, 30f + FloatLevel});
    }

    protected float berserkDamageFactor(){
        int modlevel = (attackMod>>ATK_BERSERK)&0x3;
        float lose = 1f-(float)this.HP/this.HT;
        return 1f+modlevel*lose*lose*0.33f;
    }

    protected float suppressDamageFactor(){
        int modlevel = (attackMod>>ATK_SUPPRESS)&0x3;
        float left = (float)this.HP/this.HT;
        return 1f+modlevel*left*left*0.25f;
    }
    //add damage = dr(enemy) * multiplier
    protected float highDefenseAddDamageMultiplier(){
        int modlevel = (attackMod>>ATK_ARMOR_PIERCE) &0x3;
        if(modlevel == 0){
            return 0f;
        }
        return 0.4f*modlevel-0.2f;
    }

    protected int attacked=0;
    protected float comboDamageFactor(){
        int modlevel = (attackMod>>ATK_COMBO)&0x3;
        return 1f+modlevel*attacked*0.1f;
    }

    protected void rootProc(Char enemy){
        int modlevel = (attackMod>>ATK_ROOT)&0x3;
        if(modlevel>0) {
            if (Random.Float() < 0.4f + 0.12f*modlevel) {
                Buff.prolong(enemy, Roots.class, 2 * modlevel);
            }
        }
    }

    protected void curseAttackProc(Char enemy){
        int modlevel = (attackMod>>ATK_CURSE)&0x3;
        if(modlevel>0) {
            if (Random.Int(5 - modlevel) == 0) {
                float duration = 2 << modlevel;
                Buff.prolong(enemy, Weakness.class, duration);
                Buff.prolong(enemy, Blindness.class, duration);
                Buff.prolong(enemy, Hex.class, duration);
                Buff.affect(enemy, Vulnerable.class, duration);
            }
        }
    }

    protected void freezingAttackProc(Char enemy){
        int modlevel = (attackMod>>ATK_FROZEN)&0x3;
        if(modlevel>0) {
            if (Random.Float() < 0.4f + 0.12f*modlevel) {
                float duration = modlevel * 2.5f - 1f;
                Buff.prolong(enemy, Chill.class, duration);
            }
        }
    }

    protected void fireAttackProc(Char enemy){
        int modlevel = (attackMod>>ATK_FIRE)&0x3;
        if(modlevel>0) {
            if(Random.Int(5-modlevel)==0) {
                Buff.affect(enemy, Burning.class).reignite(enemy);
            }
        }
    }

    protected float MeleeResistanceFactor() {
        int modlevel = (resistMod>>RES_MELEE) & 0x3;
        return 0.21f*modlevel;
    }

    protected float MissileResistanceFactor() {
        int modlevel = (resistMod>>RES_MISSILE)&0x3;
        return 0.26f*modlevel;
    }

    protected float MagicalResistanceFactor(){
        int modlevel = (resistMod>>RES_MAGIC)&0x3;
        return 0.31f*modlevel;
    }

    protected float defenseCopyFactor(){
        int modlevel = (defendMod>>DEF_DEFENSE_COPY)&0x3;
        return 0.4f*modlevel;
    }

    protected float ComboResistanceFactor(int combo){
        int modlevel = (defendMod>>DEF_COMBO_RESIST)&0x3;
        if(modlevel>0) {
            return 1f - modlevel / (2.5f + modlevel) * (1f - (float) Math.pow(0.75 - 0.05 * modlevel, combo));
        }
        return 1f;
    }

    protected int limitMaxDamage(int damage){
        int modlevel = (defendMod>>DEF_HIGH_DAMAGE)&0x3;
        if(modlevel <= 0){
            return damage;
        }
        int limit = Math.round((float)this.HT/(modlevel*2+1));
        if(damage > limit){
            return Math.round(0.2f*(damage-limit)+limit);
        }else{
            return damage;
        }
    }

    protected int limitMinDamage(int damage){
        int modlevel = (defendMod>>DEF_LOW_DAMAGE)&0x3;
        if(modlevel == 0){
            return damage;
        }
        int limit = Math.round(this.HT*(0.075f*modlevel-0.01f*modlevel*modlevel));
        if(damage<limit){
            return 1;
        }else{
            return damage;
        }
    }

    protected void vampireProc(int damage){
        int modlevel = (defendMod>>DEF_VAMPIRE)&0x3;
        if(modlevel>0){
            this.HP = Math.min(this.HT, Math.round(this.HP + damage*0.17f*modlevel));
            sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
        }
    }

    protected int pushBackProc(Char enemy, int damage){
		if(enemy == null) return damage;
        int modlevel = (defendMod>>DEF_PUSH_BACK)&0x3;
        if(modlevel>0){
            if(Random.Int(2)==0) {
                WandOfBlastWave.throwChar(enemy, new Ballistica(enemy.pos, 2*enemy.pos - this.pos, Ballistica.MAGIC_BOLT), 1 + modlevel, true, true, MimicForChallenge.class );
                damage = damage/5;
            }
        }
        return damage;
    }

    protected int shieldProc(int damage){
        int modlevel = (defendMod>>DEF_PERIODIC_SHIELD)&0x3;
        if(modlevel>0) {
            if (getHit % (6-modlevel) == 0) {
                return 1;
            }
        }
        return damage;
    }

    protected int attackRange(){
        int modlevel = (trickMod>>TRK_RANGE)&0x3;
        switch (modlevel){
            case 3: return 4;
            case 2: return 3;
            case 1: return 2;
            case 0: default: return 1;
        }
    }

    protected void disappearProc(){
        int modlevel = (trickMod>>TRK_DISAPPEAR)&0x3;
        if(modlevel>0){
            if(Random.Int(17-modlevel*4)==0){
                flying=false;
                new TeleportationTrap().set(this.pos).activate();
                flying=true;
            }
        }
    }

    protected void degradeProc(Char enemy){
        int modlevel = (trickMod>>TRK_DEGRADE)&0x3;
        if(modlevel>0){
            if(Random.Int(2)==0){
                float duration = (Random.Int(666)==0)? 999999f : 1<<modlevel;
                Buff.prolong(enemy, Degrade.class, duration);
            }
        }
    }

    protected void alertProc(Char target){
		if(target == null) target = this;
        int modlevel = (trickMod>>TRK_ALERT)&0x3;
        if(modlevel>0){
            for(Mob m: Dungeon.level.mobs.toArray(new Mob[0])){
                if(m.alignment == Alignment.ENEMY) {
                    if (Random.Int(7 - 2*modlevel) == 0) {
                        m.beckon(target.pos);
                    }
                }
            }
        }
    }

    protected void summonProc(){
        int modlevel = (trickMod>>TRK_SUMMON)&0x3;
        if(modlevel>0 && Dungeon.level.passable != null){
            int nMobs = modlevel;

            ArrayList<Integer> candidates = new ArrayList<>();

            for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                int p = this.pos + PathFinder.NEIGHBOURS8[i];
                if (Actor.findChar( p ) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
                    candidates.add( p );
                }
            }

            ArrayList<Integer> respawnPoints = new ArrayList<>();

            while (nMobs > 0 && candidates.size() > 0) {
                int index = Random.index( candidates );

                respawnPoints.add( candidates.remove( index ) );
                nMobs--;
            }

            ArrayList<Mob> mobs = new ArrayList<>();

            for (Integer point : respawnPoints) {
                Mob mob = Dungeon.level.createMob();
                while (Char.hasProp(mob, Property.LARGE) && !Dungeon.level.openSpace[point]){
                    mob = Dungeon.level.createMob();
                }
                if (mob != null) {
                    mob.state = mob.WANDERING;
                    mob.pos = point;
                    GameScene.add(mob, 2f);
                    mobs.add(mob);
                }
            }

            for (Mob mob : mobs){
                ScrollOfTeleportation.appear(mob, mob.pos);
                Dungeon.level.occupyCell(mob);
            }
        }
    }

    protected void scanProcess(){
        int modLevel = (trickMod>>TRK_SCAN)&0x3;
        if(modLevel>0){
            if(this.alignment == Alignment.ENEMY){
                if(Dungeon.level.distance(this.pos, Dungeon.hero.pos)<=6*modLevel-5){
                    Buff.detach(Dungeon.hero, Invisibility.class);
                }
            }
        }
    }

    protected void chargeEatProc(Char enemy){
        if(!(enemy instanceof Hero )) return;
        int modLevel = (trickMod>>TRK_CHARGE_EATER)&0x3;
        if(modLevel>0){
            for(Item i: ((Hero) enemy).belongings){
                if(i instanceof Wand){
                    ((Wand)i).curCharges=Math.max(((Wand)i).curCharges-modLevel, 0);
                }
            }
            Item.updateQuickslot();
        }
    }

    protected void throwProc(Char enemy){
        int modlevel = (trickMod>>TRK_THROW)&0x3;
        if(modlevel>0) {
            if(Random.Int(2)==0) {
                //trace a ballistica to our target (which will also extend past them
                Ballistica trajectory = new Ballistica(this.pos, enemy.pos, Ballistica.STOP_TARGET);
                //trim it to just be the part that goes past them
                trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
                WandOfBlastWave.throwChar(enemy, trajectory, 2 * modlevel, true, true, MimicForChallenge.class);
            }
        }
    }

    public void setFloatLevel(int floatLevel){
        this.FloatLevel = floatLevel;
        createType();
        createBasicModFactor();
        createSpecialModFactor();
        /*
        basicModFactor = Integer.MAX_VALUE;
        attackMod = Integer.MAX_VALUE;
        defendMod = Integer.MAX_VALUE;
        trickMod = Integer.MAX_VALUE;

         */
        initStatus();
    }

    protected void adjustStats(){
        HT = Math.round((6+ FloatLevel *6f)*HealthModFactor());
        defenseSkill = Math.round((2 + FloatLevel *2/5)*EvasionModFactor());
        baseSpeed = MoveSpeedFactor();
        enemySeen = true;
    }

    protected void initStatus(){
        adjustStats();
        HP = HT;
    }

    @Override
    protected boolean act(){
        scanProcess();
        return super.act();
    }

    @Override
    public void stopHiding(){
        super.stopHiding();
        if(buff(MimicStatusShower.class)!=null) {
            summonProc();
        }
    }

    @Override
    protected boolean canAttack( Char enemy ){
        int modlevel = (trickMod>>TRK_RANGE)&0x3;
        if(modlevel>0){
            //can see, and in range
            return ((Dungeon.level.distance(this.pos, enemy.pos)<=attackRange()) && this.fieldOfView[enemy.pos])
                    || super.canAttack(enemy);
        }
        return super.canAttack(enemy);
    }

    @Override
    public float attackDelay(){
        return super.attackDelay()/AttackSpeedFactor();
    }

    @Override
    public int attackSkill( Char target ) {
        if (target != null && alignment == Alignment.NEUTRAL && target.invisible <= 0){
            return INFINITE_ACCURACY;
        } else {
            return Math.round((6 + FloatLevel *1.2f)*AccuracyModFactor());
        }
    }
    @Override
    public int damageRoll() {
        float base = (alignment == Alignment.NEUTRAL? Random.NormalFloat( 2 +  FloatLevel * 3 / 2, 3 + 2* FloatLevel) : Random.NormalFloat( 1 + FloatLevel, 3 + 7 * FloatLevel / 5));
        base = base * AttackModFactor() * berserkDamageFactor() * suppressDamageFactor() * comboDamageFactor();
        return Math.round(base);
    }
    //NO dr, for we have resistance arguments and defensive perks.
    @Override
    public int drRoll() {
        return 0;
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc( enemy, damage );
        int dr  = enemy.drRoll();
        int postDamage = damage;
        postDamage += Math.round(dr*highDefenseAddDamageMultiplier());

        curseAttackProc(enemy);

        freezingAttackProc(enemy);

        rootProc(enemy);

        fireAttackProc(enemy);

        degradeProc(enemy);

        vampireProc(damage);

        chargeEatProc(enemy);

        throwProc(enemy);

        attacked++;

        return super.attackProc(enemy, postDamage);
    }
    @Override
    public int defenseProc(Char enemy, int damage) {

        damage -= Math.round(enemy.drRoll()*defenseCopyFactor());
        if(damage<0) damage = 0;

        float meleeRes = MeleeResistanceFactor();
        float missileRes = MissileResistanceFactor();



        if(enemy instanceof Hero){

            if(((Hero) enemy).belongings.thrownWeapon != null){
                damage = Math.round(damage * (1f - missileRes));
            } else if(((Hero) enemy).belongings.weapon instanceof MeleeWeapon) {
                damage = Math.round(damage * (1f - meleeRes));
            }
        }
		
		damage=pushBackProc(enemy, damage);

        return super.defenseProc(enemy,damage);
    }
    //count total times of damage it has taken
    protected int getHit = 0;
    @Override
    public void damage(int dmg, Object src) {
        if (state == PASSIVE){
            alignment = Alignment.ENEMY;
            stopHiding();
        }
        dmg=shieldProc(dmg);
        float magicalRes = MagicalResistanceFactor();
        if(src instanceof Wand || src instanceof Buff || src instanceof Blob || src instanceof Scroll || src instanceof Stone || src instanceof Bomb){
            dmg = Math.round(dmg * (1f - magicalRes));
        }

        dmg = Math.round(dmg*ComboResistanceFactor(getHit));
        dmg = limitMinDamage(dmg);
        dmg = limitMaxDamage(dmg);
        if(dmg<0) dmg = 0;
        if(!(src instanceof Buff || src instanceof Blob)){
            getHit++;
        }
        if(dmg>1) disappearProc();
        //WARNING: enemy should not appear in damage(). Enemy may be null.
        if(dmg>1) alertProc(enemy);

        super.damage(dmg, src);
    }

    @Override
    public synchronized boolean add( Buff buff ) {
        super.add(buff);
        if(buff.type== Buff.buffType.NEGATIVE) {
            int modlevel = (defendMod >> DEF_NEGATIVE_IMMUNE) & 0x3;
            if (modlevel > 0) {
                if (Random.Int(3) < modlevel) {
                    Buff.detach(this, buff.getClass());
                    return false;
                }
            }
        }

        return true;
    }

    private float basicModPower(int level){
        if(level>0 && level<8){
            return 1f + level/17f + level*level / 119f;
        }
        return 1f;
    }

    private float specialModPower(int level){
        switch (level){
            case 0: default: return 1f;
            case 1: return 1.2f;
            case 2: return 1.4f;
            case 3: return 1.7f;
        }
    }

   public float showPower() {
       float power = 1.1f;//base power of flying
        for(int i=0;i<6;++i){
            power *= basicModPower((basicModFactor>>(3*i))&0x7);
        }
        int lvl;
       for(int i=0; i<3; ++i){
           lvl = (resistMod>>(2*i))&0x3;
           power*=specialModPower(lvl);
       }

       for(int i=0; i<8; ++i){
           lvl = (attackMod>>(2*i))&0x3;
           power*=specialModPower(lvl);
       }

       for(int i=0; i<8; ++i){
           lvl = (defendMod>>(2*i))&0x3;
           power*=specialModPower(lvl);
       }

       for(int i=0; i<8; ++i){
           lvl = (trickMod>>(2*i))&0x3;
           power*=specialModPower(lvl);
       }

       return power;

   }

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( LEVEL, FloatLevel);
        bundle.put( BASIC_MOD_FACTOR, basicModFactor);
        bundle.put( TYPE_FACTOR, resistMod);
        bundle.put( ATTACK_FACTOR, attackMod);
        bundle.put( DEFENSE_FACTOR, defendMod);
        bundle.put( TRICK_FACTOR, trickMod);
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        FloatLevel = bundle.getFloat( LEVEL );
        basicModFactor = bundle.getInt(BASIC_MOD_FACTOR);
        resistMod = bundle.getInt(TYPE_FACTOR);
        attackMod = bundle.getInt(ATTACK_FACTOR);
        defendMod = bundle.getInt(DEFENSE_FACTOR);
        trickMod = bundle.getInt(TRICK_FACTOR);
        //WHY this fucking code works???
        //Too bad that we use a same-name private parameter with superclass, refactor.
        //adjustStatus()
        super.restoreFromBundle(bundle);
        adjustStats();
    }


    //stone, seed
    protected void generateLevelOneConsumable(float power, ArrayList<Item> loots){
        Item reward = null;
        float common_chance;
        float rare_chance;
        if(power < 2f){
            common_chance = 0.3f + (power - 1f) * 0.1f;
            rare_chance = (power - 1f) * 0.05f;
        }else if(power < 4f){
            common_chance = 0.4f + (power - 2f) * 0.1f;
            rare_chance = 0.05f + (power - 2f) * 0.05f;
        }else if(power < 8f){
            common_chance = 0.6f + (power - 4f) * 0.05f;
            rare_chance = 0.15f + (power - 4f) * 0.05f;
        }else{
            common_chance = 0.8f;
            rare_chance = 0.4f;
        }
        if(Random.Float()<common_chance) {
            do {
                reward = (Random.Int(2) == 0) ?
                    Generator.randomUsingDefaults(Generator.Category.STONE) : Generator.randomUsingDefaults(Generator.Category.SEED);
            } while (reward == null || Challenges.isItemBlocked(reward));
            reward.quantity(Random.Float() < rare_chance ? 2 : 1);
            loots.add(reward);
        }
    }

    //scroll, potion
    protected void generateLevelTwoConsumable(float power, ArrayList<Item> loots){
        Item reward = null;
        float common_chance;
        float rare_chance;
        if(power < 2f){
            common_chance = 0f;
            rare_chance = 0f;
        }else if(power < 4f){
            common_chance = 0.15f + (power - 2f) * 0.1f;
            rare_chance = (power - 2f) * 0.05f;
        }else if(power < 8f){
            common_chance = 0.35f + (power - 4f) * 0.075f;
            rare_chance = 0.1f + (power - 4f) * 0.05f;
        }else if(power < 16f){
            common_chance = 0.65f + (power -8f) * 0.025f;
            rare_chance = 0.3f;
        }else{
            common_chance = 0.95f;
            rare_chance = 0.4f;
        }
        if(Random.Float()<common_chance) {
            do {
                reward = (Random.Int(2) == 0) ?
                    Generator.randomUsingDefaults(Generator.Category.SCROLL) : Generator.randomUsingDefaults(Generator.Category.POTION);
            } while (reward == null || Challenges.isItemBlocked(reward));
            reward.quantity(Random.Float() < rare_chance ? 2 : 1);
            loots.add(reward);
        }
    }

    //stone, seed
    protected void generateMissile(float power, ArrayList<Item> loots){
        Item reward = null;
        float common_chance;
        float rare_chance;
        if(power < 2f){
            common_chance = 0.3f;
            rare_chance = (power - 1f) * 0.4f;
        }else if(power < 4f){
            common_chance = 0.4f;
            rare_chance = 0.4f;
        }else{
            common_chance = 0f;
            rare_chance = 0f;
        }
        if(Random.Float()<common_chance) {
            do {
                reward = Generator.randomMissile();
            } while (reward == null || Challenges.isItemBlocked(reward));
            reward.quantity(Random.Float() < rare_chance ? Random.IntRange(2, 3): 1);
            loots.add(reward);
        }
    }

    //catalyst
    protected void generateCat(float power, ArrayList<Item> loots){
        Item reward = null;
        float common_chance;
        if(power < 2f){
            common_chance = 0f;
        }else if(power < 4f){
            common_chance = (power-2f)*0.1f;
        }else if(power < 8f){
            common_chance = (power-4f)*0.025f + 0.2f;
        }else{
            common_chance = 0f;
        }
        if(Random.Float()<common_chance) {
            reward = new TrinketCatalyst();
            loots.add(reward);
        }
    }

    //scroll, potion
    protected void generateLevelThreeConsumable(float power, ArrayList<Item> loots){
        Item reward = null;
        float common_chance;
        float rare_chance;
        if(power < 4f){
            common_chance = 0f;
            rare_chance = 0f;
        }else if(power < 8f){
            common_chance = 0.1f + (power - 4f) * 0.075f;
            rare_chance = (power - 4f) * 0.05f;
        }else if(power < 16f){
            common_chance = 0.4f + (power - 8f) * 0.02f;
            rare_chance = 0.2f + (power - 8f) * 0.02f;
        }else{
            common_chance = 0.55f;
            rare_chance = 0.35f;
        }
        if(Random.Float()<common_chance) {
            do {
                switch (Random.Int(3)) {
                    case 0:
                        reward = Generator.randomUsingDefaults(Generator.Category.POTION);
                        reward = Reflection.newInstance(ExoticPotion.regToExo.get(reward.getClass()));
                        break;
                    case 1:
                        reward = Generator.randomUsingDefaults(Generator.Category.SCROLL);
                        reward = Reflection.newInstance(ExoticScroll.regToExo.get(reward.getClass()));
                        break;
                    case 2:
                        reward = new Bomb();
                        break;
                }
            } while (reward == null || Challenges.isItemBlocked(reward));
            reward.quantity(Random.Float() < rare_chance ? 2 : 1);
            loots.add(reward);
        }
    }

    //scroll, potion
    protected void generateLevelFourConsumable(float power, ArrayList<Item> loots){
        Item reward = null;
        float common_chance;
        if(power < 8f){
            common_chance = 0f;
        }else if(power < 16f){
            common_chance = 0.05f+(power-8f)*0.0125f;
        }else{
            common_chance = 0.15f+(power-16f)*0.0125f;
        }
        if(Random.Float()<common_chance) {
            reward = new StoneOfEnchantment();
            reward.quantity(1);
            loots.add(reward);
        }
        if(Random.Float()<common_chance) {
            reward = new ScrollOfTransmutation();
            reward.quantity(1);
            loots.add(reward);
        }
        if(Random.Float()<common_chance) {
            reward = new PotionOfExperience();
            reward.quantity(1);
            loots.add(reward);
        }
    }

    @Override
    protected void generatePrize(boolean useDecks){

        Item reward = null;
        float power = showPower();
        power = Math.min(power, 32f);
        if(power < 2f){
            generateLevelOneConsumable(power, items);
            generateLevelTwoConsumable(power, items);
            generateMissile(power, items);
        }
        else if(power < 4f){
            generateLevelOneConsumable(power, items);
            generateLevelTwoConsumable(power, items);
            generateMissile(power, items);
            generateCat(power, items);
        }
        else if(power<8f){
            generateLevelOneConsumable(power, items);
            generateLevelTwoConsumable(power, items);
            generateCat(power, items);
            generateLevelThreeConsumable(power, items);

            if(Random.Float()<0.1f + (power-4f)*0.05f){
                do {
                    switch(Random.Int(4)) {
                        case 0:
                            reward = Generator.randomArmor();
                            break;
                        case 1:
                            reward = Generator.randomWeapon();
                            break;
                        case 2:
                            reward = Generator.randomUsingDefaults(Generator.Category.RING);
                            break;
                        case 3:
                            reward = Generator.randomUsingDefaults(Generator.Category.WAND);
                            break;
                    }
                } while (reward == null || Challenges.isItemBlocked(reward));
                if(reward.isUpgradable()) {
                    for(int i=0; i<3; ++i) {
                        if (Random.Float() < 0.1f + (power - 4f) * 0.05f) {
                            reward.upgrade();
                        }
                    }
                }
                items.add(reward);
            }
        }
        else if(power < 16f){
            generateLevelTwoConsumable(power, items);
            generateLevelThreeConsumable(power, items);
            generateLevelFourConsumable(power, items);
            if(Random.Float()<0.15f+(power-8f)*0.025f){
                do{
                    switch(Random.Int(4)){
                        case 0:
                            reward = Generator.randomArmor();
                            break;
                        case 1:
                            reward = Generator.randomWeapon();
                            break;
                        case 2:
                            reward = Generator.randomUsingDefaults(Generator.Category.RING);
                            break;
                        case 3:
                            reward = Generator.randomUsingDefaults(Generator.Category.WAND);
                            break;
                    }
                }while (reward == null || Challenges.isItemBlocked(reward));
                if(reward.isUpgradable()) {
                    reward.cursed = false;
                    reward.level(1);
                    for(int i=0; i<5; ++i){
                        if(Random.Float()<0.2f+(power-8f)*0.0125f){
                            reward.upgrade();
                        }
                    }
                }
                items.add(reward);
            }
        }
        else{
            generateLevelTwoConsumable(power, items);
            generateLevelThreeConsumable(power, items);
            generateLevelFourConsumable(power, items);
            if(Random.Float()<0.4f){
                do{
                    switch(Random.Int(4)){
                        case 0:
                            reward = Generator.randomArmor();
                            break;
                        case 1:
                            reward = Generator.randomWeapon();
                            break;
                        case 2:
                            reward = Generator.randomUsingDefaults(Generator.Category.RING);
                            break;
                        case 3:
                            reward = Generator.randomUsingDefaults(Generator.Category.WAND);
                            break;
                    }
                }while (reward == null || Challenges.isItemBlocked(reward));
                if(reward.isUpgradable()) {
                    reward.cursed = false;
                    reward.level(3);
                    for(int i=0; i<6; ++i){
                        if(Random.Float()<0.36f+(power-16f)*0.0125f){
                            reward.upgrade();
                        }
                    }
                }
                items.add(reward);
            }
        }
    }

    @Override
    public String description(){
        return super.description() + ModifierDesc();
    }

    protected String ModifierDesc(){
        StringBuilder desc = new StringBuilder("\n");

        int lvl;
        lvl = (basicModFactor>>BASE_HEALTH_MOVE)&0x7;
        if(lvl>0) desc.append(Messages.get(this, "health", lvl));
        lvl = (basicModFactor>>BASE_ATTACK_MOVE)&0x7;
        if(lvl>0) desc.append(Messages.get(this, "attack", lvl));
        lvl = (basicModFactor>>BASE_ACCURACY_MOVE)&0x7;
        if(lvl>0) desc.append(Messages.get(this, "accuracy", lvl));
        lvl = (basicModFactor>>BASE_EVASION_MOVE)&0x7;
        if(lvl>0) desc.append(Messages.get(this, "evasion", lvl));
        lvl = (basicModFactor>>BASE_MOVE_SPEED_MOVE)&0x7;
        if(lvl>0) desc.append(Messages.get(this, "move_speed", lvl));
        lvl = (basicModFactor>>BASE_ATTACK_SPEED_MOVE)&0x7;
        if(lvl>0) desc.append(Messages.get(this, "attack_speed", lvl));

        for(int i=0; i<3; ++i){
            lvl = (resistMod>>(2*i))&0x3;
            if(lvl>0) desc.append(Messages.get(this, "resist_"+ (i + 1), lvl));
        }

        for(int i=0; i<8; ++i){
            lvl = (attackMod>>(2*i))&0x3;
            if(lvl>0) desc.append(Messages.get(this, "attack_"+(i + 1), lvl));
        }

        for(int i=0; i<8; ++i){
            lvl = (defendMod>>(2*i))&0x3;
            if(lvl>0) desc.append(Messages.get(this, "defend_"+(i + 1), lvl));
        }

        for(int i=0; i<8; ++i){
            lvl = (trickMod>>(2*i))&0x3;
            if(lvl>0) desc.append(Messages.get(this, "trick_"+(i + 1), lvl));
        }

        desc.append(Messages.get(this, "power", showPower()));


        return desc.toString();
    }


    @Override
    public void rollToDropLoot(){

        if (items != null) {
            for (Item item : items) {
                if (item.quantity() == 0) {
                    item.quantity(1);
                }
                Dungeon.level.drop( item, pos ).sprite.drop();
            }
            items = null;
        }
        super.rollToDropLoot();
    }
}
