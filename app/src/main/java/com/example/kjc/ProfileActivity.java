package com.example.kjc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ProgressDialog loadingBar;
    private CircleImageView ProfileView;
    private DatabaseReference ProfileRef,CacheRef;
    private FirebaseAuth mAuth;
    private String currentUserId,Emailid;
    private TextView ProfileGender,ProfileFullname,ProfileDob,ProfileMailID,ProfileMobile,ProfileCourse,ProfileSection;
    public SharedPreferences profilecacheRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        Emailid = mAuth.getCurrentUser().getEmail();
        ProfileRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        ProfileRef.keepSynced(true);


        loadingBar = new ProgressDialog(this);

        mToolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        mToolbar.setTitle("Profile");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ProfileGender = (TextView) findViewById(R.id.profile_gender_textview);
        ProfileCourse = (TextView) findViewById(R.id.profile_course);
        ProfileSection = (TextView) findViewById(R.id.profile_section);
        ProfileFullname = (TextView) findViewById(R.id.profile_full_name);
        ProfileMailID= (TextView) findViewById(R.id.college_email_id);
        ProfileDob = (TextView) findViewById(R.id.profile_dob_textview);
        ProfileMobile = (TextView) findViewById(R.id.profile_mobile_textview);
        ProfileView = (CircleImageView) findViewById(R.id.my_profile_pic);


        ProfileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                if(dataSnapshot.exists())
                {


                    String fullname = dataSnapshot.child("fullname").getValue().toString();
                    String gender = dataSnapshot.child("gender").getValue().toString();
                    String dob = dataSnapshot.child("dob").getValue().toString();
                    final String image = dataSnapshot.child("profileimage").getValue().toString();
                    String mobilenumber = dataSnapshot.child("mobilenumber").getValue().toString();
                    String course = dataSnapshot.child("course").getValue().toString();
                    String section = dataSnapshot.child("section").getValue().toString();

                    ProfileFullname.setText(fullname);
                    ProfileCourse.setText(course);
                    ProfileSection.setText(section);
                    ProfileGender.setText(gender);
                    ProfileDob.setText(dob);
                    ProfileMobile.setText(mobilenumber);
                    ProfileMailID.setText(Emailid);
                    Picasso.with(ProfileActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).
                            placeholder(R.drawable.profile).into(ProfileView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError()
                        {
                            Picasso.with(ProfileActivity.this).load(image).
                                    placeholder(R.drawable.profile).into(ProfileView);


                        }
                    });
                    loadingBar.dismiss();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onStart()
    {
        super.onStart();
        loadingBar.setTitle("Loading");
        loadingBar.setMessage("Please wait, while we update your profile");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();



    }
}
