package com.prestech.Trackit;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    final static int MY_LOCATION_PERMISSION_REQUEST_CODE = 5;
    final static int LOCATION_SETTINGS_REQUEST = 3;

    //reference to the map object
    private GoogleMap mMap;

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private Location mLastKnownLocation;
    private LocationRequest mLocationRequest;


    private TextView cordTextView;

    PolylineOptions polylineOptions;
    private Polyline polyline;

    /*************************************************************
     * Activity's onCreate() call back method
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        cordTextView = (TextView)findViewById(R.id.cord_display);

        //set up the google Api
         setUpGoogleApi();

        //set up location setting requests
        setUpLocationSettingsRequest();

        //validate location settings
        validateLocationSettings();


    }//onCreate() Ends



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        boolean mlocationPermissionGranted = ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;

        mMap = googleMap;

        polylineOptions = new PolylineOptions();

        if (mlocationPermissionGranted) {

            return;
        }//ends

        //check if Location permission has been granted
        mMap.setMyLocationEnabled(true);

        mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLastKnownLocation != null)
        {
            updateTextView(mLastKnownLocation);
            updateCameraPosition(mCurrentLocation);
        }

    }//onMapReady() Ends


    /*******************************************************************************
     * This is the activity's start callback method
     */
    @Override
    protected void onStart()
    {
        super.onStart();

        //connect to the google API
        mGoogleApiClient.connect();
    }//onStart() Ends


    /*******************************************************************************
     * This is the activity's stop call back method
     */
    @Override
    protected void onStop()
    {
        super.onStop();

        //disconnect from the google API
        mGoogleApiClient.disconnect();

    }//onStop() Ends


    /***********************************************************************************
     * This is A google API Call back method; called when app connects to the API
     */
    @Override
    public void onConnected(Bundle bundle)
    {

        boolean mlocationPermissionGranted = ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;



            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }//onConnected() Ends

    /***********************************************************************************
     * This is a google API callback method; called when is Connection Suspended
     */
    @Override
    public void onConnectionSuspended(int i)
    {

    }//onConnectionSuspended Ends

    /***********************************************************************************
     * This is a Google API callback method; called when connections fails
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {

    }//onConnectionFailed() Ends

    /*********************************************************************************
     *This is a google call back method; called when location changes
     */
    @Override
    public void onLocationChanged(Location location)
    {

        //set the current location
        this.mCurrentLocation = location;

        //update the UI with the current data
        updateTextView(this.mCurrentLocation);

        //move camera to the new location
        updateCameraPosition(this.mCurrentLocation);

        //draw the path
        tracePath(this.mCurrentLocation);
    }//onLocationChanged





    /**********************************************************************************
     *validateLocationSettings() checks if the Location settings are setup as requested
     * by the locationRequest object.
     * If not, it asked permission from the user to set it up
     */
    private void validateLocationSettings()
    {
        //location setting rquest object
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        //process location settings
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());


        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates locationSettingsStates = locationSettingsResult
                        .getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    MapsActivity.this,
                                    MapsActivity.LOCATION_SETTINGS_REQUEST);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });

    }//validateLocationSettings() Ends


    /***************************************************************************
     * This method draws the polyline on the map; tracing the user's path
     */
    private void tracePath(Location mCurrentLocation)
    {

        float mCurrentLat = (float) mCurrentLocation.getLatitude();
        float mCurrentLng = (float) mCurrentLocation.getLongitude();

        this.polylineOptions.add(new LatLng(mCurrentLat, mCurrentLng));
        this.polyline = mMap.addPolyline(this.polylineOptions);

    }

    /**********************************************************************
     * This method computes the number of miles coverred by the trip
     */
    private void computeMiles()
    {

    }


    /**********************************************************************
     *This method updates the information displayed in the UI
     */
    private void updateTextView(Location location)
    {

        String latLngCordinates = "Lat: " + location.getLatitude()+" | lng: "+ location.getLongitude();
        cordTextView.setText(latLngCordinates);

    }//updateTextView() Ends



    /***************************************************************************
     * This method updates the cameer positioning of the map
     */
    private void updateCameraPosition(Location location)
    {
        if(mMap != null)
        {
            LatLng initialLatLng = new LatLng
                    (location.getLatitude(), location.getLongitude());

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(initialLatLng, 16.10f);
            this.mMap.moveCamera(cameraUpdate);

        }//if ends
    }//updateCameraPosition Ends




    /*********************************************************
     * This method sets up the google Api for the application
     */
    private void setUpGoogleApi()
    {
        //setup google Api
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }//setUpGoogleApi() Ends

    /************************************************************
     * This method sets up Location setting requests necessary for
     * the apps functionality
     */
    private void setUpLocationSettingsRequest()
    {
        //setup location Service
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }//setUpLocationSettingsApi() Ends


}
