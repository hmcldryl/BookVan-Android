package com.opustech.bookvan.adapters.transport;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.opustech.bookvan.ui.fragments.transport.bookings.BookingsCancelledTransportFragment;
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
            default:
                return new BookingsPendingTransportFragment();
            case 1:
                return new BookingsConfirmedTransportFragment();
            case 2:
                return new BookingsCancelledTransportFragment();
            case 3:
                return new BookingsHistoryTransportFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}