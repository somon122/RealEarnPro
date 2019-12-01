package com.real_cash.earning_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
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
import java.util.Random;

public class Spin_Activity extends AppCompatActivity {



    private InterstitialAd mInterstitialAd;
    private AdView mAdView;


    private Button tapButton;
    private TextView scoreShow, tapCount;
    private SharedPreferences myScoreStore;

    private Random r;
    private int degree = 0, degree_old = 0;
    private static final float FACTOR = 15f;
    private ImageView imageView;

    private MediaPlayer player;

    private int score = 0;

    FirebaseAuth auth;
    FirebaseUser user;

    private static final long START_TIME_IN_MILLIS = 50000;

    //private static final long START_TIME_IN_MILLIS = 3657000;

    private TextView waitingTV;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    private long mEndTime;

    private CountDownTimer countDownTimer;
    private long timeLeft = 10000;
    private boolean timeRunning;
    private String timeText;
    private ProgressBar progressBar;

    private ControlClass controlClass;
    private int showScore;



    SharedPreferences.Editor editor;
    int waitingScore;
    int lastScore;


    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String uId;


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
        setContentView(R.layout.activity_spin_);

        Toolbar toolbar = findViewById(R.id.spin_Toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Spinning Task");


        if (haveNetwork()){

            mAdView = findViewById(R.id.adView);
            tapButton = findViewById(R.id.tapButton_id);
            imageView = findViewById(R.id.imageView);
            scoreShow = findViewById(R.id.scoreTV_id);
            waitingTV = findViewById(R.id.waiting_id);
            progressBar= findViewById(R.id.progressBar);
            tapCount = findViewById(R.id.showScore);
            inisilization();


        }else {
            Toast.makeText(this, "Net connection is problem ", Toast.LENGTH_SHORT).show();

        }




    }

