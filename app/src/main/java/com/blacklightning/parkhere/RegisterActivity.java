package com.blacklightning.parkhere;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by franc on Oct/31/2017.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    Button buttonRegister;
    EditText firstNameText;
    EditText lastNameText;
    EditText userName;
    EditText emailText;
    EditText password;
    EditText reEnterPassword;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        buttonRegister = (Button) findViewById(R.id.bRegister);
        firstNameText = (EditText) findViewById(R.id.firstNameText);
        lastNameText = (EditText) findViewById(R.id.lastNameText);
        userName = (EditText) findViewById(R.id.userName);
        emailText = (EditText) findViewById(R.id.emailText);
        password = (EditText) findViewById(R.id.password);
        reEnterPassword = (EditText) findViewById(R.id.reEnterPassword);
        buttonRegister.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
    }
    private void registerUser(){
        String email = emailText.toString().trim();
        String pw = password.toString().trim();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "email is empty", Toast.LENGTH_LONG).show();

        }
        if(TextUtils.isEmpty(pw)){
            Toast.makeText(this, "pw is empty", Toast.LENGTH_LONG).show();

        }
        progressDialog.setMessage("Registering...");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email,pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "reg. succ", Toast.LENGTH_LONG).show();

                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "reg. fail", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }
    @Override
    public void onClick(View view) {
        if(view == this.buttonRegister){
            registerUser();

        }
    }
}









