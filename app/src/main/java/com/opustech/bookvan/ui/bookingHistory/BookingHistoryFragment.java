package com.opustech.bookvan.ui.bookingHistory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.opustech.bookvan.R;

public class BookingHistoryFragment extends Fragment {

    private BookingHistoryViewModel bookingHistoryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        bookingHistoryViewModel =
                new ViewModelProvider(this).get(BookingHistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_booking_history, container, false);



        return root;
    }
}