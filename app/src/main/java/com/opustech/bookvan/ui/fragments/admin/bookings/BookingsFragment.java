package com.opustech.bookvan.ui.fragments.admin.bookings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.R;
import com.opustech.bookvan.ui.adapters.BookingsAdminViewPagerAdapter;

public class BookingsFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private BookingsAdminViewPagerAdapter bookingsAdminViewPagerAdapter;

    private String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bookings, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");

        //currentUserID = firebaseAuth.getCurrentUser().getUid();

        viewPager = root.findViewById(R.id.bookingsViewPager);
        tabLayout = root.findViewById(R.id.bookingsTab);
        bookingsAdminViewPagerAdapter = new BookingsAdminViewPagerAdapter(getParentFragmentManager());
        viewPager.setAdapter(bookingsAdminViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        return root;
    }
}