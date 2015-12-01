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
import com.mycompany.wait.waitActivity2;


public class SendSMSActivity extends Activity {

    Button buttonSend;
    EditText textPhoneNo;
    EditText textSMS;
    double destLat;
    double destLong;

    //Intent i = new Intent(this, LocationComparator.class);
    //startActivityForResult(i, 1);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        // Get values from intent
        Intent intent = getIntent();
        destLat = intent.getDoubleExtra("destLat", 0);
        destLong = intent.getDoubleExtra("destLong", 0);

        // locate elements from xml file
        buttonSend = (Button) findViewById(R.id.buttonSend);
        textPhoneNo = (EditText) findViewById(R.id.editTextPhoneNo);
        textSMS = (EditText) findViewById(R.id.editTextSMS);

    }

            public void onSubmit (View v) {
                // get data from elements
                String phoneNo = textPhoneNo.getText().toString();
                String sms = textSMS.getText().toString();

                //Log.d("destLat", Float.toString(destLat));
                //start wait activity
                Intent intent = new Intent(this, waitActivity2.class);
                intent.putExtra("phoneNo", phoneNo);
                intent.putExtra("message", sms);
                intent.putExtra("destLat", destLat);
                intent.putExtra("destLong", destLong);
                startActivity(intent);

            }
        }

