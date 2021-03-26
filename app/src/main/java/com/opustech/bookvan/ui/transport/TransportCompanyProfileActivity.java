package com.opustech.bookvan.ui.transport;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.opustech.bookvan.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TransportCompanyProfileActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference partnersReference;

    private TextView companyName,
            companyDescription,
            companyAddress,
            companyTelephoneNumber,
            companyCellphoneNumber,
            companyEmail,
            companyWebsite;
    private TextView companyTelephoneNumberLabel,
            companyCellphoneNumberLabel,
            companyEmailLabel,
            companyWebsiteLabel;
    private CircleImageView companyPhoto;
    private ImageView companyBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_company_profile);

        firebaseFirestore = FirebaseFirestore.getInstance();
        partnersReference = firebaseFirestore.collection("partners");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        getSupportActionBar().setTitle("Company Profile");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        initializeUi();
    }

    private void initializeUi() {
        companyPhoto = findViewById(R.id.companyPhoto);
        companyBanner = findViewById(R.id.companyBanner);
        companyName = findViewById(R.id.companyName);
        companyDescription = findViewById(R.id.companyDescription);
        companyAddress = findViewById(R.id.companyAddress);
        companyTelephoneNumber = findViewById(R.id.companyTelephoneNumber);
        companyCellphoneNumber = findViewById(R.id.companyCellphoneNumber);
        companyEmail = findViewById(R.id.companyEmail);
        companyWebsite = findViewById(R.id.companyWebsite);
        companyTelephoneNumberLabel = findViewById(R.id.companyTelephoneNumberLabel);
        companyCellphoneNumberLabel = findViewById(R.id.companyCellphoneNumberLabel);
        companyEmailLabel = findViewById(R.id.companyEmailLabel);
        companyWebsiteLabel = findViewById(R.id.companyWebsiteLabel);
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
                                List<String> telephone_number = (List<String>) value.get("telephone_number");
                                List<String> cellphone_number = (List<String>) value.get("cellphone_number");
                                updateUi(photo_url, banner_url, name, description, address, email, website, telephone_number, cellphone_number);
                            }
                        }
                    }
                });
    }

    private void updateUi(String photo_url, String banner_url, String name, String description, String address, String email, String website, List<String> telephone_number, List<String> cellphone_number) {
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

        if (telephone_number != null) {
            if (telephone_number.size() == 1) {
                companyTelephoneNumber.setText(telephone_number.get(0));
            } else if (telephone_number.size() > 1) {
                companyTelephoneNumber.setText(formatArrayData(telephone_number));
            } else if (telephone_number.size() == 0) {
                companyTelephoneNumber.setVisibility(View.GONE);
                companyTelephoneNumberLabel.setVisibility(View.GONE);
            }
        } else {
            companyTelephoneNumber.setVisibility(View.GONE);
            companyTelephoneNumberLabel.setVisibility(View.GONE);
        }

        if (cellphone_number != null) {
            if (cellphone_number.size() == 1) {
                companyCellphoneNumber.setText(cellphone_number.get(0));
            } else if (cellphone_number.size() > 1) {
                companyCellphoneNumber.setText(formatArrayData(cellphone_number));
            } else if (cellphone_number.size() == 0) {
                companyCellphoneNumber.setVisibility(View.GONE);
                companyCellphoneNumberLabel.setVisibility(View.GONE);
            }
        } else {
            companyCellphoneNumber.setVisibility(View.GONE);
            companyCellphoneNumberLabel.setVisibility(View.GONE);
        }

        if (email != null) {
            if (!email.isEmpty()) {
                companyEmail.setText(email);
            } else {
                companyEmail.setVisibility(View.GONE);
                companyEmailLabel.setVisibility(View.GONE);
            }
        } else {
            companyEmail.setVisibility(View.GONE);
            companyEmailLabel.setVisibility(View.GONE);
        }

        if (website != null) {
            if (!website.isEmpty()) {
                companyWebsite.setText(website);
            } else {
                companyWebsite.setVisibility(View.GONE);
                companyWebsiteLabel.setVisibility(View.GONE);
            }
        } else {
            companyWebsite.setVisibility(View.GONE);
            companyWebsiteLabel.setVisibility(View.GONE);
        }
    }

    private String formatArrayData(List<String> stringList) {
        String result = "";
        if (stringList.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : stringList) {
                stringBuilder.append(s).append("\n");
            }
            result = stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
        }
        return result;
    }
}