    private void inisilization() {

        waitingTV.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        tapButton.setEnabled(false);
        controlClass = new ControlClass(this);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        if (user != null){
            uId = user.getUid();
            balanceControl();
            startTimerLoad();
        }
        MobileAds.initialize(this,getString(R.string.test_AppUnitId));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.test_InterstitialAdUnit));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        r = new Random();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            waitingScore++;
            // Toast.makeText(getContext(), waitingScore, Toast.LENGTH_SHORT).show();

        }

        tapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //startTimer();


                {
                    degree_old = degree % 360;
                    degree = r.nextInt(3600) + 720;

                    RotateAnimation animationRotate = new RotateAnimation(degree_old,degree,RotateAnimation.RELATIVE_TO_SELF,
                            0.5f,RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                    animationRotate.setDuration(3600);
                    animationRotate.setFillAfter(true);
                    animationRotate.setInterpolator(new DecelerateInterpolator());
                    animationRotate.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                            tapButton.setEnabled(false);

                            if (player == null){
                                player = MediaPlayer.create(Spin_Activity.this,R.raw.sound);
                                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        stopPlayer();
                                    }
                                });
                            }
                            player.start();


                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                            stopPlayer();
                            courrentNumber(360 - (degree%360));

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    imageView.startAnimation(animationRotate);

                }

            }
        });

        //scoreShow.setText("Score is "+controlClass.getScore());


        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {


            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {

            }

            @Override
            public void onAdLeftApplication() {


                Toast.makeText(Spin_Activity.this, "You are doing mistake...!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {


                int addScore = lastScore + score;
                String setScore = String.valueOf(addScore);

                myRef.child(uId).child("MainPoints").setValue(setScore).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            gameOver(score);
                        }else {

                            Toast.makeText(Spin_Activity.this, "try again", Toast.LENGTH_SHORT).show();
                            reLoaded_Ads();
                        }


                    }
                });



            }

        });


    }


    private void reLoaded_Ads(){

        AlertDialog.Builder builder = new AlertDialog.Builder(Spin_Activity.this);

        builder.setMessage("Your net connection is slow\n\nCan you try again? ")
                .setCancelable(false)
                .setPositiveButton(" Yes ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        startActivity(new Intent(Spin_Activity.this,Spin_Activity.class));

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


    private void balanceControl() {

        tapCount.setText("Show: "+controlClass.getScore());

        // Read from the database
        myRef.child(uId).child("MainPoints").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String value = dataSnapshot.getValue(String.class);
                    lastScore = Integer.parseInt(value);
                    scoreShow.setText("T.Points: "+value);


                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });



    }

    private void stopPlayer() {
        if (player != null){
            player.release();
            player=null;
        }
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
                updateCountDownText();
                //updateButtons();
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

        if (player != null){
            stopPlayer();

        }
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
                resetTimer();

            }
        }.start();

        mTimerRunning = true;
        //updateButtons();
    }


    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        //updateButtons();
        tapButton.setVisibility(View.VISIBLE);
        tapButton.setEnabled(false);
        waitingTV.setVisibility(View.GONE);
        if (mInterstitialAd.isLoaded()){

            tapButton.setEnabled(true);

        }else {
            reLoaded_Ads();
        }

    }

    private void updateCountDownText() {
        int hour = (int) ((mTimeLeftInMillis/1000) /60) /60;
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d",hour, minutes, seconds);

        if (waitingScore == 1){
            waitingTV.setVisibility(View.VISIBLE);
            tapButton.setVisibility(View.GONE);
            waitingTV.setText("Wait for next Work.."+"\n\n"+timeLeftFormatted);
        }


    }



    private boolean haveNetwork ()
    {
        boolean have_WiFi = false;
        boolean have_Mobile = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

        for (NetworkInfo info : networkInfo){

            if (info.getTypeName().equalsIgnoreCase("WIFI"))
            {
                if (info.isConnected())
                {
                    have_WiFi = true;
                }
            }
            if (info.getTypeName().equalsIgnoreCase("MOBILE"))

            {
                if (info.isConnected())
                {
                    have_Mobile = true;
                }
            }

        }
        return have_WiFi || have_Mobile;

    }



    private String courrentNumber (int degrees){
        String text = "";

        if (degrees >= (FACTOR *1) && degrees < (FACTOR * 3)){

            score = score+1;
            mInterstitialAd.show();

        } if (degrees >= (FACTOR *3) && degrees < (FACTOR * 5)){

            score = score+2;
            mInterstitialAd.show();

        } if (degrees >= (FACTOR *5) && degrees < (FACTOR * 7)){

            score = score+3;
            mInterstitialAd.show();

        } if (degrees >= (FACTOR *7) && degrees < (FACTOR * 9)){

            score = score+4;
            mInterstitialAd.show();

        } if (degrees >= (FACTOR *9) && degrees < (FACTOR * 11)){

            score = score+5;
            mInterstitialAd.show();


        } if (degrees >= (FACTOR *11) && degrees < (FACTOR * 13)){

            score = score+6;
            mInterstitialAd.show();

        } if (degrees >= (FACTOR *13) && degrees < (FACTOR * 15)){

            score = score+7;
            mInterstitialAd.show();

        } if (degrees >= (FACTOR *15) && degrees < (FACTOR * 17)){

            score = score+8;
            mInterstitialAd.show();

        } if (degrees >= (FACTOR *17) && degrees < (FACTOR * 19)){

            score = score+9;
            mInterstitialAd.show();

        } if (degrees >= (FACTOR *19) && degrees < (FACTOR * 21)){

            score = score+10;
            mInterstitialAd.show();

        } if (degrees >= (FACTOR *21) && degrees < (FACTOR * 23)){

            score = score+11;
            mInterstitialAd.show();

        }

        if ((degrees >= (FACTOR * 23 ) && degrees < 360) || (degrees >= 0 && degrees < (FACTOR * 1)))
        {

            score = score+12;
            mInterstitialAd.show();

        }

        return text;

    }

    private void gameOver(final int mScore){

        AlertDialog.Builder builder = new AlertDialog.Builder(Spin_Activity.this);

        builder.setMessage("Congratulation..!"+"\n\n"+"You Got : "+mScore+" point"+
                "\n\n"+" Click Ok For Continue Game ..." +
                "\n")
                .setCancelable(false)
                .setPositiveButton(" Ok ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        if (controlClass.getScore() >= 20){

                            Intent intent = new Intent(Spin_Activity.this,ClickActivity.class);
                            intent.putExtra("spin","spin");
                            startActivity(intent);

                        }else {

                            showScore++;
                            int tapCounter = controlClass.getScore()+showScore;
                            controlClass.setStoreScore(tapCounter);

                            startActivity(new Intent(Spin_Activity.this,Spin_Activity.class));

                        }

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void startTimerLoad() {
        if (timeRunning){
            stopTime();
        }else {
            startTime();
        }

    }


    private void startTime() {
        countDownTimer = new CountDownTimer(timeLeft,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft =millisUntilFinished;
                updateTimer();

            }

            @Override
            public void onFinish() {
                if (mInterstitialAd.isLoaded()){
                    progressBar.setVisibility(View.GONE);
                    tapButton.setEnabled(true);
                }else {
                    progressBar.setVisibility(View.GONE);
                    reLoaded_Ads();

                }



            }
        }.start();
        timeRunning = true;
        //startBtn.setText("Pause");

    }

    private void updateTimer() {

        int minutes = (int) (timeLeft /60000);
        int seconds = (int) (timeLeft % 60000 /1000);
        timeText = ""+minutes;
        timeText += ":";
        if (seconds <10)timeText += "0";
        timeText +=seconds;
        progressBar.setVisibility(View.VISIBLE);
    }

    private void stopTime() {
        countDownTimer.cancel();
        timeRunning = false;
        // startBtn.setText("Start");



    }





}
