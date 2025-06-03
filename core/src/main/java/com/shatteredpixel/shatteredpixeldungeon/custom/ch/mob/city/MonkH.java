package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.city;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.MobHard;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.BallisticaFloat;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.GME;
import com.shatteredpixel.shatteredpixeldungeon.custom.visuals.MissileSpriteCustom;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gauntlet;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gloves;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MonkSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class MonkH extends MobHard {
    {
        EXP=13;

        spriteClass = MonkSprite.class;

        HP = HT = 70;
        defenseSkill = 30;

        maxLvl = 21;

        loot = new Food();
        lootChance = 0.083f;

        properties.add(Property.UNDEAD);
    }
    @Override
    public float attackDelay(){ return 0.45f*super.attackDelay();}

    protected int hitsToDisarm = 0;

    @Override
    public int attackProc(Char enemy, int damage){
        if (enemy == Dungeon.hero) {

            Hero hero = Dungeon.hero;
            KindOfWeapon weapon = hero.belongings.weapon;

            if (weapon != null
                    && !(weapon instanceof Gloves)
                    && !(weapon instanceof Gauntlet)
                    && !weapon.cursed) {
                if (hitsToDisarm == 0) hitsToDisarm = Random.NormalIntRange(5, 7);

                if (--hitsToDisarm == 0) {
                    hero.belongings.weapon = null;
                    Dungeon.quickslot.convertToPlaceholder(weapon);
                    Item.updateQuickslot();
                    GLog.n(M.L(this, "disarm", weapon.name()));

                    BallisticaFloat ba = new BallisticaFloat(hero.pos, GME.angle(pos, hero.pos) + Random.Float(-22.5f, 22.5f), 6, Ballistica.PROJECTILE);
                    ((MissileSpriteCustom) hero.sprite.parent.recycle(MissileSpriteCustom.class)).
                            reset(hero.sprite,
                                    ba.collisionPosI,
                                    weapon, 0.6f, 1.25f,
                                    new Callback() {
                                        @Override
                                        public void call() {
                                            Dungeon.level.drop(weapon, ba.collisionPosI).sprite.drop();
                                        }
                                    });
                }
            }
        }
        return super.attackProc(enemy, damage);
    }

    private static String FOCUS_COOLDOWN = "focus_cooldown";

    @Override
    public void storeInBundle(Bundle b){
        super.storeInBundle(b);
        b.put("hitToDisarm", hitsToDisarm);
        b.put( FOCUS_COOLDOWN, focusCooldown );
    }
    @Override
    public void restoreFromBundle(Bundle b){
        super.restoreFromBundle(b);
        hitsToDisarm = b.getInt("hitToDisarm");
        focusCooldown = b.getInt( FOCUS_COOLDOWN );
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 12, 25 );
    }

    @Override
    public int attackSkill( Char target ) {
        return 30;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 2);
    }

    @Override
    public void rollToDropLoot() {
        Imp.Quest.process( this );

        super.rollToDropLoot();
//        super.rollToDropLoot();
    }

    protected float focusCooldown = 0;

    @Override
    protected boolean act() {
        boolean result = super.act();
        if (buff(MonkH.Focus.class) == null && state == HUNTING && focusCooldown <= 0) {
            Buff.affect( this, MonkH.Focus.class );
        }
        return result;
    }

    @Override
    protected void spend( float time ) {
        focusCooldown -= time;
        super.spend( time );
    }

    @Override
    public void move( int step, boolean travelling) {
        // moving reduces cooldown by an additional 0.67, giving a total reduction of 1.67f.
        // basically monks will become focused notably faster if you kite them.
        if (travelling) focusCooldown -= 0.67f;
        super.move( step, travelling);
    }

    @Override
    public int defenseSkill( Char enemy ) {
        if (buff(MonkH.Focus.class) != null && paralysed == 0 && state != SLEEPING){
            return INFINITE_EVASION;
        }
        return super.defenseSkill( enemy );
    }

    @Override
    public String defenseVerb() {
        MonkH.Focus f = buff(MonkH.Focus.class);
        if (f == null) {
            return super.defenseVerb();
        } else {
            f.detach();
            if (sprite != null && sprite.visible) {
                Sample.INSTANCE.play(Assets.Sounds.HIT_PARRY, 1, Random.Float(0.96f, 1.05f));
            }
            focusCooldown = Random.NormalFloat( 6, 7 );
            return Messages.get(this, "parried");
        }
    }

    public static class Focus extends Buff {

        {
            type = buffType.POSITIVE;
            announced = true;
        }

        @Override
        public int icon() {
            return BuffIndicator.MIND_VISION;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.25f, 1.5f, 1f);
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc");
        }
    }
}
