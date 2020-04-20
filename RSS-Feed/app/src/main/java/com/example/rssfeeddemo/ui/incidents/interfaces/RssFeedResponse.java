package com.example.rssfeeddemo.ui.incidents.interfaces;

import com.example.rssfeeddemo.ui.incidents.model.RssItem;

import java.util.List;

public interface RssFeedResponse {
    void processFinish(List<RssItem> output);
}
