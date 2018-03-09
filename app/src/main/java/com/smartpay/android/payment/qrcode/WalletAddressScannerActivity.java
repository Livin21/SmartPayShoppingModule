package com.smartpay.android.payment.qrcode;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;
import com.smartpay.android.R;
import com.smartpay.android.payment.Wallet;

import java.util.List;

public class WalletAddressScannerActivity extends AppCompatActivity {

    BarcodeView barcodeView;


    private double billAmount;

    BarcodeCallback barcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getBarcodeFormat().equals(BarcodeFormat.QR_CODE)){
                barcodeView.pause();
                final ProgressDialog progressDialog = new ProgressDialog(WalletAddressScannerActivity.this);
                progressDialog.setMessage("Processing Payment. Please Wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                Wallet wallet = Wallet.getWallet(WalletAddressScannerActivity.this);
                wallet.addTransaction(result.getResult().getText(), billAmount, new Wallet.OnTransactionCompleteListener(){
                    @Override
                    public void onComplete(){

                    }
                    @Override
                    public void onError(String error){

                    }
                });


            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_address_scanner);

        barcodeView = findViewById(R.id.barcode_scanner);


        billAmount = getIntent().getDoubleExtra("BILL_AMOUNT",0);
    }
}
