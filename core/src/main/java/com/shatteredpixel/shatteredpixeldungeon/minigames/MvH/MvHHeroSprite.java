package com.shatteredpixel.shatteredpixeldungeon.minigames.mvh;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.RectF;

public class MvHHeroSprite extends MvHCharSprite {
        private static final int FRAME_WIDTH	= 12;
        private static final int FRAME_HEIGHT	= 15;

        private static final int RUN_FRAMERATE	= 20;

        private static TextureFilm tiers;

        private Animation fly;
        private Animation read;
        private Animation runSlowly;

        public int tier = 0;
        public boolean isDied = false;
        public MvHMobSprite enemy;
        public int attackdelay = 0;
        public int attackDelay = 1;
        public boolean haveAmulet = false;
        public int gameBurning = 0;
        public int gameFrost = 0;
        public int gameAttack = 50;
        public int level = 1;

        public Amulet amulet;

        public void setMapToPos(float x, float y) {
            this.x = x * 20 + 15;
            this.y = y * 20 + 33;
        }

        public MvHHeroSprite() {
            super();

            texture(Assets.Sprites.WARRIOR);

            updateArmor();

//            link( Dungeon.hero );

            if (ch.isAlive())
                idle();
            else
                die();
        }

        public MvHHeroSprite(boolean game,int tier,int heroClass) {
            super();
            this.gameMvH = game;
            this.tier = tier;

            switch (heroClass) {
                default:
                case 0:
                    texture(Assets.Sprites.WARRIOR);
                    break;
                case 1:
                    texture(Assets.Sprites.MAGE);
                    break;
                case 2:
                    texture(Assets.Sprites.ROGUE);
                    break;
                case 3:
                    texture(Assets.Sprites.HUNTRESS);
                    break;
                case 4:
                    texture(Assets.Sprites.DUELIST);
                    break;
            }

            updateArmor();
            idle();
        }

        public MvHHeroSprite(int tier) {
            super();
            this.gameHungry = true;
            this.tier = tier;

            texture(Assets.Sprites.WARRIOR);
            updateArmor();
            idle();
        }

        public void updateArmor() {
            TextureFilm film;
            if (gameMvH || gameHungry) {
                film = new TextureFilm(tiers(),tier,FRAME_WIDTH,FRAME_HEIGHT);
            } else {
                film = new TextureFilm( tiers(), Dungeon.hero.tier(), FRAME_WIDTH, FRAME_HEIGHT );
            }

            idle = new Animation( 1, true );
            idle.frames( film, 0, 0, 0, 1, 0, 0, 1, 1 );

            run = new Animation( RUN_FRAMERATE, true );
            run.frames( film, 2, 3, 4, 5, 6, 7 );

            runSlowly = new Animation(10,true);
            runSlowly.frames(film,2, 3, 4, 5, 6, 7);

            die = new Animation( 20, false );
            die.frames( film, 8, 9, 10, 11, 12, 11 );

            attack = new Animation( 15, false );
            attack.frames( film, 13, 14, 15, 0 );

            zap = attack.clone();

            operate = new Animation( 8, false );
            operate.frames( film, 16, 17, 16, 17 );

            fly = new Animation( 1, true );
            fly.frames( film, 18 );

            read = new Animation( 20, false );
            read.frames( film, 19, 20, 20, 20, 20, 20, 20, 20, 20, 19 );

            if (gameMvH) {
                runSlowly();
            } else {
                if (Dungeon.hero.isAlive())
                    idle();
                else
                    die();
            }

        }

        @Override
        public void place( int p ) {
            super.place( p );
            if (Game.scene() instanceof GameScene) Camera.main.panTo(center(), 5f);
        }

        @Override
        public void move( int from, int to ) {
            super.move( from, to );
            if (ch != null && ch.flying) {
                play( fly );
            }
            Camera.main.panFollow(this, 20f);
        }

        public void run() {
            play(run);
        }

        public void runSlowly() {
            play(runSlowly);
        }

        @Override
        public void idle() {
            super.idle();
            if (ch != null && ch.flying) {
                play( fly );
            }
        }

        @Override
        public void jump( int from, int to, Callback callback ) {
            super.jump( from, to, callback );
            play( fly );
        }

        public void read() {
            animCallback = new Callback() {
                @Override
                public void call() {
                    idle();
                    ch.onOperateComplete();
                }
            };
            play( read );
        }

