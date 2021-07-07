package com.opustech.bookvan.ui.user;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.opustech.bookvan.R;
import com.opustech.bookvan.adapters.user.AdapterRentChatMessageRV;
import com.opustech.bookvan.model.RentChatMessage;

public class UserRentMessageActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, rentalsReference, partnersReference;

    private RecyclerView rentChatList;

    private AdapterRentChatMessageRV adapterRentChatMessageRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_rent_conversation);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        rentalsReference = firebaseFirestore.collection("rentals");
        partnersReference = firebaseFirestore.collection("partners");

        rentChatList = findViewById(R.id.rentChatList);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Rent-a-Van Chat");
        getSupportActionBar().setSubtitle("Chat with Van Transport Provider");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initList();
    }

    private void initList() {
        Query query = rentalsReference.document(firebaseAuth.getCurrentUser().getUid())
                .collection("rentals")
                .orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<RentChatMessage> options = new FirestoreRecyclerOptions.Builder<RentChatMessage>()
                .setQuery(query, RentChatMessage.class)
                .build();

        adapterRentChatMessageRV = new AdapterRentChatMessageRV(options, firebaseAuth.getCurrentUser().getUid());
        LinearLayoutManager manager = new LinearLayoutManager(UserRentMessageActivity.this);
        manager.setStackFromEnd(true);

        rentChatList.setHasFixedSize(true);
        rentChatList.setLayoutManager(manager);
        rentChatList.setAdapter(adapterRentChatMessageRV);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterRentChatMessageRV.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterRentChatMessageRV.stopListening();
    }
}