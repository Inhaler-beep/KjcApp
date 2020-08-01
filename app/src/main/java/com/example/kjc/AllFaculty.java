package com.example.kjc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllFaculty extends AppCompatActivity
{

    private Toolbar mToolbar;
    private RecyclerView mUserRecyclerView;
    private DatabaseReference UsersRef;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private ProgressDialog loadingBar;
    private SearchView facultySearch;
    private EditText facultysearchInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_faculty);

        mToolbar = (Toolbar) findViewById(R.id.allfaculty_toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Faculty");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        loadingBar = new ProgressDialog(this);
        facultysearchInput = (EditText) findViewById(R.id.faculty_search_input);


        UsersRef = FirebaseDatabase.getInstance().getReference().child("Faculties");
        UsersRef.keepSynced(true);

        mUserRecyclerView = (RecyclerView) findViewById(R.id.all_faculty_recycler_view);
        mUserRecyclerView.setHasFixedSize(true);
        mUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        facultysearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.toString()!=null)
                {
                    LoadUserData(s.toString());
                }
                else
                {
                    LoadUserData("");
                }

            }
        });
        LoadUserData("");


    }

    @Override
    protected void onStart() {
        super.onStart();
        loadingBar.setTitle("Loading");
        loadingBar.setMessage("Please wait while we load the faculties");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();


    }

    private void LoadUserData(String data)
    {
        Query query = UsersRef.orderByChild("facultyfullname").startAt(data).endAt(data+"\uf8ff");

       FirebaseRecyclerOptions<Faculties> options =
               new FirebaseRecyclerOptions.Builder<Faculties>()
               .setQuery(query,Faculties.class)
               .build();



        FirebaseRecyclerAdapter<Faculties,FindFacultyViewHolder> adapter =
                new FirebaseRecyclerAdapter<Faculties, FindFacultyViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFacultyViewHolder holder, int position, @NonNull Faculties model)
                    {
                        final String number = "919961050015";
                        final String key = getRef(position).getKey();

                        holder.FULLNAME.setText(model.getFacultyfullname());
                        holder.DEPARTMENT.setText(model.getFacultydepartment());
                        Picasso.with(getApplicationContext()).load(model.getFacultyimage()).placeholder(R.drawable.profile).into(holder.THUMBIMAGE);
                        loadingBar.dismiss();

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {

                               Intent facprofileIntent = new Intent(AllFaculty.this,FacultyProfile.class);
                               facprofileIntent.putExtra("KeyRef",key);
                               startActivity(facprofileIntent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public FindFacultyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.all_faculty_list,parent,false);
                        FindFacultyViewHolder facultyViewHolder = new FindFacultyViewHolder(view);
                        return facultyViewHolder;
                    }
                };
        mUserRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FindFacultyViewHolder extends RecyclerView.ViewHolder
    {
        TextView FULLNAME,DEPARTMENT;
        CircleImageView THUMBIMAGE;
        View mView;

        public FindFacultyViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
            FULLNAME = itemView.findViewById(R.id.faculty_fullname);
            DEPARTMENT = itemView.findViewById(R.id.faculty_department);
            THUMBIMAGE = itemView.findViewById(R.id.faculty_profile_image);

        }
    }
}
