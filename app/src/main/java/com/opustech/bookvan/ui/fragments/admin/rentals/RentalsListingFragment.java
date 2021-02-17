package com.opustech.bookvan.ui.fragments.admin.rentals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.opustech.bookvan.R;

public class RentalsListingFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rentals, container, false);



        return root;
    }
}