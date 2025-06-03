package com.shatteredpixel.shatteredpixeldungeon.custom.testmode;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class TestBag extends Bag {
    {
        image = ItemSpriteSheet.LOCKED_CHEST;
    }

    @Override
    public boolean canHold( Item item ) {
        if (item instanceof TestItem){
            return super.canHold(item);
        } else {
            return false;
        }
    }

    @Override
    public int value() {
        return 40;
    }

    public int capacity(){
        return 19;
    }
}
