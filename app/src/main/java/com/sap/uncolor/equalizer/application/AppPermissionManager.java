package com.sap.uncolor.equalizer.application;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Uncolor on 05.09.2018.
 */

public class AppPermissionManager {

    public static final int PERMISSION_REQUEST_CODE = 10;

    public static void requestAppPermissions(Activity activity, final String[] requestedPermissions, final int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity, requestedPermissions, requestCode);
        }
    }

    public static boolean checkIfAlreadyHavePermission(Activity activity, final String requestedPermissions) {
        int result = ContextCompat.checkSelfPermission(activity, requestedPermissions);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
}

