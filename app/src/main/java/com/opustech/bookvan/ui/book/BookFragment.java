package com.opustech.bookvan.ui.book;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class BookFragment extends Fragment {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;

    TextInputLayout bookingCustomerName,
            bookingContactNumber,
            bookingLocationFrom,
            bookingLocationTo,
            bookingScheduleDate,
            bookingScheduleTime,
            bookingCountAdult,
            bookingCountChild;
    ImageButton addAdultCount,
            addChildCount,
            subtractAdultCount,
            subtractChildCount;
    Button btnBook;

    private BookViewModel bookViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        bookViewModel =
                new ViewModelProvider(this).get(BookViewModel.class);
        View root = inflater.inflate(R.layout.fragment_book, container, false);

        bookingCustomerName = root.findViewById(R.id.bookingCustomerName);
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

        btnBook = root.findViewById(R.id.btnConfirmBooking);

        AutoCompleteTextView bookingLocationFromACT = root.findViewById(R.id.bookingLocationFromACT);
        AutoCompleteTextView bookingLocationToACT = root.findViewById(R.id.bookingLocationToACT);

        ArrayList<String> locationArray = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.destinations)));
        ArrayAdapter<String> locationArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, locationArray);

        bookingLocationFromACT.setAdapter(locationArrayAdapter);
        bookingLocationFromACT.setThreshold(1);
        bookingLocationToACT.setAdapter(locationArrayAdapter);
        bookingLocationToACT.setThreshold(1);

        inputCustomerName();

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

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputCheck();
                btnBook.setEnabled(false);
            }
        });

        return root;
    }

    private void inputCheck() {
        if (bookingCustomerName.getEditText().getText().toString().isEmpty()) {
            bookingCustomerName.getEditText().setError("Please enter your name.");
            btnBook.setEnabled(true);
        }
        if (bookingContactNumber.getEditText().getText().toString().isEmpty()) {
            bookingContactNumber.getEditText().setError("Please enter a contact number.");
            btnBook.setEnabled(true);
        }
        if (bookingLocationFrom.getEditText().getText().toString().isEmpty()) {
            bookingLocationFrom.getEditText().setError("Please enter your starting location.");
            btnBook.setEnabled(true);
        }
        if (bookingLocationTo.getEditText().getText().toString().isEmpty()) {
            bookingLocationTo.getEditText().setError("Please enter the location you wish to go.");
            btnBook.setEnabled(true);
        }
        if (bookingScheduleDate.getEditText().getText().toString().isEmpty()) {
            bookingScheduleDate.getEditText().setError("Please enter desired schedule date.");
            btnBook.setEnabled(true);
        }
        if (bookingScheduleTime.getEditText().getText().toString().isEmpty()) {
            bookingScheduleTime.getEditText().setError("Please enter desired schedule time.");
            btnBook.setEnabled(true);
        }
        if (Integer.parseInt(bookingCountAdult.getEditText().getText().toString()) == 0 && Integer.parseInt(bookingCountChild.getEditText().getText().toString()) == 0) {
            bookingCountAdult.getEditText().setError("Must have at least 1 adult passenger.");
            btnBook.setEnabled(true);
        }
        else {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(getCurrentUserId())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        String customer_name = bookingCustomerName.getEditText().getText().toString();
                        String customer_email = task.getResult().getString("email");
                        String booking_contact_number = bookingContactNumber.getEditText().getText().toString();
                        String booking_location_from = bookingLocationFrom.getEditText().getText().toString();
                        String booking_location_to = bookingLocationTo.getEditText().getText().toString();
                        String booking_schedule_date = bookingScheduleDate.getEditText().getText().toString();
                        String booking_schedule_time = bookingScheduleTime.getEditText().getText().toString();
                        String booking_count_adult = bookingCountAdult.getEditText().getText().toString();
                        String booking_count_child = bookingCountChild.getEditText().getText().toString();
                        uploadBooking(getCurrentUserId(),
                                customer_name,
                                customer_email,
                                booking_contact_number,
                                booking_location_from,
                                booking_location_to,
                                booking_schedule_date,
                                booking_schedule_time,
                                booking_count_adult,
                                booking_count_child);
                    }
                }
            });
        }
    }

    private String getCurrentUserId() {
        return firebaseAuth.getCurrentUser().getUid();
    }

    private void inputCustomerName() {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(getCurrentUserId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String name = task.getResult().getString("name");
                    bookingCustomerName.getEditText().setText(name);
                    if (!bookingCustomerName.getEditText().getText().toString().isEmpty()) {
                        bookingCustomerName.getEditText().setEnabled(false);
                    }
                }
            }
        });
    }

    private void uploadBooking(String customerUid,
                               String customerName,
                               String customerEmail,
                               String customerContactNumber,
                               String bookingLocationFrom,
                               String bookingLocationTo,
                               String bookingScheduleDate,
                               String bookingScheduleTime,
                               String bookingCountAdult,
                               String bookingCountChild) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(customerUid, "customer_uid");
        hashMap.put(customerName, "customer_name");
        hashMap.put(customerEmail, "customer_email");
        hashMap.put(customerContactNumber, "booking_contact_number");
        hashMap.put(bookingLocationFrom, "booking_location_from");
        hashMap.put(bookingLocationTo, "booking_location_to");
        hashMap.put(bookingScheduleDate, "booking_schedule_date");
        hashMap.put(bookingScheduleTime, "booking_schedule_time");
        hashMap.put(bookingCountAdult, "booking_count_adult");
        hashMap.put(bookingCountChild, "booking_count_child");
        hashMap.put("pending", "booking_status");

        FirebaseFirestore.getInstance()
                .collection("users")
                .document("btLTtUYnMuWvkrJspvKqZIirLce2")
                .collection("pending_bookings")
                .add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    btnBook.setEnabled(true);
                }
            }
        });


    }

}