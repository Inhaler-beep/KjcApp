package com.example.kjc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    public SharedPreferences profilecacheRef;
    private DatabaseReference usersRef;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("KJC Students");

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_nav_view);
        navigation.setOnNavigationItemSelectedListener(this);

        profilecacheRef = getApplicationContext().getSharedPreferences("ProfileValues",MODE_PRIVATE);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(HomeActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);


        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);

        String emailid = mAuth.getCurrentUser().getEmail();
        profilecacheRef.edit().putString("EmailID",emailid).apply();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                UserMenuSelector(item);
                return false;
            }
        });



        loadFragment(new HomeFragment());


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item)
    {
        switch (item.getItemId()) {
                case R.id.nav_my_account:
                    SendUserToProfileActivity();
                    break;

                case R.id.nav_faculties:
                   SendUserToAllFacultyAcitivity();
                    break;

                case R.id.nav_talk_to_us:
                    SendUserToSuggestionActivity();
                    break;

                case R.id.nav_logout:
                    mAuth.signOut();
                    SendUserToLoginActivity();
                    break;

                case R.id.nav_inbox:
                    SendUserToInboxActivity();
                    break;

                case R.id.nav_admin:
                    SendUserToAdminActivity();
                    break;
            case R.id.nav_forms:
                    SendUserToFormsActivity();
                    break;

            }

    }

    private void SendUserToFormsActivity()
    {
        Intent forms = new Intent(HomeActivity.this,FormsActivity.class);
        startActivity(forms);

    }

    private void SendUserToAllFacultyAcitivity()
    {
       Intent faculty = new Intent(HomeActivity.this,AllFaculty.class);
       startActivity(faculty);

    }

    private void SendUserToAdminActivity()
    {
        Intent adminIntent = new Intent(HomeActivity.this, AdminActivity.class);
        startActivity(adminIntent);

    }

    private void SendUserToInboxActivity()
    {
        Intent inboxIntent = new Intent(HomeActivity.this, InboxActivity.class);
        startActivity(inboxIntent);
    }


    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void SendUserToSuggestionActivity()
    {
        Intent suggestionIntent = new Intent(HomeActivity.this, SuggestionActivity.class);
        startActivity(suggestionIntent);

    }

    private void SendUserToProfileActivity() {
        Intent profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(profileIntent);
    }

    private boolean loadFragment(Fragment fragment){
        if(fragment !=null)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container,fragment)
                    .commit();

            return true;

        }
        return false;

    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 3000);
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {

        Fragment fragment = null;
        switch (menuItem.getItemId())
        {
            case R.id.bottom_home :
                fragment = new HomeFragment();
                break;
            case R.id.bottom_notification :
                fragment = new KjcFragment();
                break;
            case R.id.bottom_my_account :
                fragment = new ProfileFragment();
                break;



        }
        return loadFragment(fragment);
    }
}


