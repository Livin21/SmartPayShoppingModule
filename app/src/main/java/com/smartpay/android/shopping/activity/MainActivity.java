package com.smartpay.android.shopping.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.smartpay.android.R;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity {

    DrawerLayout drawer;
    FloatingActionButton fab;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();


        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ScannerActivity.class));
            }
        });

        Uri photoUri = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        if (photoUri != null)
            Picasso.with(this).load(photoUri)
                    .placeholder(R.drawable.user)
                    .into((ImageView) findViewById(R.id.profileImageViewDrawer));

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void locateShops(View view) {
        startActivity(new Intent(this, LocateShops.class));
    }

    public void launchHistory(View view) {
        startActivity(new Intent(this, History.class));
    }

    public void searchProducts(View view) {
        startActivity(new Intent(this, SearchProducts.class));
    }

    public void launchProfile(View view) {
        drawer.closeDrawer(GravityCompat.START);
        startActivity(new Intent(this, Profile.class));
    }

    public void launchSettings(View view) {
        drawer.closeDrawer(GravityCompat.START);
        Snackbar.make(fab, "To be added soon", Snackbar.LENGTH_SHORT).show();

    }

    public void sendFeedBack(View view) {
        drawer.closeDrawer(GravityCompat.START);
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:jestinajohn30@gmail.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Smart Shopper - Feedback");
        startActivity(Intent.createChooser(intent, "Send Feedback"));
    }

    public void rateApp(View view) {
        drawer.closeDrawer(GravityCompat.START);
        Snackbar.make(fab, "To be added soon", Snackbar.LENGTH_SHORT).show();
    }

    public void shareApp(View view) {
        drawer.closeDrawer(GravityCompat.START);
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Download Smart Shopper and Do Smart Shopping..!!!";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Invitation");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(sharingIntent);

    }
}
