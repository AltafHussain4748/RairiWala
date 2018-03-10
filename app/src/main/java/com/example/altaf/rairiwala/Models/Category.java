package com.example.altaf.rairiwala.Models;

/**
 * Created by AltafHussain on 3/3/2018.
 */

public class Category {
    private  int  categroy_id;
    private  String  categroy_name;
    private  String  categroy_image;

    public int getCategroy_id() {
        return categroy_id;
    }

    public String getCategroy_name() {
        return categroy_name;
    }

    public String getCategroy_image() {
        return categroy_image;
    }

    public void setCategroy_id(int categroy_id) {
        this.categroy_id = categroy_id;
    }

    public void setCategroy_name(String categroy_name) {
        this.categroy_name = categroy_name;
    }

    public void setCategroy_image(String categroy_image) {
        this.categroy_image = categroy_image;
    }
}
