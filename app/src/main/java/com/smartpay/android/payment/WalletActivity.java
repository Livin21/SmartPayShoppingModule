package com.smartpay.android.payment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import com.smartpay.android.R;
import com.smartpay.android.shopping.qrcode.QRCodeHandler;

public class WalletActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        setPrivateKeyQRCode();
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

