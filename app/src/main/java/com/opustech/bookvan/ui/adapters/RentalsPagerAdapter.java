package com.opustech.bookvan.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.opustech.bookvan.ui.fragments.admin.rentals.RentalsFormFragment;
import com.opustech.bookvan.ui.fragments.admin.rentals.RentalsListingFragment;

public class RentalsPagerAdapter extends FragmentStateAdapter {

    public RentalsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new RentalsFormFragment();
            default:
                return new RentalsListingFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
