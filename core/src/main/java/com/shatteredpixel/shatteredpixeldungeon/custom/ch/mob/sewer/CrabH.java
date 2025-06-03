package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.sewer;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Crab;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.MobHard;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CrabSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class CrabH extends MobHard {
    {
        spriteClass = CrabSprite.class;

        HP = HT = 15;
        defenseSkill = 5;
        baseSpeed = 2f;

        EXP = 4;
        maxLvl = 9;

        loot = new MysteryMeat();
        lootChance = 0.167f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 1, 7 );
    }

    @Override
    public int attackSkill( Char target ) {
        return 12;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 4);
    }

    private float deduced = 0;
    @Override
    public int attackProc(Char enemy, int damage){
        Hunger h = enemy.buff(Hunger.class);
        if(h!=null){
            float reduce = -Random.Float(5f, 12f);
            h.satisfy(reduce);
            deduced += -reduce;
            if(deduced > 20f){
                Buff.affect(enemy, Weakness.class, (deduced-20f)/5f);
            }
            enemy.sprite.showStatus(0x505050, "%d", (int)reduce);
        }
        return super.attackProc(enemy, damage);
    }

    @Override
    public void storeInBundle(Bundle b){
        super.storeInBundle(b);
        b.put("hungerDrain", deduced);
    }

    @Override
    public void restoreFromBundle(Bundle b){
        super.restoreFromBundle(b);
        deduced = b.getFloat("hungerDrain");
    }
}
