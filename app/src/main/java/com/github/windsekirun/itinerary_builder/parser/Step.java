package com.github.windsekirun.itinerary_builder.parser;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pyxis on 2016. 10. 20..
 */
public class Step implements Serializable{
    private double distance;
    private String endAddressText;
    private LatLng endPosition;
    private String startAddressText;
    private LatLng startPosition;
    private String instruction;
    private String maneuver;
    private List<LatLng> points;
    private String travelMode;

    public Step() {
        points = new ArrayList<LatLng>();
    }

    public String getStartAddressText() {
        return startAddressText;
    }

    public String getEndAddressText() {
        return endAddressText;
    }

    public LatLng getStartPosition() {
        return startPosition;
    }

    public double getDistance() {
        return distance;
    }

    public LatLng getEndPosition() {
        return endPosition;
    }

    public List<LatLng> getPoints() {
        return points;
    }

    public String getInstruction() {
        return instruction;
    }

    public String getManeuver() {
        return maneuver;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setStartPosition(LatLng startPosition) {
        this.startPosition = startPosition;
    }

    public void setStartAddressText(String startAddressText) {
        this.startAddressText = startAddressText;
    }

    public void setEndPosition(LatLng endPosition) {
        this.endPosition = endPosition;
    }

    public void setEndAddressText(String endAddressText) {
        this.endAddressText = endAddressText;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public void setManeuver(String maneuver) {
        this.maneuver = maneuver;
    }

    public void setPoints(List<LatLng> points) {
        this.points = points;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }
}
