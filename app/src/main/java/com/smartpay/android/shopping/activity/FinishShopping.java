package com.smartpay.android.shopping.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;

import com.smartpay.android.R;
import com.smartpay.android.payment.nfc.AddressGrabberActivity;
import com.smartpay.android.payment.nfc.BeamPayActivity;
import com.smartpay.android.shopping.qrcode.QRCodeHandler;

public class FinishShopping extends AppCompatActivity {

    ImageView qrCodeImageView;
    TextView amount,items;
    double amt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_shopping);

        String bill = getIntent().getStringExtra("BILL");
        amt = getIntent().getDoubleExtra("AMOUNT",0);
        int noOfItems = getIntent().getIntExtra("NO_OF_ITEMS",0);
        qrCodeImageView = findViewById(R.id.qrcodeimageview);
        try {
            qrCodeImageView.setImageBitmap(QRCodeHandler.generateQRCode(bill,getScreenRes()));
        } catch (WriterException e) {
            e.printStackTrace();
        }
        amount = findViewById(R.id.totalamount);
        amount.setText(getString(R.string.total_amount, amt));
        items = findViewById(R.id.totalitems);
        items.setText(getString(R.string.no_of_items_purchased, noOfItems));

    }
    public int getScreenRes() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.densityDpi;
    }

    public void launchPaymentModule(View view) {
        Intent intent = new Intent(this, BeamPayActivity.class);
        intent.putExtra("BILL_AMOUNT", amt);
        startActivity(intent);
    }
}
