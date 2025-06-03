package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.cave;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.MobHard;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class SpinnerH extends MobHard {
    {
        baseSpeed  = 1.35f;
        EXP = 11;

        spriteClass = SpinnerHSprite.class;

        HP = HT = 50;
        defenseSkill = 17;

        maxLvl = 17;

        loot = new MysteryMeat();
        lootChance = 0.125f;

        FLEEING = new SpinnerH.Fleeing();
    }

    @Override
    public void move(int step, boolean traveling){
        GameScene.add(Blob.seed(step, 8, Web.class));

        if (traveling && enemySeen && webCoolDown <= 0 && lastEnemyPos != -1){
            if (webPos() != -1){
                if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
                    sprite.zap( webPos() );
                    shotWebVisually = true;
                } else {
                    shootWeb();
                }
            }
        }

        super.move(step, traveling);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(10, 20);
    }

    @Override
    public int attackSkill(Char target) {
        return 22;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 6);
    }

    private int webCoolDown = 0;
    private int lastEnemyPos = -1;

    private static final String WEB_COOLDOWN = "web_cooldown";
    private static final String LAST_ENEMY_POS = "last_enemy_pos";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(WEB_COOLDOWN, webCoolDown);
        bundle.put(LAST_ENEMY_POS, lastEnemyPos);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        webCoolDown = bundle.getInt( WEB_COOLDOWN );
        lastEnemyPos = bundle.getInt( LAST_ENEMY_POS );
    }

    @Override
    protected boolean act() {
        AiState lastState = state;
        boolean result = super.act();

        //if state changed from wandering to hunting, we haven't acted yet, don't update.
        if (!(lastState == WANDERING && state == HUNTING)) {
            webCoolDown--;
            if (shotWebVisually){
                result = shotWebVisually = false;
            } else {
                if (enemy != null && enemySeen) {
                    lastEnemyPos = enemy.pos;
                } else {
                    lastEnemyPos = Dungeon.hero.pos;
                }
            }
        }

        if (state == FLEEING && buff( Terror.class ) == null && buff( Dread.class ) == null &&
                enemy != null && enemySeen && enemy.buff( Poison.class ) == null) {
            state = HUNTING;
        }
        return result;
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc( enemy, damage );
        if (Random.Int(2) == 0) {
            int duration = Random.IntRange(7, 8);
            //we only use half the ascension modifier here as total poison dmg doesn't scale linearly
            duration = Math.round(duration * (AscensionChallenge.statModifier(this)/2f + 0.5f));
            Buff.affect(enemy, Poison.class).set(duration);
            webCoolDown = 0;
            state = FLEEING;
        }

        return damage;
    }

    private boolean shotWebVisually = false;

    public int webPos(){

        Char enemy = this.enemy;
        if (enemy == null) return -1;

        Ballistica b;
        //aims web in direction enemy is moving, or between self and enemy if they aren't moving
        if (lastEnemyPos == enemy.pos){
            b = new Ballistica( enemy.pos, pos, Ballistica.WONT_STOP );
        } else {
            b = new Ballistica( lastEnemyPos, enemy.pos, Ballistica.WONT_STOP );
        }

        int collisionIndex = 0;
        for (int i = 0; i < b.path.size(); i++){
            if (b.path.get(i) == enemy.pos){
                collisionIndex = i;
                break;
            }
        }

        //in case target is at the edge of the map and there are no more cells in the path
        if (b.path.size() <= collisionIndex+1){
            return -1;
        }

        int webPos = b.path.get( collisionIndex+1 );

        //ensure we aren't shooting the web through walls
        int projectilePos = new Ballistica( pos, webPos, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID).collisionPos;

        if (webPos != enemy.pos && projectilePos == webPos && Dungeon.level.passable[webPos]){
            return webPos;
        } else {
            return -1;
        }

    }

    public void shootWeb(){
        int webPos = webPos();
        if (webPos != -1){
            int i;
            for (i = 0; i < PathFinder.CIRCLE8.length; i++){
                if ((enemy.pos + PathFinder.CIRCLE8[i]) == webPos){
                    break;
                }
            }

            //spread to the tile hero was moving towards and the two adjacent ones
            int leftPos = enemy.pos + PathFinder.CIRCLE8[left(i)];
            int rightPos = enemy.pos + PathFinder.CIRCLE8[right(i)];

            if (Dungeon.level.passable[leftPos]) GameScene.add(Blob.seed(leftPos, 20, Web.class));
            if (Dungeon.level.passable[webPos])  GameScene.add(Blob.seed(webPos, 20, Web.class));
            if (Dungeon.level.passable[rightPos])GameScene.add(Blob.seed(rightPos, 20, Web.class));

            webCoolDown = 10;

            if (Dungeon.level.heroFOV[enemy.pos]){
                Dungeon.hero.interrupt();
            }
        }
        next();
    }

    private int left(int direction){
        return direction == 0 ? 7 : direction-1;
    }

    private int right(int direction){
        return direction == 7 ? 0 : direction+1;
    }

    {
        resistances.add(Poison.class);
    }

    {
        immunities.add(Web.class);
    }

    private class Fleeing extends Mob.Fleeing {
        @Override
        protected void nowhereToRun() {
            if (buff(Terror.class) == null && buff(Dread.class) == null) {
                state = HUNTING;
            } else {
                super.nowhereToRun();
            }
        }
    }

    public static class SpinnerHSprite extends MobSprite {

        public SpinnerHSprite() {
            super();

            perspectiveRaise = 0f;

            texture( Assets.Sprites.SPINNER );

            TextureFilm frames = new TextureFilm( texture, 16, 16 );

            idle = new MovieClip.Animation( 10, true );
            idle.frames( frames, 0, 0, 0, 0, 0, 1, 0, 1 );

            run = new MovieClip.Animation( 15, true );
            run.frames( frames, 0, 2, 0, 3 );

            attack = new MovieClip.Animation( 12, false );
            attack.frames( frames, 0, 4, 5, 0 );

            zap = attack.clone();

            die = new MovieClip.Animation( 12, false );
            die.frames( frames, 6, 7, 8, 9 );

            play( idle );
        }

        @Override
        public void link(Char ch) {
            super.link(ch);
            if (parent != null) {
                parent.sendToBack(this);
                if (aura != null){
                    parent.sendToBack(aura);
                }
            }
            renderShadow = false;
        }

        public void zap( int cell ) {

            turnTo( ch.pos , cell );
            play( zap );

            MagicMissile.boltFromChar( parent,
                    MagicMissile.MAGIC_MISSILE,
                    this,
                    cell,
                    new Callback() {
                        @Override
                        public void call() {
                            ((SpinnerH)ch).shootWeb();
                        }
                    } );
            Sample.INSTANCE.play( Assets.Sounds.MISS );
        }

        @Override
        public void onComplete( MovieClip.Animation anim ) {
            if (anim == zap) {
                play( run );
            }
            super.onComplete( anim );
        }

        @Override
        public int blood() {
            return 0xFFBFE5B8;
        }
    }

}
