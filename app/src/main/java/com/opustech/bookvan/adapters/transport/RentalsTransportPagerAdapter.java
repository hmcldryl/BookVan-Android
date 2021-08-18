package com.opustech.bookvan.adapters.transport;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.opustech.bookvan.ui.fragments.transport.bookings.BookingsCancelledTransportFragment;
import com.opustech.bookvan.ui.fragments.transport.bookings.BookingsConfirmedTransportFragment;
import com.opustech.bookvan.ui.fragments.transport.bookings.BookingsHistoryTransportFragment;
import com.opustech.bookvan.ui.fragments.transport.bookings.BookingsPendingTransportFragment;
import com.opustech.bookvan.ui.fragments.transport.rentals.RentalsCancelledTransportFragment;
import com.opustech.bookvan.ui.fragments.transport.rentals.RentalsConfirmedTransportFragment;
import com.opustech.bookvan.ui.fragments.transport.rentals.RentalsHistoryTransportFragment;
import com.opustech.bookvan.ui.fragments.transport.rentals.RentalsPendingTransportFragment;

public class RentalsTransportPagerAdapter extends FragmentStateAdapter {

    public RentalsTransportPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            default:
                return new RentalsPendingTransportFragment();
            case 1:
                return new RentalsConfirmedTransportFragment();
            case 2:
                return new RentalsCancelledTransportFragment();
            case 3:
                return new RentalsHistoryTransportFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}