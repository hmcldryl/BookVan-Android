package com.opustech.bookvan.ui.activeBooking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.opustech.bookvan.R;

public class ActiveBookingFragment extends Fragment {

    private ActiveBookingViewModel activeBookingViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        activeBookingViewModel =
                new ViewModelProvider(this).get(ActiveBookingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_active_booking, container, false);



        return root;
    }
}