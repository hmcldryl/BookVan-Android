package com.opustech.bookvan.ui.fragments.user;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, bookingsReference, partnersReference;

    private TextInputLayout bookingCustomerName,
            bookingContactNumber,
            bookingVanTransport,
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
    private ExtendedFloatingActionButton btnBook;
    private AutoCompleteTextView inputVanTransportACT, bookingLocationFromACT, bookingLocationToACT;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_book, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        bookingsReference = firebaseFirestore.collection("bookings");
        partnersReference = firebaseFirestore.collection("partners");

        initializeUi(root);

        populateVanTransportList();
        populateLocationList();
        initializeDatePicker();
        initializeTimePicker();

        bookingCountAdult.getEditText().setText("1");
        bookingCountChild.getEditText().setText("0");

        addAdultCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count_adult = Integer.parseInt(bookingCountAdult.getEditText().getText().toString());
                if (count_adult >= 0) {
                    int count = count_adult + 1;
                    bookingCountAdult.getEditText().setText(String.valueOf(count));
                }
            }
        });

        addChildCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count_child = Integer.parseInt(bookingCountChild.getEditText().getText().toString());
                if (count_child >= 0) {
                    int count = count_child + 1;
                    bookingCountChild.getEditText().setText(String.valueOf(count));
                }
            }
        });

        subtractAdultCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count_adult = Integer.parseInt(bookingCountAdult.getEditText().getText().toString());
                if (count_adult > 0) {
                    int count = count_adult - 1;
                    bookingCountAdult.getEditText().setText(String.valueOf(count));
                }
            }
        });

        subtractChildCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count_child = Integer.parseInt(bookingCountChild.getEditText().getText().toString());
                if (count_child > 0) {
                    int count = count_child - 1;
                    bookingCountChild.getEditText().setText(String.valueOf(count));
                }
            }
        });

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableInput();
                inputCheck();
            }
        });

        return root;
    }

    private void initializeUi(View root) {
        bookingCustomerName = root.findViewById(R.id.bookingCustomerName);
        bookingContactNumber = root.findViewById(R.id.bookingContactNumber);
        bookingVanTransport = root.findViewById(R.id.inputVanTransport);
        inputVanTransportACT = root.findViewById(R.id.inputVanTransportACT);
        bookingLocationFrom = root.findViewById(R.id.bookingLocationFrom);
        bookingLocationFromACT = root.findViewById(R.id.bookingLocationFromACT);
        bookingLocationTo = root.findViewById(R.id.bookingLocationTo);
        bookingLocationToACT = root.findViewById(R.id.bookingLocationToACT);
        bookingScheduleDate = root.findViewById(R.id.bookingScheduleDate);
        bookingScheduleTime = root.findViewById(R.id.bookingScheduleTime);
        bookingCountAdult = root.findViewById(R.id.bookingCountAdult);
        bookingCountChild = root.findViewById(R.id.bookingCountChild);

        addAdultCount = root.findViewById(R.id.btnCountAdultAdd);
        subtractAdultCount = root.findViewById(R.id.btnCountAdultSubtract);
        addChildCount = root.findViewById(R.id.btnCountChildAdd);
        subtractChildCount = root.findViewById(R.id.btnCountChildSubtract);

        btnBook = root.findViewById(R.id.btnBook);
    }

    private void generateRefNum(String uid, String name, String contact_number, String transport_name, String location_from, String location_to, String schedule_date, String schedule_time, int count_adult, int count_child) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(getActivity())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(getResources().getColor(R.color.white))
                .text("Processing...")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();

        bookingsReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int num = task.getResult().getDocuments().size() + 1;
                            String reference_number = "BV-" + String.format(Locale.ENGLISH, "%06d", num);
                            addNewBooking(dialog, reference_number, uid, name, contact_number, transport_name, location_from, location_to, schedule_date, schedule_time, count_adult, count_child);
                        }
                    }
                });
    }

    private void disableInput() {
        btnBook.setEnabled(false);
        bookingCustomerName.setEnabled(false);
        bookingContactNumber.setEnabled(false);
        bookingLocationFrom.setEnabled(false);
        bookingLocationTo.setEnabled(false);
        bookingScheduleDate.setEnabled(false);
        bookingScheduleTime.setEnabled(false);
        bookingCountAdult.setEnabled(false);
        bookingCountChild.setEnabled(false);
    }

    private void enableInput() {
        btnBook.setEnabled(true);
        bookingCustomerName.setEnabled(true);
        bookingContactNumber.setEnabled(true);
        bookingLocationFrom.setEnabled(true);
        bookingLocationTo.setEnabled(true);
        bookingScheduleDate.setEnabled(true);
        bookingScheduleTime.setEnabled(true);
        bookingCountAdult.setEnabled(true);
        bookingCountChild.setEnabled(true);
    }

    private void inputCheck() {
        String name = bookingCustomerName.getEditText().getText().toString();
        String contact_number = bookingContactNumber.getEditText().getText().toString();
        String transport_name = bookingVanTransport.getEditText().getText().toString();
        String location_from = bookingLocationFrom.getEditText().getText().toString();
        String location_to = bookingLocationTo.getEditText().getText().toString();
        String schedule_date = bookingScheduleDate.getEditText().getText().toString();
        String schedule_time = bookingScheduleTime.getEditText().getText().toString();
        int count_adult = Integer.parseInt(bookingCountAdult.getEditText().getText().toString());
        int count_child = Integer.parseInt(bookingCountChild.getEditText().getText().toString());

        if (name.isEmpty()) {
            enableInput();
            bookingCustomerName.getEditText().setError("Please enter your name.");
        } else if (contact_number.isEmpty()) {
            enableInput();
            bookingContactNumber.getEditText().setError("Please enter a contact number.");
        } else if (location_from.isEmpty()) {
            enableInput();
            bookingLocationFrom.getEditText().setError("Please enter your starting location.");
        } else if (transport_name.isEmpty()) {
            enableInput();
            bookingVanTransport.getEditText().setError("Please select your preferred van transport.");
        } else if (location_to.isEmpty()) {
            enableInput();
            bookingLocationTo.getEditText().setError("Please enter the location you wish to go.");
        } else if (schedule_date.isEmpty()) {
            enableInput();
            bookingScheduleDate.getEditText().setError("Please enter desired schedule date.");
        } else if (schedule_time.isEmpty()) {
            enableInput();
            bookingScheduleTime.getEditText().setError("Please enter desired schedule time.");
        } else if (count_adult == 0 && count_child == 0) {
            enableInput();
            bookingCountAdult.getEditText().setError("Must have at least 1 adult passenger.");
        } else {
            generateRefNum(firebaseAuth.getCurrentUser().getUid(), name, contact_number, transport_name, location_from, location_to, schedule_date, schedule_time, count_adult, count_child);
        }
    }

    private String generateTimestamp() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        return format.format(Calendar.getInstance().getTime());
    }

    private void addNewBooking(ACProgressFlower dialog, String reference_number, String uid, String name, String contact_number, String transport_name, String location_from, String location_to, String schedule_date, String schedule_time, int count_adult, int count_child) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("reference_number", reference_number);
        hashMap.put("uid", uid);
        hashMap.put("name", name);
        hashMap.put("contact_number", contact_number);
        hashMap.put("transport_name", transport_name);
        hashMap.put("location_from", location_from);
        hashMap.put("location_to", location_to);
        hashMap.put("schedule_date", schedule_date);
        hashMap.put("schedule_time", schedule_time);
        hashMap.put("count_adult", count_adult);
        hashMap.put("count_child", count_child);
        hashMap.put("timestamp", generateTimestamp());
        hashMap.put("status", "pending");

        bookingsReference.add(hashMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            enableInput();
                            Toast.makeText(getActivity(), "Success.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                        calendar.get(Calendar.MINUTE), false)
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
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
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

    private void populateVanTransportList() {
        ArrayList<String> vanTransportList = new ArrayList<>();
        partnersReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                vanTransportList.add(i, task.getResult().getDocuments().get(i).getString("name"));
                            }
                            ArrayAdapter<String> vanTransportAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, vanTransportList);
                            inputVanTransportACT.setAdapter(vanTransportAdapter);
                            inputVanTransportACT.setThreshold(1);
                        }
                    }
                });
    }

    private void populateLocationList() {
        ArrayList<String> locationArray = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.destinations)));
        ArrayAdapter<String> locationArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, locationArray);
        bookingLocationFromACT.setAdapter(locationArrayAdapter);
        bookingLocationToACT.setAdapter(locationArrayAdapter);
        bookingLocationFromACT.setThreshold(1);
        bookingLocationToACT.setThreshold(1);
    }
}