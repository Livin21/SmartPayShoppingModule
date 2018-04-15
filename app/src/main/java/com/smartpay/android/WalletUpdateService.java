package com.smartpay.android;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smartpay.android.payment.Transaction;

import java.util.HashMap;
import java.util.List;

public class WalletUpdateService extends IntentService {

    private static final String ACTION_UPDATE_WALLETS = "com.smartpay.android.action.UPDATE_WALLETS";

    private static final String EXTRA_TO_ADDRESS = "com.smartpay.android.extra.TO_ADDRESS";
    private static final String EXTRA_FROM_ADRESS = "com.smartpay.android.extra.FROM_ADDRESS";
    private static final String EXTRA_BILL_AMOUNT = "com.smartpay.android.extra.BILL_AMOUNT";
    private static final String EXTRA_TIMESTAMP = "com.smartpay.android.extra.TIMESTAMP";

    public WalletUpdateService() {
        super("WalletUpdateService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    public static void updateWallets(Context context, String toAddress, String fromAddress, Double amount, Long when) {
        Intent intent = new Intent(context, WalletUpdateService.class);
        intent.setAction(ACTION_UPDATE_WALLETS);
        intent.putExtra(EXTRA_TO_ADDRESS, toAddress);
        intent.putExtra(EXTRA_FROM_ADRESS, fromAddress);
        intent.putExtra(EXTRA_BILL_AMOUNT, amount);
        intent.putExtra(EXTRA_TIMESTAMP, when);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_WALLETS.equals(action)) {
                final String toAddress = intent.getStringExtra(EXTRA_TO_ADDRESS);
                final String fromAddress = intent.getStringExtra(EXTRA_FROM_ADRESS);
                final double billAmount = intent.getDoubleExtra(EXTRA_BILL_AMOUNT, 0.0);
                final Long timeStamp = intent.getLongExtra(EXTRA_TIMESTAMP, System.currentTimeMillis());
                handleActionUpdateWallets(toAddress, fromAddress, billAmount, timeStamp);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateWallets(String toAddress, String fromAddress, double billAmount, Long timestamp) {

        FirebaseFirestore.getInstance().collection("wallets")
                .get().addOnCompleteListener(
                task -> {
                    boolean toDone = false, fromDone = false;
                    if (task.isSuccessful()){
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        for (DocumentSnapshot d: documents){

                            if (!toDone && d.getString("address").equals(toAddress)){
                                HashMap<String, Object> update = new HashMap<>();
                                update.put("balance", d.getDouble("balance") + billAmount);
                                FirebaseFirestore.getInstance().collection("wallets")
                                        .document(d.getId()).update(update);
                                toDone = true;

                                Transaction transaction = new Transaction();
                                transaction.setAmount(billAmount);
                                transaction.toAddress = toAddress;
                                transaction.fromAddress = fromAddress;
                                transaction.when = timestamp;

                                FirebaseFirestore.getInstance().collection("wallets")
                                        .document(d.getId()).collection("transactions")
                                        .add(transaction);

                            }

                            if (!fromDone && d.getString("address").equals(fromAddress)){
                                HashMap<String, Object> update = new HashMap<>();
                                update.put("balance", d.getDouble("balance") - billAmount);
                                FirebaseFirestore.getInstance().collection("wallets")
                                        .document(d.getId()).update(update);
                                fromDone = true;
                            }

                            if (toDone && fromDone){
                                break;
                            }

                        }
                    }
                }
        );

    }
}
