package com.opustech.bookvan;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.model.ChatConversation;
import com.opustech.bookvan.model.ChatMessage;

public class MessageAdminActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;

    private TextView chatStatusNone;
    private RecyclerView chatMessageList;

    private TextInputLayout inputChat;
    private ImageButton btnSendChat;

    private AdapterMessageListRV adapterMessageListRV;

    private String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_admin);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setTitle("");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Query query = usersReference.document(admin_uid)
                .collection("conversation")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatConversation> options = new FirestoreRecyclerOptions.Builder<ChatConversation>()
                .setQuery(query, ChatConversation.class)
                .build();

        adapterMessageListRV = new AdapterMessageListRV(options);

        chatStatusNone = findViewById(R.id.chatStatusNone);
        chatMessageList = findViewById(R.id.chatMessageList);
        chatMessageList.setHasFixedSize(true);
        chatMessageList.setLayoutManager(new LinearLayoutManager(MessageAdminActivity.this));
        chatMessageList.setAdapter(adapterMessageListRV);

        usersReference.document(admin_uid)
                .collection("conversations")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        int size = value.size();

                        if (size > 0) {
                            chatMessageList.setVisibility(View.VISIBLE);
                            chatStatusNone.setVisibility(View.GONE);
                        } else {
                            chatStatusNone.setVisibility(View.VISIBLE);
                            chatMessageList.setVisibility(View.GONE);
                        }
                    }
                });

    }
}