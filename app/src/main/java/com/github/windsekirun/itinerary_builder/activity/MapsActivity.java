package com.github.windsekirun.itinerary_builder.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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

    CoordinatorLayout coordinatorLayout;
    View bottomSheet;
    BottomSheetBehavior behavior;
    LegsModalListAdapter legsModalListAdapter;
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
                .waypoints(wayPoints)
                .key(API_KEY)
                .build();

        routing.execute();
    }

    public void analyzeRouteToList(Route route) {
        itemSet = new ArrayList<>();

        int legSize = route.getLegs().size();
        for (int i = 0; i < legSize; i++) {
            Leg leg = route.getLegs().get(i);
            String title;
            if (i == 0) {
                title = routeModel.getStartLocation().getName();
            } else if (i == legSize - 1) {
                title = routeModel.getEndLocation().getName();
            } else {
                title = routeModel.getLocationRoutes().get(i - 1).getName();
            }
            itemSet.add(new Pair<>(title, leg));
        }

        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.sheet, null);

        legsList = (RecyclerView) view.findViewById(R.id.recyclerView);

        legsModalListAdapter = new LegsModalListAdapter(MapsActivity.this, itemSet);
        legsList.setLayoutManager(new LinearLayoutManager(MapsActivity.this));
        legsList.setHasFixedSize(true);
        legsList.setAdapter(legsModalListAdapter);

        legsModalListAdapter.notifyDataSetChanged();

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(view);
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    public void displayBasicInfo(long distance, long duration) {
        basicInfoView = (RelativeLayout) findViewById(R.id.basicInfoView);
        routeTitle = (TextView) findViewById(R.id.routeTitle);
        timeView = (TextView) findViewById(R.id.timeView);

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
        progressDialog.dismiss();
        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);

        map.moveCamera(center);
        map.moveCamera(zoom);

        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(start);
        map.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(end);
        map.addMarker(options);

        // We only support first Route!
        final Route route = routes.get(0);
        int legSize = route.getLegs().size();

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
                options.icon(getMarkerIcon(ContextCompat.getColor(MapsActivity.this, COLORS[colorIndex])));
                map.addMarker(options);
            }
        }

        analyzeRouteToList(route);

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mBottomSheetDialog.show();
                return false;
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayBasicInfo(route.getDistance(), route.getDuration());
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

    public class LegsModalListAdapter extends RecyclerView.Adapter<LegsModalViewHolder> {
        Context c;
        ArrayList<Pair<String, Leg>> itemSet;

        public LegsModalListAdapter(Context c, ArrayList<Pair<String, Leg>> legs) {
            this.c = c;
            this.itemSet = legs;
        }

        @Override
        public LegsModalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new LegsModalViewHolder(View.inflate(c, R.layout.row_maps_leg_item, null));
        }

        @Override
        public void onBindViewHolder(LegsModalViewHolder holder, int position) {
            final Pair<String, Leg> leg = itemSet.get(position);
            holder.title.setText(leg.first);

            int colorIndex = position % COLORS.length;
            holder.legColorRoute.setBackgroundColor(ContextCompat.getColor(c, COLORS[colorIndex]));

            StringBuilder stringBuilder = new StringBuilder();
            int distance = leg.second.getDistanceValue();
            int duration = leg.second.getDurationValue();

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

            holder.duration.setText(stringBuilder.toString());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MapsActivity.this, RouteModalActivity.class);
                    intent.putExtra(LEG_OBJECT, leg.second);
                    startActivity(intent);
                }
            });
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

    public class LegsModalViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        FrameLayout legColorRoute;
        TextView duration;

        public LegsModalViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            legColorRoute = (FrameLayout) itemView.findViewById(R.id.frameLayout);
            duration = (TextView) itemView.findViewById(R.id.textView3);
        }
    }
}
