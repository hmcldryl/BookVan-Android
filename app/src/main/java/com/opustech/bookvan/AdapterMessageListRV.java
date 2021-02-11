package com.opustech.bookvan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.model.Booking;
import com.opustech.bookvan.model.ChatConversation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterMessageListRV extends FirestoreRecyclerAdapter<ChatConversation, AdapterMessageListRV.ChatConversationHolder> {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference usersReference = firebaseFirestore.collection("users");

    private String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";

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
        String uid = model.getUid();
        String name = model.getName();
        String photo_url = model.getPhoto_url();
        String email = model.getEmail();
        String contact_number = model.getContact_number();

        holder.customerName.setText(name);
        Glide.with(holder.itemView.getContext())
                .load(photo_url)
                .into(holder.customerPhoto);
        holder.customerEmail.setText(email);
        holder.customerContactNumber.setText(contact_number);

        holder.customerConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ChatAdminActivity.class);
                intent.putExtra("uid", uid);
                view.getContext().startActivity(intent);
            }
        });

    }

    @NonNull
    @Override
    public ChatConversationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_item_layout, parent, false);
        return new ChatConversationHolder(view);
    }


    class ChatConversationHolder extends RecyclerView.ViewHolder {
        LinearLayout customerConversation;
        CircleImageView customerPhoto;
        TextView customerName, customerEmail, customerContactNumber;

        public ChatConversationHolder(View view) {
            super(view);
            customerConversation = view.findViewById(R.id.customerConversation);
            customerPhoto = view.findViewById(R.id.customerPhoto);
            customerName = view.findViewById(R.id.customerName);
            customerEmail = view.findViewById(R.id.customerEmail);
            customerContactNumber = view.findViewById(R.id.customerContactNumber);

        }
    }

}
