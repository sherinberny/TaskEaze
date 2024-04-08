package com.ads.taskeaze.locationClasses;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class MyLocationListener implements LocationListener {

    private LocationUpdateListener updateListener;

    @Override
    public void onLocationChanged(Location location) {
        Log.d("LocationListener", "Location changed: " + location.getLatitude() + ", " + location.getLongitude());

        if (updateListener != null) {
            updateListener.onLocationUpdate(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
        Log.d("LocationListener", "Status changed: " + provider + ", status: " + status);
    }
    // Handle location provider status changes if needed


    @Override
    public void onProviderEnabled(String provider) {
        Log.d("LocationListener", "Provider enabled: " + provider);
        // Handle location provider enabled if needed
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("LocationListener", "Provider disabled: " + provider);
        // Handle location provider disabled if needed
    }
    public void setUpdateListener(LocationUpdateListener listener) {
        updateListener = listener;
    }
}