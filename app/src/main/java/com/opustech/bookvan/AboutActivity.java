package com.opustech.bookvan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

public class AboutActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;

    private ImageView btnSocialFacebook, btnSocialTwitter, btnSocialInstagram, btnSocialYoutube;
    private Button btnLicenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.officeMap);
        mapFragment.getMapAsync(this);

        btnLicenses = findViewById(R.id.btnLicenses);
        btnSocialFacebook = findViewById(R.id.btnSocialFacebook);
        btnSocialTwitter = findViewById(R.id.btnSocialTwitter);
        btnSocialInstagram = findViewById(R.id.btnSocialInstagram);
        btnSocialYoutube = findViewById(R.id.btnSocialYoutube);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("About BookVan");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnSocialFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUrl("https://www.facebook.com/BookVan.ph");
            }
        });

        btnSocialTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUrl("https://twitter.com/BookvanOfficial");
            }
        });

        btnSocialInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUrl("https://www.instagram.com/bookvan.ph/");
            }
        });

        btnSocialYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUrl("https://www.youtube.com/channel/UCF4Q-yrY7Rqdopb37qd2ntQ?view_as=subscriber");
            }
        });

        btnLicenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AboutActivity.this, OssLicensesMenuActivity.class));
            }
        });
    }

    private void goToUrl(String url) {
        Uri uri = Uri.parse(url);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng office = new LatLng(9.777869272890038, 118.73571612331892);
        googleMap.addMarker(new MarkerOptions()
        .position(office)
        .title("BookVan Office"));
        googleMap.setMinZoomPreference(17.5f);
        googleMap.setMaxZoomPreference(20.0f);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(office));
    }
}