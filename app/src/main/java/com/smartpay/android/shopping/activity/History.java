package com.smartpay.android.shopping.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.lmntrx.android.library.livin.missme.ProgressDialog;
import com.smartpay.android.R;
import com.smartpay.android.shopping.SmartPay;
import com.smartpay.android.shopping.adapter.BillHistoryAdapter;
import com.smartpay.android.shopping.model.BillHistory;
import com.smartpay.android.shopping.model.BillItem;
import com.smartpay.android.shopping.util.Constants;
import com.smartpay.android.shopping.util.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class History extends AppCompatActivity {

    ArrayList<BillHistory> bills;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar!=null;
        actionBar.setTitle("History");

        final RecyclerView billsList = findViewById(R.id.billHistoryRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        billsList.setLayoutManager(layoutManager);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();



        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                Constants.BASE_URL + "bills/" + FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                null,
                response -> {
                    bills = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++){
                        try {

                            JSONObject object = response.getJSONObject(i);
                            String shopName = object.getString("shopName");
                            double amount = object.getDouble("total");
                            String date = object.getString("date");
                            ArrayList<BillItem> items = new ArrayList<>();
                            JSONArray itemsArray = object.getJSONArray("items");
                            for (int j = 0; j < itemsArray.length(); j++){
                                JSONObject itemObject = itemsArray.getJSONObject(j);
                                String itemName = itemObject.getString("name");
                                String itemId = itemObject.getString("itemId");
                                double price = itemObject.getDouble("price");
                                int qty = itemObject.getInt("qty");
                                items.add(new BillItem(itemName,itemId,price,qty));
                            }
                            bills.add(new BillHistory(date,shopName,amount,items));

                            BillHistoryAdapter adapter = new BillHistoryAdapter(History.this, bills);
                            billsList.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    progressDialog.dismiss();
                },
                error -> {
                    error.printStackTrace();
                    progressDialog.dismiss();
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-access-token", Preferences.getAuthToken(History.this));
                return headers;
            }
        };
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constants.VOLLEY_REQUEST_TIMEOUT,
                Constants.VOLLEY_REQUEST_RETRIES,
                Constants.VOLLEY_REQUEST_BACKOFF_MULTIPLIER));
        SmartPay.getInstance(History.this).addToRequestQueue(jsonArrayRequest);

    }

    @Override
    public void onBackPressed() {
        progressDialog.onBackPressed(()->{
            super.onBackPressed();
            return null;
        });
    }
}
