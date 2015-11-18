package com.mycompany.sms;

/**
 * Created by alliekim on 9/15/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mycompany.googlemap.R;
import com.mycompany.wait.waitActivity;


public class SendSMSActivity extends Activity {

    Button buttonSend;
    EditText textPhoneNo;
    EditText textSMS;
    float destLat;
    float destLong;

    //Intent i = new Intent(this, LocationComparator.class);
    //startActivityForResult(i, 1);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        Intent intent = getIntent();
        destLat = intent.getFloatExtra("destLat", 0);
        destLong = intent.getFloatExtra("destLong", 0);

        // locate elements from xml file
        buttonSend = (Button) findViewById(R.id.buttonSend);
        textPhoneNo = (EditText) findViewById(R.id.editTextPhoneNo);
        textSMS = (EditText) findViewById(R.id.editTextSMS);

    }

            public void onSubmit (View v) {
                // get data from elements
                String phoneNo = textPhoneNo.getText().toString();
                String sms = textSMS.getText().toString();
                //start wait activity
                Intent intent = new Intent(this, waitActivity.class);
                intent.putExtra("phoneNo", phoneNo);
                intent.putExtra("message", sms);
                intent.putExtra("destLat", destLat);
                intent.putExtra("destLong", destLong);
                startActivity(intent);
            /*    try {
                    // initialize an SmsManager class called smsManager
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, sms, null, null);
                    Toast.makeText(getApplicationContext(), "SMS Sent!",
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again later!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }*/
            }
        }

