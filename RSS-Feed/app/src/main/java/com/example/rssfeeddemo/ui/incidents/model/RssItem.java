package com.example.rssfeeddemo.ui.incidents.model;

//S1826060 Scott Derek Robertson
public class RssItem {
    private String pubDate;
    private String description;
    private String link;
    private String title;
    private String georss_point;
    private String author;
    private String comments;

    public RssItem() {
    }

    public RssItem(String title, String link) {
        this.title = title;
        this.link = link;
    }
    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGeorss_point() {
        return georss_point;
    }

    public void setGeorss_point(String georss_point) {
        this.georss_point = georss_point;
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
}
