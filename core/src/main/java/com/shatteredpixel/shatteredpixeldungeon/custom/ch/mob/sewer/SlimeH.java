package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.sewer;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Slime;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.MobHard;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SlimeSprite;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

public class SlimeH extends MobHard {
    {
        spriteClass = SlimeSprite.class;

        HP = HT = 20;
        defenseSkill = 5;

        EXP = 4;
        maxLvl = 9;

        lootChance = 0.4f; //by default, see lootChance()
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 2, 5 );
    }

    @Override
    public int attackSkill( Char target ) {
        return 12;
    }

    @Override
    public float lootChance(){
        //each drop makes future drops 1/3 as likely
        // so loot chance looks like: 1/5, 1/15, 1/45, 1/135, etc.
        return super.lootChance() * (float)Math.pow(1/3f, Dungeon.LimitedDrops.SLIME_WEP.count);
    }

    @Override
    public Item createLoot() {
        Dungeon.LimitedDrops.SLIME_WEP.count++;
        Generator.Category c = Generator.Category.WEP_T2;
        MeleeWeapon w = (MeleeWeapon) Reflection.newInstance(c.classes[Random.chances(c.probs)]);
        w.random();
        w.level(0);
        return w;
    }

    @Override
    public int attackProc(Char enemy, int damage){
        Ballistica trajectory = new Ballistica(pos, enemy.pos, Ballistica.STOP_TARGET);
        trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size()-1), Ballistica.PROJECTILE);
        WandOfBlastWave.throwChar(enemy, trajectory, 2, true, false, SlimeH.class);
        return super.attackProc(enemy,damage);
    }

    @Override
    public void damage(int damage, Object src){
        damage = Math.min(damage,5);
        super.damage(damage, src);
    }
}
