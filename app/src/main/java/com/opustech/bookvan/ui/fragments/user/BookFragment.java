package com.opustech.bookvan.ui.fragments.user;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.R;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import se.simbio.encryption.Encryption;
import third.part.android.util.Base64;

public class BookFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;

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
    private TextView priceTotal;

    private String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_book, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");

        String currentUserId = firebaseAuth.getCurrentUser().getUid();

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

        priceTotal = root.findViewById(R.id.price);
        priceTotal.setText("170.00");

        AutoCompleteTextView bookingLocationFromACT = root.findViewById(R.id.bookingLocationFromACT);
        AutoCompleteTextView bookingLocationToACT = root.findViewById(R.id.bookingLocationToACT);

        ArrayList<String> locationArray = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.destinations)));
        ArrayAdapter<String> locationArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, locationArray);

        bookingLocationFromACT.setAdapter(locationArrayAdapter);
        bookingLocationFromACT.setThreshold(1);
        bookingLocationToACT.setAdapter(locationArrayAdapter);
        bookingLocationToACT.setThreshold(1);

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
                btnBook.setEnabled(false);
                inputCheck(currentUserId);
            }
        });

        return root;
    }

    private void generateRefNum(String uid, String name, String contact_number, String location_from, String location_to, String schedule_date, String schedule_time, int count_adult, int count_child, float price) {
        usersReference.document(admin_uid)
                .collection("bookings")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int num = task.getResult().getDocuments().size() + 1;
                            String reference_number = "BV-" + String.format(Locale.ENGLISH, "%06d", num);
                            addNewBooking(reference_number, uid, name, contact_number, location_from, location_to, schedule_date, schedule_time, count_adult, count_child, price);
                        }
                    }
                });
    }

    private void inputCheck(String uid) {
        String name = bookingCustomerName.getEditText().getText().toString();
        String contact_number = bookingContactNumber.getEditText().getText().toString();
        String location_from = bookingLocationFrom.getEditText().getText().toString();
        String location_to = bookingLocationTo.getEditText().getText().toString();
        String schedule_date = bookingScheduleDate.getEditText().getText().toString();
        String schedule_time = bookingScheduleTime.getEditText().getText().toString();
        int count_adult = Integer.parseInt(bookingCountAdult.getEditText().getText().toString());
        int count_child = Integer.parseInt(bookingCountChild.getEditText().getText().toString());
        float price = Float.parseFloat(priceTotal.getText().toString());

        if (name.isEmpty()) {
            bookingCustomerName.getEditText().setError("Please enter your name.");
            btnBook.setEnabled(true);
        } else if (contact_number.isEmpty()) {
            bookingContactNumber.getEditText().setError("Please enter a contact number.");
            btnBook.setEnabled(true);
        } else if (location_from.isEmpty()) {
            bookingLocationFrom.getEditText().setError("Please enter your starting location.");
            btnBook.setEnabled(true);
        } else if (location_to.isEmpty()) {
            bookingLocationTo.getEditText().setError("Please enter the location you wish to go.");
            btnBook.setEnabled(true);
        } else if (schedule_date.isEmpty()) {
            bookingScheduleDate.getEditText().setError("Please enter desired schedule date.");
            btnBook.setEnabled(true);
        } else if (schedule_time.isEmpty()) {
            bookingScheduleTime.getEditText().setError("Please enter desired schedule time.");
            btnBook.setEnabled(true);
        } else if (count_adult == 0 && count_child == 0) {
            bookingCountAdult.getEditText().setError("Must have at least 1 adult passenger.");
            btnBook.setEnabled(true);
        } else {
            generateRefNum(uid, name, contact_number, location_from, location_to, schedule_date, schedule_time, count_adult, count_child, price);
        }
    }

    private String generateTimestamp() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        return format.format(Calendar.getInstance().getTime());
    }

    private void addNewBooking(String reference_number, String uid, String name, String contact_number, String location_from, String location_to, String schedule_date, String schedule_time, int count_adult, int count_child, float price) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(getActivity())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(getResources().getColor(R.color.colorAccent))
                .text("Processing...")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("reference_number", reference_number);
        hashMap.put("uid", uid);
        hashMap.put("name", name);
        hashMap.put("contact_number", contact_number);
        hashMap.put("location_from", location_from);
        hashMap.put("location_to", location_to);
        hashMap.put("schedule_date", schedule_date);
        hashMap.put("schedule_time", schedule_time);
        hashMap.put("count_adult", count_adult);
        hashMap.put("count_child", count_child);
        hashMap.put("price", price);
        hashMap.put("timestamp", generateTimestamp());
        hashMap.put("status", "pending");

        usersReference.document(admin_uid)
                .collection("bookings")
                .add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    btnBook.setEnabled(true);
                    new MaterialAlertDialogBuilder(getActivity())
                            .setTitle("Booking success!")
                            .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
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
}