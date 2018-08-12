package com.summertaker.stocknews.data;

import android.support.annotation.NonNull;

import java.util.Comparator;
import java.util.Date;

public class News {
    private long id;
    private String title;
    private String url;
    private String published;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }
}
