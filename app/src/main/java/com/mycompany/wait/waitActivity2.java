package com.mycompany.wait;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private float distToSend;  //distance between points when message should be sent (meters)

    private float numOfMiles; //number of miles from destination to send text
    private static final float mile = 1609; //value of a mile in meters


    private String phoneNo;
    private String message;
    Chronometer timer;
    ProgressBar progressBar;
    int progressStatus = 0;
    Button startButton;

    LocationManager locationManager;

    public boolean sent = false;

    NotificationManager myNotificationManager;
    NotificationCompat.Builder myNotificationBuilder;
    Notification myNotification;


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
        numOfMiles = intent.getFloatExtra("distToSend", 0);

        destLoc = new Location(Context.LOCATION_SERVICE);

        Log.d("Test", Double.toString(destLat));

        //initialize locationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,1000,5,locationListener);

        //initialize notificationManager
        myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //initialize notification builder
        myNotificationBuilder = new NotificationCompat.Builder(this);
        myNotificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        myNotificationBuilder.setContentTitle("I.M. Here");
        myNotificationBuilder.setContentText("Your Message Has Been Sent");

        myNotification = myNotificationBuilder.build();


        // Set up view elements
        timer = (Chronometer) findViewById(R.id.chronometer);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        startButton = (Button) findViewById(R.id.Bstart);





    }

    @Override
    protected void onResume(){
        super.onResume();
    }


    public void onBegin(View view){

        //calculate dist from destination to send sms
        distToSend = numOfMiles * mile;

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

        ((ViewGroup) startButton.getParent()).removeView(startButton);

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
            new AlertDialog.Builder(this).setTitle("I.M. Here").setMessage("Message Sent!").setNeutralButton("Close", null).show();
            myNotificationManager.notify(1, myNotification);
            sent = true;

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS failed",
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
                locationManager.removeUpdates(locationListener);
            }

        }
    };
}