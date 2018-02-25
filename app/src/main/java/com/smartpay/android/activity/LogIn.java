package com.smartpay.android.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;

import com.smartpay.android.SmartShopper;
import com.smartpay.android.name_generator.RandomNameGenerator;
import com.smartpay.android.util.Constants;
import com.smartpay.android.util.Preferences;

public class LogIn extends AppCompatActivity {

    final int SIGN_IN_REQUEST_CONST = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setProviders(Collections.singletonList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()))
                            .build(),
                    SIGN_IN_REQUEST_CONST);

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == SIGN_IN_REQUEST_CONST) {
            // Successfully signed in
            if (resultCode == ResultCodes.OK) {

                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Please Wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                        Constants.BASE_URL + "users/email/" + FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getBoolean("status")) {
                                        final HashMap<String, String> params = new HashMap<>();
                                        params.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                        params.put("password", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        JsonObjectRequest authenticateRequest = new JsonObjectRequest(Request.Method.POST,
                                                Constants.BASE_URL + "users/authenticate/",
                                                new JSONObject(params),
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        progressDialog.dismiss();
                                                        try {
                                                            if (response.getBoolean("success")){
                                                                String token = response.getString("token");
                                                                Preferences.saveAuthToken(LogIn.this,token);
                                                                startActivity(new Intent(LogIn.this, MainActivity.class));
                                                                finish();
                                                            }else {
                                                                FirebaseAuth.getInstance().signOut();
                                                                Toast.makeText(LogIn.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                            FirebaseAuth.getInstance().signOut();
                                                            Toast.makeText(LogIn.this, "Authentication Failed " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {

                                                        progressDialog.dismiss();
                                                        FirebaseAuth.getInstance().signOut();
                                                        Toast.makeText(LogIn.this, "Authentication Failed " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                        );
                                        authenticateRequest.setRetryPolicy(new DefaultRetryPolicy(
                                                Constants.VOLLEY_REQUEST_TIMEOUT,
                                                Constants.VOLLEY_REQUEST_RETRIES,
                                                Constants.VOLLEY_REQUEST_BACKOFF_MULTIPLIER));
                                        SmartShopper.getInstance(LogIn.this).addToRequestQueue(authenticateRequest);
                                    } else {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        String fname,lname;
                                        try {
                                            String userName = user.getDisplayName();
                                            fname = userName.substring(0, userName.indexOf(" "));
                                            lname = userName.substring(userName.indexOf(" ") + 1);
                                        }catch (Exception e){
                                            String userName = new RandomNameGenerator(LogIn.this).next();
                                            fname = userName.substring(0, userName.indexOf("_"));
                                            lname = userName.substring(userName.indexOf("_") + 1);
                                        }
                                        String password = user.getUid();
                                        String city = "Ernakulam";

                                        final HashMap<String, String> params = new HashMap<>();
                                        params.put("email", user.getEmail());
                                        params.put("fname", fname);
                                        params.put("lname", lname);
                                        params.put("password", password);
                                        params.put("city", city);
                                        params.put("phone", "00000000");
                                        JsonObjectRequest signUpRequest = new JsonObjectRequest(Request.Method.POST,
                                                Constants.BASE_URL + "users/signup/",
                                                new JSONObject(params),
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        try {
                                                            if (response.getBoolean("success")){
                                                                Toast.makeText(LogIn.this, "SignUp Success", Toast.LENGTH_SHORT).show();
                                                                final HashMap<String, String> params = new HashMap<>();
                                                                params.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                                                params.put("password", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                                JsonObjectRequest authenticateRequest = new JsonObjectRequest(Request.Method.POST,
                                                                        Constants.BASE_URL + "users/authenticate/",
                                                                        new JSONObject(params),
                                                                        new Response.Listener<JSONObject>() {
                                                                            @Override
                                                                            public void onResponse(JSONObject response) {
                                                                                progressDialog.dismiss();
                                                                                try {
                                                                                    if (response.getBoolean("success")){
                                                                                        String token = response.getString("token");
                                                                                        Preferences.saveAuthToken(LogIn.this,token);
                                                                                        startActivity(new Intent(LogIn.this, MainActivity.class));
                                                                                        finish();
                                                                                    }else {
                                                                                        FirebaseAuth.getInstance().signOut();
                                                                                        Toast.makeText(LogIn.this, "Authentication after signUp Failed", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                } catch (JSONException e) {
                                                                                    e.printStackTrace();
                                                                                    FirebaseAuth.getInstance().signOut();
                                                                                    Toast.makeText(LogIn.this, "Authentication after signUp Failed " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        },
                                                                        new Response.ErrorListener() {
                                                                            @Override
                                                                            public void onErrorResponse(VolleyError error) {

                                                                                progressDialog.dismiss();
                                                                                FirebaseAuth.getInstance().signOut();
                                                                                Toast.makeText(LogIn.this, "Authentication after signUp Failed " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                                                            }
                                                                        }
                                                                );
                                                                authenticateRequest.setRetryPolicy(new DefaultRetryPolicy(
                                                                        Constants.VOLLEY_REQUEST_TIMEOUT,
                                                                        Constants.VOLLEY_REQUEST_RETRIES,
                                                                        Constants.VOLLEY_REQUEST_BACKOFF_MULTIPLIER));
                                                                SmartShopper.getInstance(LogIn.this).addToRequestQueue(authenticateRequest);
                                                            }else {
                                                                Toast.makeText(LogIn.this, "SignUp Failed", Toast.LENGTH_SHORT).show();
                                                                progressDialog.dismiss();
                                                                FirebaseAuth.getInstance().signOut();
                                                            }
                                                        } catch (JSONException e) {
                                                            progressDialog.dismiss();
                                                            FirebaseAuth.getInstance().signOut();
                                                            Toast.makeText(LogIn.this, "SignUp Failed " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                progressDialog.dismiss();
                                                FirebaseAuth.getInstance().signOut();
                                                Toast.makeText(LogIn.this, "SignUp Failed " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        );
                                        signUpRequest.setRetryPolicy(new DefaultRetryPolicy(
                                                Constants.VOLLEY_REQUEST_TIMEOUT,
                                                Constants.VOLLEY_REQUEST_RETRIES,
                                                Constants.VOLLEY_REQUEST_BACKOFF_MULTIPLIER));
                                        SmartShopper.getInstance(LogIn.this).addToRequestQueue(signUpRequest);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    progressDialog.dismiss();
                                    FirebaseAuth.getInstance().signOut();
                                    Toast.makeText(LogIn.this, "SignIn Failed while checking email", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LogIn.this, "Something Happened", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        Constants.VOLLEY_REQUEST_TIMEOUT,
                        Constants.VOLLEY_REQUEST_RETRIES,
                        Constants.VOLLEY_REQUEST_BACKOFF_MULTIPLIER));
                SmartShopper.getInstance(LogIn.this).addToRequestQueue(jsonObjectRequest);

            } else {
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Request Cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "SigIn Failed. Please check your internet connection.", Toast.LENGTH_LONG).show();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setMessage("Retry?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivityForResult(
                                        AuthUI.getInstance()
                                                .createSignInIntentBuilder()
                                                .setProviders(Collections.singletonList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()))
                                                .build(),
                                        SIGN_IN_REQUEST_CONST);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                builder.create().show();
            }
        }
    }
}
