package com.im_near.wait;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.im_near.googlemap.R;

/**
 * Created by Tim on 11/9/15.
 */
public class waitActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private float eta = 5; // time to destination
    private LatLng dest;    // destination latitude and longitude
    private float destLat;
    private float destLong;

    private LatLng currentLatLng; //current latlng
    private Location currentLoc;  //current Location
    private LocationListener myListener;    //Location Listener


    private String phoneNo; //= new String("8082236901");  //Michele's number
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);


        Intent intent = getIntent();
        phoneNo = intent.getStringExtra("phoneNo");
        message = intent.getStringExtra("message");
        destLat = intent.getFloatExtra("destLat", 0);
        destLong = intent.getFloatExtra("destLong", 0);

        setUpMapIfNeeded();


        //while we are not close enough to destination
        //int n = 1000;

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

    public void onStart(View view){
        int m = 0;
        for(int n = 100000; n > 0; n--) {
            m++;
            if(n % 1000 == 0) {
                Location myLocation = getCurrent();
                Log.d("location", "myLocation");
                LatLng myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
            }
        }
        //sendSMS();
    }

    Location getCurrent() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location myLocation = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        return myLocation;
    }

    protected void sendSMS() {
        int n = 10000;
        boolean sendText = false;

       /* while(!sendText){
            n = n-1;
            Location myLocation = getCurrent();
            Log.d("location", "myLocation");
            LatLng myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
        }
        if(sendText) {
            try {
                // initialize an SmsManager class called smsManager
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, null, message, null, null);
                Toast.makeText(getApplicationContext(), "SMS Sent!",
                        Toast.LENGTH_LONG).show();
                new AlertDialog.Builder(this).setTitle("MapTest").setMessage("Message Sent!").setNeutralButton("Close", null).show();

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        "SMS failed, please try again later!",
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }*/
    }

}