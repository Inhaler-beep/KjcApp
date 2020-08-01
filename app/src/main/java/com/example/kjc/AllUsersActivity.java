package com.example.kjc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity
{
    private Toolbar mToolbar;
    private RecyclerView mUserRecyclerView;
    private DatabaseReference UsersRef;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        mToolbar = (Toolbar) findViewById(R.id.allusers_toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("U S E R S");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        loadingBar = new ProgressDialog(this);



        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mUserRecyclerView = (RecyclerView) findViewById(R.id.all_users_recycler_view);
        mUserRecyclerView.setHasFixedSize(true);
        mUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadingBar.setTitle("Loading");
        loadingBar.setMessage("Please wait while we load the users");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(UsersRef,Users.class)
                .build();


        FirebaseRecyclerAdapter<Users,FindUsersViewholder> adpater =
                new FirebaseRecyclerAdapter<Users, FindUsersViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindUsersViewholder holder, int position, @NonNull Users model)
                    {


                        final String user_id = getRef(position).getKey();
                        holder.FULLNAME.setText(model.getFullname());
                        holder.COURSE.setText(model.getCourse());
                        Picasso.with(getApplicationContext()).load(model.getThumbs()).placeholder(R.drawable.profile).into(holder.THUMBIMAGE);
                        loadingBar.dismiss();
                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                if(user_id.equals(current_user_id))
                                {

                                    Intent userProfileIntent = new Intent(AllUsersActivity.this,ProfileActivity.class);
                                    startActivity(userProfileIntent);

                                }
                                else
                                {
                                    Intent userDetailsIntent = new Intent(AllUsersActivity.this,UserProfileActivity.class);
                                    userDetailsIntent.putExtra("userid",user_id);
                                    startActivity(userDetailsIntent);


                                }


                            }
                        });

                    }

                    @NonNull
                    @Override
                    public FindUsersViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_layout, parent,false);

                      FindUsersViewholder viewholder = new FindUsersViewholder(view);
                      return viewholder;
                    }
                };

            mUserRecyclerView.setAdapter(adpater);
            adpater.startListening();
    }

    public static class FindUsersViewholder extends RecyclerView.ViewHolder
    {
        TextView FULLNAME,COURSE;
        CircleImageView THUMBIMAGE;
        View mView;

        public FindUsersViewholder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;


            FULLNAME = itemView.findViewById(R.id.user_single_fullname);
            COURSE = itemView.findViewById(R.id.user_single_course);
            THUMBIMAGE = itemView.findViewById(R.id.user_single_image);
        }
    }
}
