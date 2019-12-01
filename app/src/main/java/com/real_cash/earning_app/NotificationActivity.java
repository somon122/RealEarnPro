package com.real_cash.earning_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationActivity extends AppCompatActivity {


    EditText notificationET;
    Button submit;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;

    String uId;
    String pushId;

    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Toolbar toolbar = findViewById(R.id.notify_Toolbar);
        setSupportActionBar(toolbar);


        setTitle("Notification");

        auth = FirebaseAuth.getInstance();
        user= auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        dialog = new ProgressDialog(this);

        if (user != null){
            uId = user.getUid();
            pushId = myRef.push().getKey();

        }

        notificationET = findViewById(R.id.notifyTV_id);
        submit = findViewById(R.id.submitNotify);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notification();
            }
        });


    }

    private void notification() {

        String notify = notificationET.getText().toString();

        if (notify.isEmpty()){

            notificationET.setError("Enter notification");
        }else {

            dialog.setMessage("Notification is uploading...");
            dialog.show();
            myRef.child(uId).child("Notification").setValue(notify)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()){

                        dialog.dismiss();
                        Toast.makeText(NotificationActivity.this, "Notification submit success", Toast.LENGTH_SHORT).show();

                    }else {
                        dialog.dismiss();
                        Toast.makeText(NotificationActivity.this, "Check net connection", Toast.LENGTH_SHORT).show();
                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    dialog.dismiss();
                    Toast.makeText(NotificationActivity.this, "Check net connection", Toast.LENGTH_SHORT).show();

                }
            });

        }



    }
}
