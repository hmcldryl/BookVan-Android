package com.opustech.bookvan.ui.fragments.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.R;

public class ScheduleNorthFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_schedule_user_north, container, false);
        return root;
    }
}