package com.example.rssfeeddemo;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.rssfeeddemo.tabadapter.EventDetailsAdapter;
import com.google.android.material.tabs.TabLayout;

//S1826060 Scott Derek Robertson
public class EventDetailsActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private EventDetailsAdapter accessorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);


        Toolbar toolbar = findViewById(R.id.eventdetailstoolbar);

        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.eventDetailsTabLayout);
        viewPager = findViewById(R.id.eventDetailsViewPager);
        accessorAdapter = new EventDetailsAdapter(getSupportFragmentManager());
        viewPager.setAdapter(accessorAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(tabLayout.getTabCount());

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event_details_menu_share, menu);
        //MenuItem search = menu.findItem(R.id.action_search);

        return true;
    }
}
