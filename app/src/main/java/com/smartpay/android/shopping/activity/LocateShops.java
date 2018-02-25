package com.smartpay.android.shopping.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.smartpay.android.R;
import com.smartpay.android.shopping.SmartPay;
import com.smartpay.android.shopping.util.Constants;
import com.smartpay.android.shopping.util.Preferences;
import com.smartpay.android.shopping.util.UserLocation;

public class LocateShops extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;
    MapView mapView;

    private final int MAP_ZOOM_LEVEL = 13;

    Location userLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate_shops);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar!=null;
        actionBar.setTitle("Locate Shops");
        mapView = findViewById(R.id.mapView);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);

        JsonArrayRequest shopsListRequest = new JsonArrayRequest(
                Request.Method.GET,
                Constants.BASE_URL + "shops/",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Drawable circleDrawable;
                        try {
                            circleDrawable = ContextCompat.getDrawable(LocateShops.this,R.drawable.ic_shopping_cart_black_24dp);
                        }catch (Exception e){
                            e.printStackTrace();
                            circleDrawable = ContextCompat.getDrawable(LocateShops.this,R.drawable.ic_action_shopping_cart);
                        }
                        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
                        map.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(9.99142,76.5892773),
                                        10
                                )
                        );
                        for (int i = 0; i < response.length(); i++){
                            try {
                                JSONObject shop = response.getJSONObject(i);
                                map.addMarker(new MarkerOptions()
                                        .position(new LatLng(shop.getDouble("lat"), shop.getDouble("lng")))
                                        .title(shop.getString("name")))
                                        .setIcon(markerIcon);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LocateShops.this, "Failed to initialize location module", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-access-token", Preferences.getAuthToken(LocateShops.this));
                return headers;
            }
        };
        shopsListRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constants.VOLLEY_REQUEST_TIMEOUT,
                Constants.VOLLEY_REQUEST_RETRIES,
                Constants.VOLLEY_REQUEST_BACKOFF_MULTIPLIER));
        SmartPay.getInstance(LocateShops.this).addToRequestQueue(shopsListRequest);

    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                UserLocation.LocationResult locationResult = new UserLocation.LocationResult() {
                    @Override
                    public void gotLocation(Location location) {
                        userLoc = location;

                        sendBroadcast(new Intent(Constants.GOT_USER_LOCATION_BROADCAST));
                    }
                };
                UserLocation location = new UserLocation();
                location.getLocation(this,locationResult);
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setBuildingsEnabled(true);
        map.setIndoorEnabled(true);
        try {
            UserLocation.LocationResult locationResult = new UserLocation.LocationResult() {
                @Override
                public void gotLocation(Location location) {
                    userLoc = location;
                    sendBroadcast(new Intent(Constants.GOT_USER_LOCATION_BROADCAST));
                }
            };
            UserLocation location = new UserLocation();
            location.getLocation(this,locationResult);
        }catch (IllegalStateException ignored){}

    }

    private BroadcastReceiver gotLocationBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(userLoc.getLatitude(), userLoc.getLongitude()))
                    .title("You are here!"));
            map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            new LatLng(userLoc.getLatitude(),userLoc.getLongitude()),
                            MAP_ZOOM_LEVEL
                    )
            );
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        unregisterReceiver(gotLocationBR);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        registerReceiver(gotLocationBR,new IntentFilter(Constants.GOT_USER_LOCATION_BROADCAST));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
