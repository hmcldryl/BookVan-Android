package com.opustech.bookvan.ui.user;

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

import com.opustech.bookvan.R;
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
import com.opustech.bookvan.adapters.chat.AdapterMessageChatRV;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class UserChatActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference conversationsReference;

    private TextView chatStatusNone;
    private RecyclerView chatMessageList;

    private TextInputLayout inputChat;
    private ImageButton btnSendChat;

    private AdapterMessageChatRV adapterMessageChatRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        conversationsReference = firebaseFirestore.collection("conversations");

        String currentUserId = firebaseAuth.getCurrentUser().getUid();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("BookVan Tech Support");
        getSupportActionBar().setSubtitle("For comments, suggestions, and concerns.");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        inputChat = findViewById(R.id.inputChat);
        btnSendChat = findViewById(R.id.btnSendChat);

        Query query = conversationsReference.document(currentUserId)
                .collection("chat")
                .orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .build();

        adapterMessageChatRV = new AdapterMessageChatRV(options);
        LinearLayoutManager manager = new LinearLayoutManager(UserChatActivity.this);
        manager.setStackFromEnd(true);

        chatStatusNone = findViewById(R.id.chatStatusNone);
        chatMessageList = findViewById(R.id.chatMessageList);
        chatMessageList.setHasFixedSize(true);
        chatMessageList.setLayoutManager(manager);
        chatMessageList.setAdapter(adapterMessageChatRV);

        conversationsReference.document(currentUserId)
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
                    hashMap.put("uid", currentUserId);
                    hashMap.put("message", message);
                    hashMap.put("timestamp", timestamp);
                    conversationsReference.document(currentUserId)
                            .collection("chat")
                            .add(hashMap)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("uid", currentUserId);
                                        hashMap.put("timestamp", timestamp);
                                        conversationsReference.document(currentUserId)
                                                .set(hashMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            chatMessageList.smoothScrollToPosition(adapterMessageChatRV.getItemCount() - 1);
                                                            btnSendChat.setEnabled(true);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                }
                else {
                    btnSendChat.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterMessageChatRV.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterMessageChatRV.stopListening();
    }
}