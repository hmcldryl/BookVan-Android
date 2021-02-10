package com.opustech.bookvan.ui.vanCompany;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.opustech.bookvan.R;

public class VanCompanyFragment extends Fragment {

    private VanCompanyViewModel vanCompanyViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        vanCompanyViewModel =
                new ViewModelProvider(this).get(VanCompanyViewModel.class);
        View root = inflater.inflate(R.layout.fragment_van_companies, container, false);

        return root;
    }

}