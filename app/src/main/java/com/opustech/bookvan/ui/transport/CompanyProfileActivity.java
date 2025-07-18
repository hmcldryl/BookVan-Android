package com.opustech.bookvan.ui.transport;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.opustech.bookvan.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompanyProfileActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference partnersReference;

    private TextView companyName,
            companyDescription,
            companyAddress,
            companyEmail,
            companyWebsite;
    private CircleImageView companyPhoto;
    private ImageView companyBanner;
    private FloatingActionButton btnEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_profile_admin);

        firebaseFirestore = FirebaseFirestore.getInstance();
        partnersReference = firebaseFirestore.collection("partners");

        initializeUi();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        getSupportActionBar().setTitle("Your Company Profile");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CompanyProfileActivity.this, EditCompanyProfileActivity.class);
                intent.putExtra("uid", getIntent().getStringExtra("uid"));
                startActivity(intent);
            }
        });
    }

    private void initializeUi() {
        btnEdit = findViewById(R.id.btnEdit);
        companyPhoto = findViewById(R.id.companyPhoto);
        companyBanner = findViewById(R.id.companyBanner);
        companyName = findViewById(R.id.companyName);
        companyDescription = findViewById(R.id.companyDescription);
        companyAddress = findViewById(R.id.companyAddress);
        companyEmail = findViewById(R.id.companyEmail);
        companyWebsite = findViewById(R.id.companyWebsite);
    }

    @Override
    protected void onStart() {
        super.onStart();
        partnersReference.document(getIntent().getStringExtra("uid"))
                .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            if (value.exists()) {
                                String photo_url = value.getString("photo_url");
                                String banner_url = value.getString("banner_url");
                                String name = value.getString("name");
                                String description = value.getString("description");
                                String address = value.getString("address");
                                String email = value.getString("email");
                                String website = value.getString("website");
                                updateUi(photo_url, banner_url, name, description, address, email, website);
                            }
                        }
                    }
                });
    }

    private void updateUi(String photo_url, String banner_url, String name, String description, String address, String email, String website) {
        if (photo_url != null) {
            if (!photo_url.isEmpty()) {
                Glide.with(this)
                        .load(photo_url)
                        .into(companyPhoto);
            }
        }

        if (banner_url != null) {
            if (!banner_url.isEmpty()) {
                Glide.with(this)
                        .load(banner_url)
                        .into(companyBanner);
            }
        }

        companyName.setText(name);
        companyDescription.setText(description);
        companyAddress.setText(address);

        if (email != null) {
            if (!email.isEmpty()) {
                companyEmail.setText(email);
            } else {
                companyEmail.setVisibility(View.GONE);
            }
        } else {
            companyEmail.setVisibility(View.GONE);
        }

        if (website != null) {
            if (!website.isEmpty()) {
                companyWebsite.setText(website);
            } else {
                companyWebsite.setVisibility(View.GONE);
            }
        } else {
            companyWebsite.setVisibility(View.GONE);
        }
    }
}