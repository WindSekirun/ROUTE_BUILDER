package com.github.windsekirun.itinerary_builder.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.github.windsekirun.itinerary_builder.R;
import com.github.windsekirun.itinerary_builder.model.LocationModel;
import com.github.windsekirun.itinerary_builder.model.MoveMethod;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.mikepenz.materialize.color.Material;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * MakeRouteActivity
 * Created by Pyxis on 2016. 10. 6..
 */
public class MakeRouteActivity extends AppCompatActivity {
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

    public final int START_PICK_CODE = 4;
    public final int END_PICK_CODE = 5;
    public final int VIA_PICK_CODE = 6;

    LocationModel startLocation;
    LocationModel endLocation;
    ArrayList<LocationModel> viaLocationList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        ButterKnife.bind(this);
        inflateToolbar();

        carButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchRadio(MoveMethod.CAR);
            }
        });

        publicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchRadio(MoveMethod.PUBLIC);
            }
        });

        walkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchRadio(MoveMethod.WALK);
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                    Intent intent = intentBuilder.build(MakeRouteActivity.this);
                    startActivityForResult(intent, START_PICK_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), MakeRouteActivity.this, 0);
                } catch (GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(MakeRouteActivity.this, "Google Play Services is not available.", Toast.LENGTH_LONG).show();
                }
            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                    Intent intent = intentBuilder.build(MakeRouteActivity.this);
                    startActivityForResult(intent, END_PICK_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), MakeRouteActivity.this, 0);
                } catch (GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(MakeRouteActivity.this, "Google Play Services is not available.", Toast.LENGTH_LONG).show();
                }
            }
        });

        updateLocation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (resultCode == RESULT_OK) {
                if (requestCode == START_PICK_CODE) {
                    final Place place = PlacePicker.getPlace(data, MakeRouteActivity.this);
                    startLocation = PlacetoLocationModel(place);
                } else if (requestCode == END_PICK_CODE) {
                    final Place place = PlacePicker.getPlace(data, MakeRouteActivity.this);
                    endLocation = PlacetoLocationModel(place);
                }

                updateLocation();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
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
        model.setWebSiteUri(place.getWebsiteUri());
        model.setRating(place.getRating());
        model.setPlaceTypes(place.getPlaceTypes());

        return model;
    }

    public void switchRadio(MoveMethod method) {
        carButton.setChecked(false);
        publicButton.setChecked(false);
        walkButton.setChecked(false);

        if (method == MoveMethod.WALK) {
            walkButton.setChecked(true);
        } else if (method == MoveMethod.PUBLIC) {
            publicButton.setChecked(true);
        } else {
            carButton.setChecked(true);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void inflateToolbar() {
        getSupportActionBar().setTitle("경로 수정");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_make_route, menu);
        return true;
    }

    public class ViaListAdapter extends ArrayAdapter<LocationModel> {

        public ViaListAdapter(Context context, ArrayList<LocationModel> itemSet) {
            super(context, 0, itemSet);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_add_via:
                // TODO: 경유지 추가
                break;
            case R.id.menu_added:
                // TODO: 경로 추가 완료!
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
