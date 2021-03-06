package com.opustech.bookvan.ui.fragments.user.booking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.adapters.user.ProfilePagerAdapter;
import com.opustech.bookvan.R;

public class BookingFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, bookingsReference;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private ProfilePagerAdapter profilePagerAdapter;

    private final String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_booking, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        bookingsReference = firebaseFirestore.collection("bookings");

        String currentUserId = firebaseAuth.getCurrentUser().getUid();

        viewPager = root.findViewById(R.id.viewPager);
        tabLayout = root.findViewById(R.id.tabLayout);

        profilePagerAdapter = new ProfilePagerAdapter(getActivity());
        viewPager.setAdapter(profilePagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Active Booking");
                        updateActiveListTabBadge(tab, currentUserId);
                        break;
                    case 1:
                        tab.setText("Booking History");
                        updateHistoryListTabBadge(tab, currentUserId);
                        break;
                }
            }
        }).attach();

        return root;
    }

    private void updateActiveListTabBadge(TabLayout.Tab tab, String uid) {
        bookingsReference.whereEqualTo("uid", uid)
                .whereEqualTo("status", "pending")
                .whereEqualTo("status", "confirmed")
                .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            int size = value.size();
                            if (size > 0) {
                                BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                                badgeDrawable.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorBadgeBackground));
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
                .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            int size = value.size();
                            if (size > 0) {
                                BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                                badgeDrawable.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorBadgeBackground));
                                badgeDrawable.setMaxCharacterCount(2);
                                badgeDrawable.setNumber(size);
                                badgeDrawable.setVisible(true);
                            }
                        }
                    }
                });
    }
}