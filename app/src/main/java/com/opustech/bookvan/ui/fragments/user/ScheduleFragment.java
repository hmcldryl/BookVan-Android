package com.opustech.bookvan.ui.fragments.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.R;
import com.opustech.bookvan.adapters.admin.RentalsPagerAdapter;
import com.opustech.bookvan.adapters.user.SchedulePagerAdapter;

public class ScheduleFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private SchedulePagerAdapter schedulePagerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_schedule_user, container, false);

        viewPager = root.findViewById(R.id.viewPager);
        tabLayout = root.findViewById(R.id.tabLayout);

        schedulePagerAdapter = new SchedulePagerAdapter(getActivity());
        viewPager.setAdapter(schedulePagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("North");
                        break;
                    case 1:
                        tab.setText("South");
                        break;
                }
            }
        }).attach();

        return root;
    }
}