package com.mycompany.googlemap;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mycompany.sms.SendSMSActivity;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity {

    public GoogleMap mMap; // Might be null if Google Play services APK is not available.
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

    public void onSearch(View view) throws IOException {

        Log.d("onSearch","entered");
        EditText location_tf = (EditText)findViewById(R.id.TFaddress);
        String location = location_tf.getText().toString();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(location_tf.getWindowToken(), 0);
        location_tf.setText("");
        location_tf.clearFocus();
        List<Address> addressList = null;

        if (location != null || !location.equals(" ")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }

            if(addressList.get(0) != null) {
                Address address = addressList.get(0);

                 dest =  new LatLng(address.getLatitude(), address.getLongitude());
                /*mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));*/
                Intent intent = new Intent(this, SendSMSActivity.class);
                startActivity(intent);


            }
            else{
                Log.d("error", "addressList[0] == null");
                return;
            }

        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
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
