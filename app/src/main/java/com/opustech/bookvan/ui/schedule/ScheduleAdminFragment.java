package com.opustech.bookvan.ui.schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.opustech.bookvan.R;

public class ScheduleAdminFragment extends Fragment {

    private ScheduleViewModel scheduleViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        scheduleViewModel =
                new ViewModelProvider(this).get(ScheduleViewModel.class);
        View root = inflater.inflate(R.layout.fragment_schedule, container, false);
        return root;
    }
}