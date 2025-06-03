package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.prison;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.MobHard;
import com.shatteredpixel.shatteredpixeldungeon.effects.Chains;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.EtherealChains;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GuardSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class GuardH extends MobHard {

    //they can only use their chains INFINITELY
    private boolean chainsUsed = false;
    private float cd = 5f;

    {
        spriteClass = GuardSprite.class;

        HP = HT = 40;
        defenseSkill = 10;

        EXP = 8;
        maxLvl = 14;

        loot = Generator.Category.ARMOR;
        lootChance = 0.2f; //by default, see rollToDropLoot()

        properties.add(Property.UNDEAD);

        HUNTING = new Hunting();
    }

    @Override
    public boolean act(){
        if(chainsUsed && cd<=0f){
            chainsUsed = false;
            cd = 5f;
        }else if(chainsUsed){
            cd -= 1f;
        }
        return super.act();
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(4, 12);
    }

    private boolean chain(int target){
        if (chainsUsed || enemy.properties().contains(Property.IMMOVABLE))
            return false;

        Ballistica chain = new Ballistica(pos, target, Ballistica.PROJECTILE);

        if (chain.collisionPos != enemy.pos
                || chain.path.size() < 2
                || Dungeon.level.pit[chain.path.get(1)])
            return false;
        else {
            int newPos = -1;
            for (int i : chain.subPath(1, chain.dist)){
                if (!Dungeon.level.solid[i] && Actor.findChar(i) == null){
                    newPos = i;
                    break;
                }
            }

            if (newPos == -1){
                return false;
            } else {
                final int newPosFinal = newPos;
                this.target = newPos;
                yell( Messages.get(this, "scorpion") );
                new Item().throwSound();
                Sample.INSTANCE.play( Assets.Sounds.CHAINS );
                sprite.parent.add(new Chains(sprite.center(), enemy.sprite.center(), Effects.Type.CHAIN, new Callback() {
                    public void call() {
                        Actor.addDelayed(new Pushing(enemy, enemy.pos, newPosFinal, new Callback(){
                            public void call() {
                                enemy.pos = newPosFinal;
                                Dungeon.level.occupyCell(enemy);
                                Cripple.prolong(enemy, Cripple.class, 4f);
                                if (enemy == Dungeon.hero) {
                                    Dungeon.hero.interrupt();
                                    Dungeon.observe();
                                    GameScene.updateFog();
                                }
                            }
                        }), -1);
                        next();
                    }
                }));

                for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                    if(mob.alignment == Alignment.ENEMY && mob != this){
                        if(Dungeon.level.distance(pos, mob.pos)<10) {
                            mob.beckon(this.pos);
                        }
                    }
                }
                sprite.centerEmitter().start( Speck.factory( Speck.SCREAM ), 0.3f, 2 );

            }
        }
        chainsUsed = true;
        return true;
    }

    @Override
    public int attackSkill( Char target ) {
        return 12;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 7);
    }

    @Override
    public void rollToDropLoot() {
        if (Dungeon.hero.lvl <= maxLvl + 2){
            float chance = 0.01f;
            chance *= RingOfWealth.dropChanceMultiplier( Dungeon.hero );
            chance = Math.min(0.04f, chance);

            if(Random.Float()<chance) {
                int index = 0;
                for (int i = Generator.Category.ARTIFACT.classes.length - 1; i >= 0; --i) {
                    if (Generator.Category.ARTIFACT.classes[i] == EtherealChains.class) {
                        index = i;
                        break;
                    }
                }
                if (Generator.Category.ARTIFACT.probs[index] > 0.01f) {
                    Dungeon.level.drop(new EtherealChains(), pos).sprite.drop();
                    Generator.removeArtifact(EtherealChains.class);
                }
            }
        }

        //each drop makes future drops 1/2 as likely
        // so loot chance looks like: 1/5, 1/10, 1/20, 1/40, etc.
        lootChance *= Math.pow(1/2f, Dungeon.LimitedDrops.GUARD_ARM.count);
        super.rollToDropLoot();
    }

    @Override
    public Item createLoot() {
        Dungeon.LimitedDrops.GUARD_ARM.count++;
        return super.createLoot();
    }

    private final String CHAINSUSED = "chainsusedused";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(CHAINSUSED, chainsUsed);
        bundle.put("chainRefreshCD", cd);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        chainsUsed = bundle.getBoolean(CHAINSUSED);
        cd = bundle.getFloat("chainRefreshCD");
    }

    private class Hunting extends Mob.Hunting{
        @Override
        public boolean act( boolean enemyInFOV, boolean justAlerted ) {
            enemySeen = enemyInFOV;

            if (!chainsUsed
                    && enemyInFOV
                    && !isCharmedBy( enemy )
                    && !canAttack( enemy )
                    && Dungeon.level.distance( pos, enemy.pos ) < 5
                    && Random.Int(3) == 0

                    && chain(enemy.pos)){
                return false;
            } else {
                return super.act( enemyInFOV, justAlerted );
            }

        }
    }
}
