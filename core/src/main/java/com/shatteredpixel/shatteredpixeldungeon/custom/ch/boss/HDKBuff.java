package com.shatteredpixel.shatteredpixeldungeon.custom.ch.boss;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LifeLink;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Monk;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Warlock;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.GME;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.timing.VirtualActor;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.ShieldHalo;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class HDKBuff {

    //should have a tag deciding if buff can get dispelled by external forces.
    //fuck CleansingDart
    public static class NeutralBarrier extends Barrier{
        {
            type = buffType.NEUTRAL;
        }

        @Override
        public boolean act() {
            incShield();
            return super.act();
        }
    }


    public static class HDKSummoning extends Buff {

        public int delay;
        public int pos;
        public Class<?extends Mob> summon;

        private Emitter particles;

        public int getPos() {
            return pos;
        }

        @Override
        public boolean act() {
            delay--;

            if (delay <= 0){

                if (summon.equals(HDKSummon.CurseWarlock.class) || summon.equals(HDKSummon.DPSWarlock.class) || summon.equals(HDKSummon.HealingWarlock.class)){
                    particles.burst(ShadowParticle.CURSE, 10);
                    Sample.INSTANCE.play(Assets.Sounds.CURSED);
                } else if (summon.equals(HDKSummon.EvasiveMonk.class) || summon.equals(HDKSummon.SpeedyMonk.class) || summon.equals(HDKSummon.TankMonk.class)){
                    particles.burst(ElmoParticle.FACTORY, 10);
                    Sample.INSTANCE.play(Assets.Sounds.BURNING);
                } else if (summon.equals(HDKSummon.HDKGhoul.class)){
                    particles.burst(Speck.factory(Speck.BONE), 10);
                    Sample.INSTANCE.play(Assets.Sounds.BONES);
                }
                particles = null;

                if (Actor.findChar(pos) != null){
                    ArrayList<Integer> candidates = new ArrayList<>();
                    for (int i : PathFinder.NEIGHBOURS8){
                        if (Dungeon.level.passable[pos+i] && Actor.findChar(pos+i) == null){
                            candidates.add(pos+i);
                        }
                    }
                    if (!candidates.isEmpty()){
                        pos = Random.element(candidates);
                    }
                }

                if (Actor.findChar(pos) == null) {
                    Mob m = Reflection.newInstance(summon);
                    m.pos = pos;
                    GameScene.add(m);
                    m.state = m.HUNTING;
                } else {
                    Char ch = Actor.findChar(pos);
                    if(ch == Dungeon.hero){
                        ch.damage(Dungeon.hero.HP / 2 + Random.NormalIntRange(10, 25), summon);
                        if (!ch.isAlive()) {
                            Dungeon.fail(NewHardDK.class);
                        }
                    }else if(!(ch instanceof NewHardDK)){
                        ch.die(NewHardDK.class);
                    }
                }

                detach();
            }

            spend(TICK);
            return true;
        }

        @Override
        public void fx(boolean on) {
            if (on && particles == null) {
                particles = CellEmitter.get(pos);

                if (summon.equals(HDKSummon.CurseWarlock.class) || summon.equals(HDKSummon.DPSWarlock.class) || summon.equals(HDKSummon.HealingWarlock.class)){
                    particles.pour(ShadowParticle.UP, 0.1f);
                } else if (summon.equals(HDKSummon.EvasiveMonk.class) || summon.equals(HDKSummon.SpeedyMonk.class) || summon.equals(HDKSummon.TankMonk.class)){
                    particles.pour(ElmoParticle.FACTORY, 0.1f);
                } else {
                    particles.pour(Speck.factory(Speck.RATTLE), 0.1f);
                }

            } else if (!on && particles != null) {
                particles.on = false;
            }
        }

        private static final String DELAY = "delay";
        private static final String POS = "pos";
        private static final String SUMMON = "summon";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(DELAY, delay);
            bundle.put(POS, pos);
            bundle.put(SUMMON, summon);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            delay = bundle.getInt(DELAY);
            pos = bundle.getInt(POS);
            summon = bundle.getClass(SUMMON);
        }
    }

    public static class MustPhysicalAtk extends Buff {
        {
            type = buffType.NEUTRAL;
            announced = true;
        }

        @Override
        public boolean act() {
            spend(TICK);
            return true;
        }

        @Override
        public int icon() {
            return BuffIndicator.WEAPON;
        }

        @Override
        public void tintIcon(Image icon) {
            super.tintIcon(icon);
            icon.hardlight(0f, 0.8f, 0f);
        }

        @Override
        public String desc() {
            return M.L(this,"desc");
        }

        @Override
        public String toString() {
            return M.L(this, "name");
        }
    }

}