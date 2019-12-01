package com.real_cash.earning_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class ClickActivity extends AppCompatActivity {



    private TextView showScore;
    private Button clickButton;

    private InterstitialAd mInterstitialAd;
    private int adCount;
    private CountDownTimer countDownTimer;
    private long timeLeft = 50000;
    private boolean timeRunning;
    private String timeText;
    private String spin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click);




        showScore = findViewById(R.id.clickScoreShow_id);
        clickButton = findViewById(R.id.clickButton_id);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            spin = bundle.getString("spin");
            spin = bundle.getString("click");
            daleted();




        }

        MobileAds.initialize(this, getString(R.string.test_AppUnitId));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.test_InterstitialAdUnit));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        clickButton.setVisibility(View.GONE);



        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mInterstitialAd.isLoaded()){
                    mInterstitialAd.show();
                }else {

                    Toast.makeText(ClickActivity.this, " Try Again.. Ok! ", Toast.LENGTH_SHORT).show();

                }


            }
        });

        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {

                clickButton.setVisibility(View.VISIBLE);

                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(ClickActivity.this, "Please try Again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {

            }

            @Override
            public void onAdLeftApplication() {

                startTimer();


            }

            @Override
            public void onAdClosed() {

                if (adCount >=1){

                    Intent intent = new Intent(ClickActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();


                }else {
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    Toast.makeText(ClickActivity.this, " Try Again.. Ok! ", Toast.LENGTH_SHORT).show();

                }


            }

        });


    }

    private void daleted() {


        if (spin.equals("spin")){
            ControlClass controlClass = new ControlClass(ClickActivity.this);
            controlClass.Delete();

        }else {

            QuizControlClass quizControlClass = new QuizControlClass(ClickActivity.this);
            quizControlClass.Delete();

        }


    }


    private void startTimer() {
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
                adCount++;



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
        Toast.makeText(this, "Wait: "+timeText, Toast.LENGTH_SHORT).show();

    }

    private void stopTime() {
        countDownTimer.cancel();
        timeRunning = false;
        // startBtn.setText("Start");



    }




}
