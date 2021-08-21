package com.opustech.bookvan.ui.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.opustech.bookvan.R;
import com.opustech.bookvan.adapters.admin.AdapterAdminSystemScheduleListRV;
import com.opustech.bookvan.model.SystemSchedule;

import java.util.ArrayList;
import java.util.Arrays;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class SystemSchedulesActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference systemSchedulesReference;

    private TextInputLayout scheduleRouteFrom,
            scheduleRouteTo,
            scheduleRouteCategory,
            scheduleRoutePrice;
    private AutoCompleteTextView scheduleRouteFromACT, scheduleRouteToACT, scheduleRouteCategoryACT;
    private MaterialButton btnAddRoute;
    private RecyclerView systemScheduleList;

    private final double price = 0.00;

    private AdapterAdminSystemScheduleListRV adapterAdminSystemScheduleListRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_route_schedule);

        firebaseFirestore = FirebaseFirestore.getInstance();
        systemSchedulesReference = firebaseFirestore.collection("system").document("notificationData").collection("schedules");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Schedules");
        getSupportActionBar().setSubtitle("Manage trip routes and pricing.");

        initializeUi();
        populateRouteFromList();
        populateRouteToList();
        populateRouteCategoryList();

        Query query = systemSchedulesReference.orderBy("route_from", Query.Direction.ASCENDING)
                .orderBy("route_to", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<SystemSchedule> options = new FirestoreRecyclerOptions.Builder<SystemSchedule>()
                .setQuery(query, SystemSchedule.class)
                .build();

        adapterAdminSystemScheduleListRV = new AdapterAdminSystemScheduleListRV(options, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);

        systemScheduleList = findViewById(R.id.systemScheduleList);

        systemScheduleList.setHasFixedSize(true);
        systemScheduleList.setLayoutManager(manager);
        systemScheduleList.setAdapter(adapterAdminSystemScheduleListRV);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        btnAddRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableInput();
                checkInput();
            }
        });

    }

    private void enableInput() {
        scheduleRouteFrom.setEnabled(true);
        scheduleRouteTo.setEnabled(true);
        scheduleRouteCategory.setEnabled(true);
        scheduleRoutePrice.setEnabled(true);
        scheduleRouteFromACT.setEnabled(true);
        scheduleRouteToACT.setEnabled(true);
        scheduleRouteCategoryACT.setEnabled(true);
        btnAddRoute.setEnabled(true);
    }

    private void disableInput() {
        scheduleRouteFrom.setEnabled(false);
        scheduleRouteTo.setEnabled(false);
        scheduleRouteCategory.setEnabled(false);
        scheduleRoutePrice.setEnabled(false);
        scheduleRouteFromACT.setEnabled(false);
        scheduleRouteToACT.setEnabled(false);
        scheduleRouteCategoryACT.setEnabled(false);
        btnAddRoute.setEnabled(false);
    }

    private void clearInput() {
        scheduleRouteFrom.getEditText().getText().clear();
        scheduleRouteTo.getEditText().getText().clear();
        scheduleRouteCategory.getEditText().getText().clear();
        scheduleRoutePrice.getEditText().getText().clear();
    }

    private void checkInput() {
        if (scheduleRouteFrom.getEditText().getText().toString().isEmpty()) {
            enableInput();
            scheduleRouteFrom.setError("Please enter route starting point.");
        } else if (scheduleRouteTo.getEditText().getText().toString().isEmpty()) {
            enableInput();
            scheduleRouteTo.setError("Please enter route destination.");
        } else if (scheduleRouteCategory.getEditText().getText().toString().isEmpty()) {
            enableInput();
            scheduleRouteCategory.setError("Please enter route category.");
        } else if (scheduleRoutePrice.getEditText().getText().toString().isEmpty()) {
            enableInput();
            scheduleRoutePrice.setError("Please enter route price.");
        } else {
            addTripRoute(scheduleRouteFrom.getEditText().getText().toString(), scheduleRouteTo.getEditText().getText().toString(), scheduleRouteCategory.getEditText().getText().toString().toLowerCase(), Double.parseDouble(scheduleRoutePrice.getEditText().getText().toString()));
        }
    }

    private void initializeUi() {
        scheduleRouteFrom = findViewById(R.id.scheduleRouteFrom);
        scheduleRouteTo = findViewById(R.id.scheduleRouteTo);
        scheduleRouteCategory = findViewById(R.id.scheduleRouteCategory);
        scheduleRoutePrice = findViewById(R.id.scheduleRoutePrice);
        scheduleRouteFromACT = findViewById(R.id.scheduleRouteFromACT);
        scheduleRouteToACT = findViewById(R.id.scheduleRouteToACT);
        scheduleRouteCategoryACT = findViewById(R.id.scheduleRouteCategoryACT);
        btnAddRoute = findViewById(R.id.btnAddRoute);
        systemScheduleList = findViewById(R.id.systemScheduleList);
    }

    private void addTripRoute(String route_from, String route_to, String category, double price) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(getResources().getColor(R.color.white))
                .text("Processing...")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        SystemSchedule schedule = new SystemSchedule(route_from, route_to, category, price);
        systemSchedulesReference.add(schedule)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            enableInput();
                            clearInput();
                            Toast.makeText(SystemSchedulesActivity.this, "Success.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void populateRouteFromList() {
        ArrayList<String> routeFromArray = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.destinations)));
        ArrayAdapter<String> routeFromArrayAdapter = new ArrayAdapter<>(SystemSchedulesActivity.this, R.layout.support_simple_spinner_dropdown_item, routeFromArray);
        scheduleRouteFromACT.setAdapter(routeFromArrayAdapter);
        scheduleRouteFromACT.setThreshold(1);
    }

    private void populateRouteToList() {
        ArrayList<String> routeToArray = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.destinations)));
        ArrayAdapter<String> routeToArrayAdapter = new ArrayAdapter<>(SystemSchedulesActivity.this, R.layout.support_simple_spinner_dropdown_item, routeToArray);
        scheduleRouteToACT.setAdapter(routeToArrayAdapter);
        scheduleRouteToACT.setThreshold(1);
    }

    private void populateRouteCategoryList() {
        ArrayList<String> routeFromArray = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.category)));
        ArrayAdapter<String> routeFromArrayAdapter = new ArrayAdapter<>(SystemSchedulesActivity.this, R.layout.support_simple_spinner_dropdown_item, routeFromArray);
        scheduleRouteCategoryACT.setAdapter(routeFromArrayAdapter);
        scheduleRouteCategoryACT.setThreshold(1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterAdminSystemScheduleListRV.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterAdminSystemScheduleListRV.stopListening();
    }
}