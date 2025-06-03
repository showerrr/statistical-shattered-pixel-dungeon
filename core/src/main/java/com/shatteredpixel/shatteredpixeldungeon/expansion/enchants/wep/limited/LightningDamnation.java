package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.timing.VirtualActor;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.watabou.noosa.Game;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class LightningDamnation extends CountInscription {
    {
        defaultTriggers = 20;
    }
    //0 = havent executed, 1 = killed, -1 = still alive
    private int killed  = 0;

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        if(damage >= defender.HP){
            defender.damage(damage * 11 / 10, attacker);
            if(defender.isAlive()) {return 0;}

            DeathListener deathListener = new DeathListener();
            deathListener.setAttacker(attacker);
            attacker.sprite.parent.add(deathListener);
            Char aim = findLowestHealth(attacker);
            if(aim != null) {
                lightningStrike(aim);
                consume(weapon, attacker);
            }

            VirtualActor.delay(Math.max(0.5f, Game.elapsed * Actor.chars().size() * 2f));
        }
        return damage;
    }

    private void lightningStrike(Char aim){
        float x = aim.sprite.center().x;
        float y = aim.sprite.center().y;
        aim.sprite.parent.add(new Lightning(aim.sprite.center(), new PointF( x + Random.Float(-4f, 4f), y-300f), null));
        Sample.INSTANCE.play( Assets.Sounds.LIGHTNING, 1.0f);

        aim.damage(Random.Int(Dungeon.depth + 4, Dungeon.depth * 2 + 9), LightningDamnation.class);
        aim.sprite.centerEmitter().burst( SparkParticle.FACTORY, 10 );
        aim.sprite.flash();
        killed = aim.isAlive() ? -1 : 1;
    }

    private Char findLowestHealth(Char attacker){
        int minHealth = Integer.MAX_VALUE;
        Char minCh = null;
        for(Char ch : Actor.chars()){
            if(ch.alignment != Char.Alignment.ALLY && ch != attacker && ch.isAlive() && Dungeon.level.distance(ch.pos, attacker.pos) < 7){
                if(ch.HP < minHealth){
                    minHealth = ch.HP;
                    minCh = ch;
                }
            }
        }
        return minCh;
    }

    private class DeathListener extends Visual{
        Char attacker;

        public void setAttacker(Char attacker) {
            this.attacker = attacker;
        }

        public DeathListener(){
            super(0, 0, 0, 0);
        }

        @Override
        public void update() {
            super.update();
            if(killed == 1){
                killed = 0;
                Char aim = findLowestHealth(attacker);
                if(aim!=null) {
                    lightningStrike(aim);
                }else{
                    killAndErase();
                }
            }else if(killed == -1){
                killed = 0;
                killAndErase();
            }
        }
    }
}
