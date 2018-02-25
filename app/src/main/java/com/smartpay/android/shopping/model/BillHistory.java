package com.smartpay.android.shopping.model;


import java.util.ArrayList;

public class BillHistory {
    private String date;
    private String shopName;
    private double amount;
    private ArrayList<BillItem> items;

    public BillHistory(String date, String shopName, double amount, ArrayList<BillItem> items) {
        this.date = date;
        this.shopName = shopName;
        this.amount = amount;
        this.items = items;
    }

    public String getDate() {
        return date;
    }

    public String getShopName() {
        return shopName;
    }

    public double getAmount() {
        return amount;
    }

    public ArrayList<BillItem> getItems() {
        return items;
    }

}
