package com.real_cash.earning_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class RewardActivity extends AppCompatActivity implements  RewardedVideoAdListener {


    private void loadRewardedVideoAd() {

        mRewardedVideoAd.loadAd(getString(R.string.test_RewardedVideoUnit),
                new AdRequest.Builder().build());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){

            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private RewardedVideoAd mRewardedVideoAd;
    ImageView imageView;
    TextView maainBalanceTV;
    SharedPreferences.Editor editor;

    //private static final long START_TIME_IN_MILLIS = 20000;

    private static final long START_TIME_IN_MILLIS = 3657000;

    private TextView waitingTV;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    private long mEndTime;



    ProgressDialog dialog;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String uId;

    FirebaseAuth auth;
    FirebaseUser user;

    int lastScore;
    int score;
    int waitingScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);


        Toolbar toolbar = findViewById(R.id.rewardToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        maainBalanceTV = findViewById(R.id.rewardMainBalance);

        dialog = new ProgressDialog(this);
        dialog.show();
        dialog.setMessage("Reward Video is loading...");

        setTitle("Reward Video");


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        if (user != null){
            uId = user.getUid();
            balanceControl();
        }


        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();

        imageView = findViewById(R.id.RewardImageView2);
        waitingTV = findViewById(R.id.waitingTV_id);
        waitingTV.setVisibility(View.GONE);

        imageView.setVisibility(View.GONE);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRewardedVideoAd.isLoaded()){
                    mRewardedVideoAd.show();

                }
            }
        });


    }

    private void balanceControl() {

        myRef.child(uId).child("MainPoints").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String value = dataSnapshot.getValue(String.class);
                    lastScore = Integer.parseInt(value);
                    maainBalanceTV.setText("T.Points: "+value);


                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });


    }




    @Override
    public void onRewardedVideoAdLoaded() {

        if (mTimerRunning){
            Toast.makeText(this, "Try after time", Toast.LENGTH_SHORT).show();
            dialog.dismiss();

        }else {
            imageView.setVisibility(View.VISIBLE);
            dialog.dismiss();

        }


    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

        score = 5;
        int addScore = lastScore + score;
        String setScore = String.valueOf(addScore);

        myRef.child(uId).child("MainPoints").setValue(setScore).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    waitingScore++;
                    gameOver(score);

                }else {

                    startTimer();
                    Toast.makeText(RewardActivity.this, "try again", Toast.LENGTH_SHORT).show();


                }

            }
        });


    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {


    }



    private void gameOver(final int mScore){

        AlertDialog.Builder builder = new AlertDialog.Builder(RewardActivity.this);

        builder.setMessage("Congratulation..!"+"\n\n"+"You Got : "+mScore+" point"+
                "\n\n"+" Click Ok For Continue Quiz ..." +
                "\n")
                .setCancelable(false)
                .setPositiveButton(" Ok ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        startTimer();



                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();


    }



    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        mTimeLeftInMillis = prefs.getLong("millisLeft", START_TIME_IN_MILLIS);
        mTimerRunning = prefs.getBoolean("timerRunning", false);



        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                //updateCountDownText();
                resetTimer();
            } else {
                waitingScore++;
                startTimer();
            }
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            startTimer();

        }


    }


    @Override
    public void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        editor = prefs.edit();
        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);
        editor.apply();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                //updateButtons();
                waitingScore=0;
                resetTimer();

            }
        }.start();

        mTimerRunning = true;
    }


    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();



    }

    private void updateCountDownText() {
        int hour = (int) ((mTimeLeftInMillis/1000) /60) /60;
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d",hour, minutes, seconds);


        if (waitingScore >=1){
            waitingTV.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            waitingTV.setText("Wait for next Work.."+"\n"+timeLeftFormatted);
        }else {

            waitingTV.setVisibility(View.GONE);
            loadRewardedVideoAd();

        }



    }

    private void reLoaded_Ads(){

        AlertDialog.Builder builder = new AlertDialog.Builder(RewardActivity.this);

        builder.setMessage("Your net connection is slow\n\nCan you try again? ")
                .setCancelable(false)
                .setPositiveButton(" Yes ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        startActivity(new Intent(RewardActivity.this,RewardActivity.class));

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();


            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }



}
