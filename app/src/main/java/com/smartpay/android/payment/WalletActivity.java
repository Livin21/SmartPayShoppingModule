package com.smartpay.android.payment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    TextView balanceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        setPrivateKeyQRCode();

        balanceTextView = findViewById(R.id.balanceTextView);
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

    public void rechargeWallet(View view) {
        final EditText editText =  new EditText(this);
        editText.setHint("Enter amount");
        editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        new AlertDialog.Builder(this)
                .setView(editText)
                .setPositiveButton("Buy Credits", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        final ProgressDialog progressDialog = new ProgressDialog(WalletActivity.this);
                        try {
                            Double rechargeAmount = Double.parseDouble(editText.getText().toString());
                            if (rechargeAmount > 0 && rechargeAmount <= 10000){
                                dialogInterface.dismiss();
                                progressDialog.setMessage("Please Wait...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                Wallet.recharge(rechargeAmount, new Wallet.OnRechargeComplete(){
                                    @Override
                                    public void onComplete(Double newBalance){
                                        progressDialog.dismiss();
                                        balanceTextView.setText(String.valueOf(newBalance));
                                        Toast.makeText(WalletActivity.this, "Wallet Recharged", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else {
                                Toast.makeText(WalletActivity.this, "Recharge amount should be greater than 0 and less than 10000", Toast.LENGTH_LONG).show();
                            }
                        }catch (NumberFormatException e){
                            Toast.makeText(WalletActivity.this, "Enter a valid amount", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

    }
}

