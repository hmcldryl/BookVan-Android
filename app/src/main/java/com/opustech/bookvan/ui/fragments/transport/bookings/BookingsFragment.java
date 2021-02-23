package com.opustech.bookvan.ui.fragments.transport.bookings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.R;
import com.opustech.bookvan.ui.adapters.admin.BookingsAdminPagerAdapter;
import com.opustech.bookvan.ui.adapters.transport.BookingsTransportPagerAdapter;

public class BookingsFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, partnersReference;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private BookingsTransportPagerAdapter bookingsTransportPagerAdapter;

    private Context context;

    private String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bookings_transport, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        partnersReference = firebaseFirestore.collection("partners");

        //currentUserID = firebaseAuth.getCurrentUser().getUid();

        viewPager = root.findViewById(R.id.viewPager);
        tabLayout = root.findViewById(R.id.tabLayout);

        bookingsTransportPagerAdapter = new BookingsTransportPagerAdapter(getActivity());
        viewPager.setAdapter(bookingsTransportPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Confirmed");
                        updateConfirmedListTabBadge(tab);
                        break;
                    case 1:
                        tab.setText("Pending");
                        updatePendingListTabBadge(tab);
                        break;
                    case 2:
                        tab.setText("History");
                        updateHistoryListTabBadge(tab);
                        break;
                }
            }
        }).attach();

        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private String getCompanyUid() {
        Intent intent = getActivity().getIntent();
        return intent.getStringExtra("uid");
    }

    private void updateConfirmedListTabBadge(TabLayout.Tab tab) {
        partnersReference.document(getCompanyUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            usersReference.document(admin_uid)
                                    .collection("bookings")
                                    .whereEqualTo("transport_company", task.getResult().getString("name"))
                                    .whereEqualTo("status", "confirmed")
                                    .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if (value != null) {
                                                BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                                                badgeDrawable.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBadgeBackground));
                                                badgeDrawable.setBadgeTextColor(ContextCompat.getColor(context, R.color.white));
                                                badgeDrawable.setMaxCharacterCount(2);
                                                int size = value.size();
                                                if (size > 0) {
                                                    badgeDrawable.setNumber(size);
                                                    badgeDrawable.setVisible(true);
                                                }
                                                else if (size == 0) {
                                                    badgeDrawable.setVisible(false);
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void updatePendingListTabBadge(TabLayout.Tab tab) {
        usersReference.document(admin_uid)
                .collection("bookings")
                .whereEqualTo("status", "pending")
                .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                            badgeDrawable.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBadgeBackground));
                            badgeDrawable.setBadgeTextColor(ContextCompat.getColor(context, R.color.white));
                            badgeDrawable.setMaxCharacterCount(2);
                            int size = value.size();
                            if (size > 0) {
                                badgeDrawable.setNumber(size);
                                badgeDrawable.setVisible(true);
                            }
                            else if (size == 0) {
                                badgeDrawable.setVisible(false);
                            }
                        }
                    }
                });
    }

    private void updateHistoryListTabBadge(TabLayout.Tab tab) {
        partnersReference.document(getCompanyUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            usersReference.document(admin_uid)
                                    .collection("bookings")
                                    .whereEqualTo("transport_company", task.getResult().getString("name"))
                                    .whereEqualTo("status", "done")
                                    .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if (value != null) {
                                                BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                                                badgeDrawable.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBadgeBackground));
                                                badgeDrawable.setBadgeTextColor(ContextCompat.getColor(context, R.color.white));
                                                badgeDrawable.setMaxCharacterCount(2);
                                                int size = value.size();
                                                if (size > 0) {
                                                    badgeDrawable.setNumber(size);
                                                    badgeDrawable.setVisible(true);
                                                }
                                                else if (size == 0) {
                                                    badgeDrawable.setVisible(false);
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}