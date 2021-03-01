package com.opustech.bookvan.adapters.user;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.opustech.bookvan.ui.fragments.admin.rentals.RentalsFormFragment;
import com.opustech.bookvan.ui.fragments.admin.rentals.RentalsListingFragment;
import com.opustech.bookvan.ui.fragments.user.ScheduleNorthFragment;
import com.opustech.bookvan.ui.fragments.user.ScheduleSouthFragment;

public class SchedulePagerAdapter extends FragmentStateAdapter {

    public SchedulePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new ScheduleSouthFragment();
            default:
                return new ScheduleNorthFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
