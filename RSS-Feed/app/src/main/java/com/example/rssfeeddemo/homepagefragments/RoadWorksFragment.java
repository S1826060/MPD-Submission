package com.example.rssfeeddemo.homepagefragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.rssfeeddemo.ui.roadworks.adapter.RoadWorksAdapter;
import com.example.rssfeeddemo.ui.roadworks.interfaces.RoadWorksAdapterListener;
import com.example.rssfeeddemo.ui.roadworks.interfaces.RssFeedResponse;
import com.example.rssfeeddemo.ui.roadworks.model.RoadWorksRssFeed;
import com.example.rssfeeddemo.ui.roadworks.model.RssItem;
import com.google.gson.Gson;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

//S1826060 Scott Derek Robertson
public class RoadWorksFragment extends Fragment implements RoadWorksAdapterListener, RssFeedResponse, NearMeSelect {

    View view;
    private RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    RoadWorksAdapter roadWorksAdapter;
    TextView noDataTv;
    private List<RssItem> rssItemList;
    private List<RssItem> filterItems;
    private List<RssItem> nearMeItemListFix;
    private UpdateUiTabData updateUiTabData;
    RoadWorksRssFeed roadWorksRssFeed;


    public RoadWorksFragment() {
        // doesn't do anything special
    }

    public RoadWorksFragment(UpdateUiTabData updateUiTabData) {
        // Required empty public constructor
        this.updateUiTabData = updateUiTabData;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_road_works, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        HomePageActivity.getInstance().nearMeSelect3 = this;
        recyclerView = view.findViewById(R.id.road_works_RV);
        noDataTv = view.findViewById(R.id.no_data_tv);
        layoutManager = new LinearLayoutManager(getContext());
        rssItemList=new ArrayList<>();
        filterItems=new ArrayList<>();
        nearMeItemListFix = new ArrayList<>();
        recyclerView.setLayoutManager(layoutManager);
        roadWorksAdapter = new RoadWorksAdapter(getContext(),rssItemList, rssItemList, RoadWorksFragment.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(roadWorksAdapter);

        roadWorksRssFeed = new RoadWorksRssFeed();
        roadWorksRssFeed.response = this;
        roadWorksRssFeed.execute("https://trafficscotland.org/rss/feeds/roadworks.aspx");

        getDistence();



    }

    List<RssItem> nearMeEvent;
    private void getDistence() {
        nearMeEvent = new ArrayList<>();

        for (int i = 0; i < nearMeItemListFix.size(); i++) {
            if (nearMeItemListFix.get(i).getGeorss_point() != null) {
                String latlng = nearMeItemListFix.get(i).getGeorss_point();
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
                    nearMeEvent.add(nearMeItemListFix.get(i));
                }
            }
        }
        rssItemList.clear();
        rssItemList.addAll(nearMeEvent);
        roadWorksAdapter.notifyDataSetChanged();
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
    @Override
    public void onRoadWorksItemClickListener(RssItem item, int pos) {

        PermissionHelper permissionHelper = new PermissionHelper(getContext());
        if (!permissionHelper.checkPermission()) {
            permissionHelper.requestPermission();
        } else {
            Gson gson = new Gson();
            String jsonString = gson.toJson(rssItemList);
            Intent intent = new Intent(getActivity(), EventDetailsActivity.class);
            intent.putExtra("listItem", jsonString);
            intent.putExtra("pos", pos);
            intent.putExtra("RoadWorks", "RoadWork");
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
                rssItemList.clear();
                rssItemList.addAll(filteredList);
                roadWorksAdapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                List<RssItem> filteredList = new ArrayList<>();
                Log.d("SEARCH_DATA", "onQueryTextSubmit: "+newText);
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

                rssItemList.clear();
                rssItemList.addAll(filteredList);
                roadWorksAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible){
            searchData(filterItems);
        }
    }

    @Override
    public void processFinish(List<RssItem> output) {
        noDataTv.setVisibility(View.GONE);
        rssItemList.clear();
        nearMeItemListFix.clear();
        rssItemList.addAll(output);
        filterItems.addAll(output);
        nearMeItemListFix.addAll(output);
        roadWorksAdapter.notifyDataSetChanged();
        updateUiTabData.onRoadWorksCount(output.size());
        nearMeDataFilter();
    }

    @Override
    public void onNearMeItemClick() {
        nearMeDataFilter();
    }

    private void nearMeDataFilter(){
        if(HomePageActivity.getInstance().sharedPref.getNearMeValue() !=0){
            getDistence();
            updateUiTabData.onRoadWorksCount(rssItemList.size());
        } else {
            rssItemList.clear();
            rssItemList.addAll(nearMeItemListFix);
            roadWorksAdapter.notifyDataSetChanged();
            updateUiTabData.onRoadWorksCount(rssItemList.size());
        }
    }
}
