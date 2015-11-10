package com.mycompany.wait;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
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
        Location myLocation = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
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
}
