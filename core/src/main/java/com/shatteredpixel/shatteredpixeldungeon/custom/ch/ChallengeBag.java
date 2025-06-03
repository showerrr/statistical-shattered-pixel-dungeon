package com.shatteredpixel.shatteredpixeldungeon.custom.ch;

import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KingsCrown;
//import com.shatteredpixel.shatteredpixeldungeon.items.MerchantsBeacon;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.ArrayList;

public class ChallengeBag extends Bag {
    {
        image = ItemSpriteSheet.CRYSTAL_CHEST;
    }

    private static final ArrayList<Class<? extends Item>> miscItems=new ArrayList<>();
    static {
        /*
        miscItems.add(Ankh.class);
        miscItems.add(Stylus.class);
        miscItems.add(TengusMask.class);
        miscItems.add(KingsCrown.class);
        miscItems.add(Amulet.class);
        miscItems.add(Honeypot.class);
        miscItems.add(Torch.class);
        miscItems.add(MerchantsBeacon.class);
         */
    }

    @Override
    public boolean canHold( Item item ) {
        if(item instanceof ChallengeItem){
            return super.canHold(item);
        }
        for(int i=0, L=miscItems.size();i<L;++i){
            if (item.getClass() == miscItems.get(i)) {
                return super.canHold(item);
            }
        }
        return false;
    }

    @Override
    public int value() {
        return 40;
    }

    public int capacity(){
        return 19;
    }
}
