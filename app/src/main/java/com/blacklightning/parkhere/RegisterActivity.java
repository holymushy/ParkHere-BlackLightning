package com.blacklightning.parkhere;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by franc on Oct/31/2017.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    Button buttonRegister;
    EditText firstNameText;
    EditText lastNameText;
    EditText userName;
    EditText phoneNumber;
    EditText emailText;
    EditText password;
    EditText reEnterPassword;

    // Firebase References
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference fBase;
    private FirebaseUser currentUser;

    private ProgressDialog progressDialog;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Establish Database and User
        fBase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        buttonRegister = (Button) findViewById(R.id.bRegister);
        firstNameText = (EditText) findViewById(R.id.firstNameText);
        lastNameText = (EditText) findViewById(R.id.lastNameText);
        userName = (EditText) findViewById(R.id.userName);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        emailText = (EditText) findViewById(R.id.emailText);
        password = (EditText) findViewById(R.id.passwordEditText);
        reEnterPassword = (EditText) findViewById(R.id.reEnterPassword);

        buttonRegister.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);

    }
    private void registerUser(){

        if(!checkFields()){
            return;
        }
        String email = emailText.getText().toString().trim();
        String pw = password.getText().toString().trim();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("Auth State", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("Auth State", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        firebaseAuth.addAuthStateListener(authListener);

        progressDialog.setMessage("Registering...");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email,pw)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registered.", Toast.LENGTH_LONG).show();
                            //return;
                            currentUser = firebaseAuth.getCurrentUser();
                            Log.d("User after: ", currentUser.getUid());
                            createProfile();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "Registration Failed. Try Again.", Toast.LENGTH_LONG).show();
                            //return;
                        }
                    }
                });
        currentUser = firebaseAuth.getCurrentUser();

        if(currentUser != null) {
            Log.d("User registered: ", currentUser.getUid());
        } else {
            Log.d("Auth status:", "user is null");
        }
    }

    @Override
    public void onClick(View view) {
        if(view == this.buttonRegister){
            registerUser();
            Log.d("Skipped creation:", "dammit");
        }
    }

    public void createProfile() {
        //Reconfirm User
        Log.d("Logged in User is", currentUser.getUid());
        //Registration successful, let's add the user's info into the database

        fBase.child("users").child(currentUser.getUid()).child("userName").setValue(userName.getText().toString());
        fBase.child("users").child(currentUser.getUid()).child("emailText").setValue(emailText.getText().toString());
        fBase.child("users").child(currentUser.getUid()).child("firstNameText").setValue(firstNameText.getText().toString());
        fBase.child("users").child(currentUser.getUid()).child("lastNameText").setValue(lastNameText.getText().toString());
        fBase.child("users").child(currentUser.getUid()).child("phoneNumber").setValue(phoneNumber.getText().toString());
    }

    private boolean checkFields(){
        boolean clear = true;
        String email = emailText.getText().toString().trim();
        String pw = password.getText().toString().trim();
        String fName = firstNameText.getText().toString().trim();
        String lName = lastNameText.getText().toString().trim();
        String pw2 = reEnterPassword.getText().toString().trim();
        String phoneNo = phoneNumber.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(RegisterActivity.this, "Please Enter an email.", Toast.LENGTH_LONG).show();
            clear=false;
            return clear;
        }
        if(TextUtils.isEmpty(pw)){
            Toast.makeText(RegisterActivity.this, "Please Enter a password", Toast.LENGTH_LONG).show();
            clear=false;
            return clear;
        }
        if(pw.length()<8){
            Toast.makeText(RegisterActivity.this, "Minimum Password requirement: 8 Characters long.", Toast.LENGTH_LONG).show();
            clear=false;
            return clear;
        }
        if(!pw.equals(pw2)) {
            Toast.makeText(RegisterActivity.this, "Passwords do not match.", Toast.LENGTH_LONG).show();
            clear = false;
            return clear;
        }
        if(TextUtils.isEmpty(fName)){
            Toast.makeText(RegisterActivity.this, "Please Enter a first name.", Toast.LENGTH_LONG).show();
            clear=false;
            return clear;
        }
        if(TextUtils.isEmpty(lName)) {
            Toast.makeText(RegisterActivity.this, "Please Enter a last  name.", Toast.LENGTH_LONG).show();
            clear = false;
            return clear;
        }
        if(TextUtils.isEmpty(phoneNo)||phoneNo.length()!=10){
            Toast.makeText(RegisterActivity.this, "Please Enter a valid 10 digit phone Number.", Toast.LENGTH_LONG).show();
            clear=false;
            return clear;
        }
        return clear;
    }
}









