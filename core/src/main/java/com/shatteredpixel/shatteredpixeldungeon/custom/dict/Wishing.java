package com.shatteredpixel.shatteredpixeldungeon.custom.dict;

import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.AssassinsBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.BattleAxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Crossbow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dirk;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Flail;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gauntlet;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Glaive;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gloves;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greataxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatshield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.HandAxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Katana;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Longsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Mace;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Quarterstaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Rapier;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RoundShield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RunicBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sai;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Scimitar;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Shortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sickle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Spear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WarHammer;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WarScythe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Whip;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Trident;

import java.util.HashMap;

public class Wishing {

    public static HashMap<Float, Class<?>> hashMap;
    public static float min;
    public static boolean full;

    public static float getSimilarityRatio(String str, String target) {
//see https://blog.csdn.net/JavaReact/article/details/82144732
        int d[][]; // 矩阵
        int n = str.length();
        int m = target.length();
        int i; // 遍历str的
        int j; // 遍历target的
        char ch1; // str的
        char ch2; // target的
        int temp; // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
        if (n == 0 || m == 0) {
            return 0;
        }
        d = new int[n + 1][m + 1];
        for (i = 0; i <= n; i++) { // 初始化第一列
            d[i][0] = i;
        }

        for (j = 0; j <= m; j++) { // 初始化第一行
            d[0][j] = j;
        }

        for (i = 1; i <= n; i++) { // 遍历str
            ch1 = str.charAt(i - 1);
            // 去匹配target
            for (j = 1; j <= m; j++) {
                ch2 = target.charAt(j - 1);
                if (ch1 == ch2 || ch1 == ch2 + 32 || ch1 + 32 == ch2) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                // 左边+1,上边+1, 左上角+temp取最小
                d[i][j] = Math.min(Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1), d[i - 1][j - 1] + temp);
            }
        }

        return (1 - (float) d[n][m] / Math.max(str.length(), target.length())) * 100F;
    }

    public static void checkSimilarity(String target) {
        min = 0.1f;
        full = false;

        float a;
        hashMap = new HashMap<>();

        for (String str : wishToCompare.keySet()) {
            a = getSimilarityRatio(str, target);
            if (a >= min) {
                if (full) {
                    hashMap.remove(min);
                    min = a;
                } else {
                    if (hashMap.size() == 7) {
                        full = true;
                    }
                }
                hashMap.put(a,wishToCompare.get(str));
            }
        }
    }



    private static final HashMap<String,Class<?>> wishToCompare = new HashMap<>();
    static {
//        wishToCompare.put("",);
        wishToCompare.put("破损的短剑", WornShortsword.class);
        wishToCompare.put("法师魔杖", MagesStaff.class);
        wishToCompare.put("匕首", Dagger.class);
        wishToCompare.put("镶钉手套", Gloves.class);
        wishToCompare.put("刺剑", Rapier.class);

        wishToCompare.put("短剑", Shortsword.class);
        wishToCompare.put("手斧", HandAxe.class);
        wishToCompare.put("长矛", Spear.class);
        wishToCompare.put("铁头棍", Quarterstaff.class);
        wishToCompare.put("长匕首", Dirk.class);
        wishToCompare.put("短柄镰", Sickle.class);

        wishToCompare.put("单手剑", Sword.class);
        wishToCompare.put("硬头锤", Mace.class);
        wishToCompare.put("弯刀", Scimitar.class);
        wishToCompare.put("圆盾", RoundShield.class);
        wishToCompare.put("双钗", Sai.class);
        wishToCompare.put("长鞭", Whip.class);

        wishToCompare.put("长剑", Longsword.class);
        wishToCompare.put("战斧", BattleAxe.class);
        wishToCompare.put("链枷", Flail.class);
        wishToCompare.put("符文之刃", RunicBlade.class);
        wishToCompare.put("暗杀之刃", AssassinsBlade.class);
        wishToCompare.put("十字弩", Crossbow.class);
        wishToCompare.put("武士刀", Katana.class);

        wishToCompare.put("巨剑", Greatsword.class);
        wishToCompare.put("战锤", WarHammer.class);
        wishToCompare.put("关刀", Glaive.class);
        wishToCompare.put("巨斧", Greataxe.class);
        wishToCompare.put("巨型方盾", Greatshield.class);
        wishToCompare.put("魔岩拳套", Gauntlet.class);
        wishToCompare.put("战镰", WarScythe.class);
//        wishToCompare.put("手斧", HandAxe.class);
//        wishToCompare.put("长矛", Spear.class);
//        wishToCompare.put("铁头棍", Quarterstaff.class);
//        wishToCompare.put("长匕首", Dict.class);
//        wishToCompare.put("短柄镰", Sickle.class);
    }

}

