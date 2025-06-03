package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invulnerability;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LostInventory;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.custom.buffs.FlavourBuffOpen;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.GME;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Bundle;

public class Sacrificing extends CountInscription {
    {
        defaultTriggers = 140;
    }
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        float power = 1.35f + Math.min(weapon.buffedLvl() * 0.5f, 2f);
        damage = Math.round(damage * power);
        float sacrifice = (damage - defender.HP)*0.35f;
        if(sacrifice < 0) sacrifice = 0;
        int canDamageHP = attacker.HP - attacker.HT/3 - 1;
        int selfdmg = Math.min(GME.accurateRound(sacrifice), Math.max(canDamageHP, 0));
        if(selfdmg>0) {
            attacker.damage(selfdmg, Sacrificing.class);
        }
        consume(weapon, attacker);
        return damage;
    }

    @Override
    public void useUp(Weapon w, Char attacker) {
        GameScene.flash(0xFF0000);
        final int power = attacker.HP - 1;
        new Flare(5, 32).color(0x80FFFF40, true).show(attacker.sprite, 1f);

        attacker.HP = 1;
        Buff.affect(attacker, Invulnerability.class, Math.min(5f + power / 7.5f, 30f));
        for (Buff b : attacker.buffs()){
            if (b.type == Buff.buffType.NEGATIVE
                    && !(b instanceof AllyBuff)
                    && !(b instanceof LostInventory)){
                b.detach();
            }
        }

        for(Mob m : Dungeon.level.mobs.toArray(new Mob[0])){
            if(m.alignment == Char.Alignment.ENEMY){
                if(!m.properties().contains(Char.Property.BOSS)){
                    Buff.affect(m, Slow.class, Math.min(5f + power / 7.5f, 30f)-2f);
                }
            }
        }

        FlavourBuffOpen reward = new FlavourBuffOpen(){
            @Override
            public void detach() {
                Buff.affect(target, Barrier.class).setShield(3*power);
                super.detach();
            }
        };
        reward.setDuration(Math.min(5f + power / 7.5f, 30f));
        reward.attachTo(attacker);

        super.useUp(w, attacker);
    }
}
