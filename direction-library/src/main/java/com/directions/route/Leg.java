package com.directions.route;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Pyxis on 2016. 10. 20..
 */
public class Leg {
    private String durationText;
    private int durationValue;
    private String distanceText;
    private int distanceValue;
    private String endAddressText;
    private String startAddressText;
    private LatLng startPosition;
    private LatLng endPosition;
    private List<Step> steps;

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public int getDistanceValue() {
        return distanceValue;
    }

    public int getDurationValue() {
        return durationValue;
    }

    public LatLng getEndPosition() {
        return endPosition;
    }

    public LatLng getStartPosition() {
        return startPosition;
    }

    public String getDistanceText() {
        return distanceText;
    }

    public String getDurationText() {
        return durationText;
    }

    public String getEndAddressText() {
        return endAddressText;
    }

    public String getStartAddressText() {
        return startAddressText;
    }

    public void setDistanceText(String distanceText) {
        this.distanceText = distanceText;
    }

    public void setDistanceValue(int distanceValue) {
        this.distanceValue = distanceValue;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public void setDurationValue(int durationValue) {
        this.durationValue = durationValue;
    }

    public void setEndAddressText(String endAddressText) {
        this.endAddressText = endAddressText;
    }

    public void setEndPosition(LatLng endPosition) {
        this.endPosition = endPosition;
    }

    public void setStartAddressText(String startAddressText) {
        this.startAddressText = startAddressText;
    }

    public void setStartPosition(LatLng startPosition) {
        this.startPosition = startPosition;
    }
}
