package com.mycompany.wait;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
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

import com.mycompany.googlemap.R;



/**
 * Created by Tim on 11/23/15.
 */
public class waitActivity2 extends FragmentActivity{


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

    public boolean sent = false;


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

        //initialize locationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,1000,5,locationListener);


        // Set up view elements
        timer = (Chronometer) findViewById(R.id.chronometer);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);





    }

    @Override
    protected void onResume(){
        super.onResume();
    }


    public void onStart(View view){
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
    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            // Do work with new location. Implementation of this method will be covered later.
            Log.d("Listen", location.toString());

            progressStatus = Math.round(distBetween - destLoc.distanceTo(location));

            // update view to reflect new location
            progressBar.post(new Runnable() {
                public void run() {
                    progressBar.setProgress(progressStatus);
                }
            });

            if (destLoc.distanceTo(location)  > distToSend){

            }
            else if(!sent){
                sendSMS();
                sent = true;
                locationManager.removeUpdates(locationListener);
            }

        }
    };
}