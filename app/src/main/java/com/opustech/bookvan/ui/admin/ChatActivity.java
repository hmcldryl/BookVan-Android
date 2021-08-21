package com.opustech.bookvan.ui.admin;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.ChatMessage;
import com.opustech.bookvan.adapters.chat.AdapterMessageChatAdminRV;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;
    private CollectionReference conversationsReference;

    private TextView chatStatusNone;
    private RecyclerView chatMessageList;

    private TextInputLayout inputChat;
    private ImageButton btnSendChat;

    private AdapterMessageChatAdminRV adapterMessageChatAdminRV;

    private final String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chat);

        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        conversationsReference = firebaseFirestore.collection("conversations");

        String uid = getIntent().getStringExtra("uid");
        String name = getIntent().getStringExtra("name");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Chat with " + name);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        inputChat = findViewById(R.id.inputChat);
        btnSendChat = findViewById(R.id.btnSendChat);

        Query query = conversationsReference.document(uid)
                .collection("chat")
                .orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .build();

        adapterMessageChatAdminRV = new AdapterMessageChatAdminRV(options);
        LinearLayoutManager manager = new LinearLayoutManager(ChatActivity.this);
        manager.setStackFromEnd(true);

        chatStatusNone = findViewById(R.id.chatStatusNone);
        chatMessageList = findViewById(R.id.chatMessageList);
        chatMessageList.setHasFixedSize(true);
        chatMessageList.setLayoutManager(manager);
        chatMessageList.setAdapter(adapterMessageChatAdminRV);

        adapterMessageChatAdminRV.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                chatMessageList.smoothScrollToPosition(adapterMessageChatAdminRV.getItemCount());
            }
        });

        conversationsReference.document(uid)
                .collection("chat")
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
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
                inputChat.getEditText().setText("");
                if (!message.isEmpty()) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    String timestamp = format.format(Calendar.getInstance().getTime());
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("uid", admin_uid);
                    hashMap.put("message", message);
                    hashMap.put("timestamp", timestamp);
                    conversationsReference.document(uid)
                            .collection("chat")
                            .add(hashMap)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("uid", uid);
                                        hashMap.put("timestamp", timestamp);
                                        conversationsReference.document(uid)
                                                .set(hashMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            chatMessageList.smoothScrollToPosition(adapterMessageChatAdminRV.getItemCount() - 1);
                                                            btnSendChat.setEnabled(true);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                } else {
                    btnSendChat.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterMessageChatAdminRV.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterMessageChatAdminRV.stopListening();
    }
}