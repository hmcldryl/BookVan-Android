package com.opustech.bookvan.ui.transport;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.opustech.bookvan.R;
import com.opustech.bookvan.adapters.transport.AdapterTransportRentChatConversationRV;
import com.opustech.bookvan.adapters.user.AdapterRentChatConversationRV;
import com.opustech.bookvan.model.Rental;

public class TransportRentConversationActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, rentalsReference, partnersReference;

    private RecyclerView rentChatList;

    private AdapterTransportRentChatConversationRV adapterTransportRentChatConversationRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_rent_conversation);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        rentalsReference = firebaseFirestore.collection("rentals");
        partnersReference = firebaseFirestore.collection("partners");

        rentChatList = findViewById(R.id.rentChatList);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Rent-a-Van");
        getSupportActionBar().setSubtitle("View Your Rent Transactions");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initList();
    }

    private void initList() {
        Query query = rentalsReference.whereEqualTo("transport_uid", getIntent().getStringExtra("uid"))
                .orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Rental> options = new FirestoreRecyclerOptions.Builder<Rental>()
                .setQuery(query, Rental.class)
                .build();

        adapterTransportRentChatConversationRV = new AdapterTransportRentChatConversationRV(options, getIntent().getStringExtra("uid"), this);
        LinearLayoutManager manager = new LinearLayoutManager(TransportRentConversationActivity.this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, manager.getOrientation());

        rentChatList.setHasFixedSize(true);
        rentChatList.setLayoutManager(manager);
        rentChatList.addItemDecoration(dividerItemDecoration);
        rentChatList.setAdapter(adapterTransportRentChatConversationRV);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterTransportRentChatConversationRV.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterTransportRentChatConversationRV.stopListening();
    }
}