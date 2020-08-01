package com.example.kjc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


public class RegisterActivity extends AppCompatActivity {
    private Button setupButton;
    private EditText setupFullname,setupMobileNumber;
    private CircleImageView setupProfileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef,ImagedataRef;
    private StorageReference UserProfileImageRef,ImagestorageRef;
    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;
    private Spinner spinner1,spinner2;
    String[] countries = { "India","South Africa","UAE","Gulf Country" };
    private static final String FILENAME = "Example.txt";




    String currentUserID, Gender, fullname,mobilenumber,Course,Section;
    private ProgressDialog loadingBar;
    final static int Gallery_Pick = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile_image");

        setupButton = (Button) findViewById(R.id.setup_save_button);
        setupFullname = (EditText) findViewById(R.id.setup_fullname);
        addListenerOnSpinnerItemSelection();

        radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);
        setupProfileImage = (CircleImageView) findViewById(R.id.setup_profile_image);
        setupMobileNumber = (EditText) findViewById(R.id.setup_mobile_number);
        loadingBar = new ProgressDialog(this);


        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveAccountSetupInformation();
            }
        });

        setupProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);


            }
        });

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("profileimage")) {


                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.with(RegisterActivity.this).load(image).placeholder(R.drawable.profile).into(setupProfileImage);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Please Select an Image", Toast.LENGTH_SHORT).show();
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addListenerOnSpinnerItemSelection()
    {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Section = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               Course = parent.getItemAtPosition(position).toString();
                Toast.makeText(RegisterActivity.this, Course, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // some conditions for the picture
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            Uri ImageUri = data.getData();
            // crop the image
            CropImage.activity(ImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        // Get the cropped image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {       // store the cropped image into result
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait, while we updating your profile image and setting the thumb image...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();


                Uri resultUri = result.getUri();

                File thumb_filePath = new File(resultUri.getPath());
                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxHeight(200)
                        .setMaxWidth(200)
                        .setQuality(50)
                        .compressToBitmap(thumb_filePath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();


                final StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");
                final StorageReference thumb_FilePath = UserProfileImageRef.child("profileimages").child(currentUserID + ".jpg");

                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUrl = uri.toString();

                                UsersRef.child("profileimage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {


                                            UploadTask uploadTask = thumb_FilePath.putBytes(thumb_byte);
                                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                                                {
                                                    thumb_FilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri)
                                                        {
                                                            final String thumbdownloadUrl = uri.toString();
                                                            UsersRef.child("thumbs").setValue(thumbdownloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task)
                                                                {
                                                                    if(task.isSuccessful())
                                                                    {
                                                                        Intent selfIntent = new Intent(RegisterActivity.this, RegisterActivity.class);
                                                                        startActivity(selfIntent);
                                                                        Toast.makeText(RegisterActivity.this, "Image Stored", Toast.LENGTH_SHORT).show();
                                                                        loadingBar.dismiss();

                                                                    }
                                                                    else {
                                                                        String message = task.getException().getMessage();
                                                                        Toast.makeText(RegisterActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                                                                        loadingBar.dismiss();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });


                                        }
                                    }
                                });
                            }


                        });


                    }

                });



            }
            else
            {
                Toast.makeText(this, "Error Occured: Image can not be cropped. Try Again.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }


            }

    }

    private void SaveAccountSetupInformation()
    {
        final String course = Course;
        fullname = setupFullname.getText().toString();
        mobilenumber = setupMobileNumber.getText().toString();
        ImagestorageRef = FirebaseStorage.getInstance().getReference().child("Profile_image");
        int selectedId = radioSexGroup.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        radioSexButton = (RadioButton) findViewById(selectedId);
        Gender = radioSexButton.getText().toString();

        ImagedataRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ImagedataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child(currentUserID).hasChild("profileimage"))
                {

                    if (TextUtils.isEmpty(Gender))
                    {
                        Toast.makeText(RegisterActivity.this,"Please enter your username", Toast.LENGTH_SHORT).show();
                    }
                    else if (TextUtils.isEmpty(fullname))
                    {
                        Toast.makeText(RegisterActivity.this, "Enter your full name", Toast.LENGTH_SHORT).show();

                    }
                    else

                    {
                        if(TextUtils.isEmpty(mobilenumber))
                        {
                            mobilenumber = "Not Provided";
                        }
                        String CapitalizedFullname = fullname.substring(0, 1).toUpperCase() + fullname.substring(1);

                        loadingBar.setTitle("Saving");
                        loadingBar.setMessage("Please Wait while we upload your data");
                        loadingBar.show();
                        loadingBar.setCanceledOnTouchOutside(true);

                        HashMap userMap = new HashMap();
                        userMap.put("fullname", CapitalizedFullname);
                        userMap.put("mobilenumber", mobilenumber);
                        userMap.put("gender", Gender);
                        userMap.put("dob", "");
                        userMap.put("course", Course);
                        userMap.put("section",Section);
                        UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {

                                    SendUserToHomeAcitivty();
                                    Toast.makeText(RegisterActivity.this, "Successfully Uploaded User Data", Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                } else {
                                    String message = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Error Occured" + message, Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                }

                            }
                        });


                    }
                }
                else
                {
                    Toast.makeText(RegisterActivity.this,"Please Upload An Image,You can change it later",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });










    }





    private void SendUserToHomeAcitivty()
    {
        Intent homeIntent = new Intent(RegisterActivity.this,HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }

}






