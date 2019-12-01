package com.real_cash.earning_app.LogIn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.real_cash.earning_app.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

public class SignUpActivity extends AppCompatActivity {



    Button signUpButton,logInButton,selectImage;
    EditText emailEditText,passwordEditText,confirmPasswordET,userNameET;
    ProgressDialog dialog;

    ImageView imageView;
    FirebaseAuth auth;
    private StorageReference mStorageRef;

    private Uri imageUri = null;
    private Bitmap compressedImageFile;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpButton = findViewById(R.id.signUpButton_id);
        logInButton = findViewById(R.id.logInButton_id);
        selectImage = findViewById(R.id.selectedImage);

        emailEditText = findViewById(R.id.signUpUsernameId);
        passwordEditText = findViewById(R.id.signUpPasswordId);
        confirmPasswordET = findViewById(R.id.confirmPasswordId);

        userNameET = findViewById(R.id.userName_id);
        imageView = findViewById(R.id.signUp_imageView_id);


        auth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("AccountImage");

        dialog = new ProgressDialog(this);


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRegister();


            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
                finish();
                Toast.makeText(SignUpActivity.this, "call right", Toast.LENGTH_SHORT).show();

            }
        });


        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CropImage.activity()
                        .setMinCropResultSize(1080,1920)
                        .setAspectRatio(1,1)
                        .setAutoZoomEnabled(true)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SignUpActivity.this);


            }
        });

    }

        private void isRegister()
        {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordET.getText().toString();

            final String userName = userNameET.getText().toString();

            if (TextUtils.isEmpty(email)){
                emailEditText.setError("Please Enter Valid Email Address");
            }
            else if (TextUtils.isEmpty(password)){
                passwordEditText.setError("Please Enter Valid Password");
            }
            else if (TextUtils.isEmpty(confirmPassword)){
                confirmPasswordET.setError("Please enter matching Password by above");
            }
            else if (TextUtils.isEmpty(userName)){
                userNameET.setError("Please enter Name");
            }
            else {

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                if (firebaseUser == null){

                    if (!password.equals(confirmPassword))
                    {
                        Toast.makeText(SignUpActivity.this, "Confirm Password could not match ", Toast.LENGTH_SHORT).show();

                    }else {


                        if (imageUri != null) {

                            dialog.show();
                            dialog.setMessage("Register is progressing ...");

                            auth.createUserWithEmailAndPassword(email, confirmPassword)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {

                                                 user = auth.getCurrentUser();


                                                File newImageFile = new File(imageUri.getPath());


                                                try {
                                                    compressedImageFile = new Compressor(SignUpActivity.this)
                                                            .setMaxWidth(720)
                                                            .setMaxHeight(570)
                                                            .setQuality(75)
                                                            .compressToBitmap(newImageFile);

                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                                byte[] newImageData = baos.toByteArray();

                                                final StorageReference imageName = mStorageRef.child(imageUri.getLastPathSegment()).child(".jpg");


                                                imageName.putBytes(newImageData)
                                                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                                                if (task.isSuccessful()){

                                                                    imageName.getDownloadUrl()
                                                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                                @Override
                                                                                public void onSuccess(Uri uri) {


                                                                                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                                                                            .setDisplayName(userName)
                                                                                            .setPhotoUri(uri)
                                                                                            .build();
                                                                                    user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {


                                                                                            if (task.isSuccessful()){

                                                                                                dialog.dismiss();
                                                                                                auth.signOut();
                                                                                                Intent intent = new Intent(SignUpActivity.this,LogInActivity.class);
                                                                                                intent.putExtra("alert","alert");
                                                                                                startActivity(intent);
                                                                                                finish();

                                                                                            }else {
                                                                                                dialog.dismiss();
                                                                                                Toast.makeText(SignUpActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                                                                                            }



                                                                                        }
                                                                                    });



                                                                                }
                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception exception) {

                                                                            dialog.dismiss();

                                                                        }
                                                                    });





                                                                }else {

                                                                    dialog.dismiss();
                                                                    Toast.makeText(SignUpActivity.this, "Upload is Field", Toast.LENGTH_SHORT).show();
                                                                }



                                                            }
                                                        });



                                            } else {
                                                dialog.dismiss();
                                                Toast.makeText(SignUpActivity.this, "Email and Password could not Valid", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }else {

                            Toast.makeText(this, "Please select Image ", Toast.LENGTH_SHORT).show();
                        }

                    }

                }else {
                    dialog.dismiss();
                    Toast.makeText(SignUpActivity.this, "You have already Registered", Toast.LENGTH_SHORT).show();
                }



            }



        }

        private void sentEmailVerification(){

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            if (firebaseUser != null){

                firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "SignUp is Successfully", Toast.LENGTH_SHORT).show();
                            auth.signOut();
                            Intent intent = new Intent(SignUpActivity.this,LogInActivity.class);
                            intent.putExtra("alert","alert");
                            startActivity(intent);
                            finish();

                        }else {
                            dialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "hello", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(SignUpActivity.this, "Connection is problem", Toast.LENGTH_SHORT).show();
                    }
                });
            }


        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                imageView.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Problem "+error, Toast.LENGTH_SHORT).show();
            }
        }
    }



}
