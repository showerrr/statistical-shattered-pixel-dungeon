package com.shatteredpixel.shatteredpixeldungeon.custom.ch.boss;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.RangeMap;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlink;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.ImpShopRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class NewHDKLevel extends Level {
    {
        color1 = 0x4b6636;
        color2 = 0xf2f2f2;

        viewDistance = 12;
    }
    private static final int WIDTH = 17;
    private static final int HEIGHT = 51;
    private static final int EXIT = 8 + WIDTH * 4;
    private static final int ENTRANCE = 8  + WIDTH * 48;
    private static final int SHOP_CENTER_X = 8;
    private static final int SHOP_CENTER_Y = 9;
    private static final int DOOR_IN = 8 + WIDTH * 43;
    private static final int DOOR_OUT = 8 + WIDTH * 15;
    private static final int[] SUMMON_WELL = new int[]{5+WIDTH*27,11+WIDTH*27,3+WIDTH*32,13+WIDTH*32,5+WIDTH*38,11+WIDTH*38};
    private static final int KING_STAY_1 = 8 + WIDTH * 27;
    private static final int KING_STAY_2 = 8 + WIDTH * 33;
    private static final int[] INV_STATUE = new int[]{3 + WIDTH * 25, 13 + WIDTH * 25, 3 + WIDTH * 41, 13 + WIDTH * 41};
    private static final int HIDDEN_TREASURE = 8 + WIDTH * 1;
    private static final int HELP_ITEM_1 = 2 + WIDTH * 48;
    private static final int HELP_ITEM_2 = 14 + WIDTH * 48;
    private static final int TREASURE_ROOM_1 = 3 + WIDTH * 19;
    private static final int TREASURE_ROOM_2 = 13 + WIDTH * 19;


    private ImpShopRoom impShop;

    private boolean sealed = false;

    @Override
    public String tilesTex() {
        return Assets.Environment.TILES_CITY;
    }

    @Override
    public String waterTex() {
        return Assets.Environment.WATER_CITY;
    }

    private static final String IMP_SHOP = "imp_shop";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( IMP_SHOP, impShop );
        bundle.put("level_is_sealed", sealed);
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        //pre-1.3.0 saves, modifies exit transition with custom size
        impShop = (ImpShopRoom) bundle.get( IMP_SHOP );
        if (map[DOOR_OUT] != Terrain.LOCKED_DOOR && !impShop.shopSpawned()){
            spawnShop();
        }
        sealed = bundle.getBoolean("level_is_sealed");
    }

    @Override
    protected boolean build() {
        setSize(WIDTH, HEIGHT);
        codeToTerrain();
        LevelTransition entrance = new LevelTransition(this, ENTRANCE, LevelTransition.Type.REGULAR_ENTRANCE);
        transitions.add(entrance);

        LevelTransition exit = new LevelTransition(this, EXIT, LevelTransition.Type.REGULAR_EXIT);
        transitions.add(exit);

        impShop = new ImpShopRoom();
        impShop.set(SHOP_CENTER_X - 4, SHOP_CENTER_Y - 4, SHOP_CENTER_X + 4, SHOP_CENTER_Y + 4);
        Painter.set(this, impShop.center(), Terrain.PEDESTAL);
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

        HDKItem.StatuePickaxe pickaxe = new HDKItem.StatuePickaxe();
        pickaxe.identify();
        pickaxe.level(3);
        drop(pickaxe, HELP_ITEM_1);

        StoneOfBlink stoneOfBlink = new StoneOfBlink();
        stoneOfBlink.quantity(5);
        drop(stoneOfBlink, HELP_ITEM_2);

        HDKItem.KingAmulet kingAmulet = new HDKItem.KingAmulet();
        kingAmulet.setUses(3);
        drop(kingAmulet, HIDDEN_TREASURE).type = Heap.Type.LOCKED_CHEST;

        Ankh ankh = new Ankh();
        ankh.bless();
        drop(ankh, TREASURE_ROOM_1);
        Ankh ankh2 = new Ankh();
        ankh2.bless();
        drop(ankh2, TREASURE_ROOM_1).type= Heap.Type.LOCKED_CHEST;

        WandOfCorruption woc = new WandOfCorruption();
        woc.level(3);
        woc.identify();
        drop(woc, TREASURE_ROOM_2).type= Heap.Type.LOCKED_CHEST;
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

    public void occupyCell( Char ch ) {
        super.occupyCell( ch );

        if ( ch == Dungeon.hero && ch.pos < DOOR_IN && !sealed) {
            seal();
            sealed = true;
        }
    }

    @Override
    public void seal() {
        super.seal();

        set(DOOR_IN, Terrain.LOCKED_DOOR);
        GameScene.updateMap(DOOR_IN);

        Dungeon.observe();

        NewHardDK boss = new NewHardDK();
        boss.pos = KING_STAY_1;
        GameScene.add(boss);
    }

    @Override
    public void unseal() {
        super.unseal();

        set(DOOR_IN, Terrain.DOOR);
        GameScene.updateMap(DOOR_IN);

        spawnShop();

        Dungeon.observe();
    }

    public int getSummoningPos(){
        NewHardDK nhdk = null;
        for(Mob m:Dungeon.level.mobs.toArray(new Mob[0])){
            if(m instanceof NewHardDK){
                nhdk = (NewHardDK) m;
                break;
            }
        }
        if(nhdk != null) {
            HashSet<HDKBuff.HDKSummoning> summons = nhdk.buffs(HDKBuff.HDKSummoning.class);
            ArrayList<Integer> positions = new ArrayList<>();
            for (int pedestal : SUMMON_WELL) {
                boolean clear = true;
                for (HDKBuff.HDKSummoning s : summons) {
                    if (s.getPos() == pedestal) {
                        clear = false;
                        break;
                    }
                }
                if (clear) {
                    positions.add(pedestal);
                }
            }
            if (positions.isEmpty()) {
                return -1;
            } else {
                return Random.element(positions);
            }
        }
        return -1;
    }

    public int getKingStay(int which){
        if(which == 2){
            return KING_STAY_2;
        }else{
            return KING_STAY_1;
        }
    }


    @Override
    public String tileName( int tile ) {
        switch (tile) {
            case Terrain.WATER:
                return Messages.get(CityLevel.class, "water_name");
            case Terrain.HIGH_GRASS:
                return Messages.get(CityLevel.class, "high_grass_name");
            default:
                return super.tileName( tile );
        }
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case Terrain.ENTRANCE:
                return Messages.get(CityLevel.class, "entrance_desc");
            case Terrain.EXIT:
                return Messages.get(CityLevel.class, "exit_desc");
            case Terrain.WALL_DECO:
            case Terrain.EMPTY_DECO:
                return Messages.get(CityLevel.class, "deco_desc");
            case Terrain.EMPTY_SP:
                return Messages.get(CityLevel.class, "sp_desc");
            case Terrain.STATUE:
            case Terrain.STATUE_SP:
                return Messages.get(CityLevel.class, "statue_desc");
            case Terrain.BOOKSHELF:
                return Messages.get(CityLevel.class, "bookshelf_desc");
            default:
                return super.tileDesc( tile );
        }
    }

    private void spawnShop(){
        impShop.spawnShop(this);
    }

    public int[] getStatuePos(){
        return INV_STATUE;
    }

    public int[] getSummonWell(){
        return SUMMON_WELL;
    }







    private void codeToTerrain(){
        for(int i=0;i<WIDTH*HEIGHT;++i) {
            switch (codeMap[i]) {
                case 65: case 69:
                    map[i] = Terrain.WALL;
                    break;
                case 257:
                    map[i] = Terrain.WATER;
                    break;
                case 1:
                    map[i] = Terrain.EMPTY;
                    break;
                case 17:
                    map[i] = Terrain.ENTRANCE;
                    break;
                case 18:
                    map[i] = Terrain.EXIT;
                    break;
                case 19:
                    map[i] = Terrain.WELL;
                    break;
                case 20:
                    map[i] = Terrain.EMPTY_WELL;
                    break;
                case 5:
                    map[i] = Terrain.EMPTY_SP;
                    break;
                case 98:
                    map[i] = Terrain.STATUE;
                    break;
                case 100:
                    map[i] = Terrain.ALCHEMY;
                    break;
                case 67:
                    map[i] = Terrain.BOOKSHELF;
                    break;
                case 81:
                    map[i] = Terrain.DOOR;
                    break;
                case 83:
                    map[i] = Terrain.LOCKED_DOOR;
                    break;
                case 21:
                    map[i] = Terrain.PEDESTAL;
                    break;
                case 99:
                    map[i] = Terrain.STATUE_SP;
                    break;
                case 82:
                    map[i] = Terrain.SECRET_DOOR;
                    break;
                case 49:
                    map[i] = Terrain.CHASM;
                    break;
            }
        }
    }


    private static final int[] codeMap = {
        65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,
        65,82,82,82,82,82,82,82,1,82,82,82,82,82,82,82,65,
        65,82,65,65,65,65,65,65,65,65,65,65,65,65,65,82,65,
        65,82,65,65,65,65,65,65,65,65,65,65,65,65,65,82,65,
        65,82,82,82,98,98,98,1,18,1,98,98,98,82,82,82,65,
        65,65,65,65,1,1,1,1,1,1,1,1,1,65,65,65,65,
        65,65,65,65,1,5,5,5,5,5,5,5,1,65,65,65,65,
        65,65,98,98,1,5,257,257,257,257,257,5,1,98,98,65,65,
        65,65,98,98,1,5,257,257,257,257,257,5,1,98,98,65,65,
        65,65,98,98,1,5,257,257,21,257,257,5,1,98,98,65,65,
        65,65,98,98,1,5,257,257,257,257,257,5,1,98,98,65,65,
        65,65,98,98,1,5,257,257,257,257,257,5,1,98,98,65,65,
        65,65,65,65,1,5,5,5,5,5,5,5,1,65,65,65,65,
        65,65,65,65,1,1,1,1,1,1,1,1,1,65,65,65,65,
        65,65,65,65,98,98,98,1,1,1,98,98,98,65,65,65,65,
        65,65,65,65,65,65,65,65,83,65,65,65,65,65,65,65,65,
        65,65,65,65,65,65,65,65,83,65,65,65,65,65,65,65,65,
        65,65,65,65,65,65,1,1,5,1,1,65,65,65,65,65,65,
        65,65,1,1,1,65,1,1,5,1,1,65,1,1,1,65,65,
        65,65,1,21,5,81,5,5,99,5,5,81,5,21,1,65,65,
        65,65,1,1,1,65,1,1,5,1,1,65,1,1,1,65,65,
        65,65,65,65,65,65,1,1,5,1,1,65,65,65,65,65,65,
        65,65,65,65,65,65,65,65,83,65,65,65,65,65,65,65,65,
        65,65,65,65,65,65,65,65,83,65,65,65,65,65,65,65,65,
        65,65,5,5,5,1,1,1,5,1,1,1,5,5,5,65,65,
        65,65,5,21,5,1,1,1,5,1,1,1,5,21,5,65,65,
        65,65,5,5,5,98,1,5,5,5,1,98,5,5,5,65,65,
        65,65,1,98,1,20,1,5,21,5,1,20,1,98,1,65,65,
        65,65,1,1,1,1,1,5,5,5,1,1,1,1,1,65,65,
        65,65,1,1,1,1,1,1,5,1,1,1,1,1,1,65,65,
        65,65,1,1,98,1,1,1,5,1,1,1,98,1,1,65,65,
        65,65,1,1,1,1,1,1,5,1,1,1,1,1,1,65,65,
        65,65,1,20,1,98,1,5,5,5,1,98,1,20,1,65,65,
        65,65,5,5,5,5,5,5,21,5,5,5,5,5,5,65,65,
        65,65,1,98,1,98,1,5,5,5,1,98,1,98,1,65,65,
        65,65,1,1,1,1,1,1,5,1,1,1,1,1,1,65,65,
        65,65,98,98,1,1,1,1,5,1,1,1,1,98,98,65,65,
        65,65,1,1,1,1,98,1,5,1,98,1,1,1,1,65,65,
        65,65,1,1,1,20,1,1,5,1,1,20,1,1,1,65,65,
        65,65,1,98,1,1,1,1,5,1,1,1,1,98,1,65,65,
        65,65,5,5,5,1,98,1,5,1,98,1,5,5,5,65,65,
        65,65,5,21,5,1,1,1,5,1,1,1,5,21,5,65,65,
        65,65,5,5,5,1,1,1,5,1,1,1,5,5,5,65,65,
        65,65,65,65,65,65,65,65,81,65,65,65,65,65,65,65,65,
        65,65,65,65,65,67,98,1,5,1,98,67,65,65,65,65,65,
        65,65,65,65,65,67,98,1,5,1,98,67,65,65,65,65,65,
        65,65,65,65,65,67,98,1,5,1,98,67,65,65,65,65,65,
        65,65,65,65,65,67,98,1,5,1,98,67,65,65,65,65,65,
        65,65,1,1,1,81,5,5,17,5,5,81,1,1,1,65,65,
        65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,
        65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65,65
    };


}
