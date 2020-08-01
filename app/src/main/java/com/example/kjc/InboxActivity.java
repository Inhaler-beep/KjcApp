package com.example.kjc;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

public class InboxActivity extends AppCompatActivity
{

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionPagerAdapter;
    private Toolbar mToolbaron;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        mToolbaron = (Toolbar) findViewById(R.id.inbox_toolbar);
        setSupportActionBar(mToolbaron);
        getSupportActionBar().setTitle("KJC Chats");





        mViewPager = (ViewPager) findViewById(R.id.main_tab_pager);
        mSectionPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.chat_main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId())
        {
            case R.id.account_settings :
                Toast.makeText(InboxActivity.this, "Account Settings", Toast.LENGTH_SHORT).show();
                break;

            case R.id.all_users_information :
                Intent alluserIntent = new Intent(InboxActivity.this,AllUsersActivity.class);
                startActivity(alluserIntent);



        }


        return true;
    }


}

