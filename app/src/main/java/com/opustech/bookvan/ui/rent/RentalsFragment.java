package com.opustech.bookvan.ui.rent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.opustech.bookvan.R;

public class RentalsFragment extends Fragment {

    private RentViewModel rentViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        rentViewModel =
                new ViewModelProvider(this).get(RentViewModel.class);
        View root = inflater.inflate(R.layout.fragment_rent, container, false);

        return root;
    }
}