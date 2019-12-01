package com.real_cash.earning_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.real_cash.earning_app.LogIn.LogInActivity;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    DrawerLayout drawer;
    NavigationView navigationView;
    private HomeFragment homeFragment;





    @Override
    protected void onStart() {

        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent(MainActivity.this, LogInActivity.class));

        }else {
            userProfile();

        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        homeFragment = new HomeFragment();
        maintainFragment(homeFragment);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();

        if (id == R.id.nev_home){

           maintainFragment(homeFragment);

        }
         if (id == R.id.nav_withdraw){

         startActivity(new Intent(MainActivity.this,WithdrawActivity.class));

                }

        if (id == R.id.nev_adminPanel_id){

            adminPanel("123456");

        } if (id == R.id.nav_share){

            shareApp();

        }
        if (id == R.id.nav_rate_us){

            Toast.makeText(MainActivity.this, "Coming soon..", Toast.LENGTH_SHORT).show();

            shareApp();

        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        return false;

            }
        });


    }


    private void userProfile (){

        View userProfileUD = navigationView.getHeaderView(0);
        TextView userName = userProfileUD.findViewById(R.id.profileUserName_id);
        TextView userEmail = userProfileUD.findViewById(R.id.profileUserEmail_id);
        ImageView image = userProfileUD.findViewById(R.id.profileImageView);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        Picasso.get().load(user.getPhotoUrl()).placeholder(getDrawable(R.drawable.person_black_24dp)).into(image);
        userName.setText(user.getDisplayName());
        userEmail.setText(user.getEmail());



    }

    private void adminPanel(final String password) {


        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view1 = getLayoutInflater().inflate(R.layout.admin_control,null);


        final EditText passwordET = view1.findViewById(R.id.adminCheckPassword_id);
        Button submit = view1.findViewById(R.id.adminSubmit_id);


        builder.setTitle("Admin Panel");
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mPassword = passwordET.getText().toString();

                if (mPassword.isEmpty()){

                    passwordET.setError("Please enter password");

                }else {

                    if (mPassword.equals(password)){

                        Toast.makeText(MainActivity.this, "Password is matches", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this,NotificationActivity.class));


                    }else {

                        Toast.makeText(MainActivity.this, "Password is not matches", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        builder.setView(view1);
        AlertDialog dialog = builder.create();
        dialog.show();


    }



    private void shareApp() {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareBody = "App link : https://youtu.be/eGn-2tGoG6s";
        String shareSub = "Make Money by Android App";
        intent.putExtra(Intent.EXTRA_SUBJECT,shareSub);
        intent.putExtra(Intent.EXTRA_TEXT,shareBody);
        startActivity(Intent.createChooser(intent,"Earning App"));

    }

    private void maintainFragment(Fragment fragment){

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.nev_hostFragment,fragment).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_logOut){

          alert();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            finishAffinity();
        } else {
            super.onBackPressed();
        }
    }

    private void alert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you Sure?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(MainActivity.this, "Successfully LogOut ", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),LogInActivity.class));
                        finish();



                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
                Toast.makeText(MainActivity.this, "Thank You for Staying...", Toast.LENGTH_SHORT).show();



            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

}