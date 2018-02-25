package com.smartpay.android.payment;

/***
 * Created by Livin Mathew <livin@acoustike.com> on 25/2/18.
 */


public class Wallet {
    private float balance;
    private String address;
    private String privateKey;

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
