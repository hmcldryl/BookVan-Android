package com.opustech.bookvan.ui.contact;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.opustech.bookvan.R;

public class ContactAdminFragment extends Fragment {

    private ContactAdminViewModel contactAdminViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        contactAdminViewModel =
                new ViewModelProvider(this).get(ContactAdminViewModel.class);
        View root = inflater.inflate(R.layout.fragment_contact, container, false);



        return root;
    }
}