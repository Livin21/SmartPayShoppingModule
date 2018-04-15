package com.smartpay.android.payment;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.smartpay.android.WalletUpdateService;
import com.smartpay.android.shopping.util.Preferences;

import java.io.Serializable;

/***
 * Created by Livin Mathew <livin@acoustike.com> on 9/3/18.
 */


public class Transaction implements Serializable {

    /* Should be public */
    public String fromAddress;
    public String toAddress;
    public Long when;
    public Double amount;

    void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    void setWhen(Long when) {
        this.when = when;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    void execute(final Context context, final Wallet.OnTransactionCompleteListener onTransactionCompleteListener) {

        // Add transaction details to transactions table
        FirebaseFirestore.getInstance().collection("wallets")
                .document(Preferences.getDocumentReference(context))
                .collection("transactions")
                .add(this)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        Log.d("TransactionProcessing", "Transaction Added");
                        onTransactionCompleteListener.onComplete();

                        WalletUpdateService.updateWallets(context, toAddress, fromAddress, amount, when);

                    } else {
                        onTransactionCompleteListener.onError("Transaction couldn't be completed");
                    }
                })
                .addOnFailureListener(e -> onTransactionCompleteListener.onError(e.getMessage()));

    }

    static class Builder {

        Transaction transaction;

        Builder() {
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
            transaction.setWhen(System.currentTimeMillis());
            return transaction;
        }
    }
}
