package com.smartpay.android.model;


import com.google.android.gms.maps.model.LatLng;

public class Shop {

    private String shopName;
    private LatLng location;
    private String city;

    public Shop(String shopName, String city, double lat, double lng) {
        this.shopName = shopName;
        this.city = city;
        location = new LatLng(lat,lng);
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
