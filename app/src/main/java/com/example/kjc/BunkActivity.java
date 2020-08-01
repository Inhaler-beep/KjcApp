package com.example.kjc;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BunkActivity extends AppCompatActivity
{


    private Toolbar mToolbar;
    private ProgressDialog loadinBar;
    private EditText TotalLecturesInput,TotalAbsentsInput;
    private TextView TotalPercentageView;

    private Button Calculate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bunk);


        mToolbar = (Toolbar) findViewById(R.id.bunk_toolbar);
        mToolbar.setTitle("Lets' Bunk");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("B U N K E R");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadinBar = new ProgressDialog(this);


        TotalLecturesInput = (EditText) findViewById(R.id.total_classes_input);
        TotalAbsentsInput = (EditText) findViewById(R.id.total_absent_input);
        TotalPercentageView = (TextView) findViewById(R.id.total_percentage);

        Calculate = (Button) findViewById(R.id.bunk_calculate_button);

        Calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                CalculateResult();




            }

        });



    }

    private void CalculateResult()
    {
        String TotalLectures = TotalLecturesInput.getText().toString();
        String TotalAbsents = TotalAbsentsInput.getText().toString();

        if(TextUtils.isEmpty(TotalLectures))
        {
            Toast.makeText(this, "Enter NUmber of classes", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(TotalAbsents))
        {
            Toast.makeText(this, "Enter number of absents", Toast.LENGTH_SHORT).show();
        }
        else
            {
            Integer tl = Integer.valueOf(TotalLectures);
            Integer ta = Integer.valueOf(TotalAbsents);

            float pc = tl - ta;
            float result = (pc / tl) * 100;
            String percentage = Float.toString((float) result);
            String RoundedPercentage = percentage.substring(0,4);
            TotalPercentageView.setText(RoundedPercentage+ " %");

            int correct85 = (int) (tl*0.85);
             int bunknumber = (int) (pc-correct85);

             if(bunknumber>1)
             {

                 AlertDialog.Builder builder = new AlertDialog.Builder(BunkActivity.this);
                 builder.setMessage("You are lucky , you can bunk " +Integer.toString((int)bunknumber)+" more classes")
                         .setTitle("Result")


                         .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which)
                             {

                             }
                         });
                 AlertDialog dialog = builder.create();
                 dialog.show();
             }
             else if(bunknumber==0)
             {
                 AlertDialog.Builder builder = new AlertDialog.Builder(BunkActivity.this);
                 builder.setMessage("OMG, you are in a vulnerable postition, Just attend one more class, and make sure you are safe..!")
                         .setTitle("Result")

                         .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which)
                             {

                             }
                         });
                 AlertDialog dialog = builder.create();
                 dialog.show();
             }
             else if(bunknumber==1)
             {



                 AlertDialog.Builder builder = new AlertDialog.Builder(BunkActivity.this);
                 builder.setMessage("Be Careful ,You have only one bunk remaining...!")
                         .setTitle("Result")

                         .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which)
                             {

                             }
                         });
                 AlertDialog dialog = builder.create();
                 dialog.show();

             }
             else
             {
                 String BUNKNUMBER = Integer.toString((int)bunknumber);

                 AlertDialog.Builder builder = new AlertDialog.Builder(BunkActivity.this);
                 builder.setMessage("Unfortunately you have to attend  "+BUNKNUMBER.substring(1)+"  more classes to get 85%")
                         .setTitle("Result")

                         .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which)
                             {

                             }
                         });
                 AlertDialog dialog = builder.create();
                 dialog.show();

             }
        }


    }



}
