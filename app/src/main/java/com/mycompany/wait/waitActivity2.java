package com.mycompany.wait;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
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
public class waitActivity2 extends AppCompatActivity {


    private static final float mile = 1609;

    private double destLat; //destination latitude
    private double destLong;    //destination longitude

    private float numOfMiles; //number of miles from destination to send text

    private Location currentLoc;  //current Location
    Location destLoc;   //destination location

    private float distBetween; //distance between start and end points in meters
    private float distToSend;  //distance between points when message should be sent (meters)
                                        //default value of 1 mile

    private String phoneNo;
    private String message;
    Chronometer timer;
    ProgressBar progressBar;
    Button  startButton;
    int progressStatus = 0;

    NotificationManager myNotificationManager;
    NotificationCompat.Builder myNotificationBuilder;
    Notification myNotification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait2);

        //mContainerView = (LinearLayout) findViewById(R.id.p)
        // Get values from intent
        Intent intent = getIntent();

        phoneNo = intent.getStringExtra("phoneNo");
        message = intent.getStringExtra("message");
        destLat = intent.getDoubleExtra("destLat", 0);
        destLong = intent.getDoubleExtra("destLong", 0);
        numOfMiles = intent.getFloatExtra("distToSend", 0);

        Log.d("Test", Float.toString(numOfMiles));

        destLoc = new Location(Context.LOCATION_SERVICE);

        Log.d("Test", Double.toString(destLat));



        myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Set up view elements
        timer = (Chronometer) findViewById(R.id.chronometer);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        myNotificationBuilder = new NotificationCompat.Builder(this);

        myNotificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        myNotificationBuilder.setContentTitle("I.M. Here");
        myNotificationBuilder.setContentText("Message Has Been Sent");

        myNotification = myNotificationBuilder.build();

        startButton = (Button) findViewById(R.id.Bstart);




    }

    @Override
    protected void onResume(){
        super.onResume();
    }


    public void onStart(View view){
        // start timer for elapsed time
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();

        distToSend = numOfMiles * mile;


        destLoc.setLongitude(destLong);
        destLoc.setLatitude(destLat);

        Log.d("Test", destLoc.toString());

        // get current location
        currentLoc = getCurrent();

        distBetween = destLoc.distanceTo(currentLoc);

        Log.d("Test",Float.toString(distBetween));


        ((ViewGroup) startButton.getParent()).removeView(startButton);

        progressBar.setMax(Math.round(distBetween - distToSend));
        // start new thread to handle collecting current location
        new Thread(new Runnable() {
            public void run() {



                // while the current location isn't the destination
                while (destLoc.distanceTo(currentLoc)  > distToSend) {


                    // re-evaluate current location
                    currentLoc = getCurrent();
                    Log.d("Test", currentLoc.toString());
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
        }).start();
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

        //initialize locationManager
       LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location myLocation = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        return myLocation;
    }

    protected void sendSMS() {

        try {
            // initialize an SmsManager class called smsManager
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            timer.stop();

            new AlertDialog.Builder(this).setTitle("I.M. Here").setMessage("Your message has been sent!").setNeutralButton("Close", null).show();
            myNotificationManager.notify(1, myNotification);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS failed, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }
}
