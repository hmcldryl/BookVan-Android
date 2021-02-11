package com.opustech.bookvan;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.model.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;

    private TextView chatStatusNone;
    private RecyclerView chatMessageList;

    private TextInputLayout inputChat;
    private ImageButton btnSendChat;

    private AdapterMessageChatCustomerRV adapterMessageChatCustomerRV;

    private String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";
    private String name;
    private String photo_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");

        String currentUserId = firebaseAuth.getCurrentUser().getUid();

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

        name = getIntent().getStringExtra("name");
        photo_url = getIntent().getStringExtra("photo_url");

        inputChat = findViewById(R.id.inputChat);
        btnSendChat = findViewById(R.id.btnSendChat);

        Query query = usersReference.document(admin_uid)
                .collection("conversations")
                .document(currentUserId)
                .collection("chat");

        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .build();

        adapterMessageChatCustomerRV = new AdapterMessageChatCustomerRV(options);

        chatStatusNone = findViewById(R.id.chatStatusNone);
        chatMessageList = findViewById(R.id.chatMessageList);
        chatMessageList.setHasFixedSize(true);
        chatMessageList.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        chatMessageList.setAdapter(adapterMessageChatCustomerRV);

        usersReference.document(currentUserId)
                .collection("chat")
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

        btnSendChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSendChat.setEnabled(false);
                String message = inputChat.getEditText().getText().toString();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.ENGLISH);
                String timestamp = simpleDateFormat.format(Calendar.getInstance().getTime());
                if (!message.isEmpty()) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("name", name);
                    hashMap.put("uid", currentUserId);
                    hashMap.put("photo_url", photo_url);
                    hashMap.put("message", message);
                    hashMap.put("timestamp", timestamp);
                    usersReference.document(admin_uid)
                            .collection("conversations")
                            .document(currentUserId)
                            .collection("chat")
                            .add(hashMap)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        btnSendChat.setEnabled(true);
                                    }
                                }
                            });
                }
            }
        });

    }
}