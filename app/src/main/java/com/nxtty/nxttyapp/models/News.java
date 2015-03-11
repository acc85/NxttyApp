package com.nxtty.nxttyapp.models;

import android.os.AsyncTask;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Raymond on 14/02/2015.
 */
@Table(name="News")
public class News extends Model{

    private String title, desc, url, src;

    public News(String title, String desc, String url, String src) {
        super();
        this.title = title;
        this.desc = desc;
        this.url = url;
        this.src = src;
    }

    public News() {
        super();
        this.title = "";
        this.desc = "";
        this.url = "";
        this.src = "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

}
