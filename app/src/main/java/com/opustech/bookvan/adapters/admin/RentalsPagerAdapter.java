package com.opustech.bookvan.adapters.admin;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.opustech.bookvan.ui.fragments.admin.rentals.RentalsCancelledFragment;
import com.opustech.bookvan.ui.fragments.admin.rentals.RentalsConfirmedFragment;
import com.opustech.bookvan.ui.fragments.admin.rentals.RentalsHistoryFragment;
import com.opustech.bookvan.ui.fragments.admin.rentals.RentalsPendingFragment;

public class RentalsPagerAdapter extends FragmentStateAdapter {

    public RentalsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            default:
                return new RentalsPendingFragment();
            case 1:
                return new RentalsConfirmedFragment();
            case 2:
                return new RentalsCancelledFragment();
            case 3:
                return new RentalsHistoryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}