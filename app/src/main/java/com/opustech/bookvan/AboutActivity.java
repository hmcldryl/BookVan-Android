package com.opustech.bookvan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
    private ImageButton btnTrigger1,
            btnTrigger2,
            btnTrigger3,
            btnTrigger4,
            btnTrigger5,
            btnTrigger6,
            btnTrigger7,
            btnTrigger8,
            btnTrigger9,
            btnTrigger10,
            btnTrigger11,
            btnTrigger12,
            btnTrigger13,
            btnTrigger14,
            btnTrigger15,
            btnTrigger16,
            btnTrigger17,
            btnTrigger18,
            btnTrigger19;
    private LinearLayout trigger1,
            trigger2,
            trigger3,
            trigger4,
            trigger5,
            trigger6,
            trigger7,
            trigger8,
            trigger9,
            trigger10,
            trigger11,
            trigger12,
            trigger13,
            trigger14,
            trigger15,
            trigger16,
            trigger17,
            trigger18,
            trigger19;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.officeMap);
        mapFragment.getMapAsync(this);

        btnSocialFacebook = findViewById(R.id.btnSocialFacebook);
        btnSocialTwitter = findViewById(R.id.btnSocialTwitter);
        btnSocialInstagram = findViewById(R.id.btnSocialInstagram);
        btnSocialYoutube = findViewById(R.id.btnSocialYoutube);

        btnTrigger1 = findViewById(R.id.btnTrigger1);
        btnTrigger2 = findViewById(R.id.btnTrigger2);
        btnTrigger3 = findViewById(R.id.btnTrigger3);
        btnTrigger4 = findViewById(R.id.btnTrigger4);
        btnTrigger5 = findViewById(R.id.btnTrigger5);
        btnTrigger6 = findViewById(R.id.btnTrigger6);
        btnTrigger7 = findViewById(R.id.btnTrigger7);
        btnTrigger8 = findViewById(R.id.btnTrigger8);
        btnTrigger9 = findViewById(R.id.btnTrigger9);
        btnTrigger10 = findViewById(R.id.btnTrigger10);
        btnTrigger11 = findViewById(R.id.btnTrigger11);
        btnTrigger12 = findViewById(R.id.btnTrigger12);
        btnTrigger13 = findViewById(R.id.btnTrigger13);
        btnTrigger14 = findViewById(R.id.btnTrigger14);
        btnTrigger15 = findViewById(R.id.btnTrigger15);
        btnTrigger16 = findViewById(R.id.btnTrigger16);
        btnTrigger17 = findViewById(R.id.btnTrigger17);
        btnTrigger18 = findViewById(R.id.btnTrigger18);
        btnTrigger19 = findViewById(R.id.btnTrigger19);
        trigger1 = findViewById(R.id.trigger1);
        trigger2 = findViewById(R.id.trigger2);
        trigger3 = findViewById(R.id.trigger3);
        trigger4 = findViewById(R.id.trigger4);
        trigger5 = findViewById(R.id.trigger5);
        trigger6 = findViewById(R.id.trigger6);
        trigger7 = findViewById(R.id.trigger7);
        trigger8 = findViewById(R.id.trigger8);
        trigger9 = findViewById(R.id.trigger9);
        trigger10 = findViewById(R.id.trigger10);
        trigger11 = findViewById(R.id.trigger11);
        trigger12 = findViewById(R.id.trigger12);
        trigger13 = findViewById(R.id.trigger13);
        trigger14 = findViewById(R.id.trigger14);
        trigger15 = findViewById(R.id.trigger15);
        trigger16 = findViewById(R.id.trigger16);
        trigger17 = findViewById(R.id.trigger17);
        trigger18 = findViewById(R.id.trigger18);
        trigger19 = findViewById(R.id.trigger19);

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

        dropdownView();
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

    private void dropdownView() {
        btnTrigger1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trigger1.getVisibility() == View.GONE) {
                    trigger1.setVisibility(View.VISIBLE);
                } else {
                    trigger1.setVisibility(View.GONE);
                }
            }
        });
        btnTrigger2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trigger2.getVisibility() == View.GONE) {
                    trigger2.setVisibility(View.VISIBLE);
                } else {
                    trigger2.setVisibility(View.GONE);
                }
            }
        });
        btnTrigger3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trigger3.getVisibility() == View.GONE) {
                    trigger3.setVisibility(View.VISIBLE);
                } else {
                    trigger3.setVisibility(View.GONE);
                }
            }
        });
        btnTrigger4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trigger4.getVisibility() == View.GONE) {
                    trigger4.setVisibility(View.VISIBLE);
                } else {
                    trigger4.setVisibility(View.GONE);
                }
            }
        });
        btnTrigger5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trigger5.getVisibility() == View.GONE) {
                    trigger5.setVisibility(View.VISIBLE);
                } else {
                    trigger5.setVisibility(View.GONE);
                }
            }
        });
        btnTrigger6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trigger6.getVisibility() == View.GONE) {
                    trigger6.setVisibility(View.VISIBLE);
                } else {
                    trigger6.setVisibility(View.GONE);
                }
            }
        });
        btnTrigger7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trigger7.getVisibility() == View.GONE) {
                    trigger7.setVisibility(View.VISIBLE);
                } else {
                    trigger7.setVisibility(View.GONE);
                }
            }
        });
        btnTrigger8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trigger8.getVisibility() == View.GONE) {
                    trigger8.setVisibility(View.VISIBLE);
                } else {
                    trigger8.setVisibility(View.GONE);
                }
            }
        });
        btnTrigger9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trigger9.getVisibility() == View.GONE) {
                    trigger9.setVisibility(View.VISIBLE);
                } else {
                    trigger9.setVisibility(View.GONE);
                }
            }
        });
        btnTrigger10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trigger10.getVisibility() == View.GONE) {
                    trigger10.setVisibility(View.VISIBLE);
                } else {
                    trigger10.setVisibility(View.GONE);
                }
            }
        });
        btnTrigger11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trigger11.getVisibility() == View.GONE) {
                    trigger11.setVisibility(View.VISIBLE);
                } else {
                    trigger11.setVisibility(View.GONE);
                }
            }
        });
        btnTrigger12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trigger12.getVisibility() == View.GONE) {
                    trigger12.setVisibility(View.VISIBLE);
                } else {
                    trigger12.setVisibility(View.GONE);
                }
            }
        });
        btnTrigger13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trigger13.getVisibility() == View.GONE) {
                    trigger13.setVisibility(View.VISIBLE);
                } else {
                    trigger13.setVisibility(View.GONE);
                }
            }
        });
        btnTrigger14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trigger14.getVisibility() == View.GONE) {
                    trigger14.setVisibility(View.VISIBLE);
                } else {
                    trigger14.setVisibility(View.GONE);
                }
            }
        });
        btnTrigger15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trigger15.getVisibility() == View.GONE) {
                    trigger15.setVisibility(View.VISIBLE);
                } else {
                    trigger15.setVisibility(View.GONE);
                }
            }
        });
        btnTrigger16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trigger16.getVisibility() == View.GONE) {
                    trigger16.setVisibility(View.VISIBLE);
                } else {
                    trigger16.setVisibility(View.GONE);
                }
            }
        });
        btnTrigger17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trigger17.getVisibility() == View.GONE) {
                    trigger17.setVisibility(View.VISIBLE);
                } else {
                    trigger17.setVisibility(View.GONE);
                }
            }
        });
        btnTrigger18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trigger18.getVisibility() == View.GONE) {
                    trigger18.setVisibility(View.VISIBLE);
                } else {
                    trigger18.setVisibility(View.GONE);
                }
            }
        });
        btnTrigger19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trigger19.getVisibility() == View.GONE) {
                    trigger19.setVisibility(View.VISIBLE);
                } else {
                    trigger19.setVisibility(View.GONE);
                }
            }
        });

    }
}