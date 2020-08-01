package com.example.kjc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private DatabaseReference UsereRef,FriendRequestDatabase,FriendsDatabase,NotificationDatabase;
    private CircleImageView UserProfileImage;
    private TextView UserProfileName , UserProfileCourse,UserprofileSection;
    private Button UserSendRequestButton,UserDeclineRequestButton;
    private String current_state;
    private FirebaseAuth mAuth;
    private ProgressDialog mLoadingBar;
    private String fullname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        final String user_id = getIntent().getStringExtra("userid");




        mToolbar = (Toolbar) findViewById(R.id.user_click_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("User Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        final String current_user_id = mAuth.getCurrentUser().getUid();


        UserProfileImage = (CircleImageView) findViewById(R.id.user_click_profile_pic);
        UserprofileSection =(TextView) findViewById(R.id.user_click_section);
        UserProfileCourse =(TextView) findViewById(R.id.user_click_course);
        UserProfileName = (TextView) findViewById(R.id.user_click_full_name);
        UserSendRequestButton = (Button) findViewById(R.id.user_click_send_request_button);
        UserDeclineRequestButton = (Button) findViewById(R.id.user_click_decline_request_button);
        mLoadingBar = new ProgressDialog(this);




        UsereRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        FriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        FriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        NotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        current_state = "not_friends";



        UsereRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {



                    fullname = dataSnapshot.child("fullname").getValue().toString();
                    String profileimage = dataSnapshot.child("thumbs").getValue().toString();
                    String section = dataSnapshot.child("section").getValue().toString();
                    String course = dataSnapshot.child("course").getValue().toString();

                    UserProfileName.setText(fullname);
                    UserProfileCourse.setText(course);
                    UserprofileSection.setText(section);
                    Picasso.with(UserProfileActivity.this).load(profileimage).placeholder(R.drawable.profile).into(UserProfileImage);

                    // -------- FRIENDS LIST REQUEST------

                    FriendRequestDatabase.child(current_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                                if(dataSnapshot.hasChild(user_id))
                                {
                                    String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                                    if (req_type.equals("received"))
                                    {

                                        current_state = "req_received";
                                        UserSendRequestButton.setText("Accept Friend Request");
                                    }
                                    else if (req_type.equals("sent"))
                                    {
                                        UserDeclineRequestButton.setVisibility(View.INVISIBLE);
                                        UserDeclineRequestButton.setEnabled(false);
                                        current_state = "req_sent";
                                        UserSendRequestButton.setText("Cancel Friend Request");
                                    }
                                }
                                else
                                {
                                    FriendsDatabase.child(current_user_id).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                        {
                                            if(dataSnapshot.hasChild(user_id))
                                            {

                                                UserDeclineRequestButton.setVisibility(View.INVISIBLE);
                                                UserDeclineRequestButton.setEnabled(false);
                                                current_state ="friends";
                                                UserSendRequestButton.setText("Unfriend "+fullname);
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        UserSendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                UserSendRequestButton.setEnabled(false);

                // ------------- SENT REQUEST AREA ---------
                if(current_state.equals("not_friends"))
                {
                    FriendRequestDatabase.child(current_user_id).child(user_id).child("request_type").setValue("sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                       FriendRequestDatabase.child(user_id).child(current_user_id).child("request_type").setValue("received")
                                               .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                   @Override
                                                   public void onSuccess(Void aVoid)
                                                   {
                                                       HashMap<String,String> notificationData = new HashMap<>();
                                                       notificationData.put("from",current_user_id);
                                                       notificationData.put("type","request");

                                                        NotificationDatabase.child(user_id).push().setValue(notificationData)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid)
                                                                    {
                                                                        current_state = "req_sent";
                                                                        UserSendRequestButton.setText("Cancel Friend Request");

                                                                        UserDeclineRequestButton.setVisibility(View.INVISIBLE);
                                                                        UserDeclineRequestButton.setEnabled(false);
                                                                        Toast.makeText(UserProfileActivity.this, "Requset Sent", Toast.LENGTH_SHORT).show();

                                                                    }
                                                                });


                                                   }
                                               });
                                        
                                    }
                                    else
                                    {
                                        Toast.makeText(UserProfileActivity.this, "Failed to Send Request", Toast.LENGTH_SHORT).show();
                                    }

                                    UserSendRequestButton.setEnabled(true);
                                }
                            });

                }

                // ------------- CANCEL REQUEST AREA ---------
                if(current_state.equals("req_sent"))
                {
                    FriendRequestDatabase.child(current_user_id).child(user_id).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid)
                                {
                                    FriendRequestDatabase.child(user_id).child(current_user_id).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid)
                                                {
                                                    UserSendRequestButton.setEnabled(true);
                                                    current_state = "not_friends";
                                                    UserSendRequestButton.setText("Send Friend Request");

                                                    UserDeclineRequestButton.setVisibility(View.INVISIBLE);
                                                    UserDeclineRequestButton.setEnabled(false);
                                                    Toast.makeText(UserProfileActivity.this, "Request Cancelled", Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                }
                            });
                }

                // -------Request Received state
                if(current_state.equals("req_received"))
                {
                    final String current_date = DateFormat.getDateTimeInstance().format(new Date());
                    FriendsDatabase.child(current_user_id).child(user_id).setValue(current_date)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid)
                            {
                                FriendsDatabase.child(user_id).child(current_user_id).setValue(current_date)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid)
                                            {
                                                FriendRequestDatabase.child(current_user_id).child(user_id).removeValue()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid)
                                                            {
                                                                FriendRequestDatabase.child(user_id).child(current_user_id).removeValue()
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid)
                                                                            {
                                                                                UserSendRequestButton.setEnabled(true);
                                                                                current_state = "friends";
                                                                                UserSendRequestButton.setText("Unfriend "+fullname);

                                                                                UserDeclineRequestButton.setVisibility(View.INVISIBLE);
                                                                                UserDeclineRequestButton.setEnabled(false);
                                                                                Toast.makeText(UserProfileActivity.this, "Friends", Toast.LENGTH_SHORT).show();

                                                                            }
                                                                        });
                                                            }
                                                        });

                                            }
                                        });

                            }
                        });
                }
                if(current_state.equals("friends"))
                {

                    FriendsDatabase.child(current_user_id).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            FriendsDatabase.child(user_id).child(current_user_id).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid)
                                        {
                                            UserSendRequestButton.setEnabled(true);
                                            current_state = "not_friends";
                                            UserSendRequestButton.setText("Send Request ");

                                        }
                                    });

                        }
                    });
                }

            }
        });


    }


}
