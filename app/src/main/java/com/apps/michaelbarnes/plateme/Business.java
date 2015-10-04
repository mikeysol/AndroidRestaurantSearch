package com.apps.michaelbarnes.plateme;

import java.util.ArrayList;
import java.util.Arrays;

class Business {
    private final String name;
    private final String url;
    private final String image_url;
    private final Number rating;
    private final Number distance;
    private final String display_phone;
    private final ArrayList<String> location;
    private final Number latitude;
    private final Number longitude;
    private final ArrayList<Object> children;

    public Business(String name, String url, String image_url,
                    Number rating, Number distance, String display_phone,
                    ArrayList<String> location, Number latitude, Number longitude) {
        this.name = name;
        this.url = url;
        this.image_url = image_url;
        this.rating = rating;
        this.distance = distance;
        this.display_phone = display_phone;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.children = new ArrayList<Object>(Arrays.asList(url, image_url, rating, distance,
                display_phone, location, latitude, longitude));
    }


    public String getName() {
        return name;
    }


    public String getUrl() {
        return url;
    }


    public String getImage_url() {
        return image_url;
    }


    public Number getRating() {
        return rating;
    }


    public Number getDistance() {
        return distance;
    }


    public String getDisplay_phone() {
        return display_phone;
    }


    public ArrayList<Object> getChildren() {
        return children;
    }


    public ArrayList<String> getLocation() {
        return location;
    }


    public Number getLatitude() {
        return latitude;
    }


    public Number getLongitude() {
        return longitude;
    }


    @Override
    public String toString() {
        return name;
    }
}
