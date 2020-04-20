package com.example.rssfeeddemo.tabadapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.rssfeeddemo.eventdetailsfragments.DetailsFragment;
import com.example.rssfeeddemo.eventdetailsfragments.MapsFragment;

//S1826060 Scott Derek Robertson
public class EventDetailsAdapter extends FragmentPagerAdapter {

    public EventDetailsAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new DetailsFragment();
            case 1:
                return new MapsFragment();
            default:
                return null;
        }
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return "DETAILS";
            case 1:
                return "MAP";
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return 2;
    }


}
