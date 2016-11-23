package com.prestech.myfirebase;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpForm extends AppCompatActivity implements  OnClickListener

{

    //views from xml
    private Button signUpBtn;
    private EditText emailField;
    private EditText passwordField;

    //password and email
    private String password;
    private String email;

    //firebase objects
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_form);

        //initializes the views
        signUpBtn = (Button) findViewById(R.id.sign_up_btn);
        emailField = (EditText)findViewById(R.id.sign_up_email_field);
        passwordField = (EditText)findViewById(R.id.sign_up_passwd_field);

        //register button
        signUpBtn.setOnClickListener(this);

        //instantiates firebase object
        mAuth = FirebaseAuth.getInstance();


        //register a firebaseAuth listener
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null)
                {
                    Toast.makeText(SignUpForm.this, "Login Succeeded ", Toast.LENGTH_LONG);

                }
                else
                {
                    //user signout
                }
            }
        };
    }//onCreate() Ends


    private void signUp()
    {
        email = emailField.getText().toString();
        password = passwordField.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpForm.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(SignUpForm.this, "Signup Failed", Toast.LENGTH_LONG);
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.sign_up_btn:
                signUp();
                break;
        }
    }
}//SignUpForm() Ends
