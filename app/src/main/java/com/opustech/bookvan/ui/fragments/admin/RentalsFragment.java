package com.opustech.bookvan.ui.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.opustech.bookvan.R;
import com.opustech.bookvan.ui.adapters.RentalsViewPagerAdapter;

public class RentalsFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private RentalsViewPagerAdapter rentalsViewPagerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rentals, container, false);

        viewPager = root.findViewById(R.id.rentalsViewPager);
        tabLayout = root.findViewById(R.id.rentalsTab);
        rentalsViewPagerAdapter = new RentalsViewPagerAdapter(getParentFragmentManager());
        viewPager.setAdapter(rentalsViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        return root;
    }
}