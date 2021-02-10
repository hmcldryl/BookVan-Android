package com.opustech.bookvan.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.opustech.bookvan.ProfileViewPagerAdapter;
import com.opustech.bookvan.R;

public class ProfileFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ProfileViewPagerAdapter profileViewPagerAdapter;

    private ProfileViewModel profileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        viewPager = root.findViewById(R.id.profileViewPager);
        tabLayout = root.findViewById(R.id.profileTab);
        profileViewPagerAdapter = new ProfileViewPagerAdapter(getParentFragmentManager());
        viewPager.setAdapter(profileViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        return root;
    }
}