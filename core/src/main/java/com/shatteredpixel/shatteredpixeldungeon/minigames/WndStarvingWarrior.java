package com.shatteredpixel.shatteredpixeldungeon.minigames;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class WndStarvingWarrior extends Window {

    public static float speed = 0.5f;
    public float time;
    
    public static int warrorX;
    public static int warrorY;
    public static int foodX;
    public static int foodY;

    public static int direction;
    public static int lastDirection;
    public static int length;

    public RedButton up;
    public RedButton down;
    public RedButton left;;
    public RedButton right;

    public ArrayList<StarvingWarrior> warriors = new ArrayList<>();
    public ArrayList<DeliciousFood> foods = new ArrayList<>();

    public ColorBlock line;

    public boolean gameOver = false;

    public WndStarvingWarrior() {

        direction = 6;
        lastDirection = 6;
        length = 6;
        warrorX = 0;
        warrorY = 0;

        StarvingWarrior starvingWarrior = new StarvingWarrior();
        starvingWarrior.x = warrorX;
        starvingWarrior.y = warrorY;
        warriors.add(starvingWarrior);
        add(starvingWarrior);

        spawnFood();

        line = new ColorBlock( 100, 1, 0xFF222222);
        line.x = 0;
        line.y = 101;
        add(line);

        up = new RedButton(Messages.get(WndStarvingWarrior.class,"up"),7){
            @Override
            protected void onClick() {
                super.onClick();
                if (lastDirection != 2) {
                    direction = 8;
                }
            }
        };
        up.setRect(35,103,30,20);
        add(up);

        down = new RedButton(Messages.get(WndStarvingWarrior.class,"down"),7){
            @Override
            protected void onClick() {
                super.onClick();
                if (lastDirection != 8) {
                    direction = 2;
                }
            }
        };
        down.setRect(35,143,30,20);
        add(down);

        left = new RedButton(Messages.get(WndStarvingWarrior.class,"left"),7){
            @Override
            protected void onClick() {
                super.onClick();
                if (lastDirection != 6) {
                    direction = 4;
                }
            }
        };
        left.setRect(0,123,30,20);
        add(left);

        right = new RedButton(Messages.get(WndStarvingWarrior.class,"right"),7){
            @Override
            protected void onClick() {
                super.onClick();
                if (lastDirection != 4) {
                    direction = 6;
                }
            }
        };
        right.setRect(70,123,30,20);
        add(right);

        resize(100,163);
    }

    @Override
    public void update() {
        if (!gameOver) {
            if ((time -= Game.elapsed) < 0) {
                move();
                time = speed;
            }
        }
    }

    public void move() {
        switch (direction) {
            case 8:
                warrorY -= 5;
                break;
            case 2:
                warrorY += 5;
                break;
            case 4:
                warrorX -= 5;
                break;
            case 6:
                warrorX += 5;
                break;
        }
        
        lastDirection = direction;

        if (warrorX == foodX && warrorY == foodY) {
            foods.get(0).remove();
            foods.remove(0);
            length++;
            spawnFood();
        }
        
        if ((warrorX < 0 || warrorX >= 100 || warrorY < 0 || warrorY >= 100) || eatYourself()) {
            gameOver();
            return;
        }

        StarvingWarrior starvingWarrior = new StarvingWarrior();
        warriors.add(starvingWarrior);
        add(starvingWarrior);

        if (warriors.size() > length) {
            warriors.get(0).remove();
            warriors.remove(0);
        }
    }

    public void spawnFood() {

        int tries = 0;
        do {
            tries++;
            foodX = 5 * Random.Int(20);
            foodY = 5 * Random.Int(20);
        } while (shouldTryAgain() && tries < 1000);

        DeliciousFood deliciousFood = new DeliciousFood();
        foods.add(deliciousFood);
        add(deliciousFood);
    }

    public boolean shouldTryAgain() {
        for (StarvingWarrior starvingWarrior : warriors) {
            if (starvingWarrior.x == foodX && starvingWarrior.y == foodY) {
                return true;
            }
        }
        return false;
    }

    public boolean eatYourself() {
        for (StarvingWarrior starvingWarrior : warriors) {
            if (starvingWarrior.x == warrorX && starvingWarrior.y == warrorY) {
                return true;
            }
        }
        return false;
    }
    
    public void gameOver() {
        gameOver = true;
        GLog.newLine();
        GLog.n("GAMEOVER");
    }

    public class StarvingWarrior extends Image {
        public StarvingWarrior() {
            super(Assets.Sprites.WARRIOR);
            frame(texture.uvRect(0,15,12,30));
            scale.set(0.5f);
            x = warrorX;
            y = warrorY;
        }
    }

    public class DeliciousFood extends Image {
        public DeliciousFood() {
            super(Assets.Sprites.ITEMS);

            int j;
            switch (Random.Int(5)) {
                default:
                case 0:
                    j = 80;
                    break;
                case 1:
                    j = 96;
                    break;
                case 2:
                    j = 112;
                    break;
                case 3:
                    j = 144;
                    break;
                case 4:
                    j = 208;
                    break;
            }

            frame(texture.uvRect(j,432,j + 16,444));
            scale.set(0.5f);
            x = foodX;
            y = foodY;
        }
    }
}
