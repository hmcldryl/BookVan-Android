package com.opustech.bookvan.adapters.admin;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.opustech.bookvan.ui.fragments.admin.bookings.BookingsCancelledAdminFragment;
import com.opustech.bookvan.ui.fragments.admin.bookings.BookingsConfirmedAdminFragment;
import com.opustech.bookvan.ui.fragments.admin.bookings.BookingsHistoryAdminFragment;
import com.opustech.bookvan.ui.fragments.admin.bookings.BookingsPendingAdminFragment;

public class BookingsAdminPagerAdapter extends FragmentStateAdapter {

    public BookingsAdminPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new BookingsConfirmedAdminFragment();
            case 2:
                return new BookingsCancelledAdminFragment();
            case 3:
                return new BookingsHistoryAdminFragment();
            default:
                return new BookingsPendingAdminFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}