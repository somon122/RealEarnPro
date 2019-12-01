package com.real_cash.earning_app.LogIn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.real_cash.earning_app.MainActivity;
import com.real_cash.earning_app.R;

public class LogInActivity extends AppCompatActivity {


    Button signUpButton, logInButton;
    EditText emailEditText, passwordEditText;
    FirebaseAuth auth;
    FirebaseUser user;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        signUpButton = findViewById(R.id.gotoSignUpPageId);
        logInButton = findViewById(R.id.logInId);
        emailEditText = findViewById(R.id.logInEmailId);
        passwordEditText = findViewById(R.id.logInPasswordId);


      /*  Bundle bundle = getIntent().getExtras();

        if (bundle != null){

            emailVerificationAlert();

        }*/

        dialog = new ProgressDialog(this);

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLogIn();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(LogInActivity.this,SignUpActivity.class));
            }
        });


    }


    private void emailVerificationAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);

        builder.setTitle("Email Verification Alert")
                .setMessage("Please check your email and confirm email for access this app ")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                finish();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }


    private void isLogIn() {

        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Please Enter Valid Email Address");
        } else if (password.isEmpty()) {
            passwordEditText.setError("Please Enter Valid Password");
        } else {

            dialog.show();
            dialog.setMessage("LogIn is progressing ...");

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                dialog.dismiss();
                                Toast.makeText(LogInActivity.this, "LogIn is successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LogInActivity.this, MainActivity.class));
                                finish();

                            } else {
                                dialog.dismiss();
                                Toast.makeText(LogInActivity.this, "Email and Password is not match", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LogInActivity.this, "please check your net connection", Toast.LENGTH_SHORT).show();
                }
            });

        }


    }

    private void checkEmailVerification(){

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Boolean isVerified = firebaseUser.isEmailVerified();


        if (isVerified){
                dialog.dismiss();
                Toast.makeText(LogInActivity.this, "LogIn is successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LogInActivity.this, MainActivity.class));
                finish();


        }else {
            Toast.makeText(this, "Please verify your email first ", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(LogInActivity.this, LogInActivity.class));
        }

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
