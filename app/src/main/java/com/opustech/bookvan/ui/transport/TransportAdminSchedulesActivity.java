package com.opustech.bookvan.ui.transport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.R;
import com.opustech.bookvan.adapters.transport.AdapterTransportDropdownTripSchedule;
import com.opustech.bookvan.adapters.transport.AdapterTransportTripScheduleListRV;
import com.opustech.bookvan.adapters.user.AdapterDropdownTripSchedule;
import com.opustech.bookvan.adapters.user.AdapterScheduleListRV;
import com.opustech.bookvan.model.Schedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class TransportAdminSchedulesActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference systemSchedulesReference, schedulesReference;

    private TextInputLayout scheduleRouteTimeQueue,
            scheduleRouteTimeDepart,
            scheduleRoute,
            scheduleRoutePrice;
    private AutoCompleteTextView scheduleRouteACT;
    private MaterialButton btnAddRouteSchedule;
    private RecyclerView systemScheduleList;

    private AdapterTransportDropdownTripSchedule adapterDropdownTripSchedule;
    private ArrayList<Schedule> routeArray;

    private String route_from, route_to;
    private double price = 0.00;

    private AdapterTransportTripScheduleListRV adapterScheduleListRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_admin_schedules);

        firebaseFirestore = FirebaseFirestore.getInstance();
        schedulesReference = firebaseFirestore.collection("schedules");
        systemSchedulesReference = firebaseFirestore.collection("system").document("data").collection("schedules");

        scheduleRouteTimeQueue = findViewById(R.id.scheduleRouteTimeQueue);
        scheduleRouteTimeDepart = findViewById(R.id.scheduleRouteTimeDepart);
        scheduleRoute = findViewById(R.id.scheduleRoute);
        scheduleRoutePrice = findViewById(R.id.scheduleRoutePrice);
        scheduleRouteACT = findViewById(R.id.scheduleRouteACT);
        btnAddRouteSchedule = findViewById(R.id.btnAddRouteSchedule);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Schedules");
        getSupportActionBar().setSubtitle("Manage trip routes and schedule.");

        initializeTimePickerQueue();
        initializeTimePickerDepart();
        populateRouteList();

        Query query = schedulesReference.whereEqualTo("van_company_uid", getTransportUid())
                .orderBy("route_from", Query.Direction.ASCENDING)
                .orderBy("route_to", Query.Direction.ASCENDING)
                .orderBy("time_depart", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Schedule> options = new FirestoreRecyclerOptions.Builder<Schedule>()
                .setQuery(query, Schedule.class)
                .build();

        adapterScheduleListRV = new AdapterTransportTripScheduleListRV(options, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);

        systemScheduleList = findViewById(R.id.systemScheduleList);

        systemScheduleList.setHasFixedSize(true);
        systemScheduleList.setLayoutManager(manager);
        systemScheduleList.setAdapter(adapterScheduleListRV);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnAddRouteSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableInput();
                checkInput();
            }
        });

        scheduleRoutePrice.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double priceInput = Double.parseDouble(scheduleRoutePrice.getEditText().getText().toString());
                if (priceInput > price) {
                    scheduleRoutePrice.setError("Error: Cannot set price more than LTFRB approved rate. " + "(" + String.format(Locale.ENGLISH, "%.2f", price) + " for " + route_from + " to " + route_to + ")");
                    btnAddRouteSchedule.setEnabled(false);
                } else if (priceInput == 0) {
                    scheduleRoutePrice.setError("Error: Cannot set 0.00 as price.");
                    btnAddRouteSchedule.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private String getTransportUid() {
        return getIntent().getStringExtra("uid");
    }

    private void enableInput() {
        btnAddRouteSchedule.setEnabled(true);
        scheduleRouteTimeQueue.setEnabled(true);
        scheduleRouteTimeDepart.setEnabled(true);
        scheduleRoute.setEnabled(true);
        scheduleRoutePrice.setEnabled(true);
    }

    private void disableInput() {
        btnAddRouteSchedule.setEnabled(false);
        scheduleRouteTimeQueue.setEnabled(false);
        scheduleRouteTimeDepart.setEnabled(false);
        scheduleRoute.setEnabled(false);
        scheduleRoutePrice.setEnabled(false);
    }

    private void checkInput() {
        if (scheduleRouteTimeQueue.getEditText().getText().toString().isEmpty()) {
            enableInput();
            scheduleRouteTimeQueue.setError("Please enter a queue time.");
        } else if (scheduleRouteTimeDepart.getEditText().getText().toString().isEmpty()) {
            enableInput();
            scheduleRouteTimeDepart.setError("Please enter a queue time.");
        } else if (scheduleRoute.getEditText().getText().toString().isEmpty()) {
            enableInput();
            scheduleRoute.setError("Please enter trip route.");
        } else {
            addTripSchedule(scheduleRouteTimeQueue.getEditText().getText().toString(), scheduleRouteTimeDepart.getEditText().getText().toString(), getTransportUid(), Double.parseDouble(scheduleRoutePrice.getEditText().getText().toString()));
        }
    }

    private void addTripSchedule(String time_queue, String time_depart, String transport_uid, double price) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(getResources().getColor(R.color.white))
                .text("Processing...")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        Schedule schedule = new Schedule(time_queue, time_depart, transport_uid, route_from, route_to, price);
        schedulesReference.add(schedule)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            enableInput();
                            Toast.makeText(TransportAdminSchedulesActivity.this, "Success.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initializeTimePickerDepart() {
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                calendar.set(Calendar.HOUR_OF_DAY, i);
                calendar.set(Calendar.MINUTE, i1);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                scheduleRouteTimeQueue.getEditText().setText(simpleDateFormat.format(calendar.getTime()));
            }
        };
        scheduleRouteTimeQueue.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(TransportAdminSchedulesActivity.this, time,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), false)
                        .show();
            }
        });
    }

    private void initializeTimePickerQueue() {
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                calendar.set(Calendar.HOUR_OF_DAY, i);
                calendar.set(Calendar.MINUTE, i1);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                scheduleRouteTimeDepart.getEditText().setText(simpleDateFormat.format(calendar.getTime()));
            }
        };
        scheduleRouteTimeDepart.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(TransportAdminSchedulesActivity.this, time,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), false)
                        .show();
            }
        });
    }

    private void populateRouteList() {
        routeArray = new ArrayList<>();
        systemSchedulesReference.orderBy("route_from", Query.Direction.ASCENDING)
                .orderBy("route_to", Query.Direction.ASCENDING)
                .orderBy("time_depart", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                Schedule schedule = new Schedule(task.getResult().getDocuments().get(i).getString("route_from"), task.getResult().getDocuments().get(i).getString("route_to"), task.getResult().getDocuments().get(i).getLong("price").doubleValue());
                                routeArray.add(i, schedule);
                            }
                            adapterDropdownTripSchedule = new AdapterTransportDropdownTripSchedule(TransportAdminSchedulesActivity.this, routeArray);
                            scheduleRouteACT.setAdapter(adapterDropdownTripSchedule);
                            scheduleRouteACT.setThreshold(1);
                            scheduleRouteACT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Schedule selectedSchedule = (Schedule) parent.getItemAtPosition(position);
                                    route_from = selectedSchedule.getRoute_from();
                                    route_to = selectedSchedule.getRoute_to();
                                    price = selectedSchedule.getPrice();
                                    scheduleRoutePrice.getEditText().setText(String.format(Locale.ENGLISH, "%.2f", selectedSchedule.getPrice()));
                                }
                            });
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterScheduleListRV.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterScheduleListRV.stopListening();
    }
}