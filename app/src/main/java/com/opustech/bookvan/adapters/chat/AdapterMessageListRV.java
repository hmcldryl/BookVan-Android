package com.opustech.bookvan.adapters.chat;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.ui.admin.ChatAdminActivity;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.ChatConversation;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterMessageListRV extends FirestoreRecyclerAdapter<ChatConversation, AdapterMessageListRV.ChatConversationHolder> {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;
    private CollectionReference conversationsReference;

    private final String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterMessageListRV(@NonNull FirestoreRecyclerOptions<ChatConversation> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatConversationHolder holder, int position, @NonNull ChatConversation model) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        conversationsReference = firebaseFirestore.collection("conversations");

        String uid = model.getUid();

        if (uid != null) {
            usersReference.document(uid)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        String name = task.getResult().getString("name");
                        String photo_url = task.getResult().getString("photo_url");
                        String email = task.getResult().getString("email");
                        String contact_number = task.getResult().getString("contact_number");

                        holder.customerName.setText(name);
                        if (photo_url != null) {
                            if (!photo_url.isEmpty()) {
                                Glide.with(holder.itemView.getContext())
                                        .load(photo_url)
                                        .into(holder.customerPhoto);
                            }
                        }
                        holder.customerEmail.setText(email);
                        holder.customerContactNumber.setText(contact_number);
                        holder.customerConversation.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(view.getContext(), ChatAdminActivity.class);
                                intent.putExtra("uid", uid);
                                intent.putExtra("name", name);
                                intent.putExtra("email", email);
                                intent.putExtra("contact_number", contact_number);
                                view.getContext().startActivity(intent);
                            }
                        });

                        Query query = conversationsReference.document(uid)
                                .collection("chat")
                                .orderBy("timestamp", Query.Direction.DESCENDING)
                                .limit(1);
                        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (value != null) {
                                    List<DocumentSnapshot> documentSnapshotList = value.getDocuments();
                                    if (documentSnapshotList.size() != 0) {
                                        String message = documentSnapshotList.get(0).getString("message");
                                        String timestamp = documentSnapshotList.get(0).getString("timestamp");
                                        try {
                                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                                            String outputText = new PrettyTime().format(simpleDateFormat.parse(timestamp));
                                            holder.customerLastMessage.setText(message);
                                            holder.customerLastMessageTimestamp.setText(outputText);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        holder.lastMessage.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.lastMessage.setVisibility(View.GONE);
                                    }
                                }
                            }
                        });

                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ChatConversationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_conversation_item_layout, parent, false);
        return new ChatConversationHolder(view);
    }


    class ChatConversationHolder extends RecyclerView.ViewHolder {
        LinearLayout customerConversation, lastMessage;
        CircleImageView customerPhoto;
        TextView customerName, customerEmail, customerContactNumber, customerLastMessage, customerLastMessageTimestamp;

        public ChatConversationHolder(View view) {
            super(view);
            customerConversation = view.findViewById(R.id.customerConversation);
            customerPhoto = view.findViewById(R.id.customerPhoto);
            customerName = view.findViewById(R.id.customerName);
            customerEmail = view.findViewById(R.id.customerEmail);
            customerContactNumber = view.findViewById(R.id.customerContactNumber);
            lastMessage = view.findViewById(R.id.lastMessage);
            customerLastMessage = view.findViewById(R.id.customerLastMessage);
            customerLastMessageTimestamp = view.findViewById(R.id.customerLastMessageTimestamp);
        }
    }

}
