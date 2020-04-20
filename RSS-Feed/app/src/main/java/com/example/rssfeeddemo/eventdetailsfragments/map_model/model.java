package com.example.rssfeeddemo.eventdetailsfragments.map_model;

import com.google.android.gms.maps.model.LatLng;

//S1826060 Scott Derek Robertson
public class model {

    private String Name;
    private String location;
    private String start_date;
    private String end_date;
    private String delay_information;
    private String link;
    private String author;
    private String comments;
    private String pubDate;
    private LatLng latLng;

    public model() {
    }

    public model(String name, String location, String start_date, String end_date, String delay_information, String link, String author, String comments, String pubDate, LatLng latLng) {
        Name = name;
        this.location = location;
        this.start_date = start_date;
        this.end_date = end_date;
        this.delay_information = delay_information;
        this.link = link;
        this.author = author;
        this.comments = comments;
        this.pubDate = pubDate;
        this.latLng = latLng;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getDelay_information() {
        return delay_information;
    }

    public void setDelay_information(String delay_information) {
        this.delay_information = delay_information;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
