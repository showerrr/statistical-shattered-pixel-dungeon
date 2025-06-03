package com.shatteredpixel.shatteredpixeldungeon.expansion.mergeManagers;

import com.shatteredpixel.shatteredpixeldungeon.expansion.magic.holder.SpellRecord;
import com.watabou.utils.Bundle;

public class StaticStorage {
    public static void storeInBundle(Bundle bundle){
        SpellRecord.save(bundle);
    }

    public static void restoreFromBundle(Bundle bundle){
        SpellRecord.load(bundle);
    }
}
