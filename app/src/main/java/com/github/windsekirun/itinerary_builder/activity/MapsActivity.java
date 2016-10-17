package com.github.windsekirun.itinerary_builder.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.github.windsekirun.itinerary_builder.Constants;
import com.github.windsekirun.itinerary_builder.R;
import com.github.windsekirun.itinerary_builder.model.LocationModel;
import com.github.windsekirun.itinerary_builder.model.RouteModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * MapsActivity
 * Created by Pyxis on 2016. 10. 10..
 */
@SuppressWarnings("ConstantConditions")
public class MapsActivity extends AppCompatActivity
        implements Constants, RoutingListener, OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    protected GoogleMap map;
    protected LatLng start;
    protected LatLng end;
    protected List<LatLng> wayPoints;

    protected GoogleApiClient mGoogleApiClient;
    protected List<Polyline> polylines;
    protected static final int[] COLORS = new int[] {
            R.color.main_route_1,
            R.color.main_route_2,
            R.color.main_route_3,
            R.color.main_route_4,
            R.color.main_route_5};

    RouteModel routeModel;
    MaterialDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        wayPoints = new ArrayList<>();

        // Getting saved data from RouteModel
        routeModel = (RouteModel) getIntent().getSerializableExtra(ROUTE_MODEL);
        start = new LatLng(routeModel.getStartLocation().getLatitude(), routeModel.getStartLocation().getLongitude());
        end = new LatLng(routeModel.getEndLocation().getLatitude(), routeModel.getEndLocation().getLongitude());

        getSupportActionBar().setTitle(routeModel.getTitle());

        for (LocationModel points : routeModel.getLocationRoutes()) {
            wayPoints.add(new LatLng(points.getLatitude(), points.getLongitude()));
        }

        wayPoints.add(0, start);
        wayPoints.add(wayPoints.size() - 1, end);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        polylines = new ArrayList<>();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        MapsInitializer.initialize(this);

        mGoogleApiClient.connect();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        progressDialog = new MaterialDialog.Builder(MapsActivity.this)
                .content(R.string.fetching_from_data)
                .progress(true, 0)
                .cancelable(false)
                .canceledOnTouchOutside(true)
                .build();

        progressDialog.show();
    }

    public void routingProcess() {
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(start.latitude, end.longitude));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(8);

        map.moveCamera(center);
        map.animateCamera(zoom);

        boolean optimize = wayPoints.size() > 2;

        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .optimize(optimize)
                .waypoints(wayPoints)
                .key(API_KEY)
                .build();

        routing.execute();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRoutingFailure(RouteException e) {
        progressDialog.dismiss();
        if (e != null) {
            Log.e("GoogleAPIClient", e.getMessage() + "");
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(List<Route> route, int shortestRouteIndex) {
        progressDialog.dismiss();
        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(20);

        map.moveCamera(center);

        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        for (int i = 0; i < route.size(); i++) {
            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = map.addPolyline(polyOptions);
            polylines.add(polyline);
        }

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(start);
        map.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(end);
        map.addMarker(options);
    }

    @Override
    public void onRoutingCancelled() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;

        routingProcess();
    }
}
