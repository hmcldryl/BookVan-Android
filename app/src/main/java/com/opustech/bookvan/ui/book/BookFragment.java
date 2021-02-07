package com.opustech.bookvan.ui.book;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.opustech.bookvan.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookFragment extends Fragment {

    TextInputLayout bookingCustomerName,
            bookingCustomerEmail,
            bookingContactNumber,
            bookingLocationFrom,
            bookingLocationTo,
            bookingScheduleDate,
            bookingScheduleTime,
            bookingCountAdult,
            bookingCountChild;

    ImageButton addAdultCount, addChildCount, subtractAdultCount, subtractChildCount;

    private BookViewModel bookViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        bookViewModel =
                new ViewModelProvider(this).get(BookViewModel.class);
        View root = inflater.inflate(R.layout.fragment_book, container, false);

        bookingCustomerName = root.findViewById(R.id.bookingCustomerName);
        bookingCustomerEmail = root.findViewById(R.id.bookingCustomerEmail);
        bookingContactNumber = root.findViewById(R.id.bookingContactNumber);
        bookingLocationFrom = root.findViewById(R.id.bookingLocationFrom);
        bookingLocationTo = root.findViewById(R.id.bookingLocationTo);
        bookingScheduleDate = root.findViewById(R.id.bookingScheduleDate);
        bookingScheduleTime = root.findViewById(R.id.bookingScheduleTime);
        bookingCountAdult = root.findViewById(R.id.bookingCountAdult);
        bookingCountChild = root.findViewById(R.id.bookingCountChild);

        addAdultCount = root.findViewById(R.id.btnCountAdultAdd);
        subtractAdultCount = root.findViewById(R.id.btnCountAdultSubtract);
        addChildCount = root.findViewById(R.id.btnCountChildAdd);
        subtractChildCount = root.findViewById(R.id.btnCountChildSubtract);

        AutoCompleteTextView bookingLocationFromACT = root.findViewById(R.id.bookingLocationFromACT);
        AutoCompleteTextView bookingLocationToACT = root.findViewById(R.id.bookingLocationToACT);

        ArrayList<String> locationArray = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.destinations)));
        ArrayAdapter<String> locationArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, locationArray);

        bookingLocationFromACT.setAdapter(locationArrayAdapter);
        bookingLocationFromACT.setThreshold(1);
        bookingLocationToACT.setAdapter(locationArrayAdapter);
        bookingLocationToACT.setThreshold(1);

        bookingCountAdult.getEditText().setText("1");
        bookingCountChild.getEditText().setText("0");

        addAdultCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(bookingCountAdult.getEditText().getText().toString()) >= 0) {
                    int count = Integer.parseInt(bookingCountAdult.getEditText().getText().toString()) + 1;
                    bookingCountAdult.getEditText().setText(String.valueOf(count));
                }
            }
        });

        addChildCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(bookingCountChild.getEditText().getText().toString()) >= 0) {
                    int count = Integer.parseInt(bookingCountChild.getEditText().getText().toString()) + 1;
                    bookingCountChild.getEditText().setText(String.valueOf(count));
                }
            }
        });

        subtractAdultCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(bookingCountAdult.getEditText().getText().toString()) > 0) {
                    int count = Integer.parseInt(bookingCountAdult.getEditText().getText().toString()) - 1;
                    bookingCountAdult.getEditText().setText(String.valueOf(count));
                }
            }
        });

        subtractChildCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(bookingCountChild.getEditText().getText().toString()) > 0) {
                    int count = Integer.parseInt(bookingCountChild.getEditText().getText().toString()) - 1;
                    bookingCountChild.getEditText().setText(String.valueOf(count));
                }
            }
        });

        return root;
    }

}