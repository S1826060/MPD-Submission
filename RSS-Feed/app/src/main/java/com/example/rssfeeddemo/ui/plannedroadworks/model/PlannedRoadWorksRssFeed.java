package com.example.rssfeeddemo.ui.plannedroadworks.model;

import android.os.AsyncTask;
import android.util.Xml;

import com.example.rssfeeddemo.ui.plannedroadworks.interfaces.RssFeedResponse;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//S1826060 Scott Derek Robertson
public class PlannedRoadWorksRssFeed extends AsyncTask<String, Void, List<RssItem>> {
    static final String PUB_DATE = "pubDate";
    static final String DESCRIPTION = "description";
    static final String CHANNEL = "channel";
    static final String LINK = "link";
    static final String TITLE = "title";
    static final String POINT = "point";
    static final String AUTHOR = "author";
    static final String COMMENT = "comment";
    static final String ITEM = "item";
    List<RssItem> list;

    public RssFeedResponse response = null;

    @Override
    protected List<RssItem> doInBackground(String... strings) {
        list = new ArrayList<>();
        XmlPullParser parser = Xml.newPullParser();
        InputStream stream = null;
        try {
            // auto-detect the encoding from the stream
            stream = new URL(strings[0]).openConnection().getInputStream();
            parser.setInput(stream, null);
            int eventType = parser.getEventType();
            boolean done = false;
            RssItem item = null;
            while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase(ITEM)) {
                            item = new RssItem();
                        } else if (item != null) {
                            if (name.equalsIgnoreCase(POINT)){
                                item.setGeorss_point(parser.nextText().trim());
                            } else if (name.equalsIgnoreCase(LINK)) {
                                item.setLink(parser.nextText());
                            } else if (name.equalsIgnoreCase(DESCRIPTION)) {
                                item.setDescription(parser.nextText().trim());
                            } else if (name.equalsIgnoreCase(PUB_DATE)) {
                                item.setPubDate(parser.nextText());
                            } else if (name.equalsIgnoreCase(TITLE)) {
                                item.setTitle(parser.nextText().trim());
                            } else if(name.equalsIgnoreCase(AUTHOR)){
                                item.setAuthor(parser.nextText().trim());
                            } else if(name.equalsIgnoreCase(COMMENT)){
                                item.setComments(parser.nextText().trim());
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase(ITEM) && item != null) {
                            list.add(item);
                        } else if (name.equalsIgnoreCase(CHANNEL)) {
                            done = true;
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    @Override
    protected void onPostExecute(List<RssItem> rssItems) {
        super.onPostExecute(rssItems);
        response.processFinish(rssItems);
    }
}
