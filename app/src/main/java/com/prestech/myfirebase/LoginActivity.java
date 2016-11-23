package com.prestech.myfirebase;

import android.*;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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

public class LoginActivity extends Activity implements View.OnClickListener{

    //reference to activity's view objects
    private Button loginBtn , mapBtn;
    private TextView signUpTextView;
    private EditText emailField;
    private EditText passwordField;

    private String email ;
    private String password;

    //log tag
    private String TAG = "SIGN_IN_ERROR";


    //Firebase database objects
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;



    /*********************************************************************
     *This is the activity's life cycle create call back method
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //initialize the views
        loginBtn = (Button)findViewById(R.id.login_button);
        emailField = (EditText)findViewById(R.id.login_email_field);
        passwordField = (EditText)findViewById(R.id.login_passwd_field);
        mapBtn = ((Button)findViewById(R.id.map_btn));

        //register onclick listeners
        loginBtn.setOnClickListener(this);

        mapBtn.setOnClickListener(this);

        //initialize Firebase object
        mAuth = FirebaseAuth.getInstance();


        //register the activity with a firebase authentication listener
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                //create a database user object
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if the user object contains user information
                if(user != null)
                {
                    //user is signed in
                    Log.w(TAG, "User is signed in");

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
            case R.id.login_button:
                email = emailField.getText().toString().trim();
                password = passwordField.getText().toString().trim();

                //call the sign_in method and pass the password and email to it
                signIn(email, password);

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

                            Log.w(TAG, "Login Failed "+task.isSuccessful());
                        }
                        else
                        {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                    }
                });
    }//signIn(String email, String password) Ends


}//LoginActivty Class Ends



