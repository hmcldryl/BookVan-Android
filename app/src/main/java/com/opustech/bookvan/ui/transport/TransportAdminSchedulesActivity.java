package com.opustech.bookvan.ui.transport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.R;
import com.opustech.bookvan.adapters.user.AdapterDropdownSchedule;
import com.opustech.bookvan.model.Schedule;
import com.opustech.bookvan.ui.user.UserBookActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TransportAdminSchedulesActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference systemSchedulesReference, schedulesReference;

    private TextInputLayout scheduleRouteTimeQueue,
            scheduleRouteTimeDepart,
            scheduleRoute,
            scheduleRoutePrice;
    private AutoCompleteTextView scheduleRouteACT;
    private MaterialButton btnAddRouteSchedule;

    private AdapterDropdownSchedule adapterDropdownSchedule;
    private ArrayList<Schedule> routeArray;

    private String route_from, route_to;
    private double price = 0.00;

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
            addTripSchedule(scheduleRouteTimeQueue.getEditText().getText().toString(), scheduleRouteTimeDepart.getEditText().getText().toString(), "", Double.parseDouble(scheduleRoutePrice.getEditText().getText().toString()));
        }
    }

    private void addTripSchedule(String time_queue, String time_depart, String transport_uid, double price) {
        Schedule schedule = new Schedule(time_queue, time_depart, transport_uid, route_from, route_to, price);
        schedulesReference.add(schedule)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

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
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                Schedule schedule = new Schedule(task.getResult().getDocuments().get(i).getString("time_depart"), task.getResult().getDocuments().get(i).getString("route_from"), task.getResult().getDocuments().get(i).getString("route_to"), task.getResult().getDocuments().get(i).getLong("price").doubleValue());
                                routeArray.add(i, schedule);
                            }
                            adapterDropdownSchedule = new AdapterDropdownSchedule(TransportAdminSchedulesActivity.this, routeArray);
                            scheduleRouteACT.setAdapter(adapterDropdownSchedule);
                            scheduleRouteACT.setThreshold(1);
                            scheduleRouteACT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Schedule selectedSchedule = (Schedule) parent.getItemAtPosition(position);
                                    route_from = selectedSchedule.getRoute_from();
                                    route_to = selectedSchedule.getRoute_to();
                                    scheduleRoutePrice.getEditText().setText(String.format(Locale.ENGLISH, "%.2f", selectedSchedule.getPrice()));
                                    Toast.makeText(TransportAdminSchedulesActivity.this, selectedSchedule.getTime_depart(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
    }

}