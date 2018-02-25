package com.smartpay.android.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;

import com.smartpay.android.R;
import com.smartpay.android.qrcode.QRCodeHandler;

public class FinishShopping extends AppCompatActivity {

    ImageView qrCodeImageView;
    TextView amount,items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_shopping);

        String bill = getIntent().getStringExtra("BILL");
        double amt = getIntent().getDoubleExtra("AMOUNT",0);
        int noofitems = getIntent().getIntExtra("NO_OF_ITEMS",0);
        qrCodeImageView = findViewById(R.id.qrcodeimageview);
        try {
            qrCodeImageView.setImageBitmap(QRCodeHandler.generateQRCode(bill,getScreenRes()));
        } catch (WriterException e) {
            e.printStackTrace();
        }
        amount = findViewById(R.id.totalamount);
        amount.setText("Total Amount = Rs."+amt);
        items = findViewById(R.id.totalitems);
        items.setText(noofitems+" Items Purchased");

    }
    public int getScreenRes() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.densityDpi;
    }
}
