package com.smartpay.android.payment.qrcode;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.lmntrx.android.library.livin.missme.ProgressDialog;
import com.smartpay.android.R;
import com.smartpay.android.payment.Wallet;
import com.smartpay.android.payment.WalletActivity;
import com.smartpay.android.shopping.activity.MainActivity;

import java.util.List;

public class WalletAddressScannerActivity extends AppCompatActivity {

    CompoundBarcodeView barcodeView;

    ProgressDialog progressDialog;

    private double billAmount;

    BarcodeCallback barcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(final BarcodeResult result) {
            Toast.makeText(WalletAddressScannerActivity.this, result.getResult().getText(), Toast.LENGTH_SHORT).show();
            if (result.getBarcodeFormat().equals(BarcodeFormat.QR_CODE)) {
                barcodeView.pause();
                progressDialog = new ProgressDialog(WalletAddressScannerActivity.this);
                progressDialog.setMessage("Processing Payment. Please Wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                Wallet.getWallet(WalletAddressScannerActivity.this, new Wallet.OnWalletFetchCompletedListener() {
                    @Override
                    public void onComplete(Wallet wallet) {
                        if (wallet.getBalance() >= billAmount)
                            wallet.addTransaction(WalletAddressScannerActivity.this, result.getResult().getText(), billAmount, new Wallet.OnTransactionCompleteListener() {
                                @Override
                                public void onComplete() {
                                    progressDialog.dismiss();
                                    Toast.makeText(WalletAddressScannerActivity.this, "Payment Complete", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(WalletAddressScannerActivity.this, MainActivity.class));
                                    finish();
                                }

                                @Override
                                public void onError(String error) {
                                    Toast.makeText(WalletAddressScannerActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(WalletAddressScannerActivity.this, "You don't have enough credits to complete this purchase. Please recharge your wallet.", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(WalletAddressScannerActivity.this, WalletActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onError(String s) {
                        Toast.makeText(WalletAddressScannerActivity.this, s, Toast.LENGTH_SHORT).show();
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


        billAmount = getIntent().getDoubleExtra("BILL_AMOUNT", 0);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(WalletAddressScannerActivity.this,
                    new String[]{android.Manifest.permission.CAMERA},
                    1);

        } else {
            barcodeView = findViewById(R.id.barcode_scanner);
            assert barcodeView != null;
            barcodeView.decodeSingle(barcodeCallback);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                barcodeView = findViewById(R.id.barcode_scanner);
                assert barcodeView != null;
                barcodeView.decodeSingle(barcodeCallback);
                break;
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            barcodeView.resume();
        } catch (NullPointerException ignored) {
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            barcodeView.pause();
        } catch (NullPointerException ignored) {
        }
    }

    @Override
    public void onBackPressed() {
        progressDialog.onBackPressed(
                () -> {
                    super.onBackPressed();
                    return null;
                }
        );
    }
}
