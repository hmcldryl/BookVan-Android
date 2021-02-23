package com.opustech.bookvan.ui.adapters.transport;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.opustech.bookvan.ui.fragments.admin.bookings.BookingsConfirmedAdminFragment;
import com.opustech.bookvan.ui.fragments.admin.bookings.BookingsHistoryAdminFragment;
import com.opustech.bookvan.ui.fragments.admin.bookings.BookingsPendingAdminFragment;
import com.opustech.bookvan.ui.fragments.transport.bookings.BookingsConfirmedTransportFragment;
import com.opustech.bookvan.ui.fragments.transport.bookings.BookingsHistoryTransportFragment;
import com.opustech.bookvan.ui.fragments.transport.bookings.BookingsPendingTransportFragment;

public class BookingsTransportPagerAdapter extends FragmentStateAdapter {

    public BookingsTransportPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new BookingsPendingTransportFragment();
            case 2:
                return new BookingsHistoryTransportFragment();
            default:
                return new BookingsConfirmedTransportFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}