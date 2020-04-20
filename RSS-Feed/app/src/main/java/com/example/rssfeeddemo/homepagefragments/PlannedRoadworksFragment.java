package com.example.rssfeeddemo.homepagefragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rssfeeddemo.EventDetailsActivity;
import com.example.rssfeeddemo.HomePageActivity;
import com.example.rssfeeddemo.R;
import com.example.rssfeeddemo.common.Common;
import com.example.rssfeeddemo.interfaces.NearMeSelect;
import com.example.rssfeeddemo.interfaces.UpdateUiTabData;
import com.example.rssfeeddemo.permission.PermissionHelper;
import com.example.rssfeeddemo.ui.plannedroadworks.adapter.PlannedRoadWorksAdapter;
import com.example.rssfeeddemo.ui.plannedroadworks.interfaces.PlannedRoadWorkAdapterListener;
import com.example.rssfeeddemo.ui.plannedroadworks.interfaces.RssFeedResponse;
import com.example.rssfeeddemo.ui.plannedroadworks.model.PlannedRoadWorksRssFeed;
import com.example.rssfeeddemo.ui.plannedroadworks.model.RssItem;
import com.google.gson.Gson;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

//S1826060 Scott Derek Robertson
public class PlannedRoadworksFragment extends Fragment implements PlannedRoadWorkAdapterListener, RssFeedResponse, NearMeSelect {

    View view;
    TextView noDataTv;
    private RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    PlannedRoadWorksAdapter plannedRoadWorksAdapter;
    private List<RssItem> rssItems;
    private List<RssItem> filterListItems;
    private List<RssItem> nearMeFilterItemFix;

    private UpdateUiTabData updateUiTabData;
    PlannedRoadWorksRssFeed plannedRoadWorksRssFeed;

    public PlannedRoadworksFragment() {
        // doesn't do anything special
    }

    public PlannedRoadworksFragment(UpdateUiTabData updateUiTabData) {
        // Required empty public constructor
        this.updateUiTabData = updateUiTabData;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_all_events, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        HomePageActivity.getInstance().nearMeSelect2 = this;
        recyclerView = view.findViewById(R.id.planned_roadworks_RV);
        noDataTv = view.findViewById(R.id.no_data_tv);
        layoutManager = new LinearLayoutManager(getContext());
        rssItems = new ArrayList<>();
        filterListItems = new ArrayList<>();
        nearMeFilterItemFix = new ArrayList<>();

        recyclerView.setLayoutManager(layoutManager);
        plannedRoadWorksAdapter = new PlannedRoadWorksAdapter(getContext(), rssItems, rssItems, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(plannedRoadWorksAdapter);
        plannedRoadWorksRssFeed = new PlannedRoadWorksRssFeed();
        plannedRoadWorksRssFeed.response = this;
        plannedRoadWorksRssFeed.execute("https://trafficscotland.org/rss/feeds/plannedroadworks.aspx");


    }

    @Override
    public void onPlannedRoadWorkItemClick(RssItem item, int pos) {

        PermissionHelper permissionHelper = new PermissionHelper(getContext());
        if (!permissionHelper.checkPermission()) {
            permissionHelper.requestPermission();
        } else {
            Gson gson = new Gson();
            String jsonString = gson.toJson(rssItems);
            Intent intent = new Intent(getActivity(), EventDetailsActivity.class);
            intent.putExtra("listItem", jsonString);
            intent.putExtra("pos", pos);
            intent.putExtra("plannedWork", "PlanedRoadWork");
            startActivity(intent);
        }
    }

    private void searchData(List<RssItem> itemList){

        HomePageActivity.getInstance().searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                List<RssItem> filteredList = new ArrayList<>();
                if (query.isEmpty()) {
                    filteredList = itemList;
                } else {

                    for (RssItem item: itemList) {

                        if (item.getTitle().toLowerCase().contains(query.toLowerCase())) {
                            filteredList.add(item);
                        }
                    }
                }
                rssItems.clear();
                rssItems.addAll(filteredList);
                plannedRoadWorksAdapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                List<RssItem> filteredList = new ArrayList<>();

                if (newText.isEmpty()) {
                    filteredList = itemList;
                } else {

                    for (RssItem item: itemList) {
                        if(item.getTitle() != null){
                            if (item.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                                filteredList.add(item);
                            }
                        }
                    }
                }
                rssItems.clear();
                rssItems.addAll(filteredList);
                plannedRoadWorksAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible){
            searchData(filterListItems);

        }

    }

    @Override
    public void processFinish(List<RssItem> output) {
        noDataTv.setVisibility(View.GONE);
        rssItems.clear();
        rssItems.addAll(output);
        filterListItems.addAll(output);
        nearMeFilterItemFix.addAll(output);
        plannedRoadWorksAdapter.notifyDataSetChanged();
        updateUiTabData.onPlannedRoadWorksCount(output.size());
        nearMeDataFilter();
    }

    @Override
    public void onNearMeItemClick() {
        nearMeDataFilter();
    }

    private void nearMeDataFilter(){
        if(HomePageActivity.getInstance().sharedPref.getNearMeValue() !=0){
            getDistence();
            updateUiTabData.onPlannedRoadWorksCount(rssItems.size());

        } else {
            rssItems.clear();
            rssItems.addAll(nearMeFilterItemFix);
            plannedRoadWorksAdapter.notifyDataSetChanged();
            updateUiTabData.onPlannedRoadWorksCount(rssItems.size());

        }
    }

    private List<RssItem> nearMeEvent;
    private void getDistence() {
        nearMeEvent = new ArrayList<>();
        for (int i = 0; i < nearMeFilterItemFix.size(); i++) {
            if (nearMeFilterItemFix.get(i).getGeorss_point() != null) {
                String latlng = nearMeFilterItemFix.get(i).getGeorss_point();
                String[] splited = latlng.split("\\s+");
                double latitude = Double.parseDouble(splited[0]);
                double longitude = Double.parseDouble(splited[1]);

                double theta = Common.Current_longitude - longitude;

                double dist = Math.sin(deg2rad(Common.Current_latitude))
                        * Math.sin(deg2rad(latitude))
                        + Math.cos(deg2rad(Common.Current_latitude))
                        * Math.cos(deg2rad(latitude))
                        * Math.cos(deg2rad(theta));

                dist = Math.acos(dist);
                dist = rad2deg(dist);
                dist = dist * 60 * 1.1515;

                if(dist<=HomePageActivity.getInstance().sharedPref.getNearMeValue()){
                    nearMeEvent.add(nearMeFilterItemFix.get(i));
                }
            }
        }
        rssItems.clear();
        rssItems.addAll(nearMeEvent);
        plannedRoadWorksAdapter.notifyDataSetChanged();
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
