package com.example.rssfeeddemo.ui.plannedroadworks.interfaces;

import com.example.rssfeeddemo.ui.plannedroadworks.model.RssItem;

import java.util.List;

public interface RssFeedResponse {
    void processFinish(List<RssItem> output);
}
