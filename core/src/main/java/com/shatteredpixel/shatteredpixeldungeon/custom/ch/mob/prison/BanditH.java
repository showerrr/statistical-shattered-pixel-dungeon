package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.prison;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BanditSprite;
import com.watabou.utils.Random;

public class BanditH extends ThiefH{
    {
        spriteClass = BanditSprite.class;
        EXP = 7;
        lootChance = 1f;
    }
    @Override
    public float speed(){
        return super.speed() * (item != null?1.2f:1f);
    }

    @Override
    protected boolean steal( Hero hero ) {
        if (super.steal( hero )) {

            Buff.prolong( hero, Blindness.class, Blindness.DURATION );
            Buff.prolong( hero, Cripple.class, Cripple.DURATION );
            Dungeon.observe();

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void rollToDropLoot(){
        if (Dungeon.hero.lvl <= maxLvl + 2){
            float chance = 0.5f;
            chance *= RingOfWealth.dropChanceMultiplier( Dungeon.hero );
            if(Random.Float()<chance){
                Dungeon.level.drop(new Blindweed.Seed(), pos).sprite.drop();
            }
        }

        super.rollToDropLoot();

    }

}
