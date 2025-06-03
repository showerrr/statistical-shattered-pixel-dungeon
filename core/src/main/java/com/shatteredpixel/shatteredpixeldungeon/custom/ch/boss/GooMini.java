package com.shatteredpixel.shatteredpixeldungeon.custom.ch.boss;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.GME;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CausticSlimeSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class GooMini extends Mob {
    {
        spriteClass = CausticSlimeSprite.class;

        HT = HP = 12;

        EXP = 0;
        maxLvl = -999;

        defenseSkill = 6;

        baseSpeed = 1.5f;
    }

    @Override
    public boolean act(){
        Buff b = buff(Timer.class);
        if (b == null) {
            explode(true);
            die(this);
            return true;
        }else{
            if(b.cooldown() < 2f){
                sprite.showStatus( CharSprite.NEGATIVE, Messages.get(this, "!!!") );
            }
        }

        return super.act();
    }

    protected boolean explode(boolean empower){
        int[] area = empower ?  GME.NEIGHBOURS20() : PathFinder.NEIGHBOURS4;
        for(int n: area){
            if(!Dungeon.level.insideMap(n+pos)) continue;
            Char ch=findChar(n+pos);
            if(ch!=null){
                if(!(ch instanceof GooHard || ch instanceof GooMini)) {
                    ch.damage(Math.round((empower?3f:1.5f)*(Math.max(damageRoll(),damageRoll()))), this);
                    Buff.affect(ch, Vulnerable.class, empower?9f:3f);
                }
                if (ch == Dungeon.hero && !ch.isAlive()) {
                    Dungeon.fail(getClass());
                }
            }

            Dungeon.level.setCellToWater(true, n+pos);

            CellEmitter.get(pos+n).burst(ElmoParticle.FACTORY, 5);

        }

        Dungeon.level.setCellToWater(true, pos);

        return true;
    }

    @Override
    public void die(Object cause){
        if(buff(Timer.class)!=null){
            explode(false);
        }
        super.die(cause);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 2, 4 );
    }

    @Override
    public int attackSkill( Char target ) {
        return 12;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(1, 1);
    }

}
