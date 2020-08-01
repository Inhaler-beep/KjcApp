package com.example.kjc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
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

public class ProfileFragment extends Fragment
{
    private ProgressDialog loadingBar;
    private CircleImageView ProfileView;
    private DatabaseReference ProfileRef,CacheRef;
    private FirebaseAuth mAuth;
    private String currentUserId,Emailid;
    private TextView ProfileGender,ProfileFullname,ProfileDob,ProfileMailID,ProfileMobile,ProfileCourse,ProfileSection;


   private View view;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile,null);

        init();

        return view;
    }

    private void init()
    {
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        Emailid = mAuth.getCurrentUser().getEmail();
        ProfileRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        ProfileRef.keepSynced(true);

        ProfileFullname = (TextView) view.findViewById(R.id.profile_full_namefragment);
        ProfileCourse = (TextView) view.findViewById(R.id.profile_coursefragment);
        ProfileSection = (TextView) view.findViewById(R.id.profile_sectionfragment);
        ProfileMailID = (TextView) view.findViewById(R.id.college_email_idfragment);
        ProfileDob = (TextView) view.findViewById(R.id.profile_dob_textviewfragment);
        ProfileMobile = (TextView) view.findViewById(R.id.profile_mobile_textviewfragment);
        ProfileGender = (TextView) view.findViewById(R.id.profile_gender_textviewfragment);
        ProfileView = (CircleImageView) view.findViewById(R.id.my_profile_picfragment);



        loadingBar = new ProgressDialog(getActivity());




        ProfileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                if(dataSnapshot.exists())
                {


                    String fullname = dataSnapshot.child("fullname").getValue().toString();
                    String gender = dataSnapshot.child("gender").getValue().toString();
                    String dob = dataSnapshot.child("dob").getValue().toString();
                   final String image = dataSnapshot.child("thumbs").getValue().toString();
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
                    Picasso.with(getContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE).
                            placeholder(R.drawable.profile).into(ProfileView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError()
                        {
                            Picasso.with(getContext()).load(image).
                                    placeholder(R.drawable.profile).into(ProfileView);


                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
