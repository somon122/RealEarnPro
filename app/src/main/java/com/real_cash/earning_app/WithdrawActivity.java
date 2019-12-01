package com.real_cash.earning_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WithdrawActivity extends AppCompatActivity {

    private TextView pointTV;
    private EditText paymentNumberET;
    private Spinner spinner;
    private String spinnerValue;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;
    String uId;
    String pushId;
    String phoneNumber;
    int mainPoints;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){

            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        Toolbar toolbar = findViewById(R.id.withdraw_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Withdraw");

        auth = FirebaseAuth.getInstance();
        user= auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        if (user != null){
            uId = user.getUid();
            pushId = myRef.push().getKey();
            balanceControl();

        }

        pointTV = findViewById(R.id.withdrawPoints_id);
        paymentNumberET = findViewById(R.id.paymentNumber_id);
        spinner = findViewById(R.id.spinner_id);

        List<String> paymentSystem = new ArrayList<String>();
        paymentSystem.add("BKash");
        paymentSystem.add("Rocket");
        paymentSystem.add("Mobile Recharge");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, paymentSystem);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                spinnerValue = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void balanceControl() {

        myRef.child(uId).child("MainPoints").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    String value = dataSnapshot.getValue(String.class);
                    mainPoints = Integer.parseInt(value);

                    pointTV.setText("Your Points : "+value);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void SubmitPayment(View view) {

        sentPayment();

    }


    private void sentPayment(){

        phoneNumber = paymentNumberET.getText().toString().trim();

        if (phoneNumber.isEmpty()){

            paymentNumberET.setError("Please enter valid phone number");

        }else {

            if (mainPoints >= 500){
                confirmAlert(spinnerValue,phoneNumber);
            }else {

                problemAlert();

            }



        }



    }

    private void confirmAlert(String name, String number){

        AlertDialog.Builder builder = new AlertDialog.Builder(WithdrawActivity.this);

        builder.setTitle("Confirm Alert!")
                .setMessage("Please check your \n Payment Method name: "+name+" and\n Number is : "+number)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        int countpoint = mainPoints - 500;
                        String lastPoint = String.valueOf(countpoint);
                        myRef.child(uId).child("MainPoints").setValue(lastPoint).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){

                                    String money = "50";

                                    Withdraw withdraw = new Withdraw(spinnerValue,phoneNumber,money);
                                    myRef.child(uId).child("Withdraw").child(pushId).setValue(withdraw)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()){

                                                        completeAlert();

                                                    }else {

                                                        Toast.makeText(WithdrawActivity.this, "Net Connection problem", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });

                                }else {
                                    Toast.makeText(WithdrawActivity.this, "Net connection problem.", Toast.LENGTH_SHORT).show();
                                }


                            }
                        });





                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void completeAlert() {


        final AlertDialog.Builder builder = new AlertDialog.Builder(WithdrawActivity.this);

        builder.setMessage("Congratulation! \nYour withdraw is successfully")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(WithdrawActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                }).setNegativeButton("Go to home", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(WithdrawActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
    private void problemAlert() {


        final AlertDialog.Builder builder = new AlertDialog.Builder(WithdrawActivity.this);

        builder.setTitle("Sorry ..!")
                .setMessage("You have not enough Points")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(WithdrawActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                }).setNegativeButton("Go to help!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

             HelpFragment helpFragment = new HelpFragment();
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.nev_hostFragment,helpFragment)
                        .commit();


            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }




}
