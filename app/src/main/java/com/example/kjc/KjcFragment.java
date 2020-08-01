package com.example.kjc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class KjcFragment extends Fragment
{

    private DatabaseReference ProfileRef,CacheRef,UpdatesRef;

    private SliderView sliderView;
    private RecyclerView mUserRecyclerVi;

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view =  inflater.inflate(R.layout.fragment_kjc,null);

        init();

        return view;
    }

    @Override
    public void onStart()
    {

        super.onStart();

        Query SortingInDecendingOder = UpdatesRef.orderByChild("counter");

        FirebaseRecyclerOptions<UpdatesModule> options =
                new FirebaseRecyclerOptions.Builder<UpdatesModule>()
                .setQuery(SortingInDecendingOder,UpdatesModule.class)
                .build();


        FirebaseRecyclerAdapter<UpdatesModule,FindUpdatesViewHolder> adapter
                = new FirebaseRecyclerAdapter<UpdatesModule, FindUpdatesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindUpdatesViewHolder holder, int position, @NonNull UpdatesModule model)
            {

                holder.heading.setText(model.getHeading());
                holder.body.setText(model.getBody());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Toast.makeText(getActivity(), "Succesfful", Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @NonNull
            @Override
            public FindUpdatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_news_updates,parent,false);
                FindUpdatesViewHolder viewHolder = new FindUpdatesViewHolder(view);
                return viewHolder;
            }
        };

        mUserRecyclerVi.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FindUpdatesViewHolder extends RecyclerView.ViewHolder
    {
        TextView heading,body;
        View mView;

        public FindUpdatesViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            heading = (TextView) itemView.findViewById(R.id.news_heading);
            body = (TextView) itemView.findViewById(R.id.news_body);

        }


    }

    private void init() {


        ProfileRef = FirebaseDatabase.getInstance().getReference("SlideImages");

        CacheRef = FirebaseDatabase.getInstance().getReference().child("Users");
        UpdatesRef = FirebaseDatabase.getInstance().getReference().child("News");
        UpdatesRef.keepSynced(true);
        CacheRef.keepSynced(true);
        sliderView = (SliderView) view.findViewById(R.id.imageSlider);
        mUserRecyclerVi = (RecyclerView) view.findViewById(R.id.all_updates_recycler_view);
        mUserRecyclerVi.setHasFixedSize(true);
        mUserRecyclerVi.setLayoutManager(new LinearLayoutManager(getContext()));

        ProfileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int counts = (int) dataSnapshot.getChildrenCount();
                sliderView.setSliderAdapter(new ImageSliderAdapter(counts));
                sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
