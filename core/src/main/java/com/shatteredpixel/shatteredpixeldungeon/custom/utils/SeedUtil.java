package com.shatteredpixel.shatteredpixeldungeon.custom.utils;

import static java.lang.Long.parseLong;

public class SeedUtil {

    public static long parseSeed(String seed){
        try {
            Long.parseLong(seed);
        } catch (NumberFormatException e) {
            return directConvert(seed, 'A', 26);
        }

        return directConvert(seed, '0', 10);
    }
    //Here we regard 'A' as 0, 'Z' as 25.
    public static long directConvert(String code, char baseCode, int radix){
        long total = 0;
        for(char c: code.toCharArray()){
            c -= baseCode;
            total *= radix;
            total += c;
        }
        if(total < 0){
            total += Long.MAX_VALUE;
        }
        return total % 5429503678976L;
    }
}
