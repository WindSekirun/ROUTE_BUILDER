package com.github.windsekirun.itinerary_builder.model;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

/**
 * LocationModel
 * Created by Pyxis on 2016. 10. 6..
 *
 * Wrap at https://developers.google.com/android/reference/com/google/android/gms/location/places/Place.html?hl=ko#getLocale()
 */
public class LocationModel implements Serializable {
    @NonNull private String placeId = "";
    private String name = "";
    private String address = "";
    private double latitude = 0L;
    private double longitude = 0L;
    private String phoneNumber = "";
    private int priceLevel = 0;
    private Uri webSiteUri = null;
    private Locale locale;
    private float rating;
    private List<Integer> placeTypes;

    public float getRating() {
        return rating;
    }

    public int getPriceLevel() {
        return priceLevel;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public List<Integer> getPlaceTypes() {
        return placeTypes;
    }

    public void setPlaceTypes(List<Integer> placeTypes) {
        this.placeTypes = placeTypes;
    }

    public Locale getLocale() {
        return locale;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @NonNull
    public String getPlaceId() {
        return placeId;
    }

    public Uri getWebSiteUri() {
        return webSiteUri;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPlaceId(@NonNull String placeId) {
        this.placeId = placeId;
    }

    public void setPriceLevel(int priceLevel) {
        this.priceLevel = priceLevel;
    }

    public void setWebSiteUri(Uri webSiteUri) {
        this.webSiteUri = webSiteUri;
    }
}
