package com.smartpay.android.payment;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smartpay.android.shopping.util.Preferences;

import java.util.HashMap;
import java.util.Random;

/***
 * Created by Livin Mathew <livin@acoustike.com> on 25/2/18.
 */


public class Wallet {
    private Double balance;
    private String address;
    private long timestamp;

    private static final Double BONUS_CREDIT = 500.0;

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
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

    public static Wallet getWallet(Context context){
        Wallet wallet =  new Wallet();
        Task<DocumentSnapshot> documentFetch = FirebaseFirestore.getInstance().collection("wallets").document(Preferences.getDocumentReference(context)).get();
        if (documentFetch.isSuccessful()){
            DocumentSnapshot document = documentFetch.getResult();
            wallet.setBalance(document.getDouble("balance"));
            wallet.setAddress(document.getString("address"));
        }else {
            Log.d("Wallet","Not Created yet");
        }
        return wallet;
    }

    public void addTransaction(String toAddress, double billAmount, OnTransactionCompleteListener onTransactionCompleteListener) {
        Transaction transaction = new Transaction.Builder()
                .amount(billAmount)
                .toAddress(toAddress)
                .fromAddress(address)
                .build();
        transaction.execute(onTransactionCompleteListener);
    }

    public interface OnTransactionCompleteListener {
        void onComplete();
        void onError(String error);
    }
}
