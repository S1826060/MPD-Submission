package com.example.rssfeeddemo.tabadapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.rssfeeddemo.homepagefragments.IncidentsFragment;
import com.example.rssfeeddemo.homepagefragments.PlannedRoadworksFragment;
import com.example.rssfeeddemo.homepagefragments.RoadWorksFragment;
import com.example.rssfeeddemo.interfaces.UpdateUiTabData;

//S1826060 Scott Derek Robertson
public class HomeTabAdapter extends FragmentPagerAdapter {

    private UpdateUiTabData updateUiTabData;

    public HomeTabAdapter(@NonNull FragmentManager fm, UpdateUiTabData updateUiTabData) {
        super(fm);
        this.updateUiTabData = updateUiTabData;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new IncidentsFragment(updateUiTabData);
            case 1:
                return new RoadWorksFragment(updateUiTabData);
            case 2:
                return new PlannedRoadworksFragment(updateUiTabData);

            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return "INCIDENTS";
            case 1:
                return "ROADWORKS";
            case 2:
                return "ALL EVENTS";

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

}
