package com.smartpay.android.model;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.smartpay.android.SmartShopper;
import com.smartpay.android.util.Constants;
import com.smartpay.android.util.Preferences;

public class User {
    private String firstName;
    private String lastName;
    private Uri photoUrl;
    private String emailId;
    private String phone;
    private String city;
    private String joined;
    private String dbPass;

    public interface OnUploadComplete{
        void onUploadComplete(Uri downloadUrl);
        void onError(Exception exception);
    }

    public interface OnSavedListener{
        void onSaveComplete();
        void onError();
    }

    public interface OnFetchedListener{
        void onFetchComplete(User user);
        void onError(Exception error);
    }

    public User(String emailId) {
        this.emailId = emailId;
        dbPass = FirebaseAuth.getInstance().getCurrentUser().getUid();
        photoUrl = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Uri getPhotoUrl() {
        return photoUrl;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getPhone() {
        return phone;
    }

    public String getCity() {
        return city;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhotoUrl(Uri photoUrl) {

        this.photoUrl = photoUrl;


    }

    public void uploadProfileImage(Bitmap bitmap, final OnUploadComplete onUploadComplete){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()+".jpg");

        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                onUploadComplete.onError(exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                onUploadComplete.onUploadComplete(downloadUrl);
            }
        });
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void fetch(Context context, final OnFetchedListener onFetchedListener){
        photoUrl = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                Constants.BASE_URL + "users/i/" + emailId,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            firstName = response.getJSONObject(0).getString("fname");
                            lastName = response.getJSONObject(0).getString("lname");
                            joined = response.getJSONObject(0).getString("joined");
                            city = response.getJSONObject(0).getString("city");
                            phone = response.getJSONObject(0).getString("phone");
                            dbPass = response.getJSONObject(0).getString("password");

                            onFetchedListener.onFetchComplete(User.this);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            onFetchedListener.onError(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onFetchedListener.onError(error);
                    }
                }
        );

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constants.VOLLEY_REQUEST_TIMEOUT,
                Constants.VOLLEY_REQUEST_RETRIES,
                Constants.VOLLEY_REQUEST_BACKOFF_MULTIPLIER));
        SmartShopper.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }

    public void save(final Context context, final OnSavedListener onSavedListener){
        HashMap<String,String> params = new HashMap<>();
        params.put("lname",lastName);
        params.put("fname",firstName);
        params.put("email",emailId);
        params.put("password",dbPass);
        params.put("joined",joined);
        params.put("city",city);
        params.put("phone",phone);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                Constants.BASE_URL + "users/email/" + emailId,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("status")){
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(firstName + " " + lastName)
                                        .setPhotoUri(photoUrl)
                                        .build();
                                FirebaseAuth.getInstance().getCurrentUser()
                                        .updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    onSavedListener.onSaveComplete();
                                                }
                                            }
                                        });
                            }else {
                                onSavedListener.onError();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            onSavedListener.onError();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onSavedListener.onError();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-access-token", Preferences.getAuthToken(context));
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constants.VOLLEY_REQUEST_TIMEOUT,
                Constants.VOLLEY_REQUEST_RETRIES,
                Constants.VOLLEY_REQUEST_BACKOFF_MULTIPLIER));
        SmartShopper.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

}
