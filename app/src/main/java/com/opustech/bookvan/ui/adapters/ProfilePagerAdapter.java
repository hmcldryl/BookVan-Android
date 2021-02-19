package com.opustech.bookvan.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.opustech.bookvan.ui.fragments.user.booking.BookingsFragment;
import com.opustech.bookvan.ui.fragments.user.booking.BookingHistoryFragment;

public class ProfilePagerAdapter extends FragmentStateAdapter {


    public ProfilePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new BookingHistoryFragment();
            default:
                return new BookingsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
