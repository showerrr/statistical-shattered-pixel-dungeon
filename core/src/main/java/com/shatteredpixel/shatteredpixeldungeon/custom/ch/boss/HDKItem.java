package com.shatteredpixel.shatteredpixeldungeon.custom.ch.boss;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.GME;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.timing.VirtualActor;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.ShieldHalo;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DarkGold;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class HDKItem {

    public static class StatuePickaxe extends MeleeWeapon{
        {
            tier = 1;
            image = ItemSpriteSheet.PICKAXE;
            defaultAction = AC_DIG;
        }

        private static String AC_DIG = "dig";

        @Override
        public ArrayList<String> actions(Hero hero) {
            ArrayList<String> action = super.actions(hero);
            action.add(AC_DIG);
            return action;
        }

        @Override
        public void execute(Hero hero, String action) {
            super.execute(hero, action);
            if(action.equals(AC_DIG)){
                if(!isEquipped(hero)){
                    GLog.w(M.L(this, "need_equip"));
                } else if(Dungeon.bossLevel()){
                    GLog.w(M.L(this, "not_allowed"));
                }else{
                    GameScene.selectCell(
                        new CellSelector.Listener() {
                            @Override
                            public void onSelect(Integer target) {
                                if(target == null){

                                } else if (Dungeon.level.adjacent(hero.pos, target)) {
                                    if (Dungeon.level.insideMap(target)) {
                                        if (Dungeon.level.solid[target] && !Dungeon.level.flamable[target]) {
                                            hero.spend( 6f );
                                            hero.busy();
                                            hero.sprite.attack( target, new Callback() {
                                                @Override
                                                public void call() {
                                                    Sample.INSTANCE.play( Assets.Sounds.EVOKE );
                                                    Level.set(target, Terrain.EMPTY);
                                                    Dungeon.level.discoverable[target] = true;
                                                    Dungeon.level.updateFieldOfView(hero, hero.fieldOfView);
                                                    GameScene.updateMap(target);
                                                    CellEmitter.get(target).burst(Speck.factory(Speck.STAR), 12);
                                                    hero.onOperateComplete();
                                                }
                                            } );

                                        }
                                    } else {
                                        GLog.w(M.L(StatuePickaxe.class, "out_map"));
                                    }
                                } else {
                                    GLog.w(M.L(StatuePickaxe.class, "not_adjacent"));
                                }
                            }

                            @Override
                            public String prompt() {
                                return M.L(StatuePickaxe.class, "prompt");
                            }
                        });
                }
            }
        }

        @Override
        public int proc(Char attacker, Char defender, int damage) {
            damage = super.proc(attacker, defender, damage);
            if(defender instanceof HDKSummon.HDKStatue || defender instanceof Statue){
                damage += defender.HT * 99 / 100;
                defender.sprite.centerEmitter().burst(Speck.factory(Speck.STAR), 12);
            }
            return damage;
        }

        @Override
        public boolean doEquip(Hero hero) {
            boolean success = super.doEquip(hero);
            if(success){
                hero.spend(-timeToEquip(hero));
            }
            return success;
        }

        @Override
        public boolean doUnequip(Hero hero, boolean collect, boolean single) {
            boolean success = super.doUnequip(hero, collect, single);
            if(success){
                hero.spend(-timeToEquip(hero));
            }
            return success;
        }

        @Override
        public int STRReq(int lvl) {
            return super.STRReq(lvl) - 20;
        }
    }

    public static class KingAmulet extends Item{
        {
            unique = true;
            image = ItemSpriteSheet.ANKH;
        }

        private int uses = 1;
        private static final String AC_APPLY = "apply";

        public void setUses(int uses) {
            this.uses = uses;
        }

        public int getUses() {
            return uses;
        }

        @Override
        public ItemSprite.Glowing glowing() {
            return new ItemSprite.Glowing(0x00CC00);
        }

        @Override
        public boolean isIdentified() {
            return true;
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public ArrayList<String> actions(Hero hero) {
            ArrayList<String> action = super.actions(hero);
            action.add(AC_APPLY);
            return action;
        }

        @Override
        public void execute(Hero hero, String action) {
            super.execute(hero, action);
            if(action.equals(AC_APPLY)){
                if(hero.buff(DamageCheater.class) == null) {
                    Buff.affect(hero, DamageCheater.class).setCounters(2);
                    --uses;
                    hero.spendAndNext(Actor.TICK);
                }else{
                    GLog.w(M.L(this, "already"));
                }

                if(uses <= 0){
                    detach(hero.belongings.backpack);
                    GLog.w(M.L(this, "use_up"));
                }
            }
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put("left_uses", uses);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            uses = bundle.getInt("left_uses");
        }

        @Override
        public String desc() {
            return M.L(KingAmulet.class, "desc", uses);
        }

        public static class DamageCheater extends Buff {
            {
                type = buffType.POSITIVE;
                announced = true;
            }

            private int counters;

            public void setCounters(int counters) {
                this.counters = counters;
            }

            public int getCounters() {
                return counters;
            }

            public int onDamage(Char toProtect, int damage){
                int threshold = Math.min(toProtect.HP/5, toProtect.HT/15);
                if(damage >= threshold){
                    for(Mob m: Dungeon.level.mobs.toArray(new Mob[0])){
                        if(m.alignment == Char.Alignment.ENEMY && Dungeon.level.distance(toProtect.pos, m.pos)<9){
                            VirtualActor.delay(0.5f, true, DamageCheater.class, ()->{
                                float x = m.sprite.center().x;
                                float y = m.sprite.center().y;
                                m.sprite.parent.add(new Lightning(m.sprite.center(), new PointF( x, y-300f),null));
                                m.sprite.parent.add(new Lightning(new PointF(x-5f, y), new PointF( x-5f, y-300f),null));
                                m.sprite.parent.add(new Lightning(new PointF(x+5f, y), new PointF( x+5f, y-300f),null));
                                Sample.INSTANCE.play( Assets.Sounds.LIGHTNING, 1.1f);

                                float missing = 1f - (float)toProtect.HP / toProtect.HT;
                                m.damage(GME.accurateRound(damage * Random.Float(0.8f, 1.25f) * (1f + 4f * missing * missing)), toProtect);
                                m.sprite.centerEmitter().burst( SparkParticle.FACTORY, 32 );
                                m.sprite.flash();
                            });
                        }
                    }
                    ShieldHalo halo = new ShieldHalo(toProtect.sprite){
                        {
                            putOut();
                        }
                    };
                    GameScene.effect(halo);
                    --counters;
                    if(counters <= 0){
                        detach();
                    }
                    return 0;
                }
                return damage;

            }

            @Override
            public boolean act() {
                if(counters <= 0){
                    detach();
                }
                spend(TICK);
                return true;
            }

            @Override
            public void storeInBundle(Bundle bundle) {
                super.storeInBundle(bundle);
                bundle.put("left_counters", counters);
            }

            @Override
            public void restoreFromBundle(Bundle bundle) {
                super.restoreFromBundle(bundle);
                counters = bundle.getInt("left_counters");
            }

            @Override
            public int icon() {
                return BuffIndicator.ANKH;
            }


            @Override
            public String iconTextDisplay() {
                return Integer.toString(getCounters());
            }

            @Override
            public String toString() {
                return Messages.get(this, "name");
            }

            @Override
            public String desc() {
                return Messages.get(this, "desc", getCounters());
            }
        }
    }


}
