package com.smartpay.android.model;


import java.io.Serializable;

public class BillItem implements Serializable{

    private String itemId;
    private double price;
    private int qty;
    private String itemName;

    public BillItem(String itemName, String itemId, double price, int qty) {
        this.itemId = itemId;
        this.price = price;
        this.qty = qty;
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemId() {
        return itemId;
    }

    public double getPrice() {
        return price;
    }

    public int getQty() {
        return qty;
    }
}
