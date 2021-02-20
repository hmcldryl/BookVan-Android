package com.opustech.bookvan.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.opustech.bookvan.ui.fragments.admin.bookings.BookingsConfirmedAdminFragment;
import com.opustech.bookvan.ui.fragments.admin.bookings.BookingsHistoryAdminFragment;
import com.opustech.bookvan.ui.fragments.admin.bookings.BookingsPendingAdminFragment;
import com.opustech.bookvan.ui.fragments.admin.rentals.RentalsFormFragment;
import com.opustech.bookvan.ui.fragments.admin.rentals.RentalsListingFragment;
import com.opustech.bookvan.ui.fragments.user.booking.BookingHistoryFragment;

public class BookingsAdminPagerAdapter extends FragmentStateAdapter {

    public BookingsAdminPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new BookingsPendingAdminFragment();
            case 2:
                return new BookingsHistoryAdminFragment();
            default:
                return new BookingsConfirmedAdminFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}