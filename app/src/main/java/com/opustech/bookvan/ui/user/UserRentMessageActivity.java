package com.opustech.bookvan.ui.user;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.R;
import com.opustech.bookvan.adapters.user.AdapterRentChatMessageRV;
import com.opustech.bookvan.model.RentChatMessage;
import com.opustech.bookvan.ui.transport.TransportRentMessageActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class UserRentMessageActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, rentalsReference, partnersReference;

    private TextView chatStatusNone;
    private RecyclerView chatMessageList;

    private TextInputLayout inputChat;
    private ImageButton btnSendChat;

    private AdapterRentChatMessageRV adapterRentChatMessageRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_rent_message);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        rentalsReference = firebaseFirestore.collection("rentals");
        partnersReference = firebaseFirestore.collection("partners");

        inputChat = findViewById(R.id.inputChat);
        btnSendChat = findViewById(R.id.btnSendChat);
        chatMessageList = findViewById(R.id.chatMessageList);
        chatStatusNone = findViewById(R.id.chatStatusNone);

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

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.btnSetStatus) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(UserRentMessageActivity.this);
                    final AlertDialog alertDialog = builder.create();
                    if (!alertDialog.isShowing()) {
                        final View dialogView = LayoutInflater.from(UserRentMessageActivity.this).inflate(R.layout.dialog_set_rent_status, null);
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.setCancelable(true);
                        alertDialog.setView(dialogView);
                        TextView rentReferenceNo = dialogView.findViewById(R.id.rentReferenceNo);

                        rentReferenceNo.setText(getIntent().getStringExtra("referenceId"));

                        MaterialButton btnConfirm = dialogView.findViewById(R.id.btnConfirm);

                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final ACProgressFlower dialog = new ACProgressFlower.Builder(UserRentMessageActivity.this)
                                        .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                                        .themeColor(UserRentMessageActivity.this.getResources().getColor(R.color.white))
                                        .text("Processing...")
                                        .fadeColor(Color.DKGRAY).build();
                                dialog.show();

                                btnConfirm.setEnabled(false);

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("status", "done");
                                rentalsReference.document(getIntent().getStringExtra("rentalId"))
                                        .update(hashMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    alertDialog.dismiss();
                                                    dialog.dismiss();
                                                    Toast.makeText(UserRentMessageActivity.this, "Success.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    dialog.dismiss();
                                                    Toast.makeText(UserRentMessageActivity.this, "Failed.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });
                        alertDialog.show();
                    }
                }
                return false;
            }
        });

        initList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_rent_message_user, menu);
        return true;
    }

    private void initList() {
        Query query = rentalsReference.document(getIntent().getStringExtra("rentalId"))
                .collection("chat")
                .orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<RentChatMessage> options = new FirestoreRecyclerOptions.Builder<RentChatMessage>()
                .setQuery(query, RentChatMessage.class)
                .build();

        adapterRentChatMessageRV = new AdapterRentChatMessageRV(options, firebaseAuth.getCurrentUser().getUid());
        LinearLayoutManager manager = new LinearLayoutManager(UserRentMessageActivity.this);
        manager.setStackFromEnd(true);

        chatMessageList.setHasFixedSize(true);
        chatMessageList.setLayoutManager(manager);
        chatMessageList.setAdapter(adapterRentChatMessageRV);

        rentalsReference.document(getIntent().getStringExtra("rentalId"))
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
                    hashMap.put("uid", firebaseAuth.getCurrentUser().getUid());
                    hashMap.put("message", message);
                    hashMap.put("type", "user_message");
                    hashMap.put("timestamp", timestamp);
                    rentalsReference.document(getIntent().getStringExtra("rentalId"))
                            .collection("chat")
                            .add(hashMap)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("timestamp", timestamp);
                                        rentalsReference.document(getIntent().getStringExtra("rentalId"))
                                                .update(hashMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            if (adapterRentChatMessageRV.getItemCount() == 0) {
                                                                chatMessageList.smoothScrollToPosition(adapterRentChatMessageRV.getItemCount() - 1);
                                                            }
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

        if (getIntent().getStringExtra("status").equals("done")) {
            inputChat.setEnabled(false);
            btnSendChat.setEnabled(false);
        }
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