package com.example.rssfeeddemo;


import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.rssfeeddemo.common.Common;
import com.example.rssfeeddemo.helper.SharedPref;
import com.example.rssfeeddemo.interfaces.NearMeSelect;
import com.example.rssfeeddemo.interfaces.UpdateUiTabData;
import com.example.rssfeeddemo.permission.PermissionHelper;
import com.example.rssfeeddemo.tabadapter.HomeTabAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

//S1826060 Scott Derek Robertson
public class HomePageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, UpdateUiTabData, NearMeSelect {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private HomeTabAdapter accessorAdapter;
    public MaterialSearchView searchView;
    private TextView totalIncidentCountTV;
    private TextView totalRoadWorksCountTV;
    private TextView totalPlannedRoadWorksCountTV;

    private ProgressBar incidentProgressbar;
    private ProgressBar roadWorksProgressbar;
    private ProgressBar plannedRoadWorksProgressbar;

    public NearMeSelect nearMeSelect1;
    public NearMeSelect nearMeSelect2;
    public NearMeSelect nearMeSelect3;


    RadioGroup near_me_radio_group;

    public LinearLayout near_toolbar_show;
    public TextView near_me_text;
    public int mile;
    RadioButton fivemiles;
    RadioButton tenmiles;
    RadioButton fiftenmiles;
    RadioButton twentymiles;
    RadioButton twentyfivemiles;



    public static HomePageActivity instance;

    //location

    Double lat;
    Double lng;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    int wantEventMiles=0;

    public SharedPref sharedPref;
    RadioButton fiftymiles;
    RadioButton hundredmiles;
    RadioButton allevent;

    public static HomePageActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        instance=this;
        setContentView(R.layout.activity_home_page);
        // checkPermission();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("S1826060");
        setSupportActionBar(toolbar);
        sharedPref = new SharedPref(this);

        near_toolbar_show = findViewById(R.id.near_me_selected_view);
        near_me_text = findViewById(R.id.text_nearme);

        if (sharedPref.getNearMeValue() == 0) {
            near_toolbar_show.setVisibility(View.GONE);
        } else {
            near_toolbar_show.setVisibility(View.VISIBLE);
            int mile = sharedPref.getNearMeValue();
            near_me_text.setText(String.valueOf(mile).toString());
        }

        searchView = (MaterialSearchView) findViewById(R.id.search_view);

        tabLayout = findViewById(R.id.mainTabBarID);
        viewPager = findViewById(R.id.mainViewPagerID);
        accessorAdapter = new HomeTabAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(accessorAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(tabLayout.getTabCount());


        View incidents = LayoutInflater.from(this).inflate(R.layout.view_incidents, null);
        incidents.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        View roadworks = LayoutInflater.from(this).inflate(R.layout.view_roadworks, null);
        roadworks.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        View allevents = LayoutInflater.from(this).inflate(R.layout.view_allevents, null);
        allevents.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        tabLayout.getTabAt(0).setCustomView(incidents);
        tabLayout.getTabAt(1).setCustomView(roadworks);
        tabLayout.getTabAt(2).setCustomView(allevents);

        totalIncidentCountTV = findViewById(R.id.incidents_count_TV);
        incidentProgressbar = findViewById(R.id.incidents_progress);
        totalRoadWorksCountTV = findViewById(R.id.roadworks_text_count_tv);
        roadWorksProgressbar = findViewById(R.id.roadworks_progress);
        totalPlannedRoadWorksCountTV = findViewById(R.id.planned_roadworks_total_TV);
        plannedRoadWorksProgressbar = findViewById(R.id.planned_roadworks_progress);

        totalIncidentCountTV.setVisibility(View.GONE);
        totalRoadWorksCountTV.setVisibility(View.GONE);
        totalPlannedRoadWorksCountTV.setVisibility(View.GONE);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                HomePageActivity.this,
                drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        PermissionHelper permissionHelper = new PermissionHelper(this);
        if (!permissionHelper.checkPermission()) {
            permissionHelper.requestPermission();
        } else {
            initLocation();
        }

    }

