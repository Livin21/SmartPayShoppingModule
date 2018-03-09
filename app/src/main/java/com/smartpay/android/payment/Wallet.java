package com.smartpay.android.payment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.smartpay.android.shopping.util.Preferences;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/***
 * Created by Livin Mathew <livin@acoustike.com> on 25/2/18.
 */


public class Wallet {
    private Double balance;
    private String address;
    private long timestamp;
    private String documentReference;

    public String getDocumentReference() {
        return documentReference;
    }

    public void setDocumentReference(String documentReference) {
        this.documentReference = documentReference;
    }

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

    public static void getWallet(Context context, final Wallet.OnWalletFetchCompletedListener listener){
        Task<DocumentSnapshot> documentFetch = FirebaseFirestore.getInstance().collection("wallets").document(Preferences.getDocumentReference(context)).get();
        documentFetch.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    Wallet wallet =  new Wallet();
                    wallet.setBalance(document.getDouble("balance"));
                    wallet.setAddress(document.getString("address"));
                    listener.onComplete(wallet);
                }else {
                    Log.d("Wallet","Not Created yet");
                    listener.onError("No Wallet Found");
                }
            }
        });
    }

    public void addTransaction(Context context, String toAddress, double billAmount, OnTransactionCompleteListener onTransactionCompleteListener) {
        Transaction transaction = new Transaction.Builder()
                .amount(billAmount)
                .toAddress(toAddress)
                .fromAddress(address)
                .build();
        transaction.execute(context, onTransactionCompleteListener);
    }

    static void getWallet(final String address, final Wallet.OnWalletFetchCompletedListener listener) {
        FirebaseFirestore.getInstance().collection("wallets").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){

                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    for (DocumentSnapshot document : documents) {
                        if (document.getString("address").equals(address)) {
                            Wallet wallet = new Wallet();
                            wallet.setAddress(address);
                            wallet.setBalance(document.getDouble("balance"));
                            wallet.setTimestamp(document.getLong("timestamp"));
                            wallet.setDocumentReference(document.getReference().getId());
                            listener.onComplete(wallet);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public interface OnTransactionCompleteListener {
        void onComplete();
        void onError(String error);
    }

    public interface OnWalletFetchCompletedListener {
        void onComplete(Wallet wallet);
        void onError(String s);
    }
}
