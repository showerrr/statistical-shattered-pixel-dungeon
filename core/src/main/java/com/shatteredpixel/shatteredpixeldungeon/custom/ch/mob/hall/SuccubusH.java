package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.hall;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.MobHard;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.RangeMap;
import com.shatteredpixel.shatteredpixeldungeon.custom.visuals.effects.BeamCustom;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SuccubusSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class SuccubusH extends MobHard {
    private int blinkCooldown = 0;
    {
        spriteClass = SuccubusSprite.class;

        HP = HT = 80;
        defenseSkill = 25;
        viewDistance = Light.DISTANCE;

        EXP = 12;
        maxLvl = 25;

        loot = Generator.Category.SCROLL;
        lootChance = 0.33f;

        properties.add(Property.DEMONIC);
    }

    @Override
    public int damageRoll(){
        if(enemy != null){
            return Random.IntRange(25, 30) + (enemy.drRoll()+enemy.drRoll())/3;
        }else{
            return Random.IntRange(25, 30);
        }
    }

    @Override
    public int attackProc(Char enemy, int damage){
        int dmg = super.attackProc(enemy, damage);

        //super content
        if (enemy.buff(Charm.class) != null ){
            int shield = (HP - HT) + (5 + damage);
            if (shield > 0){
                HP = HT;
                Buff.affect(this, Barrier.class).setShield(shield);
            } else {
                HP += 5 + damage;
            }
            if (Dungeon.level.heroFOV[pos]) {
                sprite.emitter().burst( Speck.factory( Speck.HEALING ), 2 );
                Sample.INSTANCE.play( Assets.Sounds.CHARMS );
            }
        } else if (Random.Int( 3 ) == 0) {
            Charm c = Buff.affect( enemy, Charm.class, Charm.DURATION/2f );
            c.object = id();
            c.ignoreNextHit = true; //so that the -5 duration from succubus hit is ignored
            if (Dungeon.level.heroFOV[enemy.pos]) {
                enemy.sprite.centerEmitter().start(Speck.factory(Speck.HEART), 0.2f, 5);
                Sample.INSTANCE.play(Assets.Sounds.CHARMS);
            }
        }

        for(Char ch: Actor.chars()){
            if(ch.alignment == Alignment.ENEMY){
                if(ch!=this){
                    int dist = RangeMap.manhattanDist(this.pos, ch.pos);
                    if(dist < 9){
                        int heal = 30/(1+dist/2);
                        Buff.affect(ch, Healing.class).setHeal(heal, 1, 0);
                        ch.sprite.showStatus(CharSprite.POSITIVE, "+%d", heal);
                        sprite.parent.add(new BeamCustom(sprite.center(), ch.sprite.center(), Effects.Type.HEALTH_RAY).setDiameter(0.5f).setLifespan(0.25f));
                    }
                }
            }
        }
        for(Mob m: Dungeon.level.mobs.toArray(new Mob[0])){
            if(m instanceof SuccubusH && m != this){
                m.beckon(pos);
            }
        }
        return dmg;
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
    protected boolean getCloser( int target ) {
        if (fieldOfView[target] && Dungeon.level.distance( pos, target ) > 2 && blinkCooldown <= 0) {

            if (blink( target )) {
                spend(-1 / speed());
                return true;
            } else {
                return false;
            }

        } else {

            blinkCooldown--;
            return super.getCloser( target );

        }
    }

    private boolean blink( int target ) {

        Ballistica route = new Ballistica( pos, target, Ballistica.PROJECTILE);
        int cell = route.collisionPos;

        //can't occupy the same cell as another char, so move back one.
        if (Actor.findChar( cell ) != null && cell != this.pos)
            cell = route.path.get(route.dist-1);

        if (Dungeon.level.avoid[ cell ] || (properties().contains(Property.LARGE) && !Dungeon.level.openSpace[cell])){
            ArrayList<Integer> candidates = new ArrayList<>();
            for (int n : PathFinder.NEIGHBOURS8) {
                cell = route.collisionPos + n;
                if (Dungeon.level.passable[cell]
                        && Actor.findChar( cell ) == null
                        && (!properties().contains(Property.LARGE) || Dungeon.level.openSpace[cell])) {
                    candidates.add( cell );
                }
            }
            if (candidates.size() > 0)
                cell = Random.element(candidates);
            else {
                blinkCooldown = Random.IntRange(4, 6);
                return false;
            }
        }

        ScrollOfTeleportation.appear( this, cell );

        blinkCooldown = Random.IntRange(4, 6);
        return true;
    }

    @Override
    public int attackSkill( Char target ) {
        return 40;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 10);
    }

    @Override
    public Item createLoot() {
        Class<?extends Scroll> loot;
        do{
            loot = (Class<? extends Scroll>) Random.oneOf(Generator.Category.SCROLL.classes);
        } while (loot == ScrollOfIdentify.class || loot == ScrollOfUpgrade.class);

        return Reflection.newInstance(loot);
    }

    {
        immunities.add( Charm.class );
    }

    private static final String BLINK_CD = "blink_cd";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(BLINK_CD, blinkCooldown);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        blinkCooldown = bundle.getInt(BLINK_CD);
    }

}
