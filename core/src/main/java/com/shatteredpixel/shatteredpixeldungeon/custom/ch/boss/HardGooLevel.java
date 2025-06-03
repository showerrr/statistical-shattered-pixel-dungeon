package com.shatteredpixel.shatteredpixeldungeon.custom.ch.boss;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.RangeMap;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.LloydsBeacon;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class HardGooLevel extends Level {
        {
            color1 = 0x48763c;
            color2 = 0x59994a;
        }

        private static final int WIDTH = 37;
        private static final int HEIGHT = 37;

        public static final int CENTER = 18 + WIDTH * 17;
        private static final int EXIT = 18 + WIDTH * 2;
        private static final int ENTRANCE = 18 + WIDTH * 29;

        @Override
        public String tilesTex() {
            return Assets.Environment.TILES_SEWERS;
        }

        @Override
        public String waterTex() {
            return Assets.Environment.WATER_SEWERS;
        }

        @Override
        protected boolean build() {
            setSize(WIDTH, HEIGHT);

            for (int i = 0; i < HEIGHT * WIDTH; ++i) {
                map[i] = codeToTerrain(codedMap[i]);
            }

            this.entrance = ENTRANCE;
            this.exit = EXIT;

            LevelTransition entrance = new LevelTransition(this, ENTRANCE, LevelTransition.Type.REGULAR_ENTRANCE);
            transitions.add(entrance);

            LevelTransition exit = new LevelTransition(this, EXIT, LevelTransition.Type.REGULAR_EXIT);
            transitions.add(exit);

            return true;
        }

        @Override
        protected void createMobs() {

        }

        public Actor addRespawner() {
            return null;
        }

        @Override
        protected void createItems() {
            Random.pushGenerator(Random.Long());
            ArrayList<Item> bonesItems = Bones.get();
            if (bonesItems != null) {
                int pos;
                do {
                    pos = randomRespawnCell(null);
                } while (pos == entrance());
                for (Item i : bonesItems) {
                    drop(i, pos).setHauntedIfCursed().type = Heap.Type.REMAINS;
                }
            }
            Random.popGenerator();

            MeleeWeapon mw = Generator.randomWeapon(11);
            mw.level(2);
            mw.cursed = false;
            mw.identify();
            drop(mw, 16 + WIDTH * 34).type = Heap.Type.LOCKED_CHEST;
            drop(new LloydsBeacon(), 18 + WIDTH * 34).type = Heap.Type.LOCKED_CHEST;
            Wand w;
            do {
                w = (Wand) Generator.random(Generator.Category.WAND);
            }while(Challenges.isItemBlocked(w));
            w.level(2);
            w.cursed = false;
            w.identify();
            drop(w, 20 + WIDTH * 34).type = Heap.Type.LOCKED_CHEST;

        }

        @Override
        public void occupyCell(Char ch) {
            super.occupyCell(ch);

            if (map[ENTRANCE]==Terrain.ENTRANCE && map[EXIT]==Terrain.LOCKED_EXIT &&
                    ch == Dungeon.hero && Dungeon.level.distance(ch.pos, entrance()) >= 2) {
                seal();
            }
        }

    @Override
    public int randomRespawnCell(Char ch) {
            //hero should not fall outside of arena.
            if(/*ch instanceof Hero*/true){
                int cell = -1;
                do {
                    cell = entrance() + PathFinder.NEIGHBOURS8[Random.Int(8)];
                } while (!passable[cell]);
                return cell;
            }
        return super.randomRespawnCell(ch);
    }

    @Override
        public void seal() {
            super.seal();

            //GooHard boss = new GooHard();
            GooHard boss = new GooHard();
            boss.pos = CENTER;
            GameScene.add(boss);

            Level.set(ENTRANCE, Terrain.EMPTY_SP);
            GameScene.updateMap(ENTRANCE);
            Dungeon.observe();
        }

        @Override
        public void unseal() {
            super.unseal();
            Level.set(ENTRANCE, Terrain.ENTRANCE);
            GameScene.updateMap(ENTRANCE);
            Level.set(EXIT, Terrain.UNLOCKED_EXIT);
            GameScene.updateMap(EXIT);
            Dungeon.observe();
        }

        @Override
        public String tileName(int tile) {
            switch (tile) {
                case Terrain.WATER:
                    return M.L(SewerLevel.class, "water_name");
                case Terrain.GRASS:
                    return M.L(SewerLevel.class, "grass_name");
                case Terrain.HIGH_GRASS:
                    return M.L(SewerLevel.class, "high_grass_name");
                case Terrain.STATUE:
                    return M.L(SewerLevel.class, "statue_name");
                default:
                    return super.tileName(tile);
            }
        }

        @Override
        public String tileDesc(int tile) {
            switch (tile) {
                case Terrain.WATER:
                    return M.L(HallsLevel.class, "water_desc");
                case Terrain.STATUE:
                case Terrain.STATUE_SP:
                    return M.L(HallsLevel.class, "statue_desc");
                case Terrain.BOOKSHELF:
                    return M.L(HallsLevel.class, "bookshelf_desc");
                case Terrain.EMPTY_WELL:
                    return M.L(YogRealLevel.class, "well_desc");
                default:
                    return super.tileDesc(tile);
            }
        }


        private int codeToTerrain(int code) {
            switch (code) {
                case 1:
                default:
                    return Terrain.EMPTY;
                case 65:
                    return Terrain.WALL;
                case 67:
                    return Terrain.BOOKSHELF;
                case 102:
                    return Terrain.HIGH_GRASS;
                case 5:
                case 11:
                    return Terrain.EMPTY_SP;
                case 2:
                    return Terrain.EMPTY_DECO;
                case 21:
                    return Terrain.PEDESTAL;
                case 257:
                    return Terrain.WATER;
                case 66:
                    return Terrain.WALL_DECO;
                case 98:
                    return Terrain.STATUE;
                case 83: return Terrain.LOCKED_DOOR;
                case 17: return Terrain.ENTRANCE;
                case 81: return Terrain.DOOR;
                case 85: return Terrain.LOCKED_EXIT;
            }
        }

        private static final int[] codedMap = new int[]{
                65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,
                65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,
                65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,85,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,
                65,65,65,67,67,67,67,67,67,67,67,67,67,67,67,67,67,67,1,67,67,67,67,67,67,67,67,67,67,67,67,67,67,67,65,65,65,
                65,65,65,67,65,65,65,65,65,65,65,65,65,65,65,65,65,65,83,65,65,65,65,65,65,65,65,65,65,65,65,65,65,67,65,65,65,
                65,65,65,67,65,65,65,65,65,65,65,65,65,65,65,65,65,65,83,65,65,65,65,65,65,65,65,65,65,65,65,65,65,67,65,65,65,
                65,65,65,67,65,65,102,102,102,102,102,102,5,5,81,257,257,257,257,257,257,257,81,5,5,102,102,102,102,102,102,65,65,67,65,65,65,
                65,65,65,67,65,65,102,102,257,257,257,65,81,65,65,11,11,11,257,11,11,11,65,65,81,65,257,257,257,102,102,65,65,67,65,65,65,
                65,65,65,67,65,65,102,102,257,257,65,65,257,65,65,98,1,11,257,11,1,98,65,65,257,65,65,257,257,102,102,65,65,67,65,65,65,
                65,65,65,67,65,65,102,102,257,65,65,102,257,102,65,65,98,11,257,11,98,65,65,102,257,102,65,65,257,102,102,65,65,67,65,65,65,
                65,65,65,67,65,65,102,102,65,65,102,102,257,102,102,65,65,11,257,11,65,65,102,102,257,102,102,65,65,102,102,65,65,67,65,65,65,
                65,65,65,67,65,65,5,5,81,257,257,257,98,257,257,257,81,5,257,5,81,257,257,257,98,257,257,257,81,5,5,65,65,67,65,65,65,
                65,65,65,67,65,65,81,65,65,65,102,102,257,102,102,65,65,65,81,65,65,65,102,102,257,102,102,65,65,65,81,65,65,67,65,65,65,
                65,65,65,67,65,65,257,11,1,65,65,102,257,102,65,65,11,11,11,11,11,65,65,102,257,102,65,65,1,11,257,65,65,67,65,65,65,
                65,65,65,67,65,65,257,11,1,1,65,65,257,65,65,11,11,65,65,65,11,11,65,65,257,65,65,1,1,11,257,65,65,67,65,65,65,
                65,65,65,67,65,65,257,11,98,1,1,65,81,65,11,11,257,257,257,257,257,11,11,65,81,65,1,1,98,11,257,65,65,67,65,65,65,
                65,65,65,67,65,65,257,11,11,11,11,11,257,65,11,65,257,257,257,257,257,65,11,65,257,11,11,11,11,11,257,65,65,67,65,65,65,
                65,65,65,67,65,65,257,257,257,257,257,257,257,81,11,65,257,257,257,257,257,65,11,81,257,257,257,257,257,257,257,65,65,67,65,65,65,
                65,65,65,67,65,65,257,11,11,11,11,11,257,65,11,65,257,257,257,257,257,65,11,65,257,11,11,11,11,11,257,65,65,67,65,65,65,
                65,65,65,67,65,65,257,11,98,1,1,65,81,65,11,11,257,257,257,257,257,11,11,65,81,65,1,1,98,11,257,65,65,67,65,65,65,
                65,65,65,67,65,65,257,11,1,1,65,65,257,65,65,11,11,65,65,65,11,11,65,65,257,65,65,1,1,11,257,65,65,67,65,65,65,
                65,65,65,67,65,65,257,11,1,65,65,102,257,102,65,65,11,11,11,11,11,65,65,102,257,102,65,65,1,11,257,65,65,67,65,65,65,
                65,65,65,67,65,65,81,65,65,65,102,102,257,102,102,65,65,65,81,65,65,65,102,102,257,102,102,65,65,65,81,65,65,67,65,65,65,
                65,65,65,67,65,65,11,5,81,257,257,257,98,257,257,257,81,257,257,257,81,257,257,257,98,257,257,257,81,5,11,65,65,67,65,65,65,
                65,65,65,67,65,65,102,102,65,65,102,102,257,102,102,65,65,11,257,11,65,65,102,102,257,102,102,65,65,102,102,65,65,67,65,65,65,
                65,65,65,67,65,65,102,102,257,65,65,102,257,102,65,65,1,11,257,11,1,65,65,102,257,102,65,65,257,102,102,65,65,67,65,65,65,
                65,65,65,67,65,65,102,102,257,257,65,65,257,65,65,1,1,11,257,11,1,1,65,65,257,65,65,257,257,102,102,65,65,67,65,65,65,
                65,65,65,67,65,65,102,102,257,257,257,65,81,65,65,1,98,11,257,11,98,1,65,65,81,65,257,257,257,102,102,65,65,67,65,65,65,
                65,65,65,67,65,65,102,102,102,102,102,102,5,98,65,1,98,11,257,11,98,1,65,98,5,102,102,102,102,102,102,65,65,67,65,65,65,
                65,65,65,67,65,65,102,102,102,102,102,102,11,11,81,11,11,11,17,11,11,11,81,11,5,102,102,102,102,102,102,65,65,67,65,65,65,
                65,65,65,67,65,65,102,102,102,102,102,102,102,102,65,257,257,257,257,257,257,257,65,102,102,102,102,102,102,102,102,65,65,67,65,65,65,
                65,65,65,67,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,67,65,65,65,
                65,65,65,67,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,67,65,65,65,
                65,65,65,67,67,67,67,67,67,67,67,67,67,67,67,67,67,67,67,67,67,67,67,67,67,67,67,67,67,67,67,67,67,67,65,65,65,
                65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,21,1,21,1,21,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,
                65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,
                65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65
        };
}
