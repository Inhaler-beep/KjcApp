package com.example.kjc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.BatchUpdateException;

public class FormsActivity extends AppCompatActivity {
    private StorageReference FormsRef;
    private Button FormsButton;
    private RecyclerView FormsRecycerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forms);

        FormsRef = FirebaseStorage.getInstance().getReference();
        FormsButton = (Button) findViewById(R.id.form_button);


        FormsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FormsRef.child("PDFForms").child("DIsclaimer.txt").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri)
                    {
                       String url = uri.toString();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);

                    }
                });
            }
        });
    }


}
