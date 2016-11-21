package com.github.windsekirun.itinerary_builder.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.windsekirun.itinerary_builder.Constants;
import com.github.windsekirun.itinerary_builder.R;
import com.github.windsekirun.itinerary_builder.model.LocationModel;
import com.github.windsekirun.itinerary_builder.model.RouteModel;
import com.github.windsekirun.itinerary_builder.parser.AbstractRouting;
import com.github.windsekirun.itinerary_builder.parser.Leg;
import com.github.windsekirun.itinerary_builder.parser.Route;
import com.github.windsekirun.itinerary_builder.parser.RouteException;
import com.github.windsekirun.itinerary_builder.parser.Routing;
import com.github.windsekirun.itinerary_builder.parser.RoutingListener;
import com.github.windsekirun.itinerary_builder.utils.MathUtils;
import com.github.windsekirun.itinerary_builder.utils.PostServiceExtraUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * MapsActivity
 * Created by Pyxis on 2016. 10. 10..
 */
@SuppressWarnings("ConstantConditions")
public class MapsActivity extends AppCompatActivity
        implements Constants, RoutingListener, OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    protected static final int[] COLORS = new int[]{
            R.color.md_blue_500, R.color.md_cyan_500, R.color.md_teal_500, R.color.md_green_500,
            R.color.md_lime_500, R.color.md_brown_500, R.color.md_deep_orange_500, R.color.md_amber_500,
            R.color.md_grey_500, R.color.md_yellow_500, R.color.md_blue_grey_500, R.color.md_pink_500,
            R.color.md_deep_purple_600, R.color.md_red_500, R.color.md_indigo_500, R.color.md_light_blue_500,
            R.color.md_purple_500, R.color.md_light_green_500, R.color.md_orange_500, R.color.md_teal_700,
            R.color.md_blue_500, R.color.md_green_700, R.color.md_light_blue_700, R.color.md_indigo_700,
            R.color.md_pink_200,
    };
    protected GoogleMap map;
    protected LatLng start;
    protected LatLng end;
    protected List<LatLng> wayPoints;
    protected GoogleApiClient mGoogleApiClient;
    protected List<Polyline> polylines;
    RouteModel routeModel;
    MaterialDialog progressDialog;

    RelativeLayout basicInfoView;
    TextView routeTitle;
    TextView timeView;
    ImageView routeView;

    CoordinatorLayout coordinatorLayout;
    View bottomSheet;
    BottomSheetBehavior behavior;
    ArrayList<Pair<String, Leg>> itemSet;
    RecyclerView legsList;
    private BottomSheetDialog mBottomSheetDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        wayPoints = new ArrayList<>();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cl);
        bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);

        // Getting saved data from RouteModel
        routeModel = (RouteModel) getIntent().getSerializableExtra(ROUTE_MODEL);
        start = new LatLng(routeModel.getStartLocation().getLatitude(), routeModel.getStartLocation().getLongitude());
        end = new LatLng(routeModel.getEndLocation().getLatitude(), routeModel.getEndLocation().getLongitude());

        for (LocationModel points : routeModel.getLocationRoutes()) {
            wayPoints.add(new LatLng(points.getLatitude(), points.getLongitude()));
        }

        wayPoints.add(wayPoints.size(), end);
        wayPoints.add(0, start);

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

        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // unused methods
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // unused methods
            }
        });
    }

    public BitmapDescriptor getMarkerIcon(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
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
                .alternativeRoutes(false)
                .optimize(optimize)
                .language("en")
                .waypoints(wayPoints)
                .key(API_KEY)
                .build();

        routing.execute();
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    public void displayBasicInfo(final Route route, long distance, long duration) {
        basicInfoView = (RelativeLayout) findViewById(R.id.basicInfoView);
        routeTitle = (TextView) findViewById(R.id.routeTitle);
        timeView = (TextView) findViewById(R.id.timeView);
        routeView = (ImageView) findViewById(R.id.imageView);

        routeTitle.setText(routeModel.getTitle());
        StringBuilder stringBuilder = new StringBuilder();

        // 미국 등지에서는 피트, 마일 단위, 기본적으로는 미터, 킬로미터를 사용합니다.
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

        timeView.setText(stringBuilder.toString());

        routeView.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, RouteViewPager.class);
                PostServiceExtraUtils.getInstance().setItem(route);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // unused methods
    }

    @Override
    public void onConnectionSuspended(int i) {
        // unused methods - notification needed!
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // unused methods - notification needed!
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
        // unused methods - notification needed!
    }

    @Override
    public void onRoutingSuccess(List<Route> routes, int shortestRouteIndex) {
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(start);
        options.title(routeModel.getStartLocation().getAddress());
        map.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(end);
        options.title(routeModel.getEndLocation().getAddress());
        map.addMarker(options);

        // We only support first Route!
        final Route route = routes.get(0);
        int legSize = route.getLegs().size();

        progressDialog.dismiss();
        CameraUpdate center = CameraUpdateFactory.newLatLng(route.getLatLgnBounds().getCenter());
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);

        map.moveCamera(center);
        map.moveCamera(zoom);

        for (int i = 0; i < legSize; i++) {
            int colorIndex = i % COLORS.length;
            final Leg leg = route.getLegs().get(i);

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(ContextCompat.getColor(MapsActivity.this, COLORS[colorIndex]));
            polyOptions.width(16);
            polyOptions.addAll(leg.getLegPointToDisplay());
            Polyline polyline = map.addPolyline(polyOptions);
            polylines.add(polyline);

            if (i != legSize - 1) {
                options = new MarkerOptions();
                options.position(leg.getEndPosition());
                options.title(leg.getEndAddressText());
                options.icon(getMarkerIcon(ContextCompat.getColor(MapsActivity.this, COLORS[colorIndex])));
                map.addMarker(options);
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayBasicInfo(route, route.getDistance(), route.getDuration());
            }
        });
    }

    @Override
    public void onRoutingCancelled() {
        // unused methods - notification needed!
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;

        routingProcess();
    }
}
