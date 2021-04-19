package com.opustech.bookvan.ui.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

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
import com.opustech.bookvan.adapters.user.BookingPagerAdapter;

import java.util.Arrays;

public class UserBookingActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, bookingsReference;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private BookingPagerAdapter bookingPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_booking);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        bookingsReference = firebaseFirestore.collection("bookings");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Booking");
        getSupportActionBar().setSubtitle("Check your booking status and history.");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        String currentUserId = firebaseAuth.getCurrentUser().getUid();

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        bookingPagerAdapter = new BookingPagerAdapter(this);
        viewPager.setAdapter(bookingPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Pending");
                        updatePendingListTabBadge(tab, currentUserId);
                        break;
                    case 1:
                        tab.setText("Confirmed");
                        updateConfirmedListTabBadge(tab, currentUserId);
                        break;
                    case 2:
                        tab.setText("Cancelled");
                        updateCancelledListTabBadge(tab, currentUserId);
                        break;
                    case 3:
                        tab.setText("History");
                        updateHistoryListTabBadge(tab, currentUserId);
                        break;
                }
            }
        }).attach();
    }

    private void updatePendingListTabBadge(TabLayout.Tab tab, String uid) {
        bookingsReference.whereEqualTo("uid", uid)
                .whereEqualTo("status", "pending")
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            int size = value.size();
                            if (size > 0) {
                                BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                                badgeDrawable.setBackgroundColor(ContextCompat.getColor(UserBookingActivity.this, R.color.colorBadgeBackground));
                                badgeDrawable.setMaxCharacterCount(2);
                                badgeDrawable.setNumber(size);
                                badgeDrawable.setVisible(true);
                            }
                        }
                    }
                });
    }

    private void updateConfirmedListTabBadge(TabLayout.Tab tab, String uid) {
        bookingsReference.whereEqualTo("uid", uid)
                .whereEqualTo("status", "confirmed")
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            int size = value.size();
                            if (size > 0) {
                                BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                                badgeDrawable.setBackgroundColor(ContextCompat.getColor(UserBookingActivity.this, R.color.colorBadgeBackground));
                                badgeDrawable.setMaxCharacterCount(2);
                                badgeDrawable.setNumber(size);
                                badgeDrawable.setVisible(true);
                            }
                        }
                    }
                });
    }

    private void updateCancelledListTabBadge(TabLayout.Tab tab, String uid) {
        bookingsReference.whereEqualTo("uid", uid)
                .whereEqualTo("status", "cancelled")
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            int size = value.size();
                            if (size > 0) {
                                BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                                badgeDrawable.setBackgroundColor(ContextCompat.getColor(UserBookingActivity.this, R.color.colorBadgeBackground));
                                badgeDrawable.setMaxCharacterCount(2);
                                badgeDrawable.setNumber(size);
                                badgeDrawable.setVisible(true);
                            }
                        }
                    }
                });
    }

    private void updateHistoryListTabBadge(TabLayout.Tab tab, String uid) {
        bookingsReference.whereEqualTo("uid", uid)
                .whereEqualTo("status", "done")
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            int size = value.size();
                            if (size > 0) {
                                BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                                badgeDrawable.setBackgroundColor(ContextCompat.getColor(UserBookingActivity.this, R.color.colorBadgeBackground));
                                badgeDrawable.setMaxCharacterCount(2);
                                badgeDrawable.setNumber(size);
                                badgeDrawable.setVisible(true);
                            }
                        }
                    }
                });
    }
}