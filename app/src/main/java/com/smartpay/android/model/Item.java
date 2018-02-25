package com.smartpay.android.model;

public class Item {

    private String itemName;
    private Double price;
    private boolean hasOffer;
    private String shopID;
    private int qty;
    private String id;

    public Item(String id, String itemName, Double price, boolean hasOffer, String shopID, int qty) {
        this.itemName = itemName;
        this.price = price;
        this.hasOffer = hasOffer;
        this.shopID = shopID;
        this.qty = qty;
        this.id = id;
    }

    public int getQty() {
        return qty;
    }

    public String getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public Double getPrice() {
        return price;
    }

    public boolean hasOffer() {
        return hasOffer;
    }

    public String getShopID() {
        return shopID;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
