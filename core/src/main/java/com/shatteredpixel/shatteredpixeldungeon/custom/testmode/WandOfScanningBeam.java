package com.shatteredpixel.shatteredpixeldungeon.custom.testmode;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.BallisticaReal;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.GME;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.timing.VirtualActor;
import com.shatteredpixel.shatteredpixeldungeon.custom.visuals.effects.ScanningBeam;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class WandOfScanningBeam extends WandOfDisintegration implements ScanningBeam.OnCollide {
    @Override
    public void onZap(Ballistica attack) {
        ScanningBeam.setCollide(this);
        float angle = GME.angle(curUser.pos, attack.collisionPos);
        PointF center = curUser.sprite.center().invScale(DungeonTilemap.SIZE);
        curUser.sprite.parent.add(new ScanningBeam
                (Effects.Type.DEATH_RAY, BallisticaReal.STOP_SOLID,
                    new ScanningBeam.BeamData()
                        .setPosition(center.x, center.y, angle, 999f)
                        .setSpeed(0f, 0f, 90f)
                        .setTime(0.4f, 1.5f, 0.4f)
                ).setDiameter(1.6f)
        );
        VirtualActor.delay(2.3f, null);
    }

    @Override
    public void fx(Ballistica attack, Callback callback){
        callback.call();
    }

    @Override
    public int onHitProc(Char ch) {
        if(ch.pos == curUser.pos){
            return 0;
        }
        wandProc(ch, chargesPerCast());
        ch.damage( damageRoll(buffedLvl()), this );
        ch.sprite.centerEmitter().burst( PurpleParticle.BURST, Random.IntRange( 1, 2 ) );
        ch.sprite.flash();
        return 1;
    }

    @Override
    public int cellProc(int i) {
        if(i == curUser.pos){
            return 0;
        }
        if(Dungeon.level.flamable[i]){
            Dungeon.level.destroy(i);
            GameScene.updateMap( i );
        }
        return 0;
    }
}
