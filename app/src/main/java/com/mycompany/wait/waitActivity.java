package com.mycompany.wait;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.mycompany.googlemap.R;

/**
 * Created by Tim on 11/9/15.
 */
public class waitActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private float eta = 5; // time to destination
    private LatLng dest;    // destination latitude and longitude
    private LatLng currentLatLng; //current latlng
    private Location currentLoc;  //current Location
    private LocationListener myListener;    //Location Listener
    private FollowMeLocationSource followMeLocationSource;

    private String phoneNo = new String("8082236901");  //Michele's number
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);
        followMeLocationSource = new FollowMeLocationSource();

        Intent intent = getIntent();
        //phoneNo = intent.getStringExtra("phoneNo");
        message = intent.getStringExtra("message");

        setUpMapIfNeeded();
        //while we are not close enough to destination
        int n = 1000;

        /*while (n != 0) {
            //wait a second before checking again

            Log.d("Delay", "in delay loop");
            //update current location
            currentLoc = getCurrent();
            currentLatLng = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
            n = n - 1;

        }*/
        // Now close enough to send text


    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        // Enable myLocation layer of google map
        mMap.setMyLocationEnabled(true);

        // Get LocationManager object from system service LOCATION_SERVICE
        //LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 1000, 5, myListener);

        // Create a criteria object to retrieve provider
        //Criteria criteria = new Criteria();

        // Get name of best provider
        //String provider = locationManager.getBestProvider(criteria,true);

        // Get current Location
        Location myLocation = getCurrent();
        if (myLocation == null)
            Log.d("test", "error");

        // Set Google map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Get Latitude of Current Location
        double latitude = myLocation.getLatitude();

        // Get Longitude of Current location
        double longitude = myLocation.getLongitude();

        // Create latlng object of current location
        LatLng latLng = new LatLng(latitude, longitude);

        //mMap.setLocationSource(followMeLocationSource);

        // Show current location in google map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the google map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

    }


    protected Location getCurrent() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location myLocation = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        return myLocation;
    }

    protected void sendSMS() {
        int n = 1000000;
        while (n != 0) {
            Log.d("SMS Delay", "entered");
            n = n - 1;
        }
        try {
            // initialize an SmsManager class called smsManager
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent!",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS failed, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    private class FollowMeLocationSource implements LocationSource, LocationListener {

        private OnLocationChangedListener mListener;
        private LocationManager locationManager;
        private final Criteria criteria = new Criteria();
        private String bestAvailableProvider;
        /* Updates are restricted to one every 10 seconds, and only when
         * movement of more than 10 meters has been detected.*/
        private final int minTime = 1000;     // minimum time interval between location updates, in milliseconds
        private final int minDistance = 5;    // minimum distance between location updates, in meters

        private FollowMeLocationSource() {
            // Get reference to Location Manager
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // Specify Location Provider criteria
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            criteria.setAltitudeRequired(true);
            criteria.setBearingRequired(true);
            criteria.setSpeedRequired(true);
            criteria.setCostAllowed(true);
        }

        private void getBestAvailableProvider() {
            /* The preffered way of specifying the location provider (e.g. GPS, NETWORK) to use
             * is to ask the Location Manager for the one that best satisfies our criteria.
             * By passing the 'true' boolean we ask for the best available (enabled) provider. */
            bestAvailableProvider = locationManager.getBestProvider(criteria, true);
        }

        /* Activates this provider. This provider will notify the supplied listener
         * periodically, until you call deactivate().
         * This method is automatically invoked by enabling my-location layer. */
        @Override
        public void activate(OnLocationChangedListener listener) {
            // We need to keep a reference to my-location layer's listener so we can push forward
            // location updates to it when we receive them from Location Manager.
            mListener = listener;

            // Request location updates from Location Manager
            if (bestAvailableProvider != null) {
                locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, minTime, minDistance, this);
            } else {
                // (Display a message/dialog) No Location Providers currently available.
            }
        }

        /* Deactivates this provider.
         * This method is automatically invoked by disabling my-location layer. */
        @Override
        public void deactivate() {
            // Remove location updates from Location Manager
            locationManager.removeUpdates(this);

            mListener = null;
        }

        @Override
        public void onLocationChanged(Location location) {
            /* Push location updates to the registered listener..
             * (this ensures that my-location layer will set the blue dot at the new/received location) */
            if (mListener != null) {
                mListener.onLocationChanged(location);
            }

            /* ..and Animate camera to center on that location !
             * (the reason for we created this custom Location Source !) */
            Toast.makeText(getApplicationContext(),
                    "Location Changed!",
                    Toast.LENGTH_LONG).show();
            sendSMS();
            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

}
