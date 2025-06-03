package com.shatteredpixel.shatteredpixeldungeon.minigames;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class NewLevel extends Level {

    {
        viewDistance = 999;

        color1 = 0x6a723d;
        color2 = 0x88924c;

    }

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

        setSize(64, 64);

        for (int i = 0;i < (64 * 64);i++) {
            map[i]=1;
        }
        map[67]=Terrain.ALCHEMY;

        buildFlagMaps();
        cleanWalls();

        entrance = 65;
        exit = 65;

        TownGamer townGamer = new TownGamer();
        townGamer.pos = 66;
        mobs.add(townGamer);

        LevelTransition entrance = new LevelTransition(this,this.entrance,LevelTransition.Type.SURFACE);
        transitions.add(entrance);

        LevelTransition exit = new LevelTransition(this,this.exit,LevelTransition.Type.REGULAR_EXIT);
        transitions.add(exit);

        return true;
    }

//    @Override
//    public Mob createMob() {
//        return null;
//    }

    @Override
    protected void createMobs() {
    }

    public Actor respawner() {
        return null;
    }

    @Override
    protected void createItems() {
//		Item item = Bones.get();
//		if (item != null) {
//			drop(item, exit - 1).type = Heap.Type.REMAINS;
//		}
    }

    @Override
    public String tileName(int tile) {
        if (tile == Terrain.WATER) {
            return Messages.get(SewerLevel.class, "water_name");
        }
        return super.tileName(tile);
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case Terrain.EMPTY_DECO:
                return Messages.get(SewerLevel.class, "empty_deco_desc");
            case Terrain.BOOKSHELF:
                return Messages.get(SewerLevel.class, "bookshelf_desc");
            default:
                return super.tileDesc(tile);
        }
    }

}
