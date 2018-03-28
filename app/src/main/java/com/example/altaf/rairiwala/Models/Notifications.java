package com.example.altaf.rairiwala.Models;

/**
 * Created by AltafHussain on 3/27/2018.
 */

public class Notifications {
    private String tag;
    private String title;
    private String message;

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTag() {
        return tag;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
