package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.wep.limited;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.BallisticaFloat;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.GME;
import com.shatteredpixel.shatteredpixeldungeon.custom.visuals.effects.BeamCustom;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses.CountInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public abstract class Scattering extends CountInscription {
    {
        defaultTriggers = 32;
    }
    protected int colorRGB888 = 0xFFFFFF;
    protected int sideBeam = 1;
    protected float angle = 10f;
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        for(int i=-sideBeam; i<=sideBeam; ++i){
            BallisticaFloat baf = new BallisticaFloat(attacker.pos, GME.angle(attacker.pos, defender.pos) + angle* i, weapon.RCH * 2 + 4, Ballistica.STOP_TARGET | BallisticaFloat.STOP_SOLID);
            attacker.sprite.parent.add(new BeamCustom(attacker.sprite.center(), baf.collisionPosF.clone().scale(DungeonTilemap.SIZE), Effects.Type.LIGHT_RAY)
                    .setColor(colorRGB888).setLifespan(0.4f));
            Sample.INSTANCE.play(Assets.Sounds.RAY);
            beamProc(weapon, attacker, damage, baf);
        }
        consume(weapon, attacker);
        return 0;
    }

    protected abstract void beamProc(Weapon w, Char attacker, int damage, BallisticaFloat baf);

    @Override
    protected void onGain() {
        super.onGain();
        weapon.RCH += 2;
    }

    @Override
    protected void onLose() {
        super.onLose();
        weapon.RCH -= 2;
    }

    public static class LightScattering extends Scattering{
        {
            defaultTriggers = 40;
            colorRGB888 = 0xFFFFFF;
            angle = 30f;
            sideBeam = 3;
        }

        @Override
        protected void beamProc(Weapon w, Char attacker, int damage, BallisticaFloat baf){
            for(int cell: baf.subPath(1, baf.dist)){
                Char ch = Actor.findChar(cell);
                if(ch != null && ch.alignment != Char.Alignment.ALLY){
                    ch.damage(GME.accurateRound(damage*Random.Float(0.2f, 0.3f)), attacker);
                    Buff.affect(ch, Blindness.class, 5f);
                }
            }
        }
    }

    public static class FieryScattering extends Scattering {
        {
            defaultTriggers = 25;
            colorRGB888 = 0xFFE0B0;
            sideBeam = 1;
            angle = 15f;
        }

        @Override
        protected void beamProc(Weapon w, Char attacker, int damage, BallisticaFloat baf){
            for(int cell: baf.subPath(1, baf.dist)){
                Char ch = Actor.findChar(cell);
                if(ch != null && ch.alignment != Char.Alignment.ALLY){
                    ch.damage(GME.accurateRound(damage*Random.Float(0.47f, 0.57f)), attacker);
                    Buff.affect(ch, Burning.class).reignite(ch, 4f);
                }
            }
        }
    }

    public static class IceyScattering extends Scattering {
        {
            defaultTriggers = 25;
            colorRGB888 = 0x6097F4;
            sideBeam = 2;
            angle = 22.5f;
        }

        @Override
        protected void beamProc(Weapon w, Char attacker, int damage, BallisticaFloat baf){
            for(int cell: baf.subPath(1, baf.dist)){
                Char ch = Actor.findChar(cell);
                if(ch != null && ch.alignment != Char.Alignment.ALLY){
                    ch.damage(GME.accurateRound(damage* Random.Float(0.25f, 0.45f)), attacker);
                    Freezing.freeze(ch.pos);
                }
            }
        }
    }

}