package com.example.kjc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SuggestionActivity extends AppCompatActivity {

    private Button SendSugButton;
    private EditText SendSugInput;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private Toolbar mToolbaron;

    private String currentUserID;
    private ProgressDialog loadingBar;
    private String saveCurrentDate, saveCurrentTime, postRandomName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        mToolbaron = (Toolbar) findViewById(R.id.suggestion_toolbar);
        mToolbaron.setTitle("Suggestion Inbox");
        setSupportActionBar(mToolbaron);
        getSupportActionBar().setTitle("Send a Suggestion");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        SendSugButton = (Button) findViewById(R.id.send_suggestion_button);
        SendSugInput = (EditText) findViewById(R.id.suggestion_input);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Suggestions");
        loadingBar = new ProgressDialog(this);


        SendSugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendButtonAcitivty();
            }
        });


    }

    private void SendButtonAcitivty()
    {
        String message = SendSugInput.getText().toString();

        if(TextUtils.isEmpty(message))
        {
            Toast.makeText(SuggestionActivity.this, "Please Enter Something", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Saving");
            loadingBar.setMessage("Please Wait while we upload your data");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            saveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            saveCurrentTime = currentTime.format(calFordDate.getTime());

            postRandomName = saveCurrentDate + saveCurrentTime;


            HashMap userMap = new HashMap();
            userMap.put("message",message);
            userMap.put("uid",currentUserID);
            userMap.put("datentime",postRandomName);
            UsersRef.child(postRandomName).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(SuggestionActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                    else
                    {
                        String exception = task.getException().toString();
                        Toast.makeText(SuggestionActivity.this, "Error Occurred : "+exception, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }

                }
            });
        }

    }

}

