package com.shatteredpixel.shatteredpixeldungeon.custom.ch.boss;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import java.util.Locale;

//read data in single .properties file.
public class DR {

    private static I18NBundle bundle;

    //setup before read
    public static void readFile(String filename){
        I18NBundle.setExceptionOnMissingKey(false);
        bundle = I18NBundle.createBundle(Gdx.files.internal(filename));
    }
    //clear after read
    public static void clear(){
        bundle = null;
    }

    public static int getInt(Class c, String k){
        return Integer.parseInt(getData(c, k));
    }

    public static float getFloat(Class c, String k){
        return Float.parseFloat(getData(c, k));
    }

    public static boolean getBool(Class c, String k){
        return Boolean.parseBoolean(getData(c, k));
    }

    public static int[] getIntArray(Class c, String k){
        String source = getData(c, k);
        String[] result = source.split(",");
        int[] integers = new int[result.length];
        for(int i=0, L=result.length;i<L;++i){
            integers[i]=Integer.parseInt(result[i]);
        }
        return integers;
    }

    public static float[] getFloatArray(Class c, String k){
        String source = getData(c, k);
        String[] result = source.split(",");
        float[] floats = new float[result.length];
        for(int i=0, L=result.length;i<L;++i){
            floats[i]=Float.parseFloat(result[i]);
        }
        return floats;
    }

    public static boolean[] getBoolArray(Class c, String k){
        String source = getData(c, k);
        String[] result = source.split(",");
        boolean[] bools = new boolean[result.length];
        for(int i=0, L=result.length;i<L;++i){
            bools[i]= Boolean.parseBoolean(result[i]);
        }
        return bools;
    }

    public static String getData(Class c, String k){
        String key = c.getSimpleName() + "." + k;
        if(key.equals(".")){
            return "NULL_KEY";
        }
        String value = getFromBundle(key.toLowerCase(Locale.ENGLISH));
        if (value != null){
            return value;
        } else {
            GLog.w("can't load %s", key);
            return "NOT_FOUND";
        }

    }

    private static String getFromBundle(String key){
        String result;
        result = bundle.get(key);
            //if it isn't the return string for no key found, return it
        if (result.length() != key.length()+6 || !result.contains(key)){
            return result;
        }

        return null;
    }
}
