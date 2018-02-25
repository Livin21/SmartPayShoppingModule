package com.smartpay.android.shopping.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.smartpay.android.R;
import com.smartpay.android.shopping.SmartPay;
import com.smartpay.android.shopping.util.Constants;
import com.smartpay.android.shopping.util.Preferences;

public class ScannerActivity extends AppCompatActivity {


    private CompoundBarcodeView barcodeView;

    private String shopsEndpointUrl = Constants.BASE_URL + "shops/connect/";

    BarcodeCallback barcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(final BarcodeResult result) {
            Toast.makeText(ScannerActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
            if (result.getBarcodeFormat().equals(BarcodeFormat.QR_CODE)){
                barcodeView.pause();
                final ProgressDialog progressDialog = new ProgressDialog(ScannerActivity.this);
                progressDialog.setMessage("Connecting...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                shopsEndpointUrl = shopsEndpointUrl + result.getText();

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.GET,
                        shopsEndpointUrl,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                progressDialog.dismiss();
                                try {
                                    Toast.makeText(ScannerActivity.this, "Connected to " + response.get("name").toString() + "\nHappy Shopping :)", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(ScannerActivity.this,ShopActivity.class);
                                    intent.putExtra("shopId", result.getText());
                                    intent.putExtra("shopName", response.getString("name"));
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(ScannerActivity.this, "Invalid Code", Toast.LENGTH_SHORT).show();
                                }finally {
                                    finish();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ScannerActivity.this, "Invalid Code", Toast.LENGTH_SHORT).show();
                                error.printStackTrace();
                                progressDialog.dismiss();
                                finish();
                            }
                        }
                ){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("x-access-token", Preferences.getAuthToken(ScannerActivity.this));
                        return headers;
                    }
                };
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        Constants.VOLLEY_REQUEST_TIMEOUT,
                        Constants.VOLLEY_REQUEST_RETRIES,
                        Constants.VOLLEY_REQUEST_BACKOFF_MULTIPLIER));
                SmartPay.getInstance(ScannerActivity.this).addToRequestQueue(jsonObjectRequest);
            }else {
                Toast.makeText(ScannerActivity.this, "Not a valid code", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar!=null;
        actionBar.setTitle("Scan");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ScannerActivity.this,
                    new String[]{android.Manifest.permission.CAMERA},
                    1);

        }else {
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
        }catch (NullPointerException ignored){}
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
        }catch (NullPointerException ignored){}
    }
}
