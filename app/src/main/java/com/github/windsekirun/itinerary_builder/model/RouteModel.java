package com.github.windsekirun.itinerary_builder.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * RouteModel
 * Created by Pyxis on 2016. 10. 6..
 */
public class RouteModel implements Serializable {
    private long UID = 0;
    private String title = "";
    private String description = "";
    private LocationModel startLocation = null;
    private LocationModel endLocation = null;
    private ArrayList<LocationModel> locationRoutes;

    public ArrayList<LocationModel> getLocationRoutes() {
        return locationRoutes;
    }

    public LocationModel getEndLocation() {
        return endLocation;
    }

    public LocationModel getStartLocation() {
        return startLocation;
    }

    public long getUID() {
        return UID;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public RouteModel setDescription(String description) {
        this.description = description;
        return this;
    }

    public RouteModel setEndLocation(LocationModel endLocation) {
        this.endLocation = endLocation;
        return this;
    }

    public RouteModel setLocationRoutes(ArrayList<LocationModel> locationRoutes) {
        this.locationRoutes = locationRoutes;
        return this;
    }

    public RouteModel setStartLocation(LocationModel startLocation) {
        this.startLocation = startLocation;
        return this;
    }

    public RouteModel setTitle(String title) {
        this.title = title;
        return this;
    }

    public RouteModel setUID(long UID) {
        this.UID = UID;
        return this;
    }
}
