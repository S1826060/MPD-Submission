package com.example.rssfeeddemo.ui.roadworks.interfaces;

import com.example.rssfeeddemo.ui.roadworks.model.RssItem;

import java.util.List;

public interface RssFeedResponse {
    void processFinish(List<RssItem> output);
}
