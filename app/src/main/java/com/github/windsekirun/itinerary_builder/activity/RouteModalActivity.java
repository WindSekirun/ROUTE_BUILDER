package com.github.windsekirun.itinerary_builder.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.windsekirun.itinerary_builder.Constants;
import com.github.windsekirun.itinerary_builder.R;
import com.github.windsekirun.itinerary_builder.parser.Leg;
import com.github.windsekirun.itinerary_builder.parser.Step;
import com.github.windsekirun.itinerary_builder.utils.MathUtils;
import com.github.windsekirun.itinerary_builder.utils.PostServiceExtraUtils;

import java.util.List;

/**
 * RouteModalActivity
 * Created by Pyxis on 2016. 11. 7..
 */
public class RouteModalActivity extends AppCompatActivity implements Constants {
    Leg leg;
    RecyclerView list;

    RouteModalListAdapter adapter;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modal);

        leg = (Leg) PostServiceExtraUtils.getInstance().getItem();

        if (leg == null)
            finish();

        StringBuilder stringBuilder = new StringBuilder();

        int distance = leg.getDistanceValue();
        int duration = leg.getDurationValue();

        double distanceToKm = MathUtils.getKilo(distance);
        double distanceToMile = MathUtils.getMiles(distance);
        long durationToMin = MathUtils.getMin(duration);

        stringBuilder.append(Math.round(distanceToKm))
                .append("km (")
                .append(Math.round(distanceToMile))
                .append("mi) ");

        if (durationToMin >= 60) {
            long durationToHour = durationToMin / 60;
            durationToMin = durationToMin % 60;

            stringBuilder.append(durationToHour)
                    .append("hour ")
                    .append(durationToMin)
                    .append("min");
        } else {
            stringBuilder.append(durationToMin)
                    .append("min");
        }

        list = (RecyclerView) findViewById(R.id.recyclerView);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(stringBuilder.toString());

        list.setLayoutManager(new LinearLayoutManager(RouteModalActivity.this));
        list.setHasFixedSize(true);
        adapter = new RouteModalListAdapter(RouteModalActivity.this, leg.getSteps());
        list.setAdapter(adapter);
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

    public class RouteModalListAdapter extends RecyclerView.Adapter<RouteModalViewHolder> {
        Context c;
        List<Step> itemSet;

        public RouteModalListAdapter(Context c, List<Step> step) {
            this.c = c;
            this.itemSet = step;
        }

        @Override
        public RouteModalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RouteModalViewHolder(View.inflate(c, R.layout.row_modal, null));
        }

        @Override
        public void onBindViewHolder(RouteModalViewHolder holder, int position) {
            Step step = itemSet.get(position);
            holder.routeDesc.setText(step.getInstruction());
            StringBuilder stringBuilder = new StringBuilder();
            double distance = step.getDistance();
            double distanceToFeet = MathUtils.getFeet(distance);

            stringBuilder.append(Math.round(distance))
                    .append("m (")
                    .append(Math.round(distanceToFeet))
                    .append(" feet) ");

            holder.duration.setText(stringBuilder.toString());
        }

        @Override
        public int getItemCount() {
            if (itemSet != null && !itemSet.isEmpty()) {
                return itemSet.size();
            } else {
                return 0;
            }
        }
    }

    public class RouteModalViewHolder extends RecyclerView.ViewHolder {
        TextView routeDesc;
        TextView duration;

        public RouteModalViewHolder(View itemView) {
            super(itemView);
            routeDesc = (TextView) itemView.findViewById(R.id.routeDesc);
            duration = (TextView) itemView.findViewById(R.id.duration);
        }
    }
}