        @Override
        public void bloodBurstA(PointF from, int damage) {
            //Does nothing.

            /*
             * This is both for visual clarity, and also for content ratings regarding violence
             * towards human characters. The heroes are the only human or human-like characters which
             * participate in combat, so removing all blood associated with them is a simple way to
             * reduce the violence rating of the game.
             */
        }

        @Override
        public void update() {

            if (!gameMvH && !gameHungry) {
                sleeping = ch.isAlive() && ((Hero)ch).resting;
            }

            if (gameMvH) {
                if (WndMvH.wndMvH.pause && !haveAmulet) {
                    return;
                }

                if ((delay -= Game.elapsed) < 0) {

                    if (gameBurning > 0) {
                        HP -= 10;
                        gameBurning--;
                        if (gameBurning == 0) {
                            resetColor();
                        }
                    }

                    if (HP > 0) {
                        chooseEnemy();

                        if (x <= 0) {
                            WndMvH.wndMvH.gameOver();
                            flipHorizontal = false;

                            if (!haveAmulet) {
                                amulet = new Amulet();
                                amulet.scale.set(0.2f);
                                amulet.heroSprite = this;
                                WndMvH.wndMvH.addToFront(amulet);
                                haveAmulet = true;
                            }
                        }

                        if (haveAmulet) {
                            x += gameSpeed * 5;

                        } else if (enemy == null) {
                            runSlowly();
                            x -= gameSpeed;

                        } else {
                            doAttack();
                        }

                    } else {
                        gameDie();
                    }

                    if (gameFrost > 0) {
                        delay = DELAY * 2;
                        gameFrost--;
                        if (gameFrost == 0) {
                            resetColor();
                        }
                    } else {
                        delay = DELAY;
                    }

                }

            }

            super.update();
        }

        public void doAttack() {
            if (attackdelay <= attackDelay) {
                attackdelay++;
                enemy.HP -= gameAttack;
//							GLog.n(Integer.toString(enemy.HP));
                if (enemy.HP <= 0) {
                    enemy.gameDie();
                    enemy = null;
                }
            } else {
                enemy.flash();
                attackdelay = 0;
                enemy.HP -= gameAttack;
//							GLog.n(Integer.toString(enemy.HP));
                Sample.INSTANCE.play(Assets.Sounds.HIT);
                if (enemy.HP <= 0) {
                    enemy.gameDie();
                    enemy = null;
                }
                play(attack);
            }
        }

        public void superUpdate() {
            super.update();
        }

        public void gameDie() {
            if (isDied) {
                if (WndMvH.wndMvH.mvHHeroSpawner.lastWave && WndMvH.heroSprites.size() <= 1) {
                    WndMvH.wndMvH.createToLoot(x + width() / 2,y + height() / 2);
                }
                WndMvH.heroSprites.remove(this);
                killAndErase();
            } else {
                isDied = true;
                die();
            }
        }

        public void chooseEnemy() {
            enemy = null;

            if (WndMvH.grasses.size() == 0) {
                return;
            }

            for (WndMvH.Grass grass : WndMvH.grasses) {
                if (grass.inside(x,y + height() / 2) && grass.mobSprite != null) {
                    enemy = grass.mobSprite;
                }
            }
        }

        public void blink(float x,float y) {
            alpha(0);
            parent.add( new AlphaTweener(this,1,0.4f));
//            emitter().start(Speck.factory(Speck.LIGHT),0.2f,3);
            this.x = x;
            this.y = y;
        }

        public void sprint( float speed ) {
            run.delay = 1f / speed / RUN_FRAMERATE;
        }

        public static TextureFilm tiers() {
            if (tiers == null) {
                SmartTexture texture = TextureCache.get( Assets.Sprites.ROGUE );
                tiers = new TextureFilm( texture, texture.width, FRAME_HEIGHT );
            }

            return tiers;
        }

        public static Image avatar(HeroClass cl, int armorTier ) {

            RectF patch = tiers().get( armorTier );
            Image avatar = new Image( cl.spritesheet() );
            RectF frame = avatar.texture.uvRect( 1, 0, FRAME_WIDTH, FRAME_HEIGHT );
            frame.shift( patch.left, patch.top );
            avatar.frame( frame );

            return avatar;
        }

        public class Amulet extends Image {
            public MvHHeroSprite heroSprite;

            public Amulet() {
                super(Assets.Sprites.AMULET);
            }

            @Override
            public void update() {
                point(heroSprite.center().offset(1.5f,-3f));
            }
        }
    }

