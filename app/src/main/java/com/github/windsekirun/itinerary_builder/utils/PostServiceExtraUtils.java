package com.github.windsekirun.itinerary_builder.utils;

import java.io.Serializable;

/**
 * Created by Pyxis on 2016. 11. 10..
 */
public class PostServiceExtraUtils <T> implements Serializable {
    private static PostServiceExtraUtils instance;
    private T item;
    public static PostServiceExtraUtils getInstance() {
        if (instance == null)
            instance = new PostServiceExtraUtils();
        return instance;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public T getItem() {
        return item;
    }
}