package com.smartpay.android.payment;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.smartpay.android.shopping.util.Preferences;

import java.util.HashMap;
import java.util.Random;

/***
 * Created by Livin Mathew <livin@acoustike.com> on 25/2/18.
 */


public class Wallet {
    private float balance;
    private String address;
    private long timestamp;

    private static final float BONUS_CREDIT = 500;

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

    public static String getPrivateKey(Context context){
        return Preferences.getPrivateKey(context);
    }

    public static Wallet createWallet(Context context){
        Wallet wallet = new Wallet();
        wallet.address = FirebaseAuth.getInstance().getCurrentUser().getUid();
        wallet.balance = BONUS_CREDIT;
        wallet.timestamp = System.currentTimeMillis();
        generatePrivateKey(context);
        return wallet;
    }

    public HashMap<String,String> serialize(){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("address",address);
        hashMap.put("balance",String.valueOf(balance));
        hashMap.put("timestamp",String.valueOf(timestamp));
        return hashMap;
    }

    static void generatePrivateKey(Context context) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        Preferences.savePrivateKey(context, salt.toString());
    }

    public long getTimestamp() {
        return timestamp;
    }
}
