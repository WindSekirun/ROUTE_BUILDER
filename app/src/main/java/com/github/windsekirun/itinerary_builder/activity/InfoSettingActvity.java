package com.github.windsekirun.itinerary_builder.activity;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.windsekirun.itinerary_builder.R;
import com.github.windsekirun.itinerary_builder.fragment.InfoSettingFragment;

/**
 * InfoSettingActvity
 * Created by Pyxis on 2016. 11. 14..
 */
public class InfoSettingActvity extends AppCompatActivity {

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_base);
        PreferenceFragment toInject = new InfoSettingFragment();
        getFragmentManager().beginTransaction().add(R.id.container, toInject, "TIMELINESETTING").commit();
        getFragmentManager().executePendingTransactions();

        getSupportActionBar().setTitle("Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
