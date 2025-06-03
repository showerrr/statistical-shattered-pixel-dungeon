package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.spawner;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM200;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Ghoul;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Necromancer;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.cave.BatH;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.cave.BruteH;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.cave.ShamanH;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.cave.SpinnerH;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.city.ElementalH;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.city.GolemH;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.city.MonkH;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.city.SeniorMonkH;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.city.WarlockH;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.hall.EyeH;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.hall.RipperH;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.hall.ScorpioH;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.hall.SuccubusH;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.prison.BanditH;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.prison.DM100H;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.prison.GuardH;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.prison.SkeletonH;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.prison.ThiefH;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.sewer.CrabH;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.sewer.GnollH;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.sewer.RatH;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.sewer.SlimeH;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.sewer.SnakeH;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.sewer.SwarmH;

import java.util.ArrayList;
import java.util.Arrays;

public class MobList {

    public static ArrayList<Class<? extends Mob>> HardMobList(int depth){
        switch (depth){
            case 1: default:
                return new ArrayList<>(Arrays.asList(RatH.class, RatH.class, RatH.class, SnakeH.class));
            case 2:
                return new ArrayList<>(Arrays.asList(RatH.class, RatH.class, SnakeH.class, GnollH.class, GnollH.class));
            case 3:
                return new ArrayList<>(Arrays.asList(RatH.class, GnollH.class, GnollH.class, GnollH.class, GnollH.class, CrabH.class, SnakeH.class));
            case 4: case 5:
                return new ArrayList<>(Arrays.asList(CrabH.class, CrabH.class, SwarmH.class, SwarmH.class, SlimeH.class, SlimeH.class, GnollH.class));
            case 6:
                return new ArrayList<>(Arrays.asList(SkeletonH.class, SkeletonH.class, SkeletonH.class, ThiefH.class, SwarmH.class));
            case 7:
                return new ArrayList<>(Arrays.asList(SkeletonH.class, SkeletonH.class, ThiefH.class, ThiefH.class, GuardH.class, DM100H.class));
            case 8:
                //lightning!
                return new ArrayList<>(Arrays.asList(SkeletonH.class, DM100H.class, DM100H.class, DM100H.class, GuardH.class, GuardH.class, Necromancer.class));
            case 9: case 10:
                //Heaven of crime!!!
                return new ArrayList<>(Arrays.asList(ThiefH.class, ThiefH.class, BanditH.class, BanditH.class, GuardH.class, DM100H.class));
            case 11:
                return new ArrayList<>(Arrays.asList(BatH.class, BatH.class, BatH.class, BruteH.class, ShamanH.random()));
            case 12:
                return new ArrayList<>(Arrays.asList(BatH.class, BruteH.class, BruteH.class, ShamanH.random(), ShamanH.random()));
            case 13:
                //Magic!
                return new ArrayList<>(Arrays.asList(ShamanH.random(), BruteH.class, BruteH.class, ShamanH.random(), ShamanH.random(), SpinnerH.class, ShamanH.random()));
            case 14: case 15:
                return new ArrayList<>(Arrays.asList(BatH.class, BruteH.class, ShamanH.random(), SpinnerH.class, SpinnerH.class, DM200.class));
            case 16:
                //element!
                return new ArrayList<>(Arrays.asList(Ghoul.class, ElementalH.random(), ElementalH.random(), ElementalH.random()));
            case 17:
                //darkness!
                return new ArrayList<>(Arrays.asList(Ghoul.class, ElementalH.random(), WarlockH.class, WarlockH.class, WarlockH.class));
            case 18:
                return new ArrayList<>(Arrays.asList(ElementalH.random(), WarlockH.class, MonkH.class, MonkH.class, GolemH.class));
            case 19: case 20:
                //face senior directly!
                return new ArrayList<>(Arrays.asList(WarlockH.class, MonkH.class, MonkH.class, GolemH.class, GolemH.class, SeniorMonkH.class));
            case 21:
                return new ArrayList<>(Arrays.asList(SuccubusH.class, SuccubusH.class, SuccubusH.class, EyeH.class, RipperH.class));
            case 22:
                return new ArrayList<>(Arrays.asList(SuccubusH.class, EyeH.class, RipperH.class));
            case 23:
                //Destroy!
                return new ArrayList<>(Arrays.asList(SuccubusH.class, EyeH.class, EyeH.class, EyeH.class, EyeH.class, ScorpioH.class));
            case 24: case 25:
                //HEAD SHOT!!!
                return new ArrayList<>(Arrays.asList(SuccubusH.class, EyeH.class, EyeH.class, ScorpioH.class, ScorpioH.class, ScorpioH.class));

        }
    }
}
