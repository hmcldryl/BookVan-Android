package com.opustech.bookvan.adapters.user;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.opustech.bookvan.ui.fragments.user.schedules.SchedulesNorthFragment;
import com.opustech.bookvan.ui.fragments.user.schedules.SchedulesSouthFragment;

public class SchedulePagerAdapter extends FragmentStateAdapter {

    public SchedulePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new SchedulesSouthFragment();
            default:
                return new SchedulesNorthFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
