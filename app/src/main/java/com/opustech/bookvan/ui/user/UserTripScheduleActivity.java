package com.opustech.bookvan.ui.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.opustech.bookvan.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.opustech.bookvan.adapters.user.AdapterTripScheduleListRV;
import com.opustech.bookvan.model.Schedule;
import com.opustech.bookvan.model.TripSchedule;

public class UserTripScheduleActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference schedulesReference;

    private ImageView previewImage;
    private RecyclerView scheduleList;

    private AdapterTripScheduleListRV adapterTripScheduleListRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_trip_schedule);

        firebaseFirestore = FirebaseFirestore.getInstance();
        schedulesReference = firebaseFirestore.collection("schedules");

        Toolbar toolbar = findViewById(R.id.toolbar);
        previewImage = findViewById(R.id.previewImage);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(capitalizeWords(getIntent().getStringExtra("destination")));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        String image_url = getIntent().getStringExtra("image_url");
        if (image_url != null) {
            if (!image_url.isEmpty()) {
                Glide.with(this)
                        .load(image_url)
                        .into(previewImage);
            }
        }

        String destination = capitalizeWords(getIntent().getStringExtra("destination"));

        Query query = firebaseFirestore.collection("schedules")
                .whereEqualTo("route_to", destination)
                .collectionGroup("schedules")
                .orderBy("time_queue", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<TripSchedule> options = new FirestoreRecyclerOptions.Builder<TripSchedule>()
                .setQuery(query, TripSchedule.class)
                .build();

        adapterTripScheduleListRV = new AdapterTripScheduleListRV(options);
        LinearLayoutManager manager = new LinearLayoutManager(this);

        scheduleList = findViewById(R.id.scheduleList);

        scheduleList.setHasFixedSize(true);
        scheduleList.setLayoutManager(manager);
        scheduleList.setAdapter(adapterTripScheduleListRV);
    }

    public static String capitalizeWords(String str) {
        String[] words = str.split("\\s");
        String result = "";
        for (String w : words) {
            String a = w.substring(0, 1);
            String b = w.substring(1);
            result += a.toUpperCase() + b + " ";
        }
        return result.trim();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterTripScheduleListRV.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterTripScheduleListRV.stopListening();
    }
}