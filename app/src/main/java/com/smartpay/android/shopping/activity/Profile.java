package com.smartpay.android.shopping.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smartpay.android.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import com.smartpay.android.shopping.model.User;

public class Profile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;

    private ImageView profileImageView;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final EditText fNameET = findViewById(R.id.firstNameET);
        final EditText lNameET = findViewById(R.id.lastNameET);
        final EditText phoneET = findViewById(R.id.phoneET);
        final EditText emailET = findViewById(R.id.emailIdET);
        final EditText cityET = findViewById(R.id.cityET);
        profileImageView = findViewById(R.id.profile_image);
        profileImageView.setDrawingCacheEnabled(true);

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkReadExternalPermission()){

                    Intent intent = new Intent();
                    // Show only images, no videos or anything else
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    // Always show the chooser (if there are multiple options available)
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

                }else {

                    ActivityCompat.requestPermissions(Profile.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                }

            }
        });

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;

        user = new User(firebaseUser.getEmail());

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Details...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        user.fetch(this, new User.OnFetchedListener() {
            @Override
            public void onFetchComplete(User user) {
                fNameET.setText(user.getFirstName());
                lNameET.setText(user.getLastName());
                phoneET.setText(user.getPhone());
                emailET.setText(user.getEmailId());
                cityET.setText(user.getCity());
                if (user.getPhotoUrl() != null)
                    Picasso.with(Profile.this).load(user.getPhotoUrl()).placeholder(ContextCompat.getDrawable(Profile.this,R.drawable.user)).into(profileImageView);
                else {
                    Toast.makeText(Profile.this, "No Profile Photo detected", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onError(Exception error) {
                Toast.makeText(Profile.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                progressDialog.dismiss();
                finish();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setMessage("Saving...");
                progressDialog.show();

                String fname = fNameET.getText().toString();
                String lname = lNameET.getText().toString();
                String phone = phoneET.getText().toString();
                String city = cityET.getText().toString();

                if (fname.isEmpty()){
                    fNameET.setText(R.string.cannot_be_empty);
                    return;
                }
                if (phone.isEmpty()){
                    phoneET.setText(R.string.cannot_be_empty);
                    return;
                }
                if (lname.isEmpty()){
                    lNameET.setText(R.string.cannot_be_empty);
                    return;
                }
                if (city.isEmpty()){
                    cityET.setText(R.string.cannot_be_empty);
                    return;
                }

                user.setFirstName(fname);
                user.setLastName(lname);
                user.setPhone(phone);
                user.setCity(city);

                user.save(Profile.this, new User.OnSavedListener() {
                    @Override
                    public void onSaveComplete() {
                        Toast.makeText(Profile.this, "Upload Complete", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(Profile.this, "Please check you connection", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (requestCode == PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

            } else {

                Toast.makeText(this, "Please grant permission", Toast.LENGTH_SHORT).show();

            }
        }

    }

    private boolean checkReadExternalPermission()
    {
        return ContextCompat.checkSelfPermission(Profile.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Uploading...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                user.uploadProfileImage(bitmap, new User.OnUploadComplete() {
                    @Override
                    public void onUploadComplete(Uri downloadUrl) {
                        user.setPhotoUrl(downloadUrl);
                        profileImageView.setImageBitmap(bitmap);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(Exception exception) {
                        exception.printStackTrace();
                        Toast.makeText(Profile.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Toast.makeText(Profile.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
            }

        }
    }

}
