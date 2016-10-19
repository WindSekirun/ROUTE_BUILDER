package com.github.windsekirun.itinerary_builder.parser;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Parses a url pointing to a Google JSON object to a Route object.
 * <p/>
 * Some Edit by FIXME @WindSekirun
 *
 * @return a Route object based on the JSON object by Haseem Saheed
 */

public class GoogleParser extends XMLParser implements Parser {
    private static final String VALUE = "value";
    private static final String DISTANCE = "distance";

    private int distance;
    private int duration;

    private static final String OK = "OK";

    public GoogleParser(String feedUrl) {
        super(feedUrl);
    }


    public final List<Route> parse() throws RouteException {
        List<Route> routes = new ArrayList<>();

        final String result = convertStreamToString(this.getInputStream());
        Log.e("parse-Result", result + "");
        if (result == null) {
            throw new RouteException("Result is null");
        }

        try {
            final JSONObject json = new JSONObject(result);

            if (!json.getString("status").equals(OK)) {
                throw new RouteException(json);
            }

            JSONArray jsonRoutes = json.getJSONArray("routes");

            // 루트가 여러개일 경우 여러개를 리턴해야 함.
            for (int i = 0; i < jsonRoutes.length(); i++) {
                Route route = new Route();
                distance = 0;

                // 공통 정보
                JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
                final JSONObject jsonBounds = jsonRoute.getJSONObject("bounds");
                final JSONObject jsonNortheast = jsonBounds.getJSONObject("northeast");
                final JSONObject jsonSouthwest = jsonBounds.getJSONObject("southwest");

                route.setCopyright(jsonRoute.getString("copyrights"));

                if (!jsonRoute.getJSONArray("warnings").isNull(0)) {
                    route.setWarning(jsonRoute.getJSONArray("warnings").getString(0));
                }

                if (!jsonRoute.getJSONArray("waypoint_order").isNull(0)) {
                    JSONArray wayOrderArray = jsonRoute.getJSONArray("waypoint_order");
                    int size = wayOrderArray.length();
                    List<Integer> wayPointOrder = new ArrayList<>();
                    for (int z = 0; z < size; z++) {
                        wayPointOrder.add(wayOrderArray.getInt(z));
                    }

                    route.setWaypointOrder(wayPointOrder);
                }

                route.setLatLgnBounds(new LatLng(jsonNortheast.getDouble("lat"), jsonNortheast.getDouble("lng")), new LatLng(jsonSouthwest.getDouble("lat"), jsonSouthwest.getDouble("lng")));

                // 경유지 to 경유지
                final JSONArray legsArray = jsonRoute.getJSONArray("legs");
                final int legSize = legsArray.length();
                List<Leg> legs = new ArrayList<>();

                for (int j = 0; j < legSize; j++) {
                    Leg leg = new Leg();
                    final JSONObject legObject = jsonRoute.getJSONArray("legs").getJSONObject(j);

                    leg.setDurationText(legObject.getJSONObject("duration").getString("text"));
                    leg.setDurationValue(legObject.getJSONObject("duration").getInt(VALUE));
                    leg.setDistanceText(legObject.getJSONObject(DISTANCE).getString("text"));
                    leg.setDistanceValue(legObject.getJSONObject(DISTANCE).getInt(VALUE));
                    leg.setEndAddressText(legObject.getString("end_address"));
                    leg.setStartAddressText(legObject.getString("start_address"));

                    JSONObject startPosition = legObject.getJSONObject("start_location");
                    leg.setStartPosition(new LatLng(startPosition.getDouble("lat"), startPosition.getDouble("lng")));

                    JSONObject endPosition = legObject.getJSONObject("end_location");
                    leg.setEndPosition(new LatLng(endPosition.getDouble("lat"), endPosition.getDouble("lng")));

                    // 경유지에서 스탭 (단계)
                    final JSONArray stepsArray = legObject.getJSONArray("steps");
                    final int numSteps = stepsArray.length();
                    List<Step> steps = new ArrayList<>();
                    List<LatLng> legPoints = new ArrayList<>();

                    for (int y = 0; y < numSteps; y++) {
                        Step step = new Step();
                        final JSONObject stepObject = stepsArray.getJSONObject(y);

                        startPosition = stepObject.getJSONObject("start_location");
                        step.setStartPosition(new LatLng(startPosition.getDouble("lat"), startPosition.getDouble("lng")));

                        endPosition = stepObject.getJSONObject("end_location");
                        step.setEndPosition(new LatLng(endPosition.getDouble("lat"), endPosition.getDouble("lng")));

                        step.setInstruction(stepObject.getString("html_instructions").replaceAll("<(.*?)*>", ""));

                        if (stepObject.has("maneuver")) {
                            step.setManeuver(stepObject.getString("maneuver"));
                        }

                        int length = stepObject.getJSONObject(DISTANCE).getInt(VALUE);
                        // 총 거리를 구합니다.
                        distance += length;
                        step.setDistance((double) length / (double) 1000);

                        if (stepObject.has("travel_mode")) {
                            step.setTravelMode(stepObject.getString("travel_mode"));
                        }

                        List<LatLng> points = new ArrayList<>();
                        points.addAll(decodePolyLine(stepObject.getJSONObject("polyline").getString("points")));
                        step.setPoints(points);
                        legPoints.addAll(points);

                        steps.add(step);
                    }

                    leg.setLegPointToDisplay(legPoints);
                    leg.setSteps(steps);
                    legs.add(leg);
                }

                route.setLegs(legs);
                route.setLength(distance);
                routes.add(route);
            }

        } catch (JSONException e) {
            throw new RouteException("JSONException. Msg: " + e.getMessage());
        }
        return routes;
    }

    private static String convertStreamToString(final InputStream input) {
        if (input == null) return null;

        final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        final StringBuilder sBuf = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sBuf.append(line);
            }
        } catch (IOException e) {
            Log.e("Routing Error", e.getMessage());
        } finally {
            try {
                input.close();
                reader.close();
            } catch (IOException e) {
                Log.e("Routing Error", e.getMessage());
            }
        }
        return sBuf.toString();
    }

    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }
}
