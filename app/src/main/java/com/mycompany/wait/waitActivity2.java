package com.mycompany.wait;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.model.LatLng;
import com.mycompany.googlemap.R;



/**
 * Created by Tim on 11/23/15.
 */
public class waitActivity2 extends FragmentActivity{


    private double destLat; //destination latitude
    private double destLong;    //destination longitude

    private Location currentLoc;  //current Location



    private String phoneNo; //= new String("8082236901");  //Michele's number
    private String message;
    TextView elapsedTime;
    TextView currentValue;
    Chronometer timer;
    ProgressBar progressBar;
    int progressStatus = 0;

    LocationManager locationManager;


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

        // Set up view elements
        elapsedTime = (TextView) findViewById(R.id.elapsedTime);
        timer = (Chronometer) findViewById(R.id.chronometer);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //initialize locationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


    }

    @Override
    protected void onResume(){
        super.onResume();
    }


    public void onStart(View view){
        // start timer for elapsed time
        timer.start();

        // start new thread to handle collecting current location
        new Thread(new Runnable() {
            public void run() {

                // get current location
                currentLoc = getCurrent();

                // while the current location isn't the destination
                while (currentLoc.getLongitude() != destLong && currentLoc.getLatitude() != destLat ) {


                    // re-evaluate current location
                    currentLoc = getCurrent();

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

        Location myLocation = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
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
}
