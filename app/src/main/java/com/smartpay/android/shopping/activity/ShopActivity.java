package com.smartpay.android.shopping.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.smartpay.android.R;
import com.smartpay.android.shopping.SmartShopper;
import com.smartpay.android.shopping.adapter.ScannedItemsListAdapter;
import com.smartpay.android.shopping.dialog.ItemDetailsDialog;
import com.smartpay.android.shopping.model.Bill;
import com.smartpay.android.shopping.model.Item;
import com.smartpay.android.shopping.util.Constants;
import com.smartpay.android.shopping.util.Preferences;

public class ShopActivity extends AppCompatActivity {

    ArrayList<Item> items;

    private CompoundBarcodeView barcodeView;

    RecyclerView recyclerView;
    ScannedItemsListAdapter scannedItemsListAdapter;

    int count = 0;

    String shopId, shopName;

    BarcodeCallback barcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            Toast.makeText(ShopActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
            if (result.getBarcodeFormat().equals(BarcodeFormat.CODE_128)){
                barcodeView.pause();
                final ProgressDialog progressDialog = new ProgressDialog(ShopActivity.this);
                progressDialog.setMessage("Adding...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                String itemID = result.getText();
                JsonObjectRequest itemDetailsRequest = new JsonObjectRequest(
                        Request.Method.GET,
                        Constants.BASE_URL + "items/i/" + itemID,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                progressDialog.dismiss();
                                try {
                                    String id = response.getString("_id");
                                    String name = response.getString("name");
                                    double price = response.getDouble("price");
                                    Boolean offer = response.getBoolean("offer");
                                    String shopId = response.getString("shopId");

                                    items.add(
                                            new Item(
                                                    id,
                                                    name,
                                                    price,
                                                    offer,
                                                    shopId,
                                                    1
                                            )
                                    );

                                    new ItemDetailsDialog(ShopActivity.this,items,count++,scannedItemsListAdapter,barcodeView).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(ShopActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ShopActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                ){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("x-access-token", Preferences.getAuthToken(ShopActivity.this));
                        return headers;
                    }
                };
                itemDetailsRequest.setRetryPolicy(new DefaultRetryPolicy(
                        Constants.VOLLEY_REQUEST_TIMEOUT,
                        Constants.VOLLEY_REQUEST_RETRIES,
                        Constants.VOLLEY_REQUEST_BACKOFF_MULTIPLIER));
                SmartShopper.getInstance(ShopActivity.this).addToRequestQueue(itemDetailsRequest);

            }else {
                Toast.makeText(ShopActivity.this, "Not a valid barcode", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar!=null;
        actionBar.setTitle("ShopActivity");

        items = new ArrayList<>();

        shopId = getIntent().getStringExtra("shopId");
        shopName = getIntent().getStringExtra("shopName");

        recyclerView = findViewById(R.id.scannedItemsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        scannedItemsListAdapter = new ScannedItemsListAdapter(this, items);
        recyclerView.setAdapter(scannedItemsListAdapter);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ShopActivity.this,
                    new String[]{android.Manifest.permission.CAMERA},
                    1);

        }else {
            barcodeView = findViewById(R.id.barcode_scanner);
            assert barcodeView != null;
            barcodeView.decodeContinuous(barcodeCallback);
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
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit shopping?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ShopActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            count--;
            scannedItemsListAdapter = new ScannedItemsListAdapter(ShopActivity.this, items);
            recyclerView.swapAdapter(scannedItemsListAdapter, true);

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver,new IntentFilter(Constants.DELETE_INTENT));
        try {
            barcodeView.resume();
        }catch (NullPointerException ignored){}
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shop,menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        try {
            barcodeView.pause();
        }catch (NullPointerException ignored){}
    }



    public void finishShopping(MenuItem item) {

        final Bill bill = new Bill(items);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Finishing...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            final JSONObject data = new JSONObject();
            final JSONObject form_data = new JSONObject();
            data.put("userId", FirebaseAuth.getInstance().getCurrentUser().getEmail());
            data.put("shopId", shopId);
            data.put("shopName", shopName);
            data.put("total", bill.getAmount());

            JSONArray itemData = new JSONArray();

            for (Item item1 : bill.getItems()){
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("itemId",item1.getId());
                    jsonObject.put("name",item1.getItemName());
                    jsonObject.put("price",item1.getPrice());
                    jsonObject.put("qty",item1.getQty());
                    itemData.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            data.put("items",itemData);

            form_data.put("form_data",data);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    Constants.BASE_URL + "bills/",
                    form_data,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getBoolean("status")){
                                    Toast.makeText(ShopActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                                    Intent finishShopping = new Intent(ShopActivity.this,FinishShopping.class);
                                    finishShopping.putExtra("BILL",bill.generateBill());
                                    finishShopping.putExtra("NO_OF_ITEMS",bill.getNoOfItems());
                                    finishShopping.putExtra("AMOUNT",bill.getAmount());
                                    startActivity(finishShopping);
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                                Toast.makeText(ShopActivity.this, "Bill Not Saved", Toast.LENGTH_SHORT).show();
                                System.out.print(form_data.toString());
                                Toast.makeText(ShopActivity.this, form_data.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(ShopActivity.this, "Bill Not Saved", Toast.LENGTH_SHORT).show();
                            System.out.print(data.toString());
                            Toast.makeText(ShopActivity.this, data.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("x-access-token", Preferences.getAuthToken(ShopActivity.this));
                    return headers;
                }
            };
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    Constants.VOLLEY_REQUEST_TIMEOUT,
                    Constants.VOLLEY_REQUEST_RETRIES,
                    Constants.VOLLEY_REQUEST_BACKOFF_MULTIPLIER));
            SmartShopper.getInstance(ShopActivity.this).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
            progressDialog.dismiss();
            Toast.makeText(this, "Bill Not Saved", Toast.LENGTH_SHORT).show();
        }
    }
}
