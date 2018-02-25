package com.smartpay.android.shopping.model;


import java.util.ArrayList;

public class Bill {
    private ArrayList<Item> items;
    private double amount = 0;
    private String billData = "";
    private int count = 1;

    public Bill(ArrayList<Item> items) {
        this.items = items;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public String generateBill(){
        for (Item item : items){
            billData += count++ + ". ";
            billData += item.getItemName() + " ";
            billData += item.getQty() + " ";
            billData += item.getPrice() + "\n";
        }
        amount = getAmount();
        billData += "Total= " + amount + "\n\n";
        return billData;
    }

    public double getAmount() {

        amount = 0;

        for (Item item : items){
            amount += (item.getPrice()*item.getQty());
        }

        return amount;
    }

    public int getNoOfItems() {
        return items.size();
    }
}
