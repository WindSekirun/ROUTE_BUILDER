package com.github.windsekirun.itinerary_builder.utils;

/**
 * Created by Pyxis on 2016. 10. 20..
 */
public class MathUtils {

    public static double getKilo(long meter) {
        return meter * 0.001;
    }

    public static double getFeet(long meter) {
        return meter * 3.281;
    }

    public static double getMiles(long meter) {
        return getFeet(meter) * 0.00018939;
    }

    public static long getMin(long second) {
        return second / 60;
    }
}
