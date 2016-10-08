package com.github.windsekirun.itinerary_builder.activity;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.github.windsekirun.itinerary_builder.R;
import com.melnykov.fab.FloatingActionButton;
import com.mikepenz.materialize.color.Material;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.list)
    ListView list;
    @Bind(R.id.emptyView)
    TextView emptyView;
    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("경로 빌더");
        inflateFab();

        list.setEmptyView(emptyView);
    }

    public void inflateFab() {
        fab.attachToListView(list);
        fab.setColorNormal(ContextCompat.getColor(this, R.color.colorPrimary));
        fab.setColorPressed(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MakeRouteActivity.class);
                startActivityForResult(intent, 72);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 72) {

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
