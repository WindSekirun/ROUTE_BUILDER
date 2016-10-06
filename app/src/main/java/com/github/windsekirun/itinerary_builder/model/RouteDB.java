package com.github.windsekirun.itinerary_builder.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Pyxis on 2016. 10. 6..
 */
public class RouteDB implements Serializable {
    private String nickname = "";
    private long UID = 0L;
    private ArrayList<RouteModel> routeModels = new ArrayList<>();

    public long getUID() {
        return UID;
    }

    public ArrayList<RouteModel> getRouteModels() {
        return routeModels;
    }

    public String getNickname() {
        return nickname;
    }

    public void setUID(long UID) {
        this.UID = UID;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setRouteModels(ArrayList<RouteModel> routeModels) {
        this.routeModels = routeModels;
    }
}
