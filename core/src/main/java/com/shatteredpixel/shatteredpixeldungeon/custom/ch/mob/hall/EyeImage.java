package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.hall;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;

public class EyeImage extends EyeH{
    {
        maxLvl = -10000;
        EXP = 25;
        HT = HP = 1;
        spriteClass = EyeImageSprite.class;
    }

    @Override
    protected void createImage(){

    }

    @Override
    public void damage(int damage, Object src){

    }

    @Override
    public int damageRoll(){
        return super.damageRoll()/4;
    }

    @Override
    public void deathGaze(){
        super.deathGaze();
        die(this);
    }

    public static class EyeImageSprite extends EyeHSprite{

        @Override
        public void update(){
            super.update();
            alpha(0.4f);
        }
    }

    @Override
    public void summonRipper(){}
}
