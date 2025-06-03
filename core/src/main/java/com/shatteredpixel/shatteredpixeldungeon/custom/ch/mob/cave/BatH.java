package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.cave;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.MobHard;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.RangeMap;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.timing.VirtualActor;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEvasion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BatSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;

public class BatH extends MobHard {
    {
        EXP = 10;
        defenseSkill = 20;
        HT=HP=40;
        HUNTING = new Hunting();

        spriteClass = BatSprite.class;

        baseSpeed = 2f;

        maxLvl = 15;

        flying = true;

        loot = new PotionOfHealing();
        lootChance = 0.1667f; //by default, see lootChance()
    }

    @Override
    public float attackDelay(){
        return super.attackDelay()/2f;
    }

    boolean justAttacked = false;

    @Override
    public void move(int step, boolean traveling){
        justAttacked = false;
        super.move(step, traveling);
    }

    @Override
    protected boolean doAttack(Char enemy){
        justAttacked = true;
        return super.doAttack(enemy);
    }

    @Override
    protected boolean canAttack(Char enemy){
        return super.canAttack(enemy) && !justAttacked;
    }

    @Override
    public void storeInBundle(Bundle b){
        super.storeInBundle(b);
        b.put("justAttacked", justAttacked);
    }

    @Override
    public void restoreFromBundle(Bundle b){
        super.restoreFromBundle(b);
        justAttacked = b.getBoolean("justAttacked");
    }

    @Override
    public int defenseSkill(Char enemy){
        int availableCells = 0;
        for(int i: PathFinder.NEIGHBOURS8){
            if(canPass(i+pos) && findChar(i+pos)==null){
                ++availableCells;
            }
        }
        return Math.round(super.defenseSkill(enemy) * availableCells / 3f);
    }

    @Override
    public int attackProc(Char enemy, int damage){

        int reg = Math.min( damage , HT - HP );

        if (reg > 0) {
            HP += reg;
            sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
        }

        return super.attackProc( enemy, damage );
    }

    protected boolean near(int pa, int pb){
        return Dungeon.level.adjacent(pa,pb);
    }

    protected boolean canPass(int cell){
        //doors are solid but passable!
        return !(Dungeon.level.solid[cell] && !Dungeon.level.passable[cell]);
    }

    protected void updateFOV(){
        Dungeon.level.updateFieldOfView(this, fieldOfView);
    }


    @Override
    public void rollToDropLoot(){
        if (Dungeon.hero.lvl <= maxLvl + 2){
            float chance = 0.02f;
            chance *= RingOfWealth.dropChanceMultiplier( Dungeon.hero );
            chance = Math.min(0.03f, chance);
            if(Random.Float()<chance){
                Ring r;
                r = Random.Int(2)==0? new RingOfHaste() : new RingOfEvasion();
                r.level(Random.chances(new float[]{0.6f - 2f*chance, 0.25f + chance, 0.15f+chance}));
                Dungeon.level.drop(r, pos).sprite.drop();
            }
        }

        super.rollToDropLoot();

    }

    private class Hunting extends Mob.Hunting{
        @Override
        public boolean act( boolean enemyInFOV, boolean justAlerted ) {
            //if can see enemy, keep 2 distance if healthy. if in danger, just fight
            if(enemy!=null && enemyInFOV && HP*2>=HT){
                //near, not attacked
                if(canAttack(enemy)){
                    target = enemy.pos;
                    return doAttack(enemy);
                    //near, but can't attack, means has attacked or some reason, should flee
                }else if(near(enemy.pos, pos)){
                    int fleePos = -1;
                    //int w = Dungeon.level.width();

                    //cache score map. find char is O(k) where k=number of chars, should avoid duplicate calls.
                    int[] allMap = RangeMap.centeredRect(pos, 2, 2);
                    int[] aroundMap = RangeMap.centeredRect(pos, 1, 1);

                    int[] allScoreMap = allMap.clone();
                    int[] aroundScoreMap = aroundMap.clone();

                    //build score map. can pass 1,  !can pass = 0;
                    for(int i=0;i<allMap.length;++i){
                        if(canPass(allMap[i]) && findChar(allMap[i])==null){
                            allScoreMap[i]=1;
                        }else{
                            allScoreMap[i]=0;
                        }
                    }
                    Arrays.fill(aroundScoreMap, 0);
                    for(int j=0;j<aroundScoreMap.length;++j){
                        //the most important is to avoid attacks
                        if(canPass(aroundMap[j]) && findChar(aroundMap[j])==null){
                            aroundScoreMap[j] += near(enemy.pos, aroundMap[j]) ? 0:100;
                        }else{
                            //these cells are not available, -10000 to mark
                            aroundScoreMap[j] -= 100;
                        }
                        //then consider the next move: how much empty cells around there.
                        for(int i=0;i<allScoreMap.length;++i){
                            if(near(allMap[i], aroundMap[j])){
                                aroundScoreMap[j] += allScoreMap[i];
                            }
                        }
                    }
                    int maxIndex = 0; int maxScore = 0;
                    for(int i=0;i<aroundScoreMap.length;++i){
                        if(aroundScoreMap[i]>maxScore){
                            maxScore = aroundScoreMap[i];
                            maxIndex = i;
                        }
                    }
                    if(maxScore>0){
                        fleePos = aroundMap[maxIndex];
                    }
                    //if there is at least one tile to flee
                    if(fleePos!=-1) {
                        justAttacked = false;
                        spend(TICK/speed());
                        pos = fleePos;
                        move(fleePos);
                        updateFOV();
                        enemySeen = enemyInFOV;
                        return moveSprite(pos, fleePos);
                    }
                    //no way to go, attack!
                    justAttacked = false;
                    return super.act(enemyInFOV, justAlerted);

                }else if(Dungeon.level.distance(enemy.pos, pos)==2){
                    Char oldEnemy = enemy;
                    enemy = chooseEnemy();
                    if(enemy == null){enemy = oldEnemy;}

                    //if another enemy is nearer
                    if(Dungeon.level.distance(enemy.pos, pos)<=1){
                        //surrounded by enemies, just do whatever can do
                        justAttacked = false;
                        return super.act(enemyInFOV, justAlerted);

                    }

                    //no adjacent enemy, do nothing
                    updateFOV();
                    enemySeen = enemyInFOV;
                    spend(TICK/speed());
                    return true;
                }else{
                    //do as default
                }
            }else{
                justAttacked = false;
            }
            //default cases
            return super.act(enemyInFOV, justAlerted);
        }
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 5, 18 );
    }

    @Override
    public int attackSkill( Char target ) {
        return 16;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 4);
    }

    @Override
    public float lootChance(){
        return super.lootChance() * ((7f - Dungeon.LimitedDrops.BAT_HP.count) / 7f);
    }

    @Override
    public Item createLoot(){
        Dungeon.LimitedDrops.BAT_HP.count++;
        return super.createLoot();
    }

}
