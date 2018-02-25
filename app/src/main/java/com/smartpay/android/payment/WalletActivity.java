package com.smartpay.android.payment;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.WriterException;
import com.smartpay.android.R;
import com.smartpay.android.shopping.qrcode.QRCodeHandler;
import com.smartpay.android.shopping.util.Preferences;

public class WalletActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        setPrivateKeyQRCode();

        final TextView balanceTextView = findViewById(R.id.balanceTextView);
        final TextView addressTextView = findViewById(R.id.walletAddressTextView);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating Wallet...");
        progressDialog.show();

        FirebaseFirestore.getInstance().collection("wallets")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.getId().equals(Preferences.getDocumentReference(WalletActivity.this))){
                                    balanceTextView.setText(String.valueOf(document.getDouble("balance")));
                                    addressTextView.setText(document.getString("address"));
                                    break;
                                }
                            }
                        } else {
                            Log.w("WalletActivity", "Error getting documents.", task.getException());
                        }
                    }
                });

    }

    public void generatePrivateKey(View view) {
        Wallet.generatePrivateKey(this);
        setPrivateKeyQRCode();
    }

    private void setPrivateKeyQRCode() {
        ImageView privateKeyImageView = findViewById(R.id.privateKeyImageView);
        try {
            privateKeyImageView.setImageBitmap(QRCodeHandler.generateQRCode(Wallet.getPrivateKey(this),getScreenRes()));
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }


    public int getScreenRes() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.densityDpi;
    }

}

