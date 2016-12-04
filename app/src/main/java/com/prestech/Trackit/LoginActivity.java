package com.prestech.Trackit;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import TrackitDataObjects.Driver;

/**
 *The LoginActivity is designed to authenticate the user of application to the company's backend database.
 * It provides a mechanism for the driver to input credentials and returns a boolean representing the result
 * of the authentication attempt. The Login class will receive the username and password of the Driver and pass
 * credentials along to the AvtaDataModel package by instantiating a Driver Object.  The Driver Object will create
 * a DatabaseDriver class that connects to the backend database and authenticates the user through the method
 * validateCredentials.  The Login interface will transfer the user to the Home Screen once valid credentials
 * have been confirmed.
 */
public class LoginActivity extends Activity implements View.OnClickListener{

    //reference to activity's view objects
    private Button loginBtn;
    private TextView infoTextView;
    private EditText emailField;
    private EditText passwordField;

    private String email ;
    private String password;



    //log tag
    private String TAG = "SIGN_IN_ERROR";


    //Firebase database objects
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //database references
    private static DatabaseReference mDatabase;

    //
    private static Driver loggedInDriver;


    //reference to intent that will hold user's information
    public final static  String LAST_NAME = "LAST_NAME";
    public final static  String FIRST_NAME = "FIRST_NAME";
    public final static  String EMAIL = "ENAIL";
    public final static  String PASSWORD = "PASSWORD";
    public final static  String PHONE_NUMBER = "PHONE";


    /*********************************************************************
     *This is the activity's life cycle create call back method
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtity_login);


        //initialize the views
        loginBtn = (Button)findViewById(R.id.login_btn);
        emailField = (EditText)findViewById(R.id.login_email_field);
        passwordField = (EditText)findViewById(R.id.login_passwd_field);
        infoTextView = (TextView) findViewById(R.id.login_info_display);


        //register onclick listeners
        loginBtn.setOnClickListener(this);

        //initialize Firebase object
        mAuth = FirebaseAuth.getInstance();


        //register the activity with a firebase authentication listener
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                //create a database user object
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //set mainActivity's FirbaseAuthentication
                MainTabActivity.setFirebaseUser(firebaseAuth);

                //check if the user object contains user information
                if(user != null)
                {
                    //getRerence to user's information from the database
                    mDatabase = FirebaseDatabase.getInstance().getReference("drivers/" + user.getUid());

                    //register the database reference with an even listener
                    mDatabase.addValueEventListener(dataValueEventListener);


                }//if ends
                else
                {
                    Log.w(TAG, "User is null");
                }//else ends

            }//onAuthStateChanged() Ends
        };//mAuthListener Ends

        //location permission (move to previous class)

        SystemPermission.requestLocationPermission(LoginActivity.this);

    }//create() Ends



    /************************************************************************
     *This is the activity lifecycle start callback method
     */
    @Override
    public void onStart() {
        super.onStart();

        //register the authListener when activity starts
        mAuth.addAuthStateListener(mAuthListener);

    }//onStart() Ends

    /************************************************************************
     *This is the activity lifecycle stop callback method
     */
    @Override
    public void onStop() {
        super.onStop();

        //remove the authentication listener when activity is stopped
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }//if ends

    }//onStop() Ends




    /***************************************************************************
     * This is method registers the login button with the onClick Event
     */
    @Override
    public void onClick(View view)
    {

        switch (view.getId())
        {
            case R.id.login_btn:
                email = emailField.getText().toString().trim();
                password = passwordField.getText().toString().trim();

                //call the sign_in method and pass the password and email to it
                if(!email.isEmpty() && !password.isEmpty())
                {
                    infoTextView.setText("");

                    signIn(email, password);

                }else{
                    infoTextView.setText("Email/Username and Password must be entered");
                }

                break;

        }
    }//onClick() Ends


    /*********************************************************************************
     this method signs the user into the application
     */
    private  void signIn(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {

                            infoTextView.setText("invalid credentials");
                        }
                        else
                        {
                            Intent mainActivityIntent = new Intent(LoginActivity.this, MainTabActivity.class);
                            mainActivityIntent.putExtra(EMAIL, loggedInDriver.getEmail());
                            mainActivityIntent.putExtra(LAST_NAME, loggedInDriver.getLastName());
                            mainActivityIntent.putExtra(FIRST_NAME, loggedInDriver.getFirstName());
                            mainActivityIntent.putExtra(PHONE_NUMBER, loggedInDriver.getPhoneNumber());
                            mainActivityIntent.putExtra(PASSWORD, loggedInDriver.getPassword());

                            startActivity(mainActivityIntent);
                        }
                    }
                });
    }//signIn(String email, String password) Ends





    /************************************************************
     */

     ValueEventListener dataValueEventListener = new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {

         //create a Driver Object
         loggedInDriver = new Driver();

        //make sure loggedInDriver object is not null
        if(loggedInDriver != null && dataSnapshot != null){
             //set the Driver attributes
            loggedInDriver.setFirstName(dataSnapshot.child("firstName").getValue().toString());
            loggedInDriver.setLastName(dataSnapshot.child("lastName").getValue().toString());
            loggedInDriver.setEmail(dataSnapshot.child("email").getValue().toString());
            loggedInDriver.setPhoneNumber(dataSnapshot.child("phoneNumber").getValue().toString());
         }//if ends

    }//onDataChange()

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };//dataValueEventListener Ends



}//LoginActivty Class Ends



