package com.github.windsekirun.itinerary_builder.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.windsekirun.itinerary_builder.Constants;
import com.github.windsekirun.itinerary_builder.R;
import com.github.windsekirun.itinerary_builder.model.LocationModel;
import com.github.windsekirun.itinerary_builder.model.RouteModel;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * MakeRouteActivity
 * Created by Pyxis on 2016. 10. 6..
 */
public class MakeRouteActivity extends AppCompatActivity implements Constants {
    public final int START_PICK_CODE = 4;
    public final int END_PICK_CODE = 5;
    public final int VIA_PICK_CODE = 6;
    public int cursor = 0;
    @Bind(R.id.viaList)
    ListView viaList;
    @Bind(R.id.title)
    EditText title;
    @Bind(R.id.description)
    EditText description;
    @Bind(R.id.start)
    EditText start;
    @Bind(R.id.end)
    EditText end;
    ViaListAdapter adapter;
    LocationModel startLocation;
    LocationModel endLocation;
    ArrayList<LocationModel> viaLocationList = new ArrayList<>();
    RouteModel routeModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        ButterKnife.bind(this);
        inflateToolbar();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPicker(START_PICK_CODE);
            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPicker(END_PICK_CODE);
            }
        });

        viaList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                viaLocationList.remove(viaLocationList.get(position));

                updateLocation();
                return false;
            }
        });

        routeModel = (RouteModel) getIntent().getSerializableExtra(ROUTE_MODEL);

        if (routeModel != null) {
            startLocation = routeModel.getStartLocation();
            endLocation = routeModel.getEndLocation();
            viaLocationList = routeModel.getLocationRoutes();

            title.setText(routeModel.getTitle());
            title.setSelection(title.length());

            description.setText(routeModel.getDescription());
            description.setSelection(description.length());

            cursor = getIntent().getIntExtra(CURSOR, 0);
        }

        updateLocation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (resultCode == RESULT_OK) {
                if (requestCode == START_PICK_CODE) {
                    final Place place = PlacePicker.getPlace(MakeRouteActivity.this, data);
                    startLocation = PlacetoLocationModel(place);
                } else if (requestCode == END_PICK_CODE) {
                    final Place place = PlacePicker.getPlace(MakeRouteActivity.this, data);
                    endLocation = PlacetoLocationModel(place);
                } else if (requestCode == VIA_PICK_CODE) {
                    final Place place = PlacePicker.getPlace(MakeRouteActivity.this, data);
                    viaLocationList.add(PlacetoLocationModel(place));
                }

            }
            updateLocation();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void finishWork() {
        Toast.makeText(MakeRouteActivity.this, R.string.saving_route, Toast.LENGTH_SHORT).show();

        boolean isChangeMethods = false;

        if (this.routeModel != null) {
            isChangeMethods = true;
        }

        RouteModel routeModel = new RouteModel();
        if (this.routeModel != null)
            routeModel = this.routeModel;

        routeModel.setTitle(title.getText().toString());
        routeModel.setCreatedAt(Calendar.getInstance().getTime());
        routeModel.setDescription(description.getText().toString());
        routeModel.setEndLocation(endLocation);
        routeModel.setStartLocation(startLocation);
        routeModel.setLocationRoutes(viaLocationList);

        Intent intent = new Intent();
        intent.putExtra(ROUTE_MODEL, routeModel);
        Integer intentCursor = cursor;

        if (isChangeMethods) {
            intent.putExtra(CURSOR, intentCursor);
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    @SuppressLint("SetTextI18n")
    public void updateLocation() {
        start.setFocusable(false);
        start.setMaxLines(2);

        if (startLocation != null) {
            start.setText(startLocation.getName() + " (" + startLocation.getAddress() + ") ");
        }

        end.setFocusable(false);
        end.setMaxLines(2);

        if (endLocation != null) {
            end.setText(endLocation.getName() + " (" + endLocation.getAddress() + ") ");
        }

        if (!viaLocationList.isEmpty()) {
            adapter = new ViaListAdapter(MakeRouteActivity.this, viaLocationList);
            viaList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    public LocationModel PlacetoLocationModel(Place place) {
        LocationModel model = new LocationModel();

        model.setName((String) place.getName());
        model.setPlaceId(place.getId());
        model.setAddress((String) place.getAddress());
        model.setLatitude(place.getLatLng().latitude);
        model.setLongitude(place.getLatLng().longitude);
        model.setLocale(place.getLocale());
        model.setPriceLevel(place.getPriceLevel());
        model.setPhoneNumber((String) place.getPhoneNumber());
        if (place.getWebsiteUri() != null)
            model.setWebSiteUri(place.getWebsiteUri().toString());
        model.setRating(place.getRating());
        model.setPlaceTypes(place.getPlaceTypes());

        return model;
    }

    @SuppressWarnings("deprecation")
    public void startPicker(int requestCode) {
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(MakeRouteActivity.this);
            startActivityForResult(intent, requestCode);
        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), MakeRouteActivity.this, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(MakeRouteActivity.this, R.string.gps_not_available, Toast.LENGTH_LONG).show();
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void inflateToolbar() {
        getSupportActionBar().setTitle(R.string.edit_route);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_make_route, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_add_via:
                if (viaLocationList.size() <= 22) {
                    startPicker(VIA_PICK_CODE);
                } else {
                    Toast.makeText(MakeRouteActivity.this, R.string.exceed_limit_waypoint, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.menu_added:
                if (!title.getText().toString().isEmpty()) {
                    finishWork();
                } else {
                    Toast.makeText(MakeRouteActivity.this, R.string.enter_the_title, Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class ViaListAdapter extends ArrayAdapter<LocationModel> {
        Context context;
        ArrayList<LocationModel> itemSet;
        LayoutInflater inflater;
        ViewHolder holder;

        public ViaListAdapter(Context context, ArrayList<LocationModel> itemSet) {
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

            LocationModel locationModel = itemSet.get(position);

            if (!locationModel.getName().isEmpty()) {
                holder.name.setText(locationModel.getName());
                holder.address.setText(locationModel.getAddress());
            } else {
                holder.name.setText(locationModel.getAddress());
            }
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
}
