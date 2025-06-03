package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.hall;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.MobHard;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.effects.Wound;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ScorpioSprite;
import com.watabou.noosa.Camera;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

//yeah it's already annoying enough
public class ScorpioH extends MobHard {
    {
        viewDistance = 99;

        spriteClass = ScorpioSprite.class;

        HP = HT = 110;
        defenseSkill = 24;

        EXP = 14;
        maxLvl = 27;

        loot = Generator.Category.POTION;
        lootChance = 0.5f;

        properties.add(Property.DEMONIC);
    }

    private int hasAttacked = 0;

    @Override
    public int attackSkill(Char enemy){
        return 36*(5+hasAttacked)/5;
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        int d = super.attackProc(enemy, damage);
        if (Random.Int( 2 ) == 0) {
            Buff.prolong( enemy, Cripple.class, Cripple.DURATION );
        }
        hasAttacked = Math.min(8, ++hasAttacked);
        if(canHeadShot(enemy)) {
            d = Math.max(Math.min(enemy.HP - 1, enemy.HT * 2 / 3), d + (enemy.drRoll() + enemy.drRoll()) * hasAttacked / 24);
            Wound.hit(enemy);
            enemy.sprite.showStatus(CharSprite.NEGATIVE, M.L(this, "head_shot"));
            enemy.sprite.bloodBurstA(sprite.center(), enemy.HT);
            if(enemy instanceof Hero){
                Camera.main.shake(4f, 0.4f);
            }
        }else {
            d += (enemy.drRoll() + enemy.drRoll()) * hasAttacked / 24;
            if(hasAttacked > 3) Buff.affect(enemy, Blindness.class, 3f);
        }
        return d;
    }

    protected boolean canHeadShot(Char enemy){
        return hasAttacked > 4 && !enemy.fieldOfView[pos];
    }

    @Override
    public void move(int step, boolean traveling){
        super.move(step, traveling);
        hasAttacked=Math.max(0,--hasAttacked);
    }

    @Override
    public void storeInBundle(Bundle b){
        super.storeInBundle(b);
        b.put("shots", hasAttacked);
    }

    @Override
    public void restoreFromBundle(Bundle b){
        super.restoreFromBundle(b);
        hasAttacked = b.getInt("shots");
    }

    @Override
    public void die(Object cause){
        super.die(cause);
        RipperH rh = new RipperH();
        rh.pos = Dungeon.level.randomRespawnCell(rh);
        if(!Dungeon.level.insideMap(rh.pos)){
            rh.pos = this.pos;
        }
        GameScene.add(rh);
    }

    @Override
    public void rollToDropLoot(){
        if (Dungeon.hero.lvl <= maxLvl + 2){
            float chance = 0.04f;
            chance *= RingOfWealth.dropChanceMultiplier( Dungeon.hero );
            chance = Math.min(0.1f, chance);
            if(Random.Float()<chance){
                RingOfSharpshooting r=new RingOfSharpshooting();
                r.level(Random.chances(new float[]{0.3f - 2f*chance, 0.4f + chance, 0.2f+chance/2f, 0.1f + chance/2f}));
                Dungeon.level.drop(r, pos).sprite.drop();
            }
        }

        super.rollToDropLoot();

    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 30, 40 );
    }


    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 16);
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);
        return !Dungeon.level.adjacent( pos, enemy.pos ) && attack.collisionPos == enemy.pos;
    }

    @Override
    protected boolean getCloser( int target ) {
        if (state == HUNTING) {
            return enemySeen && getFurther( target );
        } else {
            return super.getCloser( target );
        }
    }

    @Override
    public void aggro(Char ch) {
        //cannot be aggroed to something it can't see
        if (ch == null || fieldOfView == null || fieldOfView[ch.pos]) {
            super.aggro(ch);
        }
    }

    @Override
    public Item createLoot() {
        Class<?extends Potion> loot;
        do{
            loot = (Class<? extends Potion>) Random.oneOf(Generator.Category.POTION.classes);
        } while (loot == PotionOfHealing.class || loot == PotionOfStrength.class);

        return Reflection.newInstance(loot);
    }
}
