package com.github.windsekirun.itinerary_builder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.github.windsekirun.itinerary_builder.R;
import com.melnykov.fab.FloatingActionButton;
import com.mikepenz.materialize.color.Material;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * MakeRouteActivity
 * Created by Pyxis on 2016. 10. 6..
 */
public class MakeRouteActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.viaList)
    ListView viaList;
    @Bind(R.id.title)
    EditText title;
    @Bind(R.id.start)
    EditText start;
    @Bind(R.id.end)
    EditText end;
    @Bind(R.id.carButton)
    RadioButton carButton;
    @Bind(R.id.publicButton)
    RadioButton publicButton;
    @Bind(R.id.walkButton)
    RadioButton walkButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        ButterKnife.bind(this);
        inflateToolbar();
    }

    public void inflateToolbar() {
        toolbar.setTitle(R.string.app_name_readable);
        toolbar.setBackgroundResource(R.color.colorPrimary);
        toolbar.setTitleTextColor(Material.White._1000.getAsColor());
    }

}
