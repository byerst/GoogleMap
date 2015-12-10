package com.mycompany.wait;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mycompany.googlemap.R;

import java.text.DateFormat;
import java.util.Date;


/**
 * Created by Tim on 11/23/15.
 */
public class waitActivity2 extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private double destLat; //destination latitude
    private double destLong;    //destination longitude

    private Location currentLoc;  //current Location
    Location destLoc;   //destination location

    private float distBetween; //distance between start and end points in meters
    private float distToSend = 1609;  //distance between points when message should be sent (meters)
    //default value of 1 mile



    private String phoneNo; //= new String("8082236901");  //Michele's number
    private String message;
    Chronometer timer;
    ProgressBar progressBar;
    int progressStatus = 0;

    LocationManager locationManager;
    GoogleApiClient mGoogleApiClient;

    protected Boolean mRequestingLocationUpdates;
    protected LocationRequest mLocationRequest;

    public boolean sent = false;

    protected Location mCurrentLocation;
    protected String mLastUpdateTime;

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait2);

        // Get values from intent
        Intent intent = getIntent();

        phoneNo = intent.getStringExtra("phoneNo");
        message = intent.getStringExtra("message");
        destLat = intent.getDoubleExtra("destLat", 0);
        destLong = intent.getDoubleExtra("destLong", 0);

        destLoc = new Location(Context.LOCATION_SERVICE);

        Log.d("Test", Double.toString(destLat));

        mRequestingLocationUpdates = false;

        //initialize locationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        buildGoogleApiClient();


        // Set up view elements
        timer = (Chronometer) findViewById(R.id.chronometer);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);





    }

    public void onBegin(View view){
        // start timer for elapsed time
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();

        destLoc.setLongitude(destLong);
        destLoc.setLatitude(destLat);

        Log.d("Test", destLoc.toString());

        // get current location
        currentLoc = getCurrent();

        distBetween = destLoc.distanceTo(currentLoc);

        Log.d("Test", Float.toString(distBetween));

        progressBar.setMax(Math.round(distBetween - distToSend));

        // start new thread to handle collecting current location
        /*new Thread(new Runnable() {
            public void run() {



                // while the current location isn't the destination
                while (destLoc.distanceTo(currentLoc)  > distToSend) {


                    // re-evaluate current location
                    currentLoc = getCurrent();
                    //Log.d("Test", currentLoc.toString());
                    progressStatus = Math.round(distBetween - destLoc.distanceTo(currentLoc));

                    // update view to reflect new location
                    progressBar.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                        }
                    });
                }
                // when it is time to send sms do so in UI thread
                waitActivity2.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sendSMS();
                    }
                });

            }
        }).start(); */
    }


    public double distToDest() {

        // placeholder value for calculated distance
        double dist;

        // find hypotenuse
        dist = Math.pow(Math.pow(Math.abs(currentLoc.getLatitude() - destLat), 2) + Math.pow(Math.abs(currentLoc.getLongitude() - destLong), 2), .5);

        // return value
        return dist;
    }

    Location getCurrent() {
    /* function that returns the current location using locationManager services
         */

        Location myLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
        return myLocation;
    }

    protected void sendSMS() {
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

    }
    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i("TAG", "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    private void updateProgress() {
        progressStatus = Math.round(distBetween - destLoc.distanceTo(mCurrentLocation));
        progressBar.setProgress(progressStatus);
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();

        super.onStop();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateProgress();
        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Log.d("Location", mCurrentLocation.toString());
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateProgress();
        if (destLoc.distanceTo(currentLoc)  > distToSend){
            /** Do Nothing
             *
             */
        }
        else if(!sent) {
            sendSMS();
            sent = true;
        }

    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
    }


    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

}