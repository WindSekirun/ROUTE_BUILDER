package com.github.windsekirun.itinerary_builder;

/**
 * Created by Pyxis on 2016. 10. 6..
 */
public interface Constants {
    String packageName = "com.github.windsekirun.itinerary_builder";
    String withIntent = ".intent.";
    String API_KEY = "AIzaSyBZJc9mdB0raxLB416qBFrywCP55Cv8yVE";
    String BROWSER_API_KEY = "AIzaSyAPmt1L2rv_gHPlxG1VusRRkw8l9gpdXz4";

    String LOCATION_MODEL = packageName + withIntent +  "LOCATION_MODEL";
    String ROUTE_MODEL = packageName + withIntent + "ROUTE_MODEL";
    String CURSOR = packageName + withIntent + "CURSOR";
    int GENERAL_CODE = 72;
    String LEG_OBJECT = packageName + withIntent + "LEG_OBJECT";
}
