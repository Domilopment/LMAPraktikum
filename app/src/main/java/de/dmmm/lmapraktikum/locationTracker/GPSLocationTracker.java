package de.dmmm.lmapraktikum.locationTracker;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import de.dmmm.lmapraktikum.utility.TimestampCreator;
import de.dmmm.lmapraktikum.MainActivity;
import de.dmmm.lmapraktikum.sensorManager.JSONSensorData;

public class GPSLocationTracker implements LocationListener, JSONSensorData {

    private LocationManager locationManager;
    private Activity mainActivity;
    private long timeThreshold = 1000;
    private float distanceThreshold = 0;
    private OnLocationChangeListener onLocationChangeListener;
    private JSONObject point;
    private Location location;
    private boolean debug = false;
    //private TimestampCreator timestampCreator;

    public GPSLocationTracker(MainActivity mainActivity, OnLocationChangeListener onLocationChangeListener){
        this.mainActivity = mainActivity;
        //this.locationList = new LinkedList<JSONObject>();
        this.onLocationChangeListener = onLocationChangeListener;
        locationManager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
        //timestampCreator = new TimestampCreator();
    }

    @Override // LocationListener
    public void onLocationChanged(Location location) {
        this.location = location;
        //locationList.add(this.getData());
        onLocationChangeListener.onLocationChanged(location);
    }

    @Override // LocationListener
    public void onProviderDisabled(String provider) {
        Toast.makeText(mainActivity.getApplicationContext(), "Please Activate " + provider.toUpperCase(), Toast.LENGTH_LONG).show();
    }

    @Override // LocationListener
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if(debug) Toast.makeText(mainActivity.getApplicationContext(), provider.toUpperCase() + " HAS CHANGED\n" + "STATUS: " + status, Toast.LENGTH_LONG).show();
    }

    @Override // LocationListener
    public void onProviderEnabled(String provider) {
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
                point.put("timestamp", "0000-00-00T00:00:00");
            }catch (JSONException e){
                if(debug) Toast.makeText(mainActivity.getApplicationContext(), "GPSLocationTrackerJSONException", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else {
            try {
                point.put("longitude", location.getLongitude());
                point.put("latitude", location.getLatitude());
                point.put("altitude", location.getAltitude());
                point.put("timestamp", TimestampCreator.createTimestampString());
            }catch (JSONException e){
                if(debug) Toast.makeText(mainActivity.getApplicationContext(), "GPSLocationTrackerJSONException", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

        return point;
    }

    @Override
    public String getSensorName() {
        return "GPS";
    }

    /**
     * Starts location tracking only with GPS provider
     */
    public void startTracking() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, timeThreshold, distanceThreshold, this);
            if(debug) Log.e("start", "Logger Started");
            onLocationChangeListener.setLogRecord("GPS tracking starting...");
        }
        catch(SecurityException e) {
            e.printStackTrace();
            onLocationChangeListener.setLogRecord("GPS tracking failed");
        }
    }

    /**
     * Starts location tracking with network provider
     */
    public void startTrackingWithNetwork() {
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, timeThreshold, distanceThreshold, this);
            onLocationChangeListener.setLogRecord("Network tracking started");
            }
        catch(SecurityException e) {
            e.printStackTrace();
            onLocationChangeListener.setLogRecord("Network tracking failed");
        }
    }

    /**
     * Stops tracking location
     */
    public void stopTracking(){
        locationManager.removeUpdates(this);
        onLocationChangeListener.setLogRecord("Tracking stopped");
    }

    /**
     * Location list contains all locations that have been tracked since start of tracking
     * @return locationList
     */
    /*
    public List<JSONObject> getLocationList(){
        return this.locationList;
    }
    */

    /**
     * Threshold for location updates. Only locations that pass threshold will be added to location list.
     * @param timeThreshold time in milliseconds
     */
    public void setTrackingTimeThreshold(long timeThreshold){
        this.timeThreshold = timeThreshold;
    }

    /**
     * Threshold for location updates. Only locations that pass threshold will be added to location list.
     * @return timeThreshold in milliseconds
     */
    public long getTrackingTimeThreshold(){
        return this.timeThreshold;
    }

    /**
     * Threshold for location updates. Only locations that pass threshold will be added to location list.
     * @param distanceThreshold in meters
     */
    public void setTrackingDistanceThreshold(float distanceThreshold){
        this.distanceThreshold = distanceThreshold;
    }

    /**
     * Threshold for location updates. Only locations that pass threshold will be added to location list.
     * @return distanceThreshold in meters
     */
    public float getTrackingDistanceThreshold(){
        return this.distanceThreshold;
    }
}