package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

public class Curing extends CountInscription {
    {
        defaultTriggers = 125;
    }
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        if(defender.properties().contains(Char.Property.DEMONIC) || defender.properties().contains(Char.Property.UNDEAD)){
            Buff.affect(defender, Poison.class).extend(defender.properties().contains(Char.Property.BOSS) ? 4f + weapon.buffedLvl()/4f : 8f + weapon.buffedLvl());
        }else{
            Buff.affect(defender, Healing.class).setHeal(2 + weapon.buffedLvl()/2, 1f, 0);
        }
        consume(weapon, attacker);
        return damage;
    }

    @Override
    public void useUp(Weapon w, Char attacker) {

        GameScene.flash(0xAAFFC4);
        for(Char ch: Actor.chars()){
            if(ch.properties().contains(Char.Property.DEMONIC) ||ch.properties().contains(Char.Property.UNDEAD)){
                if(ch.buff(Poison.class) != null){
                    Buff.detach(ch, Poison.class);
                    ch.sprite.centerEmitter().burst(PurpleParticle.BURST, 12);
                    ch.damage(ch.properties().contains(Char.Property.BOSS)?ch.HT/20 : ch.HP - 1, attacker);
                }else {
                    Buff.affect(ch, Poison.class).extend(20f + weapon.buffedLvl() * 1.5f);
                    ch.sprite.centerEmitter().burst(PurpleParticle.BURST, 5);
                }
            }else{
                Buff.affect(ch, Healing.class).setHeal(ch.HT / 2, 0.05f, 1);
            }
        }
        super.useUp(w, attacker);
    }
}
