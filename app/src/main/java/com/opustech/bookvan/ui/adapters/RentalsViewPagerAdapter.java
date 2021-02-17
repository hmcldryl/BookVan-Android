package com.opustech.bookvan.ui.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.opustech.bookvan.ui.fragments.admin.rentals.RentalsFormFragment;
import com.opustech.bookvan.ui.fragments.admin.rentals.RentalsListingFragment;

public class RentalsViewPagerAdapter extends FragmentPagerAdapter {

    public RentalsViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
        {
            fragment = new RentalsFormFragment();
        }
        else if (position == 1)
        {
            fragment = new RentalsListingFragment();
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
            title = "Add Van for Rental";
        }
        else if (position == 1)
        {
            title = "Van Listings for Rental";
        }
        return title;
    }
}
