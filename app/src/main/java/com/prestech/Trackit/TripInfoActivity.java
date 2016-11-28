package com.prestech.Trackit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

/**
 * This Activity will be responsible for collecting the vehicle's vin and odometer
 * before the the app can begin tracking vehicles movement.
 */
public class TripInfoActivity extends AppCompatActivity implements View.OnClickListener{

    //references to the layout views
    Button startTripBtn;


    /****************************************************************
     * Activity's onCreate call back method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        startTripBtn = (Button)findViewById(R.id.start_trip_btn);


        if(startTripBtn != null)
        {
            startTripBtn.setOnClickListener(this);
        }
    }//onCreate() Ends


    /****************************************************************
     * Implementation of the OnClickListener
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {

            case R.id.start_trip_btn:
                openMapActivity();
                break;
        }//switch
    }//onClick

    /*******************************************************
     This method opens the map activity
     */
    private void openMapActivity()
    {
        Intent mapActivityIntent = new Intent(TripInfoActivity.this, MapsActivity.class);
        startActivity(mapActivityIntent);
    }//openMapActivity Ends


}

