package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.timing.VirtualActor;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Wound;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class Unholy extends CountInscription {
    {
        defaultTriggers = 100;
    }
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        float power = defender.properties().contains(Char.Property.DEMONIC) || defender.properties().contains(Char.Property.UNDEAD) ?
                Math.max(0.9f - 0.03f*weapon.buffedLvl(), 0.6f) : Math.min(1.25f + weapon.buffedLvl()*0.05f, 2f);
        damage *= power;
        if(power > 1f){
            if(Random.Float()<(power-1f)/2f){
                //attacker.damage( Math.min(2 + weapon.buffedLvl() / 2, Math.max(attacker.HP - attacker.HT/4, 0)), this);
                damage *= (1f+power);
                Wound.hit(defender);
                defender.sprite.emitter().burst(ShadowParticle.UP,  5);
            }
        }else{
            if(Random.Float()<(1f-power)){
                Buff.affect(attacker, Healing.class).setHeal(Math.min(weapon.buffedLvl() * 3 / 2 + 5, damage / 2), 1f, 0);
                damage = 0;
            }
        }
        consume(weapon, attacker);

        return damage;
    }

    @Override
    public void useUp(Weapon w, Char attacker) {

        float radius = Math.max(w.buffedLvl() * 0.4f + 4f, 8f);
        VirtualActor.delay(6f/200f* DungeonTilemap.SIZE);
        for(Char ch: Actor.chars()){
            if(ch.alignment == Char.Alignment.ENEMY && Dungeon.level.trueDistance(ch.pos, attacker.pos) < radius){
                if(ch != attacker){
                    MagicMissile.boltFromChar(attacker.sprite.parent, MagicMissile.SHADOW, attacker.sprite, ch.pos,
                            new Callback() {
                                @Override
                                public void call() {
                                    int damage = w.damageRoll(attacker);
                                    if(ch.properties().contains(Char.Property.DEMONIC) || ch.properties().contains(Char.Property.UNDEAD)){
                                        damage /= 2f;
                                    }
                                    ch.damage(damage, attacker);
                                    int preHP = attacker.HP;
                                    attacker.HP = Math.min(attacker.HP+damage/7, attacker.HT);
                                    if(attacker.HP - preHP > 0){
                                        attacker.sprite.showStatus(0x00FF00, "%d", attacker.HP - preHP);
                                    }
                                    if(!ch.isAlive()){
                                        Wraith corrupted = new Wraith();
                                        corrupted.adjustStats(Dungeon.depth);
                                        corrupted.pos = ch.pos;
                                        GameScene.add(corrupted);
                                        Buff.affect(corrupted, Corruption.class);
                                        CellEmitter.get(ch.pos).burst(ShadowParticle.CURSE, 6);
                                        Sample.INSTANCE.play(Assets.Sounds.CURSED);

                                    }
                                }
                            });
                }
            }
        }
        super.useUp(w, attacker);
    }
}
