package com.opustech.bookvan;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.opustech.bookvan.ui.activeBooking.ActiveBookingFragment;
import com.opustech.bookvan.ui.bookingHistory.BookingHistoryFragment;

public class ProfileViewPagerAdapter extends FragmentPagerAdapter {

    public ProfileViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
        {
            fragment = new ActiveBookingFragment();
        }
        else if (position == 1)
        {
            fragment = new BookingHistoryFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
        {
            title = "Active Booking";
        }
        else if (position == 1)
        {
            title = "Booking History";
        }
        return title;
    }
}
