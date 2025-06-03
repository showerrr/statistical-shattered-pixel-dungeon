package com.shatteredpixel.shatteredpixeldungeon.custom.ch.boss;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.GooBlob;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GooSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class GooHard extends Boss{
    {
        initProperty();
        initBaseStatus(3, 8, 14, 10, 150, 0, 2);
        initStatus(20);

        properties.add(Property.DEMONIC);
        properties.add(Property.ACIDIC);
        spriteClass = GooSprite.class;

    }

    private int pumpedUp = 0;

    private int surroundingWater(){
        int water = 0;
        for(int n: PathFinder.NEIGHBOURS8){
            if(Dungeon.level.water[n+pos]){
                water++;
            }
        }
        return water;
    }

    @Override
    public int damageRoll() {
        return Math.round(super.damageRoll() * (berserk()?1.33f:1f) * (pumpedUp>0?3f:1f) *(surroundingWater()>7?1.8f:1f));
    }

    @Override
    public int attackSkill( Char target ) {
        return Math.round(super.attackSkill(target)*(pumpedUp>0?3f:1f)*(HP*2<HT?1.5f:1f)*(surroundingWater()>7?1000f:1f));
    }

    @Override
    public int defenseSkill(Char enemy) {
        return (int)(super.defenseSkill(enemy) * ((HP*2 <= HT)? 1.5f : 1f)*(surroundingWater()>7?0f:1f));
    }

    @Override
    public float speed(){
        return super.speed()*(Dungeon.level.water[pos]?1.33f:1f)*(surroundingWater()>7?2f:1f);
    }

    private boolean berserk(){ return HP*2<HT; }


    @Override
    public boolean act() {
        if(state != SLEEPING) {
            Buff t = buff(Timer.class);
            if (t == null) {
                Buff.affect(this, Timer.class, berserk()? 18f:27f);
                summonMiniGoo();
            }
        }

        if (Dungeon.level.water[pos] && HP < HT) {

            if (HP*2 == HT) {
                BossHealthBar.bleed(false);
                ((GooSprite)sprite).spray(false);
            }
            HP++;
            if(surroundingWater() > 7){
                HP += 3;
            }

            HP = Math.min(HP, HT);

            sprite.emitter().burst( Speck.factory( Speck.HEALING ), surroundingWater() > 7 ? 4:1 );
        }

        Dungeon.level.setCellToWater(true, pos);

        return super.act();
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        return (pumpedUp > 0) ? distance( enemy ) <= 2 : super.canAttack(enemy);
    }

    @Override
    public int attackProc( Char enemy, int damage ) {
        damage = super.attackProc( enemy, damage );

        if (pumpedUp > 0) {
            Camera.main.shake( 3, 0.2f );
            Buff.affect(enemy, Cripple.class, 6f);
            Buff.affect(enemy, Vulnerable.class, 12f);
        }

        return damage;
    }

    @Override
    public int defenseProc(Char enemy, int damage){
        if(damage > 3) summonMiniGoo();
        return super.defenseProc(enemy, damage);
    }

    private void summonMiniGoo(){
        ArrayList<Integer> candidates = new ArrayList<>();
        boolean[] solid = Dungeon.level.solid;

        for (int n : PathFinder.NEIGHBOURS8) {
            int p = n + pos;
            if (!solid[p] && Actor.findChar( p ) == null) {
                candidates.add( p );
            }
        }

        if (candidates.size() > 0) {
            GooMini mini = new GooMini();
            Buff.affect(mini, Timer.class, berserk()? 5f:7f);
            mini.pos = Random.element( candidates );
            mini.state = mini.HUNTING;

            Dungeon.level.occupyCell(mini);

            GameScene.add( mini , 0f );
            Actor.addDelayed( new Pushing( mini, pos, mini.pos ), -1 );
        }
    }

    @Override
    public void updateSpriteState() {
        super.updateSpriteState();

        if (pumpedUp > 0){
            ((GooSprite)sprite).pumpUp( pumpedUp );
        }
    }

    @Override
    protected boolean doAttack( Char enemy ) {
        if (pumpedUp == 1) {
            ((GooSprite)sprite).pumpUp( 2 );
            pumpedUp++;
            Sample.INSTANCE.play( Assets.Sounds.CHARGEUP );

            spend( attackDelay() );

            return true;
        } else if (pumpedUp >= 2 || Random.Int( (HP*2 <= HT) ? 2 : 3 ) > 0) {

            boolean visible = Dungeon.level.heroFOV[pos];

            if (visible) {
                if (pumpedUp >= 2) {
                    ((GooSprite) sprite).pumpAttack();
                } else {
                    sprite.attack(enemy.pos);
                }
            } else {
                attack( enemy );
            }

            spend( attackDelay() );

            return !visible;

        } else {

            pumpedUp++;

            ((GooSprite)sprite).pumpUp( 1 );

            if (Dungeon.level.heroFOV[pos]) {
                sprite.showStatus( CharSprite.NEGATIVE, Messages.get(this, "!!!") );
                GLog.n( Messages.get(this, "pumpup") );
                Sample.INSTANCE.play( Assets.Sounds.CHARGEUP, 1f, 0.8f );
            }

            spend( attackDelay() );

            return true;
        }
    }

    @Override
    public boolean attack( Char enemy, float dmgMulti, float dmgBonus, float accMulti ) {
        boolean result = super.attack(  enemy, dmgMulti, dmgBonus, accMulti );
        pumpedUp = 0;
        return result;
    }

    @Override
    protected boolean getCloser( int target ) {
        if (pumpedUp != 0) {
            pumpedUp = 0;
            sprite.idle();
        }
        return super.getCloser( target );
    }

    @Override
    public void damage(int dmg, Object src) {
        if (!BossHealthBar.isAssigned()){
            BossHealthBar.assignBoss( this );
        }
        if(src instanceof Wand){ summonMiniGoo(); }
        if(surroundingWater() > 7){
            dmg = dmg * 3 / 2;
        }
        boolean bleeding = (HP*2 <= HT);
        super.damage(dmg, src);
        if ((HP*2 <= HT) && !bleeding){
            BossHealthBar.bleed(true);
            sprite.showStatus(CharSprite.NEGATIVE, Messages.get(this, "enraged"));
            ((GooSprite)sprite).spray(true);
            yell(Messages.get(this, "gluuurp"));
        }
        LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
        if (lock != null) lock.addTime(dmg*2);
    }

    @Override
    public void die( Object cause ) {

        super.die( cause );

        Dungeon.level.unseal();

        GameScene.bossSlain();
        Dungeon.level.drop( new IronKey( Dungeon.depth ).quantity(2), pos ).sprite.drop();
        Dungeon.level.drop( new GoldenKey( Dungeon.depth ), pos ).sprite.drop();

        int blobs = Random.chances(new float[]{5, 4, 3, 2, 1}) + 3;
        for (int i = 0; i < blobs; i++){
            int ofs;
            do {
                ofs = PathFinder.NEIGHBOURS8[Random.Int(8)];
            } while (!Dungeon.level.passable[pos + ofs]);
            Dungeon.level.drop( new GooBlob(), pos + ofs ).sprite.drop();
        }

        Badges.validateBossSlain();

        yell( Messages.get(this, "defeated") );
    }

    @Override
    public void notice() {
        super.notice();
        if (!BossHealthBar.isAssigned()) {
            BossHealthBar.assignBoss(this);
            yell(Messages.get(this, "notice"));
            for (Char ch : Actor.chars()){
                if (ch instanceof DriedRose.GhostHero){
                    ((DriedRose.GhostHero) ch).sayBoss();
                }
            }
        }
    }

    private final String PUMPEDUP = "pumpedup";

    @Override
    public void storeInBundle( Bundle bundle ) {

        super.storeInBundle( bundle );

        bundle.put( PUMPEDUP , pumpedUp );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {

        super.restoreFromBundle( bundle );

        pumpedUp = bundle.getInt( PUMPEDUP );
        if (state != SLEEPING) BossHealthBar.assignBoss(this);
        if ((HP*2 <= HT)) BossHealthBar.bleed(true);

    }

}
