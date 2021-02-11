package com.opustech.bookvan;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.model.ChatMessage;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterMessageChatCustomerRV extends FirestoreRecyclerAdapter<ChatMessage, AdapterMessageChatCustomerRV.ChatMessageHolder> {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterMessageChatCustomerRV(@NonNull FirestoreRecyclerOptions<ChatMessage> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatMessageHolder holder, int position, @NonNull ChatMessage model) {
        String currentUserId = firebaseAuth.getCurrentUser().getUid();

        String name = model.getName();
        String photo_url = model.getPhoto_url();
        String message = model.getMessage();
        String timestamp = model.getTimestamp();
        String uid = model.getUid();

        if (uid.equals(currentUserId)) {
            holder.sender.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(photo_url)
                    .into(holder.senderPhoto);
            holder.senderChatMessage.setText(message);
            holder.senderChatTimestamp.setText(timestamp);
        }
        else {
            holder.receiver.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(photo_url)
                    .into(holder.receiverPhoto);
            holder.receiverChatMessage.setText(message);
            holder.receiverChatTimestamp.setText(timestamp);
        }

    }

    @NonNull
    @Override
    public ChatMessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_item_layout, parent, false);
        return new ChatMessageHolder(view);
    }

    public void deleteBooking(int position, Context context) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(context)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(context.getResources().getColor(R.color.white))
                .text("Processing...")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        getSnapshots().getSnapshot(position).getReference().delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(context, "Delete booking failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class ChatMessageHolder extends RecyclerView.ViewHolder {
        RelativeLayout sender, receiver;
        CircleImageView senderPhoto, receiverPhoto;
        TextView senderChatMessage, receiverChatMessage, senderChatTimestamp, receiverChatTimestamp;

        public ChatMessageHolder(View view) {
            super(view);
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
