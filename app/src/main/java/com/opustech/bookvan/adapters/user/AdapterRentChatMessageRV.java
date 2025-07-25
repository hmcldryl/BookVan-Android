package com.opustech.bookvan.adapters.user;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.RentChatMessage;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRentChatMessageRV extends FirestoreRecyclerAdapter<RentChatMessage, AdapterRentChatMessageRV.ChatMessageHolder> {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, partnersReference;

    private final String uid, rental_id;
    String status = "";

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterRentChatMessageRV(@NonNull FirestoreRecyclerOptions<RentChatMessage> options, String uid, String rental_id, String status) {
        super(options);
        this.uid = uid;
        this.rental_id = rental_id;
        this.status = status;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatMessageHolder holder, int position, @NonNull RentChatMessage model) {
        holder.setIsRecyclable(false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        partnersReference = firebaseFirestore.collection("partners");

        String messageUid = model.getUid();
        String message = model.getMessage();
        String type = model.getType();
        String timestamp = model.getTimestamp();

        if (type.equals("user_message")) {
            if (messageUid.equals(uid)) {
                holder.sender.setVisibility(View.VISIBLE);
                holder.senderChatMessage.setText(message);
                usersReference.document(messageUid)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String photo_url = task.getResult().getString("photo_url");
                            if (photo_url != null) {
                                if (!photo_url.isEmpty()) {
                                    Glide.with(holder.itemView.getContext().getApplicationContext())
                                            .load(photo_url)
                                            .into(holder.senderPhoto);
                                }
                            }
                        }
                    }
                });
            } else {
                holder.receiver.setVisibility(View.VISIBLE);
                holder.receiverChatMessage.setText(message);
                partnersReference.document(messageUid)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String photo_url = task.getResult().getString("photo_url");
                            if (photo_url != null) {
                                if (!photo_url.isEmpty()) {
                                    Glide.with(holder.itemView.getContext().getApplicationContext())
                                            .load(photo_url)
                                            .into(holder.receiverPhoto);
                                }
                            }
                        }
                    }
                });
            }

            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                String outputText = new PrettyTime().format(simpleDateFormat.parse(timestamp));
                if (messageUid.equals(uid)) {
                    holder.senderChatTimestamp.setText(outputText);
                } else {
                    holder.receiverChatTimestamp.setText(outputText);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else if (type.equals("system_message")) {
            if (status.equals("pending")) {
                holder.systemMessageItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        final AlertDialog alertDialog = builder.create();
                        if (!alertDialog.isShowing()) {
                            final View dialogView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.dialog_confirm_rent_user, null);
                            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            alertDialog.setCancelable(true);
                            alertDialog.setView(dialogView);

                            MaterialButton btnConfirm = dialogView.findViewById(R.id.btnConfirm);

                            btnConfirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final ACProgressFlower dialog = new ACProgressFlower.Builder(holder.itemView.getContext())
                                            .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                                            .themeColor(holder.itemView.getContext().getApplicationContext().getResources().getColor(R.color.white))
                                            .text("Processing...")
                                            .fadeColor(Color.DKGRAY).build();
                                    dialog.show();

                                    btnConfirm.setEnabled(false);

                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                                    String timestamp = format.format(Calendar.getInstance().getTime());
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("status", "confirmed");
                                    hashMap.put("timestamp", timestamp);

                                    firebaseFirestore.collection("rentals")
                                            .document(rental_id)
                                            .update(hashMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        alertDialog.dismiss();
                                                        dialog.dismiss();
                                                        Toast.makeText(holder.itemView.getContext(), "Success.", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        dialog.dismiss();
                                                        btnConfirm.setEnabled(true);
                                                        Toast.makeText(holder.itemView.getContext(), "Failed.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            });
                            alertDialog.show();
                        }
                    }
                });
            }

            partnersReference.document(messageUid)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        String name = task.getResult().getString("name");
                        if (name != null) {
                            if (!name.isEmpty()) {
                                //String displayMessage = name + " set the rent fee to " + holder.itemView.getContext().getApplicationContext().getString(R.string.peso_sign) + String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(message)) + ".";
                                holder.systemMessage.setText(message);
                            }
                        }
                    }
                }
            });

            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                String outputText = new PrettyTime().format(simpleDateFormat.parse(timestamp));
                holder.systemMessageTimestamp.setText(outputText);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.systemMessageItem.setVisibility(View.VISIBLE);
        }


    }

    @NonNull
    @Override
    public ChatMessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rent_chat_message_item_layout, parent, false);
        return new ChatMessageHolder(view);
    }

    static class ChatMessageHolder extends RecyclerView.ViewHolder {
        final LinearLayout systemMessageItem;
        final RelativeLayout sender;
        final RelativeLayout receiver;
        final CircleImageView senderPhoto;
        final CircleImageView receiverPhoto;
        final TextView senderChatMessage;
        final TextView receiverChatMessage;
        final TextView senderChatTimestamp;
        final TextView receiverChatTimestamp;
        final TextView systemMessage;
        final TextView systemMessageTimestamp;

        public ChatMessageHolder(View view) {
            super(view);

            systemMessageItem = view.findViewById(R.id.systemMessageItem);
            systemMessage = view.findViewById(R.id.systemMessage);
            systemMessageTimestamp = view.findViewById(R.id.systemMessageTimestamp);

            receiver = view.findViewById(R.id.chatReceiverItem);
            receiverPhoto = view.findViewById(R.id.receiverPhoto);
            receiverChatMessage = view.findViewById(R.id.receiverChatMessage);
            receiverChatTimestamp = view.findViewById(R.id.receiverChatTimestamp);
            sender = view.findViewById(R.id.chatSenderItem);
            senderPhoto = view.findViewById(R.id.senderPhoto);
            senderChatMessage = view.findViewById(R.id.senderChatMessage);
            senderChatTimestamp = view.findViewById(R.id.senderChatTimestamp);
        }
    }

}
