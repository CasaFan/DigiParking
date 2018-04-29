package com.digiparking.android.digiparking.util;

/**
 * Created by milk1 on 4/7/2017.
 */

public abstract class TimerUtil {

    public static final int SECOND = 0;
    public static final int MINUTE = 1;
    public static final int HOUR = 2;

    public static Integer[] getMinSec(long timeInMs){
        int secs = (int) (timeInMs / 1000);
        int mins = secs / 60;
        int hueures = mins /60;
        secs = secs % 60;
        mins = mins %60;
        return new Integer[]{secs, mins, hueures};
    }

    public static long toMs(int heure, int minute, int seconde){
        return (heure * 60 * 60 + minute * 60 + seconde) * 1000;
    }
}
