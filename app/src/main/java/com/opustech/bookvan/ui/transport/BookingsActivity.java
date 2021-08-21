package com.opustech.bookvan.ui.transport;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.R;
import com.opustech.bookvan.adapters.transport.BookingsTransportPagerAdapter;

import java.util.Arrays;

public class BookingsActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, partnersReference, bookingsReference;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private BookingsTransportPagerAdapter bookingsTransportPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_admin_bookings);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        partnersReference = firebaseFirestore.collection("partners");
        bookingsReference = firebaseFirestore.collection("bookings");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Bookings");
        getSupportActionBar().setSubtitle("View Booking Transactions");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        bookingsTransportPagerAdapter = new BookingsTransportPagerAdapter(this);
        viewPager.setAdapter(bookingsTransportPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Pending");
                        updatePendingListTabBadge(tab);
                        break;
                    case 1:
                        tab.setText("Confirmed");
                        updateConfirmedListTabBadge(tab);
                        break;
                    case 2:
                        tab.setText("Cancelled");
                        updateCancelledListTabBadge(tab);
                        break;
                    case 3:
                        tab.setText("History");
                        updateHistoryListTabBadge(tab);
                        break;
                }
            }
        }).attach();
    }

    private String getCompanyUid() {
        Intent intent = getIntent();
        return intent.getStringExtra("uid");
    }

    private void updateConfirmedListTabBadge(TabLayout.Tab tab) {
        bookingsReference.whereEqualTo("transport_uid", getCompanyUid())
                .whereEqualTo("status", "confirmed")
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                            badgeDrawable.setBackgroundColor(ContextCompat.getColor(BookingsActivity.this, R.color.colorBadgeBackground));
                            badgeDrawable.setBadgeTextColor(ContextCompat.getColor(BookingsActivity.this, R.color.white));
                            badgeDrawable.setMaxCharacterCount(2);
                            int size = value.size();
                            if (size > 0) {
                                badgeDrawable.setNumber(size);
                                badgeDrawable.setVisible(true);
                            } else if (size == 0) {
                                badgeDrawable.setVisible(false);
                            }
                        }
                    }
                });
    }

    private void updatePendingListTabBadge(TabLayout.Tab tab) {
        bookingsReference.whereEqualTo("transport_uid", getCompanyUid())
                .whereEqualTo("status", "pending")
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                            badgeDrawable.setBackgroundColor(ContextCompat.getColor(BookingsActivity.this, R.color.colorBadgeBackground));
                            badgeDrawable.setBadgeTextColor(ContextCompat.getColor(BookingsActivity.this, R.color.white));
                            badgeDrawable.setMaxCharacterCount(2);
                            int size = value.size();
                            if (size > 0) {
                                badgeDrawable.setNumber(size);
                                badgeDrawable.setVisible(true);
                            } else if (size == 0) {
                                badgeDrawable.setVisible(false);
                            }
                        }
                    }
                });
    }

    private void updateCancelledListTabBadge(TabLayout.Tab tab) {
        bookingsReference.whereEqualTo("transport_uid", getCompanyUid())
                .whereEqualTo("status", "cancelled")
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                            badgeDrawable.setBackgroundColor(ContextCompat.getColor(BookingsActivity.this, R.color.colorBadgeBackground));
                            badgeDrawable.setBadgeTextColor(ContextCompat.getColor(BookingsActivity.this, R.color.white));
                            badgeDrawable.setMaxCharacterCount(3);
                            int size = value.size();
                            if (size > 0) {
                                badgeDrawable.setNumber(size);
                                badgeDrawable.setVisible(true);
                            } else if (size == 0) {
                                badgeDrawable.setVisible(false);
                            }
                        }
                    }
                });
    }

    private void updateHistoryListTabBadge(TabLayout.Tab tab) {
        bookingsReference.whereEqualTo("transport_uid", getCompanyUid())
                .whereIn("status", Arrays.asList("cancelled", "done"))
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                            badgeDrawable.setBackgroundColor(ContextCompat.getColor(BookingsActivity.this, R.color.colorBadgeBackground));
                            badgeDrawable.setBadgeTextColor(ContextCompat.getColor(BookingsActivity.this, R.color.white));
                            badgeDrawable.setMaxCharacterCount(3);
                            int size = value.size();
                            if (size > 0) {
                                badgeDrawable.setNumber(size);
                                badgeDrawable.setVisible(true);
                            } else if (size == 0) {
                                badgeDrawable.setVisible(false);
                            }
                        }
                    }
                });
    }
}