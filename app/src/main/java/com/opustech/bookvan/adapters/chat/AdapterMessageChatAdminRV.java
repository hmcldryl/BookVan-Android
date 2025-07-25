package com.opustech.bookvan.adapters.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.ChatMessage;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterMessageChatAdminRV extends FirestoreRecyclerAdapter<ChatMessage, AdapterMessageChatAdminRV.ChatMessageHolder> {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;

    private final String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterMessageChatAdminRV(@NonNull FirestoreRecyclerOptions<ChatMessage> options) {
        super(options);
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
    protected void onBindViewHolder(@NonNull ChatMessageHolder holder, int position, @NonNull ChatMessage model) {
        holder.setIsRecyclable(false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");

        String uid = model.getUid();
        String message = model.getMessage();
        String timestamp = model.getTimestamp();

        if (uid.equals(admin_uid)) {
            holder.sender.setVisibility(View.VISIBLE);
            holder.senderChatMessage.setText(message);
        } else {
            holder.receiver.setVisibility(View.VISIBLE);
            holder.receiverChatMessage.setText(message);
        }

        usersReference.document(uid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String photo_url = task.getResult().getString("photo_url");
                    if (photo_url != null) {
                        if (!photo_url.isEmpty()) {
                            if (uid.equals(admin_uid)) {
                                Glide.with(holder.itemView.getContext().getApplicationContext())
                                        .load(photo_url)
                                        .into(holder.senderPhoto);
                            } else {
                                Glide.with(holder.itemView.getContext().getApplicationContext())
                                        .load(photo_url)
                                        .into(holder.receiverPhoto);
                            }
                        }
                    }
                }
            }
        });

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            String outputText = new PrettyTime().format(simpleDateFormat.parse(timestamp));
            if (uid.equals(admin_uid)) {
                holder.senderChatTimestamp.setText(outputText);
            } else {
                holder.receiverChatTimestamp.setText(outputText);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public ChatMessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_item_layout, parent, false);
        return new ChatMessageHolder(view);
    }

    static class ChatMessageHolder extends RecyclerView.ViewHolder {
        final RelativeLayout sender;
        final RelativeLayout receiver;
        final CircleImageView senderPhoto;
        final CircleImageView receiverPhoto;
        final TextView senderChatMessage;
        final TextView receiverChatMessage;
        final TextView senderChatTimestamp;
        final TextView receiverChatTimestamp;

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
