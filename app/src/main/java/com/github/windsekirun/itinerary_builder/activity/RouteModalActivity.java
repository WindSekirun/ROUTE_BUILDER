package com.github.windsekirun.itinerary_builder.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.github.windsekirun.itinerary_builder.Constants;
import com.github.windsekirun.itinerary_builder.parser.Leg;

/**
 * RouteModalActivity
 * Created by Pyxis on 2016. 11. 7..
 */
public class RouteModalActivity extends AppCompatActivity implements Constants {
    Leg leg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        leg = (Leg) getIntent().getSerializableExtra(LEG_OBJECT);

        if (leg == null)
            finish();


    }
}
