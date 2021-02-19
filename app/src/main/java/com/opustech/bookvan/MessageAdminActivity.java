package com.opustech.bookvan;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.model.ChatConversation;
import com.opustech.bookvan.ui.adapters.AdapterMessageListRV;

public class MessageAdminActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference conversationsReference;

    private TextView chatStatusNone;
    private RecyclerView chatMessageList;

    private AdapterMessageListRV adapterMessageListRV;

    private String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_admin);

        firebaseFirestore = FirebaseFirestore.getInstance();
        conversationsReference = firebaseFirestore.collection("conversations");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setTitle("Chat");
        toolbar.setSubtitle("Select a conversation to start.");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Query query = conversationsReference
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatConversation> options = new FirestoreRecyclerOptions.Builder<ChatConversation>()
                .setQuery(query, ChatConversation.class)
                .build();

        adapterMessageListRV = new AdapterMessageListRV(options);
        LinearLayoutManager manager = new LinearLayoutManager(MessageAdminActivity.this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(MessageAdminActivity.this, manager.getOrientation());

        chatStatusNone = findViewById(R.id.chatStatusNone);
        chatMessageList = findViewById(R.id.chatMessageList);

        chatMessageList.setHasFixedSize(true);
        chatMessageList.setLayoutManager(manager);
        chatMessageList.addItemDecoration(dividerItemDecoration);
        chatMessageList.setAdapter(adapterMessageListRV);

        conversationsReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            int size = value.size();
                                if (size > 0) {
                                    chatMessageList.setVisibility(View.VISIBLE);
                                    chatStatusNone.setVisibility(View.GONE);
                                } else {
                                    chatStatusNone.setVisibility(View.VISIBLE);
                                    chatMessageList.setVisibility(View.GONE);
                                }
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterMessageListRV.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterMessageListRV.stopListening();
    }

}