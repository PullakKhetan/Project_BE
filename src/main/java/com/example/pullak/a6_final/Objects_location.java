package com.example.pullak.a6_final;

/**
 * Created by Pullak Khetan on 27-May-17.
 */

public class Objects_location {
    private String address;
    private String latitude;
    private String longitude;
    private String date;
    private String time;

    public Objects_location(String address, String latitude, String longitude, String date, String time) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
