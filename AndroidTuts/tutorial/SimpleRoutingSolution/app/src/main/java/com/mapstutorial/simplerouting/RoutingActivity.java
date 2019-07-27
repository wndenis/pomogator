/*
 * Copyright (c) 2011-2019 HERE Global B.V. and its affiliate(s).
 * All rights reserved.
 * The use of this software is conditional upon having a separate agreement
 * with a HERE company for the use or utilization of this software. In the
 * absence of such agreement, the use of the software is not allowed.
 */

package com.mapstutorial.simplerouting;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.routing.RouteManager;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;

import javax.net.ssl.HttpsURLConnection;

public class RoutingActivity extends FragmentActivity {
    private static final String LOG_TAG = RoutingActivity.class.getSimpleName();

    // map embedded in the map fragment
    private Map map = null;

    // map fragment embedded in this activity
    private SupportMapFragment mapFragment = null;

    // TextView for displaying the current map scheme
    private TextView textViewResult = null;

    // MapRoute for this activity
    private static MapRoute mapRoute = null;


    private SupportMapFragment getSupportMapFragment() {
        return (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }


    private void initialize() {
        // Search for the map fragment to finish setup by calling init().
        mapFragment = getSupportMapFragment();
        mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    // retrieve a reference of the map from the map fragment
                    map = mapFragment.getMap();
                    // Set the map center coordinate to the Vancouver region (no animation)
                    map.setCenter(new GeoCoordinate(49.196261, -123.004773, 0.0),
                            Map.Animation.NONE);
                    // Set the map zoom level to the average between min and max (no animation)
                    map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);
                } else {
                    Log.e(LOG_TAG, "Cannot initialize SupportMapFragment (" + error + ")");
                }
            }
        });

        textViewResult = (TextView) findViewById(R.id.title);
        textViewResult.setText(R.string.textview_routecoordinates_2waypoints);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_routing, menu);
        return true;
    }



    // Functionality for taps of the "Get Directions" button
    public void getDirections(View view) {

        NetworkService.getInstance()
                .getJSONApi()
                .getPostWithID(1)
                .enqueue(new Callback<Post>() {
                    @Override
                    public void onResponse(@NonNull Call<Post> call, @NonNull Response<Post> response) {
                        Post post = response.body();
                        textViewResult.setText(post.getBody());
//                        textView.append(post.getId() + "\n");
//                        textView.append(post.getUserId() + "\n");
//                        textView.append(post.getTitle() + "\n");
//                        textView.append(post.getBody() + "\n");
                    }

                    @Override
                    public void onFailure(@NonNull Call<Post> call, @NonNull Throwable t) {
                        textViewResult.setText("ERROR");
//                        textView.append("Error occurred while getting request!");
                        t.printStackTrace();
                    }
                });

        // 1. clear previous results
//        textViewResult.setText("");
//        if (map != null && mapRoute != null) {
//            map.removeMapObject(mapRoute);
//            mapRoute = null;
//        }
//
//        // 2. Initialize RouteManager
//        RouteManager routeManager = new RouteManager();
//
//        // 3. Select routing options
//        RoutePlan routePlan = new RoutePlan();
//
//        RouteOptions routeOptions = new RouteOptions();
//        routeOptions.setTransportMode(RouteOptions.TransportMode.CAR);
//        routeOptions.setRouteType(RouteOptions.Type.FASTEST);
//        routePlan.setRouteOptions(routeOptions);
//
//        // 4. Select Waypoints for your routes
//        // START: Nokia, Burnaby
//        routePlan.addWaypoint(new GeoCoordinate(49.1966286, -123.0053635));
//
//        // END: Airport, YVR
//        routePlan.addWaypoint(new GeoCoordinate(49.1947289, -123.1762924));
//
//        // 5. Retrieve Routing information via RouteManagerEventListener
//        RouteManager.Error error = routeManager.calculateRoute(routePlan, routeManagerListener);
//        if (error != RouteManager.Error.NONE) {
//            Toast.makeText(getApplicationContext(),
//                    "Route calculation failed with: " + error.toString(), Toast.LENGTH_SHORT)
//                    .show();
//        }
    }

    public void Logout(MenuItem item){
        SharedPreferences.Editor editor = getSharedPreferences("login_file", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        Intent myIntent = new Intent(this, LoginActivity.class);
        startActivity(myIntent);
        finish();
    }
    private RouteManager.Listener routeManagerListener = new RouteManager.Listener() {
        public void onCalculateRouteFinished(RouteManager.Error errorCode,
                List<RouteResult> result) {

            if (errorCode == RouteManager.Error.NONE && result.get(0).getRoute() != null) {
                // create a map route object and place it on the map
                mapRoute = new MapRoute(result.get(0).getRoute());
                map.addMapObject(mapRoute);

                // Get the bounding box containing the route and zoom in (no animation)
                GeoBoundingBox gbb = result.get(0).getRoute().getBoundingBox();
                map.zoomTo(gbb, Map.Animation.NONE, Map.MOVE_PRESERVE_ORIENTATION);

                textViewResult.setText(String.format("Route calculated with %d maneuvers.",
                        result.get(0).getRoute().getManeuvers().size()));
            } else {
                textViewResult.setText(
                        String.format("Route calculation failed: %s", errorCode.toString()));
            }
        }

        public void onProgress(int percentage) {
            textViewResult.setText(String.format("... %d percent done ...", percentage));
        }
    };
}
