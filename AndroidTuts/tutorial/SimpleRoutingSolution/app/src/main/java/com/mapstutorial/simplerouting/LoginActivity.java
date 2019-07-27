package com.mapstutorial.simplerouting;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.PendingIntent.getActivity;

public class LoginActivity extends FragmentActivity {
    private static final String LOG_TAG = RoutingActivity.class.getSimpleName();

    // permissions request code
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private String login;
    private SharedPreferences sharedPref;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };

    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        TryLogin();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                break;
        }
    }

    public void TryLogin(){
        sharedPref = getSharedPreferences("login_file", Context.MODE_PRIVATE);
        login = sharedPref.getString("login", "");
        if (login.equals("")) {
            Toast.makeText(getApplicationContext(),
                    "Введите логин", Toast.LENGTH_SHORT).show();
        }
        else{
            StartRouting(login);
        }
    }

    public void Login(View view) {
        login = ((TextView)findViewById(R.id.logineditText)).getText().toString();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.putString("login", login);
        editor.apply();
        TryLogin();
    }

    private void StartRouting(String login){
        Intent myIntent = new Intent(this, RoutingActivity.class);
        startActivity(myIntent);
        finish();
    }



}
