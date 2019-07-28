/*
 * Copyright (c) 2011-2019 HERE Global B.V. and its affiliate(s).
 * All rights reserved.
 * The use of this software is conditional upon having a separate agreement
 * with a HERE company for the use or utilization of this software. In the
 * absence of such agreement, the use of the software is not allowed.
 */

package com.realitycheck.pomogatorandroidapp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.mapping.MapRoute;
import com.realitycheck.pomogatorandroidapp.R;


public class RoutingActivity extends FragmentActivity {
    private static final String LOG_TAG = RoutingActivity.class.getSimpleName();
    private boolean paused = false;
    private PositioningManager posManager;
    private PositioningManager.OnPositionChangedListener positionListener;
    private GeoPosition currentPosition;
    private int login;

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
    }

    @Override
    public void onStart() {
        super.onStart();
        initialize();

        login = getSharedPreferences("login_file", Context.MODE_PRIVATE).getInt("login", -1);
    }

    @Override
    public void onResume() {
        super.onResume();
        paused = false;
        if (posManager != null) {
            posManager.start(
                    PositioningManager.LocationMethod.GPS_NETWORK);
        }
    }

    @Override
    public void onPause() {
        if (posManager != null) {
            posManager.stop();
        }
        super.onPause();
        paused = true;
    }

    @Override
    public void onDestroy() {
        if (posManager != null) {
            // Cleanup
            posManager.removeListener(
                    positionListener);
        }
        map = null;
        super.onDestroy();
    }

    private void initialize() {
        // Search for the map fragment to finish setup by calling init().
        mapFragment = (SupportMapFragment)(getSupportFragmentManager().findFragmentById(R.id.mapfragment));
        // Set up disk cache path for the map service for this application
        // It is recommended to use a path under your application folder for storing the disk cache
        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(
                getApplicationContext().getExternalFilesDir(null) + File.separator + ".here-maps",
                "com.realitycheck.pomogatorandroidapp"); /* ATTENTION! Do not forget to update {YOUR_INTENT_NAME} */
        if (!success) {
            Toast.makeText(getApplicationContext(), "Unable to set isolated disk cache path.",
                    Toast.LENGTH_LONG).show();
        } else {
            mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                    if (error == OnEngineInitListener.Error.NONE) {
                        // retrieve a reference of the map from the map fragment
                        map = mapFragment.getMap();
                        map.setMapScheme(Map.Scheme.CARNAV_TRAFFIC_DAY);
                        // Set the zoom level to the average between min and max
                        map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 3);
                        map.setCenter(new GeoCoordinate(55.751116, 37.618991), Map.Animation.NONE);

                        posManager = PositioningManager.getInstance();
                        positionListener = new PositioningManager.OnPositionChangedListener() {
                            @Override
                            public void onPositionUpdated(PositioningManager.LocationMethod method,
                                                          GeoPosition position, boolean isMapMatched) {
                                // set the center only when the app is in the foreground
                                // to reduce CPU consumption
                                if (!paused) {
                                    currentPosition = position;
                                    map.setCenter(position.getCoordinate(),
                                            Map.Animation.BOW);
                                }
                            }
                            @Override
                            public void onPositionFixChanged(PositioningManager.LocationMethod method,
                                                             PositioningManager.LocationStatus status) {
                            }
                        };

                        posManager.addListener(
                                new WeakReference<>(positionListener));
                        posManager.start(PositioningManager.LocationMethod.GPS_NETWORK);
                        mapFragment.getPositionIndicator().setVisible(true);
                    } else {
                        System.out.println("ERROR: Cannot initialize Map Fragment");
                    }
                    map.getPositionIndicator().setVisible(true);
                    map.setFadingAnimations(true);
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_routing, menu);
        return true;
    }


    public void satelliteMode(View view){
        map.setMapScheme(Map.Scheme.CARNAV_TRAFFIC_HYBRID_DAY);
    }
    public void vectorMode(View view){
        map.setMapScheme(Map.Scheme.CARNAV_TRAFFIC_DAY);
    }
    public void trafficMode(View view){
        map.setTrafficInfoVisible(!map.isTrafficInfoVisible());
    }

    // Functionality for taps of the "Get Directions" button
    public void getDirections(View view) {
        view.setVisibility(View.GONE);
        startLoop();
    }

    public void Logout(MenuItem item){
        SharedPreferences.Editor editor = getSharedPreferences("login_file", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        Intent myIntent = new Intent(this, LoginActivity.class);
        startActivity(myIntent);
        finish();
    }
//    private CoreRouter.Listener routeManagerListener = new CoreRouter.Listener() {
//        public void onCalculateRouteFinished(CoreRouter.Error errorCode,
//                List<RouteResult> result) {
//
//            if (errorCode == CoreRouter.Error.NONE && result.get(0).getRoute() != null) {
//                // create a map route object and place it on the map
//                mapRoute = new MapRoute(result.get(0).getRoute());
//                map.addMapObject(mapRoute);
//
//                // Get the bounding box containing the route and zoom in (no animation)
//                GeoBoundingBox gbb = result.get(0).getRoute().getBoundingBox();
//                map.zoomTo(gbb, Map.Animation.NONE, Map.MOVE_PRESERVE_ORIENTATION);
//
//                textViewResult.setText(String.format("Route calculated with %d maneuvers.",
//                        result.get(0).getRoute().getManeuvers().size()));
//            } else {
//                textViewResult.setText(
//                        String.format("Route calculation failed: %s", errorCode.toString()));
//            }
//        }
//
//        public void onProgress(int percentage) {
//            textViewResult.setText(String.format("... %d percent done ...", percentage));
//        }
//    };


    private boolean started = false;
    private Handler loop = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(started) {
                Log.i("CYCLE", "CYCLING");


                //        map.addMapObject()
                NetworkService.getInstance()
                        .getJSONApi()
                        .getPlan(login)
                        .enqueue(new Callback<Post>() {
                            @Override
                            public void onResponse(@NonNull Call<Post> call, @NonNull Response<Post> response) {
                                Post post = response.body();

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

                startLoop();
            }
        }
    };

    public void stopLoop() {
        started = false;
        loop.removeCallbacks(runnable);
    }

    public void startLoop() {
        started = true;
        loop.postDelayed(runnable, 750);
    }
}
