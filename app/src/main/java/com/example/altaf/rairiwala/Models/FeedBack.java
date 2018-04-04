package com.example.altaf.rairiwala.Models;

/**
 * Created by AltafHussain on 3/30/2018.
 */

public class FeedBack {
    private int vendor_id;
    private int stars;
    private String description;
    private String date;
    private String poster_name;
    private String type;

    public void setVendor_id(int vendor_id) {
        this.vendor_id = vendor_id;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPoster_name(String poster_name) {
        this.poster_name = poster_name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getVendor_id() {
        return vendor_id;
    }

    public int getStars() {
        return stars;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getPoster_name() {
        return poster_name;
    }

    public String getType() {
        return type;
    }
}
