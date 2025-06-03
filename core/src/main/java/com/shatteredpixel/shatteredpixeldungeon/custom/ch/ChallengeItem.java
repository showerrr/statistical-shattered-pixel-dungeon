package com.shatteredpixel.shatteredpixeldungeon.custom.ch;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;

public class ChallengeItem extends Item {
    {
        unique = true;
    }
    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

}
