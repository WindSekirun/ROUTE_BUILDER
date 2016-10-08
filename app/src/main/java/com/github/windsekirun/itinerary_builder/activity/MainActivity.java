package com.github.windsekirun.itinerary_builder.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.windsekirun.itinerary_builder.Constants;
import com.github.windsekirun.itinerary_builder.R;
import com.github.windsekirun.itinerary_builder.model.RouteModel;
import com.github.windsekirun.itinerary_builder.storage.RouteStorage;
import com.github.windsekirun.itinerary_builder.storage.RouteStorageFactory;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements Constants {
    @Bind(R.id.list)
    ListView list;
    @Bind(R.id.emptyView)
    TextView emptyView;
    @Bind(R.id.fab)
    FloatingActionButton fab;

    RouteStorage routeStorage;
    RouteListAdapter adapter;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("경로 빌더");
        inflateFab();

        list.setEmptyView(emptyView);

        routeStorage = RouteStorageFactory.getInstance(MainActivity.this);

        updateList();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RouteModel routeModel = routeStorage.getRouteModels().get(position);
                Intent intent = new Intent(MainActivity.this, MakeRouteActivity.class);
                intent.putExtra(ROUTE_MODEL, routeModel);
                startActivityForResult(intent, GENERAL_CODE);
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                routeStorage.getRouteModels().remove(routeStorage.getRouteModels().get(position));
                routeStorage.writeOutChange();

                updateList();
                return false;
            }
        });
    }

    public void inflateFab() {
        fab.attachToListView(list);
        fab.setColorNormal(ContextCompat.getColor(this, R.color.colorPrimary));
        fab.setColorPressed(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MakeRouteActivity.class);
                startActivityForResult(intent, GENERAL_CODE);
            }
        });
    }

    public void updateList() {
        adapter = new RouteListAdapter(MainActivity.this, routeStorage.getRouteModels());
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public class RouteListAdapter extends ArrayAdapter<RouteModel> {
        Context context;
        ArrayList<RouteModel> itemSet;
        LayoutInflater inflater;
        ViewHolder holder;

        public RouteListAdapter(Context context, ArrayList<RouteModel> itemSet) {
            super(context, 0, itemSet);
            this.context = context;
            this.itemSet = itemSet;
            inflater = LayoutInflater.from(context);
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.row_vialist, null, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            RouteModel routeModel = itemSet.get(position);

            holder.name.setText(routeModel.getTitle());
            holder.address.setText((!routeModel.getDescription().isEmpty()) ? routeModel.getDescription() : "");
            return convertView;
        }

        public class ViewHolder {
            public TextView name;
            public TextView address;

            public ViewHolder(View itemView) {
                name = (TextView) itemView.findViewById(R.id.textView);
                address = (TextView) itemView.findViewById(R.id.textView2);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (resultCode == RESULT_OK) {
                if (requestCode == GENERAL_CODE) {
                    RouteModel newRouteModel = (RouteModel) data.getSerializableExtra(ROUTE_MODEL);
                    routeStorage.getRouteModels().add(newRouteModel);
                    routeStorage.writeOutChange();

                    updateList();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
