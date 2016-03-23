package com.mycompany.sms;

/**
 * Created by alliekim on 9/15/15.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mycompany.googlemap.R;
import com.mycompany.wait.waitActivity2;


public class SendSMSActivity extends AppCompatActivity {

    Button buttonSend;
    EditText textPhoneNo;
    EditText textSMS;
    EditText textDistToSend;

    double destLat;
    double destLong;

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
        textDistToSend = (EditText) findViewById(R.id.editTextMiles);


    }

            public void onSubmit (View v) {

                // get data from elements
                String phoneNo = textPhoneNo.getText().toString();
                String sms = textSMS.getText().toString() + "\n\n" + "~Sent using " + R.string.app_name;
                Log.d("test", sms);
                String miles = textDistToSend.getText().toString();


                if(phoneNo.equals("") || sms.equals("") || miles.equals(""));

                else{
                    float numMiles = Float.valueOf(miles);
                    //start wait activity
                    Intent intent = new Intent(this, waitActivity2.class);
                    intent.putExtra("phoneNo", phoneNo);
                    intent.putExtra("message", sms);
                    intent.putExtra("destLat", destLat);
                    intent.putExtra("destLong", destLong);
                    intent.putExtra("distToSend", numMiles);
                    startActivity(intent);
                }


            }
        }

