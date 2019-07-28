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
import java.util.List;

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

import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.Route;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.RoutingError;
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
                        map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 1.3);
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
        map.setZoomLevel(map.getMaxZoomLevel());
        map.setTilt(35, Map.);
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

                if (currentPosition == null){
                    startLoop();
                    return;
                }
                //        map.addMapObject()
                NetworkService.getInstance()
                        .getJSONApi()
//                        .getPlan(login, currentPosition.getCoordinate().getLatitude() + 0.1, currentPosition.getCoordinate().getLongitude() + 0.1)
                        .getPlan(login, 55.8407938, 37.8090099)
                        .enqueue(new Callback<Post>() {
                            @Override
                            public void onResponse(@NonNull Call<Post> call, @NonNull Response<Post> response) {
                                Post post = response.body();

                                RouteOptions routeOptions = new RouteOptions();
                                routeOptions.setTransportMode(RouteOptions.TransportMode.TRUCK);
                                routeOptions.setRouteType(RouteOptions.Type.FASTEST);

                                if (map != null && mapRoute != null) {
                                    map.removeMapObject(mapRoute);
                                    mapRoute = null;
                                }

                                CoreRouter router = new CoreRouter();
                                RoutePlan routePlan = new RoutePlan();
                                routePlan.setRouteOptions(routeOptions);

                                if (post == null){
                                    Log.i("OOOOOOOOOOOOOOOOOOPS", "!!!!");
                                    startLoop();
                                    return;
                                }

                                if (post.getResults() == null){
                                    Log.i("NO", "RESULT");
                                    startLoop();
                                    return;
                                }

                                for (Waypoint w:post.getResults().get(1).getWaypoints()) {
                                    routePlan.addWaypoint(new RouteWaypoint(new GeoCoordinate(w.getLat(), w.getLng())));
                                }
                                Log.i("ROUTE", "NICE");

                                mapRoute.setTrafficEnabled(true);
                                map.addMapObject(new MapRoute());
                                router.calculateRoute(routePlan, new RouteListener());
                                startLoop();

//                        textView.append(post.getId() + "\n");
//                        textView.append(post.getUserId() + "\n");
//                        textView.append(post.getTitle() + "\n");
//                        textView.append(post.getBody() + "\n");
                            }

                            @Override
                            public void onFailure(@NonNull Call<Post> call, @NonNull Throwable t) {
//                        textView.append("Error occurred while getting request!");
                                t.printStackTrace();
                                startLoop();
                            }
                        });
            }
        }
    };

    private class RouteListener implements CoreRouter.Listener {

        // Method defined in Listener
        public void onProgress(int percentage) {
            // Display a message indicating calculation progress
        }

        // Method defined in Listener
        public void onCalculateRouteFinished(List<RouteResult> routeResult, RoutingError error) {
            // If the route was calculated successfully
            if (error == RoutingError.NONE) {
                // Render the route on the map
                mapRoute = new MapRoute(routeResult.get(0).getRoute());
                map.addMapObject(mapRoute);
            }
            else {
                // Display a message indicating route calculation failure
            }
        }
    }

    public void stopLoop() {
        started = false;
        loop.removeCallbacks(runnable);
    }

    public void startLoop() {
        started = true;
        loop.postDelayed(runnable, 5000);
    }
}
