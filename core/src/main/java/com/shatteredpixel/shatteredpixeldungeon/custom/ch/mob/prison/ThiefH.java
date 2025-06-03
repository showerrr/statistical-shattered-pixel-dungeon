package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.prison;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thief;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.MobHard;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ThiefSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class ThiefH extends MobHard {
    public Item item;
    {
        EXP = 6;
        HT = HP = 21;

        spriteClass = ThiefSprite.class;

        defenseSkill = 12;

        maxLvl = 11;

        loot = Random.oneOf(Generator.Category.RING, Generator.Category.ARTIFACT);
        lootChance = 0.03f; //initially, see lootChance()

        WANDERING = new ThiefH.Wandering();
        FLEEING = new ThiefH.Fleeing();

        properties.add(Property.UNDEAD);
    }

    @Override
    protected boolean canAttack(Char enemy){
        if(enemy!=null && enemySeen && Dungeon.level.distance(pos, enemy.pos) < 3){
            return true;
        }
        return super.canAttack(enemy);
    }

    @Override
    public int attackProc(Char enemy, int damage){
        if (alignment == Alignment.ENEMY && item == null
                && enemy instanceof Hero && steal( (Hero)enemy )) {
            state = FLEEING;
        }
        for(Mob m: Dungeon.level.mobs.toArray(new Mob[0])){
            if(m!=this && m.alignment == Alignment.ENEMY){
                if(m instanceof ThiefH){
                    if(((ThiefH) m).item == null){
                        Buff.affect(m, Haste.class, 5f);
                        m.beckon(enemy.pos);
                    }
                }
            }
        }
        return super.attackProc(enemy, damage);
    }

    protected boolean steal( Hero hero ){
        boolean stole;

        Item toSteal = hero.belongings.randomUnequipped();

        if (toSteal != null && !toSteal.unique && toSteal.level() < 1 ) {

            GLog.w( Messages.get(Thief.class, "stole", toSteal.name()) );
            if (!toSteal.stackable) {
                Dungeon.quickslot.convertToPlaceholder(toSteal);
            }
            Item.updateQuickslot();

            item = toSteal.detach( hero.belongings.backpack );
            if (item instanceof Honeypot){
                item = ((Honeypot)item).shatter(this, this.pos);
            } else if (item instanceof Honeypot.ShatteredPot) {
                ((Honeypot.ShatteredPot)item).pickupPot(this);
            }

            stole = true;
        } else {
            stole = false;
        }
        if(stole) Buff.detach(this, Haste.class);
        return stole;
    }

    @Override
    public void rollToDropLoot(){
        if (Dungeon.hero.lvl <= maxLvl + 2){
            float chance = 0.15f;
            chance *= RingOfWealth.dropChanceMultiplier( Dungeon.hero );
            if(Random.Float()<chance){
                Dungeon.level.drop(new PotionOfHaste(), pos).sprite.drop();
            }
        }

        if (item != null) {
            Dungeon.level.drop( item, pos ).sprite.drop();
            //updates position
            if (item instanceof Honeypot.ShatteredPot) ((Honeypot.ShatteredPot)item).dropPot( this, pos );
            item = null;
        }

        super.rollToDropLoot();

    }

    private static final String ITEM = "item";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( ITEM, item );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        item = (Item)bundle.get( ITEM );
    }

    @Override
    public float speed() {
        if (item != null) return (5*super.speed())/6;
        else return super.speed();
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 1, 10 );
    }

    @Override
    public float attackDelay() {
        return super.attackDelay()*0.5f;
    }

    @Override
    public float lootChance() {
        //each drop makes future drops 1/3 as likely
        // so loot chance looks like: 1/33, 1/100, 1/300, 1/900, etc.
        return super.lootChance() * (float)Math.pow(1/3f, Dungeon.LimitedDrops.THEIF_MISC.count);
    }

    @Override
    public Item createLoot() {
        Dungeon.LimitedDrops.THEIF_MISC.count++;
        return super.createLoot();
    }

    @Override
    public int attackSkill( Char target ) {
        return 12;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 3);
    }

    @Override
    public int defenseProc(Char enemy, int damage) {
        if (state == FLEEING) {
            Dungeon.level.drop( new Gold(), pos ).sprite.drop();
        }

        return super.defenseProc(enemy, damage);
    }

    @Override
    public String description() {
        String desc = super.description();

        if (item != null) {
            desc += Messages.get(this, "carries", item.name() );
        }

        return desc;
    }

    private class Wandering extends Mob.Wandering {

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            super.act(enemyInFOV, justAlerted);

            //if an enemy is just noticed and the thief posses an item, run, don't fight.
            if (state == HUNTING && item != null){
                state = FLEEING;
            }

            return true;
        }
    }

    private class Fleeing extends Mob.Fleeing {
        @Override
        protected void nowhereToRun() {
            if (buff( Terror.class ) == null
                    && buff( Dread.class ) == null
                    && buffs( AllyBuff.class ).isEmpty() ) {
                if (enemySeen) {
                    sprite.showStatus(CharSprite.NEGATIVE, Messages.get(Mob.class, "rage"));
                    state = HUNTING;
                } else if (item != null
                        && !Dungeon.level.heroFOV[pos]
                        && Dungeon.level.distance(Dungeon.hero.pos, pos) >= 6) {

                    int count = 32;
                    int newPos;
                    do {
                        newPos = Dungeon.level.randomRespawnCell( ThiefH.this );
                        if (count-- <= 0) {
                            break;
                        }
                    } while (newPos == -1 || Dungeon.level.heroFOV[newPos] || Dungeon.level.distance(newPos, pos) < (count/3));

                    if (newPos != -1) {

                        if (Dungeon.level.heroFOV[pos]) CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6);
                        pos = newPos;
                        sprite.place( pos );
                        sprite.visible = Dungeon.level.heroFOV[pos];
                        if (Dungeon.level.heroFOV[pos]) CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6);

                    }

                    if (item != null) GLog.n( Messages.get(Thief.class, "escapes", item.name()));
                    item = null;
                    state = WANDERING;
                } else {
                    state = WANDERING;
                }
            } else {
                super.nowhereToRun();
            }
        }
    }

}
