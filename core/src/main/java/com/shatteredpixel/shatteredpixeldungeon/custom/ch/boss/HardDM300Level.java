package com.shatteredpixel.shatteredpixeldungeon.custom.ch.boss;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.RangeMap;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ToxicTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class HardDM300Level extends Level {

    {
        color1 = 0x534f3e;
        color2 = 0xb9d661;

        viewDistance = Math.max(viewDistance, 12);
    }

    private int status = 0;
    private static final int START = 0;
    private static final int FIGHTING = 1;
    private static final int WON = 2;
    private static final int ERROR = 9999999;

    private void progress(){
        if(status == START){
            status = FIGHTING;
        }else if(status == FIGHTING){
            status = WON;
        }else{
            status = ERROR;
        }
    }

    public int getStatus(){return status;}

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( "level_status", status );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        status = bundle.getInt("level_status");
    }

    @Override
    public String tilesTex() {
        return Assets.Environment.TILES_CAVES;
    }

    @Override
    public String waterTex() {
        return Assets.Environment.WATER_CAVES;
    }

    @Override
    public String tileName( int tile ) {
        switch (tile) {
            case Terrain.GRASS:
                return Messages.get(CavesLevel.class, "grass_name");
            case Terrain.HIGH_GRASS:
                return Messages.get(CavesLevel.class, "high_grass_name");
            case Terrain.WATER:
                return Messages.get(CavesLevel.class, "water_name");
            case Terrain.STATUE:
                //city statues are used
                return Messages.get(CityLevel.class, "statue_name");
            default:
                return super.tileName( tile );
        }
    }

    @Override
    public String tileDesc( int tile ) {
        switch (tile) {
            case Terrain.WATER:
                return super.tileDesc( tile ) + "\n\n" + Messages.get(CavesBossLevel.class, "water_desc");
            case Terrain.ENTRANCE:
                return Messages.get(CavesLevel.class, "entrance_desc");
            case Terrain.EXIT:
                //city exit is used
                return Messages.get(CityLevel.class, "exit_desc");
            case Terrain.HIGH_GRASS:
                return Messages.get(CavesLevel.class, "high_grass_desc");
            case Terrain.WALL_DECO:
                return Messages.get(CavesLevel.class, "wall_deco_desc");
            case Terrain.BOOKSHELF:
                return Messages.get(CavesLevel.class, "bookshelf_desc");
            //city statues are used
            case Terrain.STATUE:
                return Messages.get(CityLevel.class, "statue_desc");
            default:
                return super.tileDesc( tile );
        }
    }

    private static final int WIDTH = 21;
    private static final int HEIGHT = 23;

    @Override
    protected boolean build() {
        setSize(WIDTH, HEIGHT);
        map = MAP.clone();

        buildFlagMaps();
        cleanWalls();

        for(int i=4*WIDTH;i<length-4*WIDTH;++i){
            if(!solid[i]) {
                if (Random.Int(100) < 15) {
                    map[i] = Terrain.INACTIVE_TRAP;
                    Trap t = new ToxicTrap().reveal();
                    t.active = false;
                    setTrap(t, i);
                }
            }
        }

        int entranceCell = 10 + 19*21;
        int exitCell = 10 + 3*21;
        this.entrance = entranceCell;
        this.exit = exitCell;

        LevelTransition entrance = new LevelTransition(this, entranceCell, LevelTransition.Type.REGULAR_ENTRANCE);
        transitions.add(entrance);

        LevelTransition exit = new LevelTransition(this, exitCell, LevelTransition.Type.REGULAR_EXIT);
        transitions.add(exit);

        map[exitCell] = Terrain.LOCKED_EXIT;

        return true;
    }

    @Override
    public void seal(){
        super.seal();

        set(entrance(), Terrain.WALL );

        Heap heap = Dungeon.level.heaps.get( entrance() );
        if (heap != null) {
            int n;
            do {
                n = entrance() + PathFinder.NEIGHBOURS8[Random.Int( 8 )];
            } while (!Dungeon.level.passable[n]);
            Dungeon.level.drop( heap.pickUp(), n ).sprite.drop( entrance() );
        }

        Char ch = Actor.findChar( entrance() );
        if (ch != null) {
            int n;
            do {
                n = entrance() + PathFinder.NEIGHBOURS8[Random.Int( 8 )];
            } while (!Dungeon.level.passable[n]);
            ch.pos = n;
            ch.sprite.place(n);
        }

        GameScene.updateMap( entrance() );
        Dungeon.observe();
    }

    @Override
    public void unseal(){
        super.unseal();

        set( entrance(), Terrain.ENTRANCE );

        GameScene.updateMap();

        Dungeon.observe();
    }

    @Override
    public boolean setCellToWater(boolean includeTraps, int cell) {
        return super.setCellToWater(false, cell);
    }

    @Override
    protected void createMobs() {
    }

    @Override
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
    }

    private static final int W = Terrain.WALL;
    private static final int D = Terrain.DOOR;
    private static final int e = Terrain.EMPTY;

    private static final int T = Terrain.INACTIVE_TRAP;

    private static final int E = Terrain.ENTRANCE;
    private static final int X = Terrain.EXIT;

    private static final int[] MAP = {
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, X, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W,
            W, W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W,
            W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W,
            W, W, W, e, e, e, e, e, e, e, W, e, e, e, e, e, e, e, W, W, W,
            W, W, W, e, e, e, e, e, e, e, W, e, e, e, e, e, e, e, W, W, W,
            W, W, W, e, e, e, e, e, e, e, W, e, e, e, e, e, e, e, W, W, W,
            W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W,
            W, W, W, e, e, e, W, W, W, e, e, e, W, W, W, e, e, e, W, W, W,
            W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W,
            W, W, W, e, e, e, e, e, e, e, W, e, e, e, e, e, e, e, W, W, W,
            W, W, W, e, e, e, e, e, e, e, W, e, e, e, e, e, e, e, W, W, W,
            W, W, W, e, e, e, e, e, e, e, W, e, e, e, e, e, e, e, W, W, W,
            W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W,
            W, W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W,
            W, W, W, W, W, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, E, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
    };

    @Override
    public Group addVisuals() {
        super.addVisuals();
        CavesLevel.addCavesVisuals(this, visuals);
        return visuals;
    }

    @Override
    public void occupyCell( Char ch ) {

        super.occupyCell( ch );

        if (status == START && ch == Dungeon.hero) {

            progress();
            seal();

            DM300Hard boss = new DM300Hard();
            boss.state = boss.WANDERING;
            boss.pos = exit() + WIDTH*2;
            GameScene.add( boss );

            Dungeon.observe();

            Camera.main.shake( 3, 0.7f );
            Sample.INSTANCE.play( Assets.Sounds.ROCKS );
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
    public Heap drop( Item item, int cell ) {

        if (status < WON && item instanceof SkeletonKey) {

            progress();
            unseal();

            Dungeon.observe();
        }

        return super.drop( item, cell );
    }

}
