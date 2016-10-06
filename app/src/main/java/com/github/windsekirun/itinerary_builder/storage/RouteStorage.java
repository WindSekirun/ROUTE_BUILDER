package com.github.windsekirun.itinerary_builder.storage;

import com.github.windsekirun.itinerary_builder.model.RouteDB;
import com.github.windsekirun.itinerary_builder.model.RouteModel;

import java.util.ArrayList;

/**
 * Created by Pyxis on 2016. 10. 6..
 */
public interface RouteStorage {
    RouteDB getDB();
    String getName();
    ArrayList<RouteModel> getRouteModels();
    void writeOutChange();
}
