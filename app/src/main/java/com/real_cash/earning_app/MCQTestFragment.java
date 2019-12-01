package com.real_cash.earning_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
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
import com.real_cash.earning_app.R;

import java.util.Locale;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class MCQTestFragment extends Fragment {


    private RadioButton answerButtonNo1, answerButtonNo2, answerButtonNo3, answerButtonNo4;
    private TextView questionTV,counterTV,mainBalanceTV;

    private Questions questions = new Questions();
    private String mAnswer;
    private int mQuestionsLenght = questions.mQuestions.length;
    private Random r;
    int score;

    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button submit;

    ScoreControl scoreControl;
    private MediaPlayer player;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String uId;
    QuizControlClass quizControlClass;

    FirebaseAuth auth;
    FirebaseUser user;

    int lastScore;

    int waitingScore;
    SharedPreferences.Editor editor;

    //private static final long START_TIME_IN_MILLIS = 20000;

    private static final long START_TIME_IN_MILLIS = 3657000;

    private TextView waitingTV;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    private long mEndTime;

    private InterstitialAd mInterstitialAd;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_mcq_test, container, false);


        r = new Random();

        answerButtonNo1 = root.findViewById(R.id.answerNo1_id);
        answerButtonNo2 =  root.findViewById(R.id.answerNo2_id);
        answerButtonNo3 = root. findViewById(R.id.answerNo3_id);
        answerButtonNo4 =  root.findViewById(R.id.answerNo4_id);
        submit =  root.findViewById(R.id.quizShowSubmit_id);

        questionTV =  root.findViewById(R.id.question_id);
        counterTV =  root.findViewById(R.id.counter_id);
        waitingTV =  root.findViewById(R.id.quizWaitingTV);
        waitingTV.setVisibility(View.GONE);


        mainBalanceTV =  root.findViewById(R.id.quizMainBalance);
        radioGroup = root.findViewById(R.id.quizOptionGroup);
        scoreControl = new ScoreControl();
        quizControlClass = new QuizControlClass(getContext());

        TimeControlClass timeControlClass = new TimeControlClass(getContext());

        if (timeControlClass.getScore() >=10){
            waitingScore++;
            startTimer();
        }



        updateQuestion(r.nextInt(mQuestionsLenght));
        counterTV.setText(quizControlClass.getScore()+"/"+"20");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        if (user != null){
            uId = user.getUid();
            balanceControl();
        }



        MobileAds.initialize(getContext(), getString(R.string.test_AppUnitId));
        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId(getString(R.string.test_InterstitialAdUnit));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


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

                Toast.makeText(getContext(), "You are doing mistake", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onAdClosed() {

                score = 1;
                int addScore = lastScore + score;
                String setScore = String.valueOf(addScore);

                myRef.child(uId).child("MainPoints").setValue(setScore).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            gameOver(score);

                        }else {
                            mInterstitialAd.loadAd(new AdRequest.Builder().build());
                            Toast.makeText(getContext(), "try again", Toast.LENGTH_SHORT).show();
                        }


                    }
                });



            }
        });



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (radioGroup.getCheckedRadioButtonId() == -1){

                    radioGroup.clearCheck();
                    Toast.makeText(getContext(), "Please select any one option", Toast.LENGTH_SHORT).show();



                }else {
                    int radioId = radioGroup.getCheckedRadioButtonId();
                    radioButton = root.findViewById(radioId);
                    String status = radioButton.getText().toString();

                    if (status.equals(mAnswer)){

                        if (mInterstitialAd.isLoaded()){
                            mInterstitialAd.show();
                            radioGroup.clearCheck();
                            play();


                        }else {

                            playSorry();
                            mInterstitialAd.loadAd(new AdRequest.Builder().build());
                            counterTV.setText(quizControlClass.getScore()+"/"+"20");
                            radioGroup.clearCheck();
                            updateQuestion(r.nextInt(mQuestionsLenght));
                        }


                    }else {

                        playWrong();
                        Toast.makeText(getContext(), "Answer is wrong", Toast.LENGTH_SHORT).show();
                        radioGroup.clearCheck();
                        updateQuestion(r.nextInt(mQuestionsLenght));

                    }
                }


            }
        });





        return root;
    }

    private void balanceControl() {

        myRef.child(uId).child("MainPoints").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String value = dataSnapshot.getValue(String.class);
                    lastScore = Integer.parseInt(value);
                    mainBalanceTV.setText("Total Points: "+value);


                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });


    }

    private void playWrong() {

        if (player == null){
            player = MediaPlayer.create(getContext(),R.raw.wrong_ans);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopPlayer();
                }
            });
        }
        player.start();


    }

    private void playSorry() {
        if (player == null){
            player = MediaPlayer.create(getContext(),R.raw.sorry);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopPlayer();
                }
            });
        }
        player.start();

    }




    private void play (){

        if (player == null){
            player = MediaPlayer.create(getContext(),R.raw.carrect_answer);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopPlayer();
                }
            });
        }
        player.start();

    }



    private void stopPlayer() {
        if (player != null){
            player.release();
            player=null;
        }
    }

   private void reload(){

       MCQTestFragment mcqTestFragment = new MCQTestFragment();
       FragmentManager manager = getFragmentManager();
       manager.beginTransaction().replace(R.id.nev_hostFragment,mcqTestFragment)
               .commit();

    }


    private void updateQuestion(int num) {

        questionTV.setText(questions.getQuestion(num));
        answerButtonNo1.setText(questions.getChoices1(num));
        answerButtonNo2.setText(questions.getChoices2(num));
        answerButtonNo3.setText(questions.getChoices3(num));
        answerButtonNo4.setText(questions.getChoices4(num));
        mAnswer = questions.getCarrectAnswer(num);


    }


    private void gameOver(final int mScore){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage("Congratulation..!"+"\n\n"+"You Got : "+mScore+" point"+
                "\n\n"+" Click Ok For Continue Quiz ..." +
                "\n")
                .setCancelable(false)
                .setPositiveButton(" Ok ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                        if (quizControlClass.getScore() >=20){

                            timeIsRunning();

                        }else {

                            int value = quizControlClass.getScore()+mScore;

                           quizControlClass.setStoreScore(value);
                            updateQuestion(r.nextInt(mQuestionsLenght));
                            counterTV.setText(quizControlClass.getScore()+"/"+"20");
                            mInterstitialAd.loadAd(new AdRequest.Builder().build());

                        }





                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void timeIsRunning() {

        TimeControlClass timeControlClass = new TimeControlClass(getContext());
        timeControlClass.setStoreScore(10);

        Intent intent = new Intent(getContext(),ClickActivity.class);
        intent.putExtra("click","click");
        startActivity(intent);
        startActivity(new Intent(getContext(), ClickActivity.class));


    }


    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences prefs = getContext().getSharedPreferences("prefs", MODE_PRIVATE);

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


    }


    @Override
    public void onStop() {
        super.onStop();

        if (player != null){
            stopPlayer();
        }

        SharedPreferences prefs = getContext().getSharedPreferences("prefs", MODE_PRIVATE);
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
            submit.setVisibility(View.GONE);
            waitingTV.setText("Wait for next Work.."+"\n"+timeLeftFormatted);

        }else {
            submit.setVisibility(View.VISIBLE);
            waitingTV.setVisibility(View.GONE);
            mInterstitialAd.loadAd(new AdRequest.Builder().build());


        }



    }



}