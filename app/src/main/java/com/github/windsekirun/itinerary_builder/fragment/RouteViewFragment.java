package com.github.windsekirun.itinerary_builder.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;

import com.github.windsekirun.itinerary_builder.Constants;
import com.github.windsekirun.itinerary_builder.R;
import com.github.windsekirun.itinerary_builder.parser.Leg;
import com.github.windsekirun.itinerary_builder.parser.Route;
import com.github.windsekirun.itinerary_builder.parser.Step;
import com.github.windsekirun.itinerary_builder.utils.MathUtils;
import com.github.windsekirun.itinerary_builder.utils.PostServiceExtraUtils;

import java.util.List;

/**
 * Created by Pyxis on 2016. 11. 17..
 */
public class RouteViewFragment extends Fragment implements Constants {
    Route route;
    RecyclerView list;

    RouteModalListAdapter adapter;

    @SuppressWarnings("StringBufferReplaceableByString")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.fragment_modal, null);

        Route route = (Route) PostServiceExtraUtils.getInstance().getItem();
        int page = getArguments().getInt(PAGING, 0);
        Leg leg = route.getLegs().get(page);

        list = (RecyclerView) v.findViewById(R.id.recyclerView);

        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setHasFixedSize(true);
        adapter = new RouteModalListAdapter(getActivity(), leg.getSteps());
        list.setAdapter(adapter);

        TextView textView = (TextView) v.findViewById(R.id.legName);
        StringBuilder builder = new StringBuilder();

        builder.append(leg.getStartAddressText())
                .append(" -> ")
                .append(leg.getEndAddressText());

        textView.setText(builder.toString());
        return v;
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
