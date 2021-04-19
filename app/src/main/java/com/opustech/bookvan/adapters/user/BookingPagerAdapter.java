package com.opustech.bookvan.adapters.user;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.opustech.bookvan.ui.fragments.user.booking.BookingHistoryFragment;
import com.opustech.bookvan.ui.fragments.user.booking.CancelledBookingFragment;
import com.opustech.bookvan.ui.fragments.user.booking.ConfirmedBookingFragment;
import com.opustech.bookvan.ui.fragments.user.booking.PendingBookingFragment;

public class BookingPagerAdapter extends FragmentStateAdapter {

    public BookingPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new ConfirmedBookingFragment();
            case 2:
                return new CancelledBookingFragment();
            case 3:
                return new BookingHistoryFragment();
            default:
                return new PendingBookingFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
