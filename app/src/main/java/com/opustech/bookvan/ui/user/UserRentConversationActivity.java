package com.opustech.bookvan.ui.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.opustech.bookvan.R;
import com.opustech.bookvan.adapters.user.AdapterRentChatConversationRV;
import com.opustech.bookvan.model.Rental;

public class UserRentConversationActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, rentalsReference, partnersReference;

    private RecyclerView rentChatList;

    private AdapterRentChatConversationRV adapterRentConversationRV;

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
        Query query = rentalsReference.whereEqualTo("uid", firebaseAuth.getCurrentUser().getUid())
                .orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Rental> options = new FirestoreRecyclerOptions.Builder<Rental>()
                .setQuery(query, Rental.class)
                .build();

        adapterRentConversationRV = new AdapterRentChatConversationRV(options, firebaseAuth.getCurrentUser().getUid(), getIntent().getStringExtra("name"), this);
        LinearLayoutManager manager = new LinearLayoutManager(UserRentConversationActivity.this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, manager.getOrientation());

        rentChatList.setHasFixedSize(true);
        rentChatList.setLayoutManager(manager);
        rentChatList.addItemDecoration(dividerItemDecoration);
        rentChatList.setAdapter(adapterRentConversationRV);

        adapterRentConversationRV.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                rentChatList.smoothScrollToPosition(adapterRentConversationRV.getItemCount());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterRentConversationRV.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterRentConversationRV.stopListening();
    }
}