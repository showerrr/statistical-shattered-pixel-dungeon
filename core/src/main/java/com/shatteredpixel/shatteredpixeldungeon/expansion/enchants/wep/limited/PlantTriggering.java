package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;

public class PlantTriggering extends CountInscription {
    {
        defaultTriggers = 150;
    }
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        Plant p = ((Plant.Seed) Generator.randomUsingDefaults(Generator.Category.SEED)).couch(defender.pos, null);
        
        if (defender instanceof Hero && ((Hero) defender).subClass == HeroSubClass.WARDEN){
            ((Hero) defender).subClass = HeroSubClass.NONE;
            p.activate( defender );
            ((Hero) defender).subClass = HeroSubClass.WARDEN;
        } else {
            p.activate( defender );
        }

        consume(weapon, attacker);

        CellEmitter.get( defender.pos ).burst( LeafParticle.LEVEL_SPECIFIC, 10 );
        return damage;
    }
}
