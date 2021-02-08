package com.opustech.bookvan.ui.book;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import com.opustech.bookvan.MainActivity;
import com.opustech.bookvan.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class BookFragment extends Fragment {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference usersReference = firebaseFirestore.collection("users");

    private TextInputLayout bookingCustomerName,
            bookingContactNumber,
            bookingLocationFrom,
            bookingLocationTo,
            bookingScheduleDate,
            bookingScheduleTime,
            bookingCountAdult,
            bookingCountChild;
    private ImageButton addAdultCount,
            addChildCount,
            subtractAdultCount,
            subtractChildCount;
    private Button btnBook;
    private TextView checkoutTotal;

    private String email, name;

    private BookViewModel bookViewModel;

    private String admin_uid = "btLTtUYnMuWvkrJspvKqZIirLce2";

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

        checkoutTotal = root.findViewById(R.id.checkoutTotal);
        checkoutTotal.setText("170.00");

        AutoCompleteTextView bookingLocationFromACT = root.findViewById(R.id.bookingLocationFromACT);
        AutoCompleteTextView bookingLocationToACT = root.findViewById(R.id.bookingLocationToACT);

        ArrayList<String> locationArray = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.destinations)));
        ArrayAdapter<String> locationArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, locationArray);

        bookingLocationFromACT.setAdapter(locationArrayAdapter);
        bookingLocationFromACT.setThreshold(1);
        bookingLocationToACT.setAdapter(locationArrayAdapter);
        bookingLocationToACT.setThreshold(1);

        fetchCustomerInfo();
        initializeDatePicker();
        initializeTimePicker();

        bookingCountAdult.getEditText().setText("1");
        bookingCountChild.getEditText().setText("0");

        checkoutTotal.setText("170.00");

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
            }
        });

        return root;
    }

    private void inputCheck() {
        if (bookingCustomerName.getEditText().getText().toString().isEmpty()) {
            bookingCustomerName.getEditText().setError("Please enter your name.");
        }
        else if (bookingContactNumber.getEditText().getText().toString().isEmpty()) {
            bookingContactNumber.getEditText().setError("Please enter a contact number.");
        }
        else if (bookingLocationFrom.getEditText().getText().toString().isEmpty()) {
            bookingLocationFrom.getEditText().setError("Please enter your starting location.");
        }
        else if (bookingLocationTo.getEditText().getText().toString().isEmpty()) {
            bookingLocationTo.getEditText().setError("Please enter the location you wish to go.");

        }
        else if (bookingScheduleDate.getEditText().getText().toString().isEmpty()) {
            bookingScheduleDate.getEditText().setError("Please enter desired schedule date.");
        }
        else if (bookingScheduleTime.getEditText().getText().toString().isEmpty()) {
            bookingScheduleTime.getEditText().setError("Please enter desired schedule time.");
        }
        else if (Integer.parseInt(bookingCountAdult.getEditText().getText().toString()) == 0 && Integer.parseInt(bookingCountChild.getEditText().getText().toString()) == 0) {
            bookingCountAdult.getEditText().setError("Must have at least 1 adult passenger.");
        }
        else if (email.isEmpty()) {
            Toast.makeText(getActivity(), "Error: Please try again.", Toast.LENGTH_SHORT).show();
            fetchCustomerInfo();
        } else {
            final ACProgressFlower dialog = new ACProgressFlower.Builder(getActivity())
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(getResources().getColor(R.color.colorAccent))
                    .text("Processing...")
                    .fadeColor(Color.DKGRAY).build();
            dialog.show();

            String customer_name = bookingCustomerName.getEditText().getText().toString();
            String customer_email = email;
            String booking_contact_number = bookingContactNumber.getEditText().getText().toString();
            String booking_location_from = bookingLocationFrom.getEditText().getText().toString();
            String booking_location_to = bookingLocationTo.getEditText().getText().toString();
            String booking_schedule_date = bookingScheduleDate.getEditText().getText().toString();
            String booking_schedule_time = bookingScheduleTime.getEditText().getText().toString();
            String booking_count_adult = bookingCountAdult.getEditText().getText().toString();
            String booking_count_child = bookingCountChild.getEditText().getText().toString();
            String booking_price = checkoutTotal.getText().toString();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("customer_uid", getCurrentUserId());
            hashMap.put("customer_name", customer_name);
            hashMap.put("customer_email", customer_email);
            hashMap.put("booking_contact_number", booking_contact_number);
            hashMap.put("booking_location_from", booking_location_from);
            hashMap.put("booking_location_to", booking_location_to);
            hashMap.put("booking_schedule_date", booking_schedule_date);
            hashMap.put("booking_schedule_time", booking_schedule_time);
            hashMap.put("booking_count_adult", booking_count_adult);
            hashMap.put("booking_count_child", booking_count_child);
            hashMap.put("booking_price", booking_price);
            hashMap.put("booking_status", "pending");

            usersReference.document(admin_uid)
                    .collection("pending_bookings")
                    .add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "Booking success!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void initializeTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                calendar.set(Calendar.HOUR_OF_DAY, i);
                calendar.set(Calendar.MINUTE, i1);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                bookingScheduleTime.getEditText().setText(simpleDateFormat.format(calendar.getTime()));
            }
        };
        bookingScheduleTime.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(getActivity(), time,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), true)
                        .show();
            }
        });
    }

    private void initializeDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);
                bookingScheduleDate.getEditText().setText(simpleDateFormat.format(calendar.getTime()));
            }
        };
        bookingScheduleDate.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private String getCurrentUserId() {
        return firebaseAuth.getCurrentUser().getUid();
    }

    private void fetchCustomerInfo() {
        Toast.makeText(getActivity(), "Fetching info...", Toast.LENGTH_SHORT).show();
        usersReference.document(getCurrentUserId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    name = task.getResult().getString("name");
                    email = task.getResult().getString("email");
                    bookingCustomerName.getEditText().setText(name);
                    Toast.makeText(getActivity(), "Fetching info successful.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}