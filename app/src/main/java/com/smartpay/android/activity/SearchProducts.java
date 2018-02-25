package com.smartpay.android.activity;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.smartpay.android.R;
import com.smartpay.android.SmartShopper;
import com.smartpay.android.adapter.SearchItemListAdapter;
import com.smartpay.android.model.Item;
import com.smartpay.android.util.Constants;
import com.smartpay.android.util.Preferences;

public class SearchProducts extends AppCompatActivity {

    RecyclerView searchResultsView;

    HashMap<String,String> shops;
    ArrayList<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Search");

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        searchResultsView = findViewById(R.id.searchItemsRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        searchResultsView.setLayoutManager(mLayoutManager);

        shops = new HashMap<>();
        items = new ArrayList<>();

        JsonArrayRequest shopsListRequest = new JsonArrayRequest(
                Request.Method.GET,
                Constants.BASE_URL + "shops/",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        for (int i = 0; i < response.length(); i++){
                            try {
                                JSONObject shop = response.getJSONObject(i);
                                shops.put(shop.getString("_id"),shop.getString("name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        JsonArrayRequest itemsRequest = new JsonArrayRequest(
                                Request.Method.GET,
                                Constants.BASE_URL + "items/",
                                null,
                                new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        for (int i = 0; i < response.length(); i++){
                                            try {
                                                JSONObject item = response.getJSONObject(i);
                                                items.add(
                                                        new Item(item.getString("_id"),
                                                                item.getString("name"),
                                                                item.getDouble("price"),
                                                                item.getBoolean("offer"),
                                                                item.getString("shopId"),
                                                                0
                                                        ));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        progressDialog.dismiss();
                                        Toast.makeText(SearchProducts.this, "Search module initiation complete", Toast.LENGTH_SHORT).show();
                                        searchResultsView.setAdapter(new SearchItemListAdapter(
                                                SearchProducts.this,
                                                items,
                                                shops
                                        ));
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(SearchProducts.this, "Failed to initiated search module", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        finish();
                                    }
                                }
                        ){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("x-access-token", Preferences.getAuthToken(SearchProducts.this));
                                return headers;
                            }
                        };
                        itemsRequest.setRetryPolicy(new DefaultRetryPolicy(
                                Constants.VOLLEY_REQUEST_TIMEOUT,
                                Constants.VOLLEY_REQUEST_RETRIES,
                                Constants.VOLLEY_REQUEST_BACKOFF_MULTIPLIER));
                        SmartShopper.getInstance(SearchProducts.this).addToRequestQueue(itemsRequest);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SearchProducts.this, "Failed to initialize search module", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-access-token",Preferences.getAuthToken(SearchProducts.this));
                return headers;
            }
        };
        shopsListRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constants.VOLLEY_REQUEST_TIMEOUT,
                Constants.VOLLEY_REQUEST_RETRIES,
                Constants.VOLLEY_REQUEST_BACKOFF_MULTIPLIER));
        SmartShopper.getInstance(SearchProducts.this).addToRequestQueue(shopsListRequest);

    }

    public void searchItem(View view) {
        EditText searchKeyET = findViewById(R.id.searchItemET);
        String searchKey = searchKeyET.getText().toString();
        ArrayList<Item> newItems = new ArrayList<>();
        for (Item item : items){
            if (item.getItemName().contains(searchKey)){
                newItems.add(item);
            }
        }
        searchResultsView.swapAdapter(new SearchItemListAdapter(
                SearchProducts.this,
                newItems,
                shops
        ),true);
    }
}
