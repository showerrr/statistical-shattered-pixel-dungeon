package com.shatteredpixel.shatteredpixeldungeon.custom.testmode;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.custom.utils.BallisticaReal;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Callback;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class WandOfReflectDisintegration extends WandOfDisintegration {

    public int min(int lvl){
        return 2+lvl;
    }

    public int max(int lvl){
        return 6+3*lvl;
    }

    protected static ArrayList<PointF> fxS = new ArrayList<>();
    protected static ArrayList<PointF> fxE = new ArrayList<>();
    protected static ArrayList<BallisticaReal> beams = new ArrayList<>();
    protected static ArrayList<Float> offset_1 = new ArrayList<>();
    private static final String AC_TEST_ZAP = "test_zap";
    private boolean isTest = false;

    protected void changeDefaultAction(String action){
        if(!allowChange(action)) return;
        defaultAction = action;
    }

    protected boolean allowChange(String action){
        return !(action.equals(AC_DROP) || action.equals(AC_THROW));
    }

    @Override
    public ArrayList<String> actions( Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        actions.add(AC_TEST_ZAP);

        return actions;
    }

    @Override
    public void execute( Hero hero, String action ) {

        super.execute( hero, action );

        if (action.equals( AC_TEST_ZAP )) {
            doTestZap(hero);
        }

        changeDefaultAction(action);
    }

    private void doTestZap(Hero user){

        curUser = user;
        curItem = this;

        isTest = true;

        GameScene.selectCell( zapper );
    }

    @Override
    public int targetingPos(Hero user, int dst) {
        return dst;
    }

    protected void buildOffsetArray(int level){
        offset_1.clear();
        float offPerStep = 45f* (1.9f+level) / (1.9f+level*3f);
        int maxDSB;
        if(level>7){maxDSB = 0;}
        else if(level>3){maxDSB = 0;}
        else{maxDSB = 0;}
        //offset_1.add(0f);
        if(maxDSB>0) {
            for (int i = -maxDSB; i < maxDSB + 1; ++i) {
                offset_1.add(i * offPerStep / maxDSB);
            }
        }else{
            offset_1.add(0f);
        }

        // if(level<9) return base_3;
        //else return base_4;
    }

    protected int nReflections(int level){
        //return Math.min(2+level/3, 5);
        return 5;
    }

    protected float reflectionDamageFactor(int reflections){
        return 1f+0.1f*reflections*reflections;
    }

    protected void buildBeams(Ballistica beam){
        fxS.clear();
        fxE.clear();
        beams.clear();
        buildOffsetArray(this.level());
        //addBeam(new Ballistica(beam.sourcePos, beam.collisionPos, Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID));
        float angle = PointF.angle(new PointF(pointToF(Dungeon.level.cellToPoint(beam.sourcePos))),
                new PointF(pointToF(Dungeon.level.cellToPoint(beam.collisionPos))));
        angle /= PointF.G2R;
        if(angle<0f) angle += 360f;
        //GLog.i("%f,", angle);
        int scatter = offset_1.size();
        for(int i=0;i<scatter;++i){
            addBeam(new BallisticaReal(beam.sourcePos, angle + offset_1.get(i), 20, BallisticaReal.STOP_SOLID | BallisticaReal.IGNORE_SOFT_SOLID));
        }
        int maxRf = nReflections(this.level());
        for(int ref = 0; ref < maxRf; ++ref) {
            for (int i = 0; i < scatter; ++i) {
                BallisticaReal br = new BallisticaReal(fxE.get(i+ref*scatter), reflectAngle(fxS.get(i+ref*scatter), fxE.get(i+ref*scatter)), 20, BallisticaReal.STOP_SOLID | BallisticaReal.IGNORE_SOFT_SOLID);
                addBeam(br);
            }
        }

    }

    protected void addBeam(BallisticaReal beam){
        beams.add(beam);
        fxS.add(beam.sourceF);
        fxE.add(beam.collisionF);
    }

    @Override
    public void onZap( Ballistica beam ) {

        if(isTest) {
            curUser.spend(-0.9f);
            isTest = false;
            curCharges++;
            return;
        }
        int count = 0;
        for(BallisticaReal b: beams){
            zapSingle(b, count);
            ++count;
        }
    }

    protected void zapSingle(BallisticaReal beam, int reflection){

        int level = buffedLvl();

        int maxDistance = beam.dist;

        ArrayList<Char> chars = new ArrayList<>();

        for (int c : beam.subPath((reflection>0?0:1), maxDistance)) {
            //prevent self_damage
            if(c==beam.sourceI && c==curUser.pos) continue;

            Char ch;
            if ((ch = Actor.findChar( c )) != null) {

                chars.add( ch );
            }

            if (Dungeon.level.flamable[c]) {
                Dungeon.level.destroy( c );
                GameScene.updateMap( c );

            }

            CellEmitter.center( c ).burst( PurpleParticle.BURST, Random.IntRange( 1, 2 ) );

        }


        Dungeon.observe();

        boolean alive = curUser.isAlive();

        int lvl = level;
        for (Char ch : chars) {
            wandProc(ch, chargesPerCast());
            int damage = Math.round(damageRoll(lvl)*reflectionDamageFactor(reflection));
            if(!ch.equals(curUser)) {
                ch.damage(damage, this);
            }else{
                ch.damage(damage/6, this);
            }
            ch.sprite.centerEmitter().burst( PurpleParticle.BURST, Random.IntRange( 1, 2 ) );
            ch.sprite.flash();
        }

        if (!curUser.isAlive() && alive) {
            Dungeon.fail( getClass() );
            GLog.n(Messages.get(this, "ondeath"));
        }
    }

    protected PointF pointToF(Point p){
        return new PointF(p.x, p.y);
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        //no direct effect, see magesStaff.reachfactor
    }

    @Override
    public void fx( Ballistica beam, Callback callback ) {
        buildBeams(beam);
        int size = fxE.size();
        for(int i=0;i<size;++i){
            //Point p = Dungeon.level.cellToPoint(fxE.get(i));
            //GLog.i("(%d, %d)", p.x, p.y);
            curUser.sprite.parent.add(new Beam.DeathRay(BallisticaReal.raisedPointToScreen(fxS.get(i)), BallisticaReal.raisedPointToScreen(fxE.get(i)) ));
        }
        callback.call();
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(0x220022);
        particle.am = 0.6f;
        particle.setLifespan(1f);
        particle.acc.set(10, -10);
        particle.setSize( 0.5f, 3f);
        particle.shuffleXY(1f);
    }

    protected float horizontalReflectAngle(float angle){
        angle = angle%360f;
        return 360f-angle;
    }

    protected float verticalReflectAngle(float angle){
        angle = angle%360f;
        if(angle<180f) angle=180f-angle;
        else angle = 540f-angle;
        return angle;
    }

    protected float reflectAngle(PointF s, PointF e){
        //PointF realPoint = nextPF(s,e);
        float angle = PointF.angle(s,e)/PointF.G2R;
        if(angle<0f) angle+= 360f;
        float dx = e.x - s.x;
        float dy = e.y - s.y;
        boolean up = dy>0;
        boolean right = dx>0;
        boolean horizontalWall = false;
        boolean verticalWall = false;

        int xTile=(int)e.x;
        if(e.x-(int)e.x<1e-5 && right){
            xTile-=1;
        }
        int yTile=(int)e.y;
        if(e.y-(int)e.y<1e-5 && up){
            yTile-=1;
        }
        final int[] neigh = new int[]{-1, 1, -Dungeon.level.width(), Dungeon.level.width()};
        boolean[] isWall = new boolean[4];
        for(int i=0; i<4; ++i){
            isWall[i]=Dungeon.level.solid[xTile+yTile*Dungeon.level.width()+neigh[i]];
        }
        if(e.x-(int)e.x<1e-5){
            verticalWall = (right && isWall[1]) || (!right && isWall[0]);
        }
        if(e.y-(int)e.y<1e-5){
            horizontalWall = (up && isWall[3]) || (!up && isWall[2]);
        }

        if(horizontalWall != verticalWall){
            if(horizontalWall) return horizontalReflectAngle(angle);
            else return verticalReflectAngle(angle);
        }else if(horizontalWall && verticalWall){
            if(Math.abs(dx)<Math.abs(dy)){
                return horizontalReflectAngle(angle);
            }else{
                return verticalReflectAngle(angle);
            }
        }else{
            return angle;
        }
    }


}

