package com.github.windsekirun.itinerary_builder.model;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.Serializable;
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
    private long latitude = 0L;
    private long longitude = 0L;
    private String phoneNumber = "";
    private int priceLevel = 0;
    private Uri webSiteUri = null;
    private Locale locale;

    public int getGetPriceLevel() {
        return priceLevel;
    }

    public Locale getLocale() {
        return locale;
    }

    public long getLatitude() {
        return latitude;
    }

    public long getLongitude() {
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

    public LocationModel setAddress(String address) {
        this.address = address;
        return this;
    }

    public LocationModel setLatitude(long latitude) {
        this.latitude = latitude;
        return this;
    }

    public LocationModel setLocale(Locale locale) {
        this.locale = locale;
        return this;
    }

    public LocationModel setLongitude(long longitude) {
        this.longitude = longitude;
        return this;
    }

    public LocationModel setName(String name) {
        this.name = name;
        return this;
    }

    public LocationModel setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public LocationModel setPlaceId(@NonNull String placeId) {
        this.placeId = placeId;
        return this;
    }

    public LocationModel setPriceLevel(int priceLevel) {
        this.priceLevel = priceLevel;
        return this;
    }

    public LocationModel setWebSiteUri(Uri webSiteUri) {
        this.webSiteUri = webSiteUri;
        return this;
    }
}
