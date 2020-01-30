package de.dmmm.lmapraktikum.locationTracker;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import de.dmmm.lmapraktikum.utility.TimestampCreator;
import de.dmmm.lmapraktikum.MainActivity;
import de.dmmm.lmapraktikum.sensorManager.JSONSensorData;

public class GPSFusedLocationTracker implements LocationListener, JSONSensorData {

    private LocationRequest locationRequest;
    private long interval = 1000;
    private long fastestInterval = 1000;
    private OnLocationChangeListener onLocationChangeListener;
    private int accuracy = LocationRequest.PRIORITY_HIGH_ACCURACY;
    private JSONObject point;
    private JSONArray data = new JSONArray();
    private MainActivity mainActivity;
    private LocationCallback callback;
    private Location location;
    private boolean debug = false;

    public GPSFusedLocationTracker(MainActivity mainActivity, OnLocationChangeListener onLocationChangeListener) {
        this.mainActivity = mainActivity;
        this.onLocationChangeListener = onLocationChangeListener;
        locationRequest = new LocationRequest();
        locationRequest.setPriority(this.accuracy);
        locationRequest.setInterval(this.interval);
        locationRequest.setFastestInterval(this.fastestInterval);

        callback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };
    }

    @Override // LocationListener
    public void onLocationChanged(Location location){
        this.location = location;
        onLocationChangeListener.onLocationChanged(location);

        JSONObject jsonData = this.getData();
        try {
            jsonData.put("time", TimestampCreator.createTimestampString());
            jsonData.put("accuracy", locationRequest.getPriority());
            jsonData.put("type:", "fusedGPS");
        }catch (JSONException e){
            Log.e("JSON EXCEPTION: ", String.valueOf(e));
        }
        String data = jsonData.toString();
    }

    @Override // LocationListener
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if(debug) Toast.makeText(mainActivity.getApplicationContext(), provider.toUpperCase() + " HAS CHANGED\n" + "STATUS: " + status, Toast.LENGTH_LONG).show();
    }

    @Override // LocationListener
    public void onProviderEnabled(String provider) {
        Toast.makeText(mainActivity.getApplicationContext(), "Please Activate " + provider.toUpperCase(), Toast.LENGTH_LONG).show();
    }

    @Override // LocationListener
    public void onProviderDisabled(String provider) {
        if(debug) Toast.makeText(mainActivity.getApplicationContext(), provider.toUpperCase() + " ENABLED", Toast.LENGTH_LONG).show();
    }

    @Override // JSONSensorData
    public JSONObject getData() {

        point = new JSONObject();

        if(location == null){
            if(debug) Toast.makeText(mainActivity.getApplicationContext(), "gps location == null", Toast.LENGTH_LONG).show();
            try {
                point.put("longitude", 0);
                point.put("latitude", 0);
                point.put("altitude", 0);
            }catch (JSONException e){
                if(debug) Toast.makeText(mainActivity.getApplicationContext(), "JSONException GPSLocationManager", Toast.LENGTH_LONG).show();
                Log.e("JSON EXCEPTION: ", String.valueOf(e));
            }
        }else{
            try {
                point.put("longitude", location.getLongitude());
                point.put("latitude", location.getLatitude());
                point.put("altitude", location.getAltitude());
            }catch (JSONException e){
                if(debug) Toast.makeText(mainActivity.getApplicationContext(), "JSONException GPSLocationManger", Toast.LENGTH_LONG).show();
                Log.e("JSON EXCEPTION: ", String.valueOf(e));
            }
        }
        return point;
    }

    @Override // JSONSensorData
    public String getSensorName() {
        return "GPS";
    }

    public void startTracking(){
        Toast.makeText(mainActivity.getApplicationContext(), "starting fusedGPS", Toast.LENGTH_LONG).show();
        LocationServices.getFusedLocationProviderClient(this.mainActivity).requestLocationUpdates(locationRequest, callback, Looper.myLooper());
    }

    public void stopTracking(){
        //Toast.makeText(mainActivity.getApplicationContext(), "stopping fusedGPS", Toast.LENGTH_LONG).show();
        LocationServices.getFusedLocationProviderClient(this.mainActivity).removeLocationUpdates(callback);
    }

    public void setHighAccuracy(){
        Toast.makeText(mainActivity.getApplicationContext(), "high accuracy", Toast.LENGTH_LONG).show();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void setBalancedAccuracy(){
        Toast.makeText(mainActivity.getApplicationContext(), "balanced accuracy", Toast.LENGTH_LONG).show();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    public void setLowAccuracy(){
        Toast.makeText(mainActivity.getApplicationContext(), "Low accuracy", Toast.LENGTH_LONG).show();
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
    }

    public void setLowestAccuracy(){
        Toast.makeText(mainActivity.getApplicationContext(), "no accuracy", Toast.LENGTH_LONG).show();
        locationRequest.setPriority(LocationRequest.PRIORITY_NO_POWER);
    }

    public void setInterval(long interval){
        locationRequest.setInterval(interval);
    }

    public void setFastestInterval(long interval){
        locationRequest.setFastestInterval(interval);
    }
}
