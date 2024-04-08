package com.ads.taskeaze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.ads.taskeaze.Fragments.ChatFragment;
import com.ads.taskeaze.Fragments.ClaimsFragment;
import com.ads.taskeaze.Fragments.HomeFragment;
import com.ads.taskeaze.Fragments.MeetingsFragment;
import com.ads.taskeaze.Fragments.PerformanceFragment;
import com.ads.taskeaze.adapters.ViewpagerAdapter;
import com.ads.taskeaze.customwidget.NoSwipePager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public BottomNavigationView navigation = null;

    private NoSwipePager viewPager = null;
    //
    public ViewpagerAdapter adapter = null;
    public Toolbar toolbar = null;
    String version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        findviewbyid();
        setupViewPager();

        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        MenuItem menuItemProfile = menu.findItem(R.id.nav_my_profile);
        menuItemProfile.setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_profile_icon));

        MenuItem menuItemLeaves = menu.findItem(R.id.nav_leaves);
        menuItemLeaves.setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_nav_leave));

        MenuItem menuItemPayslips = menu.findItem(R.id.nav_payslips);
        menuItemPayslips.setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_payslip));

        MenuItem menuItemAttendance = menu.findItem(R.id.nav_attendance);
        menuItemAttendance.setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_attendance));


        ((AppCompatTextView) findViewById(R.id.activity_base_app_version_name_id)).setText(getResources().getString(R.string.version_txt) + " \t\t~\t\t" + version);



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (navigation.getSelectedItemId() == R.id.navigation_home) {
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.alert_txt))
                        .setMessage(getResources().getString(R.string.app_close_txt) + " " + getString(R.string.app_name) + " ?")
                        .setCancelable(true)
                        .setPositiveButton(getResources().getString(R.string.yes_txt), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                try {
                                    finish();
                                } catch (Exception e) {
                                    finish();
                                    e.printStackTrace();
                                }

                            }

                        }).setNegativeButton(getResources().getString(R.string.no_txt), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create().show();
            } else {
                navigation.setSelectedItemId(R.id.navigation_home);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer_menu, menu);
        //
        MenuItem menuItemProfile = menu.findItem(R.id.nav_my_profile);
        menuItemProfile.setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_profile_icon));

        MenuItem menuItemLeaves = menu.findItem(R.id.nav_leaves);
        menuItemLeaves.setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_nav_leave));

        MenuItem menuItemPayslips = menu.findItem(R.id.nav_payslips);
        menuItemPayslips.setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_payslip));

        MenuItem menuItemAttendance = menu.findItem(R.id.nav_attendance);
        menuItemAttendance.setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_attendance));
        //
        return true;
    }
    


    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_my_profile){
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        }
        else if(id == R.id.nav_leaves){
            startActivity(new Intent(MainActivity.this, LeavesActivity.class));
        }
        else if(id == R.id.nav_payslips){
            startActivity(new Intent(MainActivity.this, PaySlipsActivity.class));
        }
        else if(id == R.id.nav_attendance){
            startActivity(new Intent(MainActivity.this, AttendanceActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void findviewbyid() {
        viewPager = (NoSwipePager) findViewById(R.id.content_base_viewpager_id);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setItemIconTintList(null);
        if (viewPager != null) {
            viewPager.setOffscreenPageLimit(5);
            viewPager.setPagingEnabled(false);
        }

        getSupportActionBar().setTitle(getResources().getString(R.string.home_txt));
        viewPager.setCurrentItem(0, true);
        navigation.getMenu().findItem(R.id.navigation_home).setIcon(R.drawable.ic_home);
        navigation.getMenu().findItem(R.id.navigation_performance).setIcon(R.drawable.ic_performance);
        navigation.getMenu().findItem(R.id.navigation_meetings).setIcon(R.drawable.ic_meeting);
        navigation.getMenu().findItem(R.id.navigation_claim).setIcon(R.drawable.ic_claim);
        navigation.getMenu().findItem(R.id.navigation_chat).setIcon(R.drawable.ic_chat);

    }

    public void setupViewPager() {
        adapter = new ViewpagerAdapter(getSupportFragmentManager());

        HomeFragment homeFragment = new HomeFragment();
        adapter.addFrag(homeFragment, getResources().getString(R.string.home_txt));

        PerformanceFragment performanceFragment = new PerformanceFragment();
        adapter.addFrag(performanceFragment, getResources().getString(R.string.performance_txt));

        MeetingsFragment meetingsFragment = new MeetingsFragment();
        adapter.addFrag(meetingsFragment, getResources().getString(R.string.meetings_txt));

        ClaimsFragment claimsFragment = new ClaimsFragment();
        adapter.addFrag(claimsFragment, getResources().getString(R.string.claim_txt));

        ChatFragment chatFragment = new ChatFragment();
        adapter.addFrag(chatFragment, getResources().getString(R.string.chat_txt));

        viewPager.setAdapter(adapter);
        navigation.setSelectedItemId(R.id.navigation_home);

    }


    BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {


            getSupportActionBar().setTitle("Home");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
            Log.d("tag","Id -- "+item.getItemId());

            if(item.getItemId() == R.id.navigation_home){
                getSupportActionBar().setTitle(getResources().getString(R.string.home_txt));
                viewPager.setCurrentItem(0, true);
            }
            else if(item.getItemId() == R.id.navigation_performance){
                getSupportActionBar().setTitle(getResources().getString(R.string.performance_txt));
                viewPager.setCurrentItem(1, true);
            }
            else if(item.getItemId() == R.id.navigation_meetings){
                getSupportActionBar().setTitle(getResources().getString(R.string.meetings_txt));
                viewPager.setCurrentItem(2, true);
            }
            else if(item.getItemId() == R.id.navigation_claim){
                getSupportActionBar().setTitle(getResources().getString(R.string.claim_txt));
                viewPager.setCurrentItem(3, true);
            }
            else if(item.getItemId() == R.id.navigation_chat){
                getSupportActionBar().setTitle(getResources().getString(R.string.chat_txt));
                viewPager.setCurrentItem(4, true);
            }

            return true;
        }

    };
}