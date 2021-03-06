package com.opustech.bookvan.ui.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.opustech.bookvan.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.opustech.bookvan.adapters.user.AdapterScheduleListRV;
import com.opustech.bookvan.model.Schedule;

public class ScheduleActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference scheduleReference;

    private RecyclerView scheduleList;

    private AdapterScheduleListRV adapterScheduleListRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        firebaseFirestore = FirebaseFirestore.getInstance();
        scheduleReference = firebaseFirestore.collection("schedules");

        Query query = scheduleReference.whereEqualTo("destination", getIntent().getStringExtra("destination"))
                .orderBy("time_pila", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Schedule> options = new FirestoreRecyclerOptions.Builder<Schedule>()
                .setQuery(query, Schedule.class)
                .build();

        adapterScheduleListRV = new AdapterScheduleListRV(options);
        LinearLayoutManager manager = new LinearLayoutManager(this);

        scheduleList = findViewById(R.id.scheduleList);

        scheduleList.setHasFixedSize(true);
        scheduleList.setLayoutManager(manager);
        scheduleList.setAdapter(adapterScheduleListRV);
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