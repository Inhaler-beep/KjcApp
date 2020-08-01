package com.example.kjc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class FacultyProfile extends AppCompatActivity {

    private Toolbar sToolbar;
    private DatabaseReference FacultyProfileRef;
    private CircleImageView UserProfileImage;
    private TextView FacultyProfileFullname, FacultyProfileDepartment, FacultyProfileStaffRoom, FacultyProfilePhoneNumber;
    private ImageView WhatsappButton, PhoneCallButton;
    private String current_state;
    private FirebaseAuth mAuth;
    private ProgressDialog mLoadingBar;
    private String facultykey, facultyphone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_profile);


        sToolbar = (Toolbar) findViewById(R.id.faculty_profile_toolbar);
        setSupportActionBar(sToolbar);
        getSupportActionBar().setTitle("Faculty Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FacultyProfileRef = FirebaseDatabase.getInstance().getReference().child("Faculties");
        WhatsappButton = (ImageView) findViewById(R.id.whatsapp_image_button);
        PhoneCallButton = (ImageView) findViewById(R.id.call_image_button);
        FacultyProfileDepartment = (TextView) findViewById(R.id.faculty_click_department);
        FacultyProfileFullname = (TextView) findViewById(R.id.faculty_click_fullname);
        FacultyProfilePhoneNumber = (TextView) findViewById(R.id.faculty_profile_phone);


        WhatsappButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://api.whatsapp.com/send?phone=" + facultyphone;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        });

        PhoneCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uri = "tel:" + facultyphone;
                Intent callintent = new Intent(Intent.ACTION_DIAL);
                callintent.setData(Uri.parse(uri));
                startActivity(callintent);
            }
        });

        FacultyProfileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String facultyfullname = dataSnapshot.child(facultykey).child("facultyfullname").getValue().toString();
                String facultydepartment = dataSnapshot.child(facultykey).child("facultydepartment").getValue().toString();
                String facultyprofilephone = dataSnapshot.child(facultykey).child("facultyphonenumber").getValue().toString();
                FacultyProfileFullname.setText(facultyfullname);
                FacultyProfileDepartment.setText(facultydepartment);
                FacultyProfilePhoneNumber.setText(facultyprofilephone);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
    @Override
    protected void onStart() {
        super.onStart();

        facultykey = getIntent().getExtras().get("KeyRef").toString();

        FacultyProfileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                facultyphone = dataSnapshot.child(facultykey).child("facultyphonenumber").getValue().toString();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
