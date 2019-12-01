package com.real_cash.earning_app.OpeningScreen;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.real_cash.earning_app.MainActivity;
import com.real_cash.earning_app.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class OpenFragment extends Fragment {

    public OpenFragment() {
    }

    private ProgressBar progressBar;
    private int progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_open, container, false);



        progressBar = root.findViewById(R.id.progressBar);


        CircleImageView openImageView_id = root.findViewById(R.id.openImageView_id);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            Picasso.get().load(user.getPhotoUrl()).placeholder(R.drawable.person_black_24dp).into(openImageView_id);
        }



            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    doTheWork();
                    startApp();
                }
            });
            thread.start();


        return root;

    }

    private void startApp() {
        Intent intent = new Intent(getContext(),MainActivity.class);
        intent.putExtra("alert","alert");
        startActivity(intent);

    }

    private void doTheWork() {

        for (progress = 25; progress <= 100; progress = progress+25){
            try {
                Thread.sleep(1000);
                progressBar.setProgress(progress);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }




    }


}
