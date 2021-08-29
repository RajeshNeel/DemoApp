package com.gaurav.demoapp.pojo;

/**
 * Created by Gaurav Sharma on 30-08-2021 on 03:52 .
 */
public class Location {

    private String Latitude,Longitude,Speed,Accuracy,Altitude;

    public Location(String latitude, String longitude, String speed, String accuracy, String altitude) {
        Latitude = latitude;
        Longitude = longitude;
        Speed = speed;
        Accuracy = accuracy;
        Altitude = altitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getSpeed() {
        return Speed;
    }

    public void setSpeed(String speed) {
        Speed = speed;
    }

    public String getAccuracy() {
        return Accuracy;
    }

    public void setAccuracy(String accuracy) {
        Accuracy = accuracy;
    }

    public String getAltitude() {
        return Altitude;
    }

    public void setAltitude(String altitude) {
        Altitude = altitude;
    }
}
