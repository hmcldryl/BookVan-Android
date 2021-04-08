package com.opustech.bookvan.ui.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.opustech.bookvan.R;
import com.opustech.bookvan.adapters.chat.AdapterMessageChatRV;
import com.opustech.bookvan.adapters.user.AdapterRentMessageChatRV;
import com.opustech.bookvan.model.ChatMessage;
import com.opustech.bookvan.model.RentChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class UserRentActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, rentalsReference;

    private TextInputLayout inputName, inputLocationPickUp, inputLocationDropOff, inputContactNumber, inputDestination, inputPickUpDate, inputPickUpTime, inputDropOffDate, inputDropOffTime;

    private RecyclerView rentChatList;

    private ExtendedFloatingActionButton btnRent;

    private AdapterRentMessageChatRV adapterRentMessageChatRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_rent);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        rentalsReference = firebaseFirestore.collection("rentals");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Renting Form");
        getSupportActionBar().setSubtitle("Fill up the following fields to rent a van.");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        inputName = findViewById(R.id.inputName);
        inputContactNumber = findViewById(R.id.inputContactNumber);
        inputLocationPickUp = findViewById(R.id.inputLocationPickUp);
        inputPickUpDate = findViewById(R.id.inputPickUpDate);
        inputPickUpTime = findViewById(R.id.inputPickUpTime);
        inputDestination = findViewById(R.id.inputDestination);
        inputLocationDropOff = findViewById(R.id.inputLocationDropOff);
        inputDropOffDate = findViewById(R.id.inputDropOffDate);
        inputDropOffTime = findViewById(R.id.inputDropOffTime);
        btnRent = findViewById(R.id.btnRent);

        initializeDatePickerPickUp();
        initializeDatePickerDropOff();
        initializeTimePickerPickUp();
        initializeTimePickerDropOff();

        btnRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableInput();
                inputCheck();
            }
        });

        Query query = rentalsReference.document(firebaseAuth.getCurrentUser().getUid())
                .collection("rentals")
                .orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<RentChatMessage> options = new FirestoreRecyclerOptions.Builder<RentChatMessage>()
                .setQuery(query, RentChatMessage.class)
                .build();

        adapterRentMessageChatRV = new AdapterRentMessageChatRV(options);
        LinearLayoutManager manager = new LinearLayoutManager(UserRentActivity.this);
        manager.setStackFromEnd(true);

        rentChatList = findViewById(R.id.rentChatList);
        rentChatList.setHasFixedSize(true);
        rentChatList.setLayoutManager(manager);
        rentChatList.setAdapter(adapterRentMessageChatRV);
    }

    private void enableInput() {
        inputName.setEnabled(true);
        inputContactNumber.setEnabled(true);
        inputLocationPickUp.setEnabled(true);
        inputPickUpDate.setEnabled(true);
        inputPickUpTime.setEnabled(true);
        inputDestination.setEnabled(true);
        inputLocationDropOff.setEnabled(true);
        inputDropOffDate.setEnabled(true);
        inputDropOffTime.setEnabled(true);
        btnRent.setEnabled(true);
    }

    private void disableInput() {
        inputName.setEnabled(false);
        inputContactNumber.setEnabled(false);
        inputLocationPickUp.setEnabled(false);
        inputPickUpDate.setEnabled(false);
        inputPickUpTime.setEnabled(false);
        inputDestination.setEnabled(false);
        inputLocationDropOff.setEnabled(false);
        inputDropOffDate.setEnabled(false);
        inputDropOffTime.setEnabled(false);
        btnRent.setEnabled(false);
    }

    private String generateTimestamp() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        return format.format(Calendar.getInstance().getTime());
    }


    private void inputCheck() {
        String name = inputName.getEditText().getText().toString();
        String contact_number = inputContactNumber.getEditText().getText().toString();
        String pick_up_location = inputLocationPickUp.getEditText().getText().toString();
        String pick_up_date = inputPickUpDate.getEditText().getText().toString();
        String pick_up_time = inputPickUpTime.getEditText().getText().toString();
        String destination = inputDestination.getEditText().getText().toString();
        String drop_off_location = inputLocationDropOff.getEditText().getText().toString();
        String drop_off_date = inputDropOffDate.getEditText().getText().toString();
        String drop_off_time = inputDropOffTime.getEditText().getText().toString();

        if (name.isEmpty()) {
            enableInput();
            inputName.setError("Please enter a name.");
        } else if (contact_number.isEmpty()) {
            enableInput();
            inputContactNumber.setError("Please enter a contact number.");
        } else if (pick_up_location.isEmpty()) {
            enableInput();
            inputLocationPickUp.setError("Please enter a contact number.");
        } else if (pick_up_date.isEmpty()) {
            enableInput();
            inputPickUpDate.setError("Please enter a contact number.");
        } else if (pick_up_time.isEmpty()) {
            enableInput();
            inputPickUpTime.setError("Please enter a contact number.");
        } else if (destination.isEmpty()) {
            enableInput();
            inputDestination.setError("Please enter a contact number.");
        } else if (drop_off_location.isEmpty()) {
            enableInput();
            inputLocationDropOff.setError("Please enter a contact number.");
        } else if (drop_off_date.isEmpty()) {
            enableInput();
            inputDropOffDate.setError("Please enter a contact number.");
        } else if (drop_off_time.isEmpty()) {
            enableInput();
            inputDropOffTime.setError("Please enter a contact number.");
        } else {
            submitRentInfo(name, contact_number, pick_up_location, pick_up_date, pick_up_time, destination, drop_off_location, drop_off_date, drop_off_time);
        }
    }

    private void submitRentInfo(String name, String contact_number, String pick_up_location, String pick_up_date, String pick_up_time, String destination, String drop_off_location, String drop_off_date, String drop_off_time) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(this.getResources().getColor(R.color.white))
                .text("Processing...")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("contact_number", contact_number);
        hashMap.put("pick_up_location", pick_up_location);
        hashMap.put("pick_up_date", pick_up_date);
        hashMap.put("pick_up_time", pick_up_time);
        hashMap.put("destination", destination);
        hashMap.put("drop_off_location", drop_off_location);
        hashMap.put("drop_off_date", drop_off_date);
        hashMap.put("drop_off_time", drop_off_time);
        hashMap.put("timestamp", generateTimestamp());
        hashMap.put("uid", firebaseAuth.getCurrentUser().getUid());
        rentalsReference.add(hashMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            enableInput();
                            Toast.makeText(UserRentActivity.this, "Success.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initializeDatePickerPickUp() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                inputPickUpDate.getEditText().setText(simpleDateFormat.format(calendar.getTime()));
            }
        };
        inputPickUpDate.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(UserRentActivity.this, date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void initializeTimePickerPickUp() {
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                calendar.set(Calendar.HOUR_OF_DAY, i);
                calendar.set(Calendar.MINUTE, i1);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                inputPickUpTime.getEditText().setText(simpleDateFormat.format(calendar.getTime()));
            }
        };
        inputPickUpTime.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(UserRentActivity.this, time,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), false)
                        .show();
            }
        });
    }

    private void initializeDatePickerDropOff() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                inputDropOffDate.getEditText().setText(simpleDateFormat.format(calendar.getTime()));
            }
        };
        inputDropOffDate.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(UserRentActivity.this, date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void initializeTimePickerDropOff() {
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                calendar.set(Calendar.HOUR_OF_DAY, i);
                calendar.set(Calendar.MINUTE, i1);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                inputDropOffTime.getEditText().setText(simpleDateFormat.format(calendar.getTime()));
            }
        };
        inputDropOffTime.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(UserRentActivity.this, time,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), false)
                        .show();
            }
        });
    }
}