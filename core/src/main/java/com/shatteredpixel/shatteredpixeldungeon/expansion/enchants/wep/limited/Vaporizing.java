package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.GME;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.RangeMap;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

public class Vaporizing extends CountInscription {
    {
        defaultTriggers = 50;
    }
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        for(Mob m: Dungeon.level.mobs.toArray(new Mob[0])){
            if(m.alignment != Char.Alignment.ALLY){
                if(Dungeon.level.map[m.pos] == Terrain.WATER && Dungeon.level.distance(defender.pos, m.pos)<=1){
                    m.damage(GME.accurateRound(Dungeon.depth * 1.5f + 5f), Vaporizing.class);
                }
            }
        }
        for(Integer cell: RangeMap.centeredRect(defender.pos, 1, 1)) {
            if(Dungeon.level.map[cell] == Terrain.WATER){
                Level.set(cell, Terrain.EMPTY);
                GameScene.updateMap(cell);
                CellEmitter.get(cell).burst(Speck.factory(Speck.STEAM), 10);
            }
        }
        Buff.affect(defender, Burning.class).reignite(defender, 4f);
        consume(weapon, attacker);
        return damage;
    }

    @Override
    public void useUp(Weapon w, Char attacker) {
        for(Integer cell: RangeMap.centeredRect(attacker.pos, 20, 20)) {
            if(Dungeon.level.map[cell] == Terrain.WATER){
                Level.set(cell, Terrain.EMPTY);
                GameScene.updateMap(cell);
                if(attacker.fieldOfView[cell]) {
                    CellEmitter.get(cell).burst(Speck.factory(Speck.STEAM), 3);
                }
            }
        }
        super.useUp(w, attacker);
    }
}