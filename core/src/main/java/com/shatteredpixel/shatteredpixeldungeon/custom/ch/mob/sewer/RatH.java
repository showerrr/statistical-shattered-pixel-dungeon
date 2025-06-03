package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.sewer;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.MobHard;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class RatH extends MobHard {
    {
        spriteClass = RatSprite.class;

        HP = HT = 8;
        defenseSkill = 2;

        maxLvl = 5;

        EXP = 1;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 1, 4 );
    }

    @Override
    public int attackSkill( Char target ) {
        return 8;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 1);
    }

    private static final String RAT_ALLY = "rat_ally";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        if (alignment == Alignment.ALLY) bundle.put(RAT_ALLY, true);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(RAT_ALLY)) alignment = Alignment.ALLY;
    }

    @Override
    public void die(Object cause) {

        int toSummon = Random.chances(new float[]{0.85f, 0.15f}) + 1;

        while (toSummon-- > 0) {
            final AlbinoH white = new AlbinoH();
            white.pos = Dungeon.level.randomRespawnCell(white);
            if(!Dungeon.level.insideMap(white.pos)){
                continue;
            }
            GameScene.add(white, 1f);
            white.beckon(pos);

            MagicMissile.boltFromChar(sprite.parent,
                    MagicMissile.MAGIC_MISSILE,
                    sprite,
                    new Ballistica(pos, white.pos, Ballistica.STOP_TARGET).collisionPos,
                    new Callback() {
                        @Override
                        public void call() {
                            new Flare(5, 25).color(0xFF4488, true).show(white.sprite, 2f);
                        }
                    });
        }

        super.die(cause);
    }
}
