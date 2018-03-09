package com.smartpay.android.payment;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smartpay.android.shopping.util.Preferences;

import java.io.Serializable;

/***
 * Created by Livin Mathew <livin@acoustike.com> on 9/3/18.
 */


public class Transaction implements Serializable {
    private String fromAddress;
    private String toAddress;
    private Long when;
    private Double amount;

    public String getFromAddress() {
        return fromAddress;
    }

    void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public Long getWhen() {
        return when;
    }

    public void setWhen(Long when) {
        this.when = when;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    void execute(Context context, final Wallet.OnTransactionCompleteListener onTransactionCompleteListener) {
        FirebaseFirestore.getInstance().collection("wallets")
                .document(Preferences.getDocumentReference(context))
                .collection("transactions")
                .add(this)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        onTransactionCompleteListener.onComplete();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onTransactionCompleteListener.onError(e.getMessage());
                    }
                });
    }

    static class Builder {

        Transaction transaction;

        Builder(){
            transaction = new Transaction();
        }

        Builder amount(double billAmount) {
            transaction.setAmount(billAmount);
            return this;
        }

        Builder toAddress(String toAddress) {
            transaction.setToAddress(toAddress);
            return this;
        }

        Builder fromAddress(String fromAddress) {
            transaction.setFromAddress(fromAddress);
            return this;
        }

        Transaction build() {
            return transaction;
        }
    }
}
