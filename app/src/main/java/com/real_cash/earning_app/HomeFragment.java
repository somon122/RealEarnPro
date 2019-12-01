package com.real_cash.earning_app;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.real_cash.earning_app.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeFragment extends Fragment {


    TextView notify, userName,homeMainPointTV;
    CircleImageView circleImageShowId,task, spin,reward,help;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;

    String uId;
    String pushId;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        task = root.findViewById(R.id.task_id);
        spin = root.findViewById(R.id.spin_id);
        reward = root.findViewById(R.id.reward_id);
        help = root.findViewById(R.id.help_id);
        notify = root.findViewById(R.id.notification);

        auth = FirebaseAuth.getInstance();
        user= auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        userName = root.findViewById(R.id.homeProfileName_id);
        homeMainPointTV = root.findViewById(R.id.homeMainPoint_id);
        circleImageShowId = root.findViewById(R.id.circleImageShowId);
        TextView email = root.findViewById(R.id.homeProfileEmail_id);

        if (user != null){
            uId = user.getUid();
            pushId = myRef.push().getKey();
            notificationMethod();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            userName.setText(user.getDisplayName());
            email.setText(user.getEmail());
            Picasso.get().load(user.getPhotoUrl()).placeholder(R.drawable.person_black_24dp).into(circleImageShowId);

        }








        task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MCQTestFragment mcqTestFragment = new MCQTestFragment();

                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.nev_hostFragment,mcqTestFragment)
                        .commit();


            }
        });

        spin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),Spin_Activity.class));

            }
        });

        reward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getContext(),RewardActivity.class));


            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HelpFragment helpFragment = new HelpFragment();
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.nev_hostFragment,helpFragment)
                        .commit();

            }
        });


        return root;
    }

    private void notificationMethod() {

        myRef.child(uId).child("Notification").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    String value = dataSnapshot.getValue(String.class);

                    notify.setText(value);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        myRef.child(uId).child("MainPoints").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String value = dataSnapshot.getValue(String.class);
                    homeMainPointTV.setText("Total Points: "+value);


                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });






    }


}