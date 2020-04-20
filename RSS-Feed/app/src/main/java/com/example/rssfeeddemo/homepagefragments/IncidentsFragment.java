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
import com.example.rssfeeddemo.ui.incidents.adapter.IncidentAdapter;
import com.example.rssfeeddemo.ui.incidents.interfaces.IncidentAdapterListener;
import com.example.rssfeeddemo.ui.incidents.interfaces.RssFeedResponse;
import com.example.rssfeeddemo.ui.incidents.model.IncidentRssFeed;
import com.example.rssfeeddemo.ui.incidents.model.RssItem;
import com.google.gson.Gson;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

//S1826060 Scott Derek Robertson
public class IncidentsFragment extends Fragment implements IncidentAdapterListener, RssFeedResponse, NearMeSelect {

    View view;
    private RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    IncidentAdapter incidentAdapter;
    TextView noDataTv;
    UpdateUiTabData updateUiTabData;
    private List<RssItem> rssItemList;
    private List<RssItem> filterItems;
    private List<RssItem> nearMeEventListFix;

    IncidentRssFeed incidentRssFeed;

    public IncidentsFragment() {
        // Does nothing
    }

    public IncidentsFragment(UpdateUiTabData updateUiTabData) {
        // Required
        this.updateUiTabData = updateUiTabData;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_incidents, container, false);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        HomePageActivity.getInstance().nearMeSelect1 = this;
        noDataTv = view.findViewById(R.id.no_data_tv);
        recyclerView = view.findViewById(R.id.incident_RV);
        layoutManager = new LinearLayoutManager(getContext());
        rssItemList = new ArrayList<>();
        filterItems = new ArrayList<>();
        nearMeEventListFix = new ArrayList<>();

        recyclerView.setLayoutManager(layoutManager);
        incidentAdapter = new IncidentAdapter(getContext(), rssItemList, rssItemList, IncidentsFragment.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(incidentAdapter);
        incidentRssFeed = new IncidentRssFeed();
        incidentRssFeed.response = this;
        incidentRssFeed.execute("https://trafficscotland.org/rss/feeds/currentincidents.aspx");

    }

    @Override
    public void onIncidentItemClick(RssItem rssItem, int pos) {
        PermissionHelper permissionHelper = new PermissionHelper(getContext());
        if (!permissionHelper.checkPermission()) {
            permissionHelper.requestPermission();
        } else {
            Gson gson = new Gson();
            String jsonString = gson.toJson(rssItemList);
            Intent intent = new Intent(getActivity(), EventDetailsActivity.class);
            intent.putExtra("listItem", jsonString);
            intent.putExtra("pos", pos);
            intent.putExtra("incidents", "Incident");
            startActivity(intent);
        }

    }

    List<RssItem> nearMeEvent;
    private void getAllEventDistance() {
        nearMeEvent = new ArrayList<>();

        for (int i = 0; i < nearMeEventListFix.size(); i++) {
            if (nearMeEventListFix.get(i).getGeorss_point() != null) {
                String latlng = nearMeEventListFix.get(i).getGeorss_point();
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
                    nearMeEvent.add(nearMeEventListFix.get(i));
                }
            }
        }
        rssItemList.clear();
        rssItemList.addAll(nearMeEvent);
        incidentAdapter.notifyDataSetChanged();
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
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
                incidentAdapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<RssItem> filteredList = new ArrayList<>();
                if (newText.isEmpty() || newText.equals(" ")) {
                    filteredList=itemList;
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
                incidentAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible){
            if(rssItemList!=null)
                searchData(filterItems);
        }
    }

    @Override
    public void processFinish(List<RssItem> output) {
        noDataTv.setVisibility(View.GONE);
        rssItemList.clear();
        rssItemList.addAll(output);
        filterItems.addAll(output);
        nearMeEventListFix.addAll(output);
        incidentAdapter.notifyDataSetChanged();
        updateUiTabData.onIncidentCount(output.size());
        nearMedataFilter();
    }


    @Override
    public void onNearMeItemClick() {
        nearMedataFilter();
    }

    private void nearMedataFilter(){
        // For near me data filter
        if(HomePageActivity.getInstance().sharedPref.getNearMeValue() !=0){
            getAllEventDistance();
            updateUiTabData.onIncidentCount(rssItemList.size());
        } else {
            rssItemList.clear();
            rssItemList.addAll(nearMeEventListFix);
            incidentAdapter.notifyDataSetChanged();
            updateUiTabData.onIncidentCount(rssItemList.size());
        }
    }

}
