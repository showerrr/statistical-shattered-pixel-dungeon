package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.GME;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.RangeMap;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;

public class KillingWeak extends CountInscription {
    {
        defaultTriggers = 60;
    }
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        int debuffs = 0;
        for(Buff b: defender.buffs()){
            if(b.type == Buff.buffType.NEGATIVE){
                ++debuffs;
            }
        }
        //each debuff = 16%
        damage = GME.accurateRound(damage * (1f + debuffs * 0.16f + debuffs * debuffs * 0.01f));
        if(defender.HP <= damage){
            for(int i: RangeMap.manhattanRing(defender.pos, 1, 3)){
                Char ch = Actor.findChar(i);
                if(ch != null && ch.alignment != Char.Alignment.ALLY){
                    Buff.affect(ch, Bleeding.class).set(damage / 4f);
                }
            }
        }
        consume(weapon, attacker);
        return damage;
    }

    @Override
    public void useUp(Weapon w, Char attacker) {

        for(Char ch: Actor.chars()){
            if(ch.alignment != Char.Alignment.ALLY && !ch.properties().contains(Char.Property.BOSS)){
                int debuffs = 0;
                for(Buff b: ch.buffs()){
                    if(b.type == Buff.buffType.NEGATIVE){
                        ++debuffs;
                    }
                }
                if(debuffs > 5){
                    ch.sprite.emitter().burst(ShadowParticle.UP, 15);
                    ch.damage(ch.HT, attacker);
                }else if(debuffs > 1){
                    ch.sprite.emitter().burst(ShadowParticle.UP, 7);
                    ch.damage(ch.HP - 1, attacker);
                }else if(debuffs > 0){
                    ch.damage(ch.HP / 2 - 1, attacker);
                }
            }
        }
        super.useUp(w, attacker);
    }
}
