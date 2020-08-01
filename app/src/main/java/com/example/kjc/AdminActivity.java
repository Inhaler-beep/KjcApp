package com.example.kjc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.internal.InternalTokenProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;


public class AdminActivity extends AppCompatActivity {
    private EditText UpdateHeading, UpdateBody;
    private Button SendUpdates,UploadImageButton;
    private ImageButton SelectPosImage;
    private DatabaseReference UpdatesRef, CountRef,SlideImageRef;
    private StorageReference PostsImagesRefrence,filePath;
    private FirebaseAuth mAuth;
    String currentuserID, heading, body;
    private long countPosts;
    private static final int count = 0;
    private final int Gallery_Pick = 1;
    private Uri ImageUri;
    private Uri resultUri;
    private ProgressDialog loadingBar;
    private String saveCurrentDate, saveCurrentTime, postRandomName, downloadUrl,sdownloadUrl, current_user_id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        final int SPLASH_TIME = 2000;


        UpdateHeading = (EditText) findViewById(R.id.heading_input);
        UpdateBody = (EditText) findViewById(R.id.body_input);
        SendUpdates = (Button) findViewById(R.id.send_updates_button);
        SelectPosImage = (ImageButton) findViewById(R.id.select_image_button);
        UploadImageButton = (Button) findViewById(R.id.upload_image_button);

        SlideImageRef = FirebaseDatabase.getInstance().getReference().child("SlideImages");

        mAuth = FirebaseAuth.getInstance();
        currentuserID = mAuth.getCurrentUser().getUid();
        loadingBar = new ProgressDialog(this);

        PostsImagesRefrence = FirebaseStorage.getInstance().getReference();
        UpdatesRef = FirebaseDatabase.getInstance().getReference().child("News");
        CountRef = FirebaseDatabase.getInstance().getReference().child("News");

        SendUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                heading = UpdateHeading.getText().toString();
                body = UpdateBody.getText().toString();


                Random random = new Random();
                final int x = (int) (random.nextInt(96) + 32);

                CountRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            countPosts = dataSnapshot.getChildrenCount();

                        } else {
                            countPosts = 0;
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                HashMap updatesMap = new HashMap();
                updatesMap.put("heading", heading);
                updatesMap.put("body", body);
                updatesMap.put("counter", countPosts);


                UpdatesRef.child(currentuserID + x).updateChildren(updatesMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AdminActivity.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(AdminActivity.this, "Error Occured" + message, Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });



        SelectPosImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                OpenGallery();
            }
        });

        UploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                ValidatePostInfo();
            }
        });





            }


    private void ValidatePostInfo()
    {



        if(resultUri == null)
        {
            Toast.makeText(this, "Please select slide image...", Toast.LENGTH_SHORT).show();
        }

        else
        {

            StoringImageToFirebaseStorage();
        }
    }



    private void StoringImageToFirebaseStorage()
    {


        postRandomName = Long.toString(countPosts);

        SlideImageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    countPosts = dataSnapshot.getChildrenCount();
                    SavetoDatabase();
                }
                else
                {
                    countPosts =0;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


         filePath = PostsImagesRefrence.child("SlideImages").child(resultUri.getLastPathSegment() + postRandomName + ".jpg");


        filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
            {
                if(task.isSuccessful())
                {

                   filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri)
                        {

                            downloadUrl = uri.toString();
                        }
                    });


                    Toast.makeText(AdminActivity.this, "Successfully uploaded ", Toast.LENGTH_LONG).show();
                    SavingPostInformationToDatabase();

                }
                else
                {
                    String message = task.getException().getMessage();
                    Toast.makeText(AdminActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    private void SavingPostInformationToDatabase()
    {
        SlideImageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    countPosts = dataSnapshot.getChildrenCount();
                    SavetoDatabase();
                }
                else
                {
                    countPosts =0;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        SavetoDatabase();


    }

    private void SavetoDatabase()
    {
        HashMap imageMap = new HashMap();
        imageMap.put("image",downloadUrl);
        imageMap.put("date",saveCurrentDate);


        SlideImageRef.child(postRandomName).updateChildren(imageMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if (task.isSuccessful())
                {
                    Toast.makeText(AdminActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
                else
                {
                    String message = task.getException().toString();
                    Toast.makeText(AdminActivity.this, "Error occurred"+message, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }







}
