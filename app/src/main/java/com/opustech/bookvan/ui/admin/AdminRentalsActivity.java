package com.opustech.bookvan.ui.admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.R;
import com.opustech.bookvan.adapters.admin.AdapterRentAdminListRV;
import com.opustech.bookvan.model.Rental;

public class AdminRentalsActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference rentalsReference;

    private TextView rentalsStatusNone;
    private RecyclerView rentalsList;

    private AdapterRentAdminListRV adapterRentAdminListRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_rentals);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        rentalsReference = firebaseFirestore.collection("rentals");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Rentals");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Query query = rentalsReference.orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Rental> options = new FirestoreRecyclerOptions.Builder<Rental>()
                .setQuery(query, Rental.class)
                .build();

        adapterRentAdminListRV = new AdapterRentAdminListRV(options, this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, manager.getOrientation());

        rentalsStatusNone = findViewById(R.id.rentalsStatusNone);
        rentalsList = findViewById(R.id.rentalsList);

        rentalsList.setHasFixedSize(true);
        rentalsList.setLayoutManager(manager);
        rentalsList.addItemDecoration(dividerItemDecoration);
        rentalsList.setAdapter(adapterRentAdminListRV);

        rentalsReference.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    int size = value.size();
                    if (size > 0) {
                        rentalsList.setVisibility(View.VISIBLE);
                        rentalsStatusNone.setVisibility(View.GONE);
                    } else {
                        rentalsStatusNone.setVisibility(View.VISIBLE);
                        rentalsList.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterRentAdminListRV.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterRentAdminListRV.stopListening();
    }
}