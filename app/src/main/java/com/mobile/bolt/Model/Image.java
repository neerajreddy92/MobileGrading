package com.mobile.bolt.Model;

/**
 * Created by Neeraj on 2/24/2016.
 */

public class Image {
    private int id;
    private String ASU_ID;

    public void setId(int id) {
        this.id = id;
    }

    private String Location;

    public int getId() {
        return id;
    }

    public String getASU_ID() {
        return ASU_ID;
    }

    public void setASU_ID(String ASU_ID) {
        this.ASU_ID = ASU_ID;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    @Override
    public String toString(){
        return id+" "+ASU_ID+" "+Location;
    }
}
