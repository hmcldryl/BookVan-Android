package com.opustech.bookvan.ui.transport;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.R;
import com.opustech.bookvan.adapters.transport.AdapterRentChatMessageRV;
import com.opustech.bookvan.model.RentChatMessage;
import com.opustech.bookvan.notification.APIService;
import com.opustech.bookvan.notification.Client;
import com.opustech.bookvan.notification.Data;
import com.opustech.bookvan.notification.NotificationSender;
import com.opustech.bookvan.notification.RequestResponse;
import com.opustech.bookvan.ui.user.UserChatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransportRentMessageActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, rentalsReference, partnersReference;

    private APIService apiService;

    private TextView chatStatusNone;
    private RecyclerView chatMessageList;

    private TextInputLayout inputChat;
    private ImageButton btnSendChat;

    private Chip btnConfirmRent, btnCancelRent;

    private AdapterRentChatMessageRV adapterRentChatMessageRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_rent_message);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        rentalsReference = firebaseFirestore.collection("rentals");
        partnersReference = firebaseFirestore.collection("partners");

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        btnConfirmRent = findViewById(R.id.btnSetPricing);
        btnCancelRent = findViewById(R.id.btnCancelRent);

        inputChat = findViewById(R.id.inputChat);
        btnSendChat = findViewById(R.id.btnSendChat);
        chatMessageList = findViewById(R.id.chatMessageList);
        chatStatusNone = findViewById(R.id.chatStatusNone);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Rent-a-Van Chat");
        getSupportActionBar().setSubtitle("Chat with Customer");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.btnSetPricing) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(TransportRentMessageActivity.this);
                    final AlertDialog alertDialog = builder.create();
                    if (!alertDialog.isShowing()) {
                        final View dialogView = LayoutInflater.from(TransportRentMessageActivity.this).inflate(R.layout.dialog_confirm_rent, null);
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.setCancelable(true);
                        alertDialog.setView(dialogView);
                        TextView rentReferenceNo = dialogView.findViewById(R.id.rentalReferenceNumber);
                        TextInputLayout inputPrice = dialogView.findViewById(R.id.inputPrice);

                        rentReferenceNo.setText(getIntent().getStringExtra("referenceId"));

                        MaterialButton btnConfirm = dialogView.findViewById(R.id.btnConfirm);

                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final ACProgressFlower dialog = new ACProgressFlower.Builder(TransportRentMessageActivity.this)
                                        .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                                        .themeColor(TransportRentMessageActivity.this.getResources().getColor(R.color.white))
                                        .text("Processing...")
                                        .fadeColor(Color.DKGRAY).build();
                                dialog.show();

                                inputPrice.setEnabled(false);
                                btnConfirm.setEnabled(false);

                                if (!inputPrice.getEditText().getText().toString().isEmpty()) {
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                                    String timestamp = format.format(Calendar.getInstance().getTime());
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("uid", getIntent().getStringExtra("transportId"));
                                    hashMap.put("message", inputPrice.getEditText().getText().toString());
                                    hashMap.put("type", "system_message");
                                    hashMap.put("timestamp", timestamp);

                                    rentalsReference.document(getIntent().getStringExtra("rentalId"))
                                            .collection("chat")
                                            .add(hashMap)
                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()) {
                                                        alertDialog.dismiss();
                                                        dialog.dismiss();
                                                        Toast.makeText(TransportRentMessageActivity.this, "Success.", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        dialog.dismiss();
                                                        inputPrice.setEnabled(true);
                                                        btnConfirm.setEnabled(true);
                                                        Toast.makeText(TransportRentMessageActivity.this, "Failed.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    dialog.dismiss();
                                    inputPrice.setEnabled(true);
                                    btnConfirm.setEnabled(true);
                                    inputPrice.setError("Please enter rental fee.");
                                }
                            }
                        });
                        alertDialog.show();
                    }
                } else if (item.getItemId() == R.id.btnCancelRent) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(TransportRentMessageActivity.this);
                    final AlertDialog alertDialog = builder.create();
                    if (!alertDialog.isShowing()) {
                        final View dialogView = LayoutInflater.from(TransportRentMessageActivity.this).inflate(R.layout.dialog_cancel_rent, null);
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.setCancelable(true);
                        alertDialog.setView(dialogView);
                        TextView rentReferenceNo = dialogView.findViewById(R.id.rentReferenceNo);

                        rentReferenceNo.setText(getIntent().getStringExtra("referenceId"));

                        MaterialButton btnConfirm = dialogView.findViewById(R.id.btnConfirm);

                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final ACProgressFlower dialog = new ACProgressFlower.Builder(TransportRentMessageActivity.this)
                                        .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                                        .themeColor(TransportRentMessageActivity.this.getResources().getColor(R.color.white))
                                        .text("Processing...")
                                        .fadeColor(Color.DKGRAY).build();
                                dialog.show();

                                btnConfirm.setEnabled(false);

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("status", "cancelled");
                                rentalsReference.document(getIntent().getStringExtra("rentalId"))
                                        .update(hashMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    alertDialog.dismiss();
                                                    dialog.dismiss();
                                                    Toast.makeText(TransportRentMessageActivity.this, "Success.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    dialog.dismiss();
                                                    Toast.makeText(TransportRentMessageActivity.this, "Failed.", Toast.LENGTH_SHORT).show();
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

        if (getIntent().getStringExtra("status").equals("done")) {
            inputChat.setEnabled(false);
            btnSendChat.setEnabled(false);
        }

        initList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_rent_message, menu);
        return true;
    }

    private void initList() {
        Query query = rentalsReference.document(getIntent().getStringExtra("rentalId"))
                .collection("chat")
                .orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<RentChatMessage> options = new FirestoreRecyclerOptions.Builder<RentChatMessage>()
                .setQuery(query, RentChatMessage.class)
                .build();

        adapterRentChatMessageRV = new AdapterRentChatMessageRV(options, getIntent().getStringExtra("transportId"));
        LinearLayoutManager manager = new LinearLayoutManager(TransportRentMessageActivity.this);
        manager.setStackFromEnd(true);

        chatMessageList.setHasFixedSize(true);
        chatMessageList.setLayoutManager(manager);
        chatMessageList.setAdapter(adapterRentChatMessageRV);

        adapterRentChatMessageRV.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                chatMessageList.smoothScrollToPosition(adapterRentChatMessageRV.getItemCount());
            }
        });

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
                    hashMap.put("uid", getIntent().getStringExtra("transportId"));
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
                                        fetchToken(getIntent().getStringExtra("userId"), getIntent().getStringExtra("name"), message);
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
    }

    private void fetchToken(String uid, String name, String message) {
        FirebaseFirestore.getInstance().collection("tokens")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            sendNotification(task.getResult().getString("token"), "New Message from " + name, name + ": \"" + message + "\".");
                        }
                    }
                });
    }

    private void sendNotification(String token, String title, String message) {
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, token);
        apiService.sendNotification(sender).enqueue(new Callback<RequestResponse>() {
            @Override
            public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(TransportRentMessageActivity.this, "Request failed.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RequestResponse> call, Throwable t) {

            }
        });
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