    private void initLocation() {

        buildLocationRequest();
        buildLocationCallback();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(HomePageActivity.this);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        fusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HomePageActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {


                //for set all the location of the event


                lat = task.getResult().getLatitude();
                lng = task.getResult().getLongitude();

                Common.Current_latitude = lat;
                Common.Current_longitude = lng;

            }

        });
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setSmallestDisplacement(10f);
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        searchView.setMenuItem(search);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_near_me:
                showAlertDialoge();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAlertDialoge() {
        androidx.appcompat.app.AlertDialog.Builder alertdialog = new androidx.appcompat.app.AlertDialog.Builder(this);

        alertdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.near_me_dialog, null);
        fivemiles = (RadioButton) add_menu_layout.findViewById(R.id.five_miles);
        tenmiles = (RadioButton) add_menu_layout.findViewById(R.id.ten_miles);
        fiftenmiles = (RadioButton) add_menu_layout.findViewById(R.id.fifteen_miles);
        twentymiles = (RadioButton) add_menu_layout.findViewById(R.id.twenty_miles);
        twentyfivemiles = (RadioButton) add_menu_layout.findViewById(R.id.twenty_five_miles);
        fiftymiles = (RadioButton) add_menu_layout.findViewById(R.id.fifty_miles);
        hundredmiles = (RadioButton) add_menu_layout.findViewById(R.id.hundred_miles);
        allevent = (RadioButton) add_menu_layout.findViewById(R.id.all_events);

        alertdialog.setView(add_menu_layout);
        final AlertDialog dialog = alertdialog.create();


        setRadioButtionChecked(sharedPref.getNearMeValue());


        near_me_radio_group = add_menu_layout.findViewById(R.id.nearme_radio_group);

        near_me_radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                View radioButton = near_me_radio_group.findViewById(checkedId);

                int index = near_me_radio_group.indexOfChild(radioButton);



                switch (index) {
                    case 0:
                        wantEventMiles = 5;
                        Common.nearMeValue = wantEventMiles;
                        sharedPref.setNearMeValue(wantEventMiles);
                        nearMeSelect1.onNearMeItemClick();
                        nearMeSelect2.onNearMeItemClick();
                        nearMeSelect3.onNearMeItemClick();


                        setRadioButtionChecked(wantEventMiles);
                        near_toolbar_show.setVisibility(View.VISIBLE);
                        mile = sharedPref.getNearMeValue();
                        near_me_text.setText(String.valueOf(mile).toString());


                        dialog.dismiss();

                        break;
                    case 1:
                        wantEventMiles = 10;
                        Common.nearMeValue = wantEventMiles;
                        sharedPref.setNearMeValue(wantEventMiles);
                        nearMeSelect1.onNearMeItemClick();
                        nearMeSelect2.onNearMeItemClick();
                        nearMeSelect3.onNearMeItemClick();

                        setRadioButtionChecked(wantEventMiles);
                        near_toolbar_show.setVisibility(View.VISIBLE);
                        mile = sharedPref.getNearMeValue();
                        near_me_text.setText(String.valueOf(mile).toString());


                        dialog.dismiss();
                        break;
                    case 2:
                        wantEventMiles = 15;
                        Common.nearMeValue = wantEventMiles;
                        sharedPref.setNearMeValue(wantEventMiles);
                        nearMeSelect1.onNearMeItemClick();
                        nearMeSelect2.onNearMeItemClick();
                        nearMeSelect3.onNearMeItemClick();

                        setRadioButtionChecked(wantEventMiles);
                        near_toolbar_show.setVisibility(View.VISIBLE);
                        mile = sharedPref.getNearMeValue();
                        near_me_text.setText(String.valueOf(mile).toString());

                        dialog.dismiss();
                        break;
                    case 3:
                        wantEventMiles = 20;
                        Common.nearMeValue = wantEventMiles;
                        sharedPref.setNearMeValue(wantEventMiles);
                        nearMeSelect1.onNearMeItemClick();
                        nearMeSelect2.onNearMeItemClick();
                        nearMeSelect3.onNearMeItemClick();

                        setRadioButtionChecked(wantEventMiles);
                        near_toolbar_show.setVisibility(View.VISIBLE);
                        mile = sharedPref.getNearMeValue();
                        near_me_text.setText(String.valueOf(mile).toString());

                        dialog.dismiss();
                        break;
                    case 4:
                        wantEventMiles = 25;
                        Common.nearMeValue = wantEventMiles;
                        sharedPref.setNearMeValue(wantEventMiles);
                        nearMeSelect1.onNearMeItemClick();
                        nearMeSelect2.onNearMeItemClick();
                        nearMeSelect3.onNearMeItemClick();

                        setRadioButtionChecked(wantEventMiles);
                        near_toolbar_show.setVisibility(View.VISIBLE);
                        mile = sharedPref.getNearMeValue();
                        near_me_text.setText(String.valueOf(mile).toString());

                        dialog.dismiss();
                        break;
                    case 5:
                        wantEventMiles = 50;
                        Common.nearMeValue = wantEventMiles;
                        sharedPref.setNearMeValue(wantEventMiles);
                        nearMeSelect1.onNearMeItemClick();
                        nearMeSelect2.onNearMeItemClick();
                        nearMeSelect3.onNearMeItemClick();

                        setRadioButtionChecked(wantEventMiles);
                        near_toolbar_show.setVisibility(View.VISIBLE);
                        mile = sharedPref.getNearMeValue();
                        near_me_text.setText(String.valueOf(mile).toString());

                        dialog.dismiss();
                        break;
                    case 6:
                        wantEventMiles = 100;
                        Common.nearMeValue = wantEventMiles;
                        sharedPref.setNearMeValue(wantEventMiles);
                        nearMeSelect1.onNearMeItemClick();
                        nearMeSelect2.onNearMeItemClick();
                        nearMeSelect3.onNearMeItemClick();;

                        setRadioButtionChecked(wantEventMiles);
                        near_toolbar_show.setVisibility(View.VISIBLE);
                        mile = sharedPref.getNearMeValue();
                        near_me_text.setText(String.valueOf(mile).toString());

                        dialog.dismiss();
                        break;
                    case 7:
                        wantEventMiles = 0;
                        Common.nearMeValue = wantEventMiles;
                        sharedPref.setNearMeValue(wantEventMiles);
                        nearMeSelect1.onNearMeItemClick();
                        nearMeSelect2.onNearMeItemClick();
                        nearMeSelect3.onNearMeItemClick();

                        setRadioButtionChecked(wantEventMiles);
                        near_toolbar_show.setVisibility(View.GONE);


                        dialog.dismiss();
                        break;
                }

            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            }
        });

        dialog.show();

    }

    private void setRadioButtionChecked(int wantEventMiles) {
        if (wantEventMiles == 5) {

            fivemiles.setChecked(true);
        }
        if (wantEventMiles == 10) {

            tenmiles.setChecked(true);
        }
        if (wantEventMiles == 15) {

            fiftenmiles.setChecked(true);
        }
        if (wantEventMiles == 20) {

            twentymiles.setChecked(true);
        }
        if (sharedPref.getNearMeValue() == 25) {

            twentyfivemiles.setChecked(true);
        }
        if (wantEventMiles == 50) {

            fiftymiles.setChecked(true);
        }
        if (wantEventMiles == 100) {

            hundredmiles.setChecked(true);
        }
        if (wantEventMiles == 0) {
            allevent.setChecked(true);
        }

    }


    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onIncidentCount(int totalIncident) {
        incidentProgressbar.setVisibility(View.GONE);
        totalIncidentCountTV.setVisibility(View.VISIBLE);
        totalIncidentCountTV.setText(String.valueOf(totalIncident));
    }

    @Override
    public void onRoadWorksCount(int totalRoadWorks) {
        roadWorksProgressbar.setVisibility(View.GONE);
        totalRoadWorksCountTV.setVisibility(View.VISIBLE);
        totalRoadWorksCountTV.setText(String.valueOf(totalRoadWorks));
    }

    @Override
    public void onPlannedRoadWorksCount(int totalPlannedWorks) {
        plannedRoadWorksProgressbar.setVisibility(View.GONE);
        totalPlannedRoadWorksCountTV.setVisibility(View.VISIBLE);
        totalPlannedRoadWorksCountTV.setText(String.valueOf(totalPlannedWorks));
    }

    @Override
    protected void onResume() {
        super.onResume();
        PermissionHelper permissionHelper = new PermissionHelper(this);
        if (!permissionHelper.checkPermission()) {
            permissionHelper.requestPermission();
        }
    }

    @Override
    public void onNearMeItemClick() {

    }
}
