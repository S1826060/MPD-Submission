package com.example.rssfeeddemo.ui.incidents.interfaces;

import com.example.rssfeeddemo.ui.incidents.model.RssItem;

public interface IncidentAdapterListener {
    public void onIncidentItemClick(RssItem rssItem, int pos);
}
