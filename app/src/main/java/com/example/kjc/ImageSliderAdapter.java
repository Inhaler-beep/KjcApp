package com.example.kjc;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderViewAdapter;



public class ImageSliderAdapter extends SliderViewAdapter<SliderViewHolder>
{

    int setTotalCount;
    String imageLink,data;
    private DatabaseReference ImageRef;

    public ImageSliderAdapter(int setTotalCount) {
        this.setTotalCount = setTotalCount;
    }

    @Override
    public SliderViewHolder onCreateViewHolder(ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item_layout,parent,false);
        return new SliderViewHolder(view);


    }



    @Override
    public void onBindViewHolder(final SliderViewHolder viewHolder, final int position)
    {
            ImageRef = FirebaseDatabase.getInstance().getReference().child("SlideImages");
            ImageRef.keepSynced(true);


        ImageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                switch(position)

                {
                    case 0:
                     String data = (String) dataSnapshot.child("1").child("image").getValue();
                     Glide.with(viewHolder.itemView).load(data).into(viewHolder.sliderImageView);

                        break;

                    case 1:
                        String data1 = (String) dataSnapshot.child("2").child("image").getValue();
                        Glide.with(viewHolder.itemView).load(data1).into(viewHolder.sliderImageView);
                        break;

                    case 2:
                        imageLink = dataSnapshot.child("3").child("image").getValue().toString();
                        Glide.with(viewHolder.itemView).load(imageLink).into(viewHolder.sliderImageView);
                        break;
                    case 3:
                        imageLink = dataSnapshot.child("4").child("image").getValue().toString();
                        Glide.with(viewHolder.itemView).load(imageLink).into(viewHolder.sliderImageView);
                        break;

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getCount() {
        return setTotalCount;
    }
}

class SliderViewHolder extends SliderViewAdapter.ViewHolder {

    ImageView sliderImageView;
    View itemView;
    public SliderViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;

        sliderImageView = itemView.findViewById(R.id.image_view);
    }
}
