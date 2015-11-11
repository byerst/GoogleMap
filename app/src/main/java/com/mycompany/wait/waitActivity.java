package com.mycompany.wait;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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
    private LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    private String phoneNo = new String("8083589165");
    private String message = new String("test");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);
       /* Intent intent = getIntent();
        phoneNo = intent.getStringExtra("phoneNo");
        message = intent.getStringExtra("message"); */

        setUpMapIfNeeded();
        //while we are not close enough to destination
        int n = 10;
        while(n!=0){
            //wait a second before checking again
            delay(1000);
            //update current location
            currentLoc = getCurrent();
            currentLatLng = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
            n = n-1;

        }
        // Now close enough to send text
        sendSMS();

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
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Create a criteria object to retrieve provider
        //Criteria criteria = new Criteria();

        // Get name of best provider
        //String provider = locationManager.getBestProvider(criteria,true);

        // Get current Location
        Location myLocation = getCurrent();
        if(myLocation == null)
            Log.d("test", "error");

        // Set Google map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Get Latitude of Current Location
        double latitude = myLocation.getLatitude();

        // Get Longitude of Current location
        double longitude = myLocation.getLongitude();

        // Create latlng object of current location
        LatLng latLng = new LatLng(latitude,longitude);

        // Show current location in google map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the google map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

    }

    protected void delay(int time){
        new CountDownTimer(time, 1000) // onTick time, not used
        {

            public void onTick(long millisUntilFinished) {
                // Not used
            }

            public void onFinish() {
                //not used
            }
        }.start();

    }

    protected Location getCurrent(){
        Location myLocation = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        return myLocation;
    }
    protected void sendSMS(){
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
}
