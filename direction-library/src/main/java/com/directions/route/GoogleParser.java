package com.directions.route;

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

            for (int i = 0; i < jsonRoutes.length(); i++) {
                Route route = new Route();
                Segment segment = new Segment();

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

                final JSONArray legs = jsonRoute.getJSONArray("legs");

                final int legSize = legs.length();

                for (int j = 0; j < legSize; j++) {
                    final JSONObject leg = jsonRoute.getJSONArray("legs").getJSONObject(j);

                    final JSONArray steps = leg.getJSONArray("steps");

                    final int numSteps = steps.length();

                    route.setName(leg.getString("start_address") + " to " + leg.getString("end_address"));

                    route.setDurationText(leg.getJSONObject("duration").getString("text"));
                    route.setDurationValue(leg.getJSONObject("duration").getInt(VALUE));
                    route.setDistanceText(leg.getJSONObject(DISTANCE).getString("text"));
                    route.setDistanceValue(leg.getJSONObject(DISTANCE).getInt(VALUE));
                    route.setEndAddressText(leg.getString("end_address"));
                    route.setLength(leg.getJSONObject(DISTANCE).getInt(VALUE));

                    for (int y = 0; y < numSteps; y++) {
                        final JSONObject step = steps.getJSONObject(y);
                        final JSONObject start = step.getJSONObject("start_location");
                        final LatLng position = new LatLng(start.getDouble("lat"), start.getDouble("lng"));
                        segment.setPoint(position);

                        final int length = step.getJSONObject(DISTANCE).getInt(VALUE);
                        distance += length;
                        segment.setLength(length);
                        segment.setDistance((double) distance / (double) 1000);

                        segment.setInstruction(step.getString("html_instructions").replaceAll("<(.*?)*>", ""));

                        if (step.has("maneuver"))
                            segment.setManeuver(step.getString("maneuver"));

                        route.addPoints(decodePolyLine(step.getJSONObject("polyline").getString("points")));
                        route.addSegment(segment.copy());
                    }
                }


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
