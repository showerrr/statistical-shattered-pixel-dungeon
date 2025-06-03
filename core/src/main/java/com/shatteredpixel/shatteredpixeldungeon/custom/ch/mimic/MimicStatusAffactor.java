package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mimic;

import com.badlogic.gdx.utils.IntMap;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.watabou.utils.Bundle;

public class MimicStatusAffactor extends Buff {
    {
        this.announced = false;
        actPriority = VFX_PRIO;
        revivePersists = true;
    }

    private boolean affected = false;
    private static final String AFFECTED = "affected";
    private int lastLevel = 0;
    private static final String LAST_LEVEL = "last_level";

    @Override
    public boolean act(){
        if(!affected){

            affected = true;
            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])){
                if(mob instanceof MimicForChallenge){
                    Buff.affect(mob, MimicStatusShower.class);
                }
            }
        }
        if(lastLevel != Dungeon.depth){
            affected = false;
            lastLevel = Dungeon.depth;
        }
        spend(TICK);
        return true;
    }
    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( AFFECTED, affected );
        bundle.put( LAST_LEVEL, lastLevel );

    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        affected = bundle.getBoolean( AFFECTED );
        lastLevel = bundle.getInt( LAST_LEVEL );
    }
}
