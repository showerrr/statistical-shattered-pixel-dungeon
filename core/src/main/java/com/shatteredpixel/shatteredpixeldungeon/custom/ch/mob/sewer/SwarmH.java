package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.sewer;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.MobHard;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.RangeMap;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SwarmSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SwarmH extends MobHard {
    {
        spriteClass = SwarmSprite.class;

        HP = HT = 50;
        defenseSkill = 5;

        EXP = 3;
        maxLvl = 9;

        flying = true;

        loot = new PotionOfHealing();
        lootChance = 0.1667f; //by default, see rollToDropLoot()
    }

    @Override
    public void die(Object cause){
        for(int i=1;i<4;++i) {
            int[] area = RangeMap.manhattanCircle(pos, i);
            for (int j : area) {
                Char ch = findChar(j);
                if(ch!=null){
                    if(ch.alignment == Alignment.ENEMY){
                        Buff.affect(ch, Healing.class).setHeal(12-3*i, 1, 0);
                        ch.sprite.showStatus(CharSprite.POSITIVE, "%d",12-3*i);
                    }
                }
            }
        }
        super.die(cause);
    }

    private static final float SPLIT_DELAY	= 1f;

    int generation	= 0;

    private static final String GENERATION	= "generation_descend";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( GENERATION, generation );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        generation = bundle.getInt( GENERATION );
        if (generation > 0) EXP = 0;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 1, 4 );
    }

    @Override
    public int defenseProc( Char enemy, int damage ) {

            if (HP >= damage + 2 && generation < 4) {
                ArrayList<Integer> candidates = new ArrayList<>();
                boolean[] solid = Dungeon.level.solid;

                int[] neighbours = {pos + 1, pos - 1, pos + Dungeon.level.width(), pos - Dungeon.level.width()};
                for (int n : neighbours) {
                    if (!solid[n] && Actor.findChar(n) == null) {
                        candidates.add(n);
                    }
                }

                if (candidates.size() > 0) {

                    SwarmH clone = split();
                    clone.HP = (HP - damage) / 2;
                    clone.pos = Random.element(candidates);
                    candidates.remove((Integer) clone.pos);
                    clone.state = clone.HUNTING;
                    clone.maxLvl = -9999;

                    Dungeon.level.occupyCell(clone);

                    GameScene.add(clone, SPLIT_DELAY);
                    Actor.addDelayed(new Pushing(clone, pos, clone.pos), -1);

                    HP -= clone.HP;
                }
            }


        return super.defenseProc(enemy, damage);
    }

    @Override
    public int attackSkill( Char target ) {
        return 10;
    }

    private SwarmH split() {
        SwarmH clone = new SwarmH();
        clone.generation = generation + 1;
        clone.EXP = 0;
        if (buff( Burning.class ) != null) {
            Buff.affect( clone, Burning.class ).reignite( clone );
        }
        if (buff( Poison.class ) != null) {
            Buff.affect( clone, Poison.class ).set(2);
        }
        if (buff(Corruption.class ) != null) {
            Buff.affect( clone, Corruption.class);
        }
        for (Buff b : buffs(ChampionEnemy.class)){
            Buff.affect( clone, b.getClass());
        }
        return clone;
    }

    @Override
    public void rollToDropLoot() {
        lootChance = 1f/(6 * (generation+1) );
        lootChance *= (5f - Dungeon.LimitedDrops.SWARM_HP.count) / 5f;
        super.rollToDropLoot();
    }

    @Override
    public Item createLoot(){
        Dungeon.LimitedDrops.SWARM_HP.count++;
        return super.createLoot();
    }

}
