package com.opustech.bookvan.adapters.user;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.opustech.bookvan.model.Rental;
import com.opustech.bookvan.ui.user.UserRentConversationActivity;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRentChatConversationRV extends FirestoreRecyclerAdapter<Rental, AdapterRentChatConversationRV.ChatMessageHolder> {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference rentalsReference, partnersReference;

    private String uid;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterRentChatConversationRV(@NonNull FirestoreRecyclerOptions<Rental> options, String uid) {
        super(options);
        this.uid = uid;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatMessageHolder holder, int position, @NonNull Rental model) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        rentalsReference = firebaseFirestore.collection("rentals");
        partnersReference = firebaseFirestore.collection("partners");

        partnersReference.document(model.getTransport_uid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            holder.rentTransportCompany.setText(task.getResult().getString("name"));
                        }
                    }
                });

        holder.rentContactNumber.setText(model.getContact_number());
        holder.rentPickUpLocation.setText(model.getPickup_location());
        holder.rentPickUpDate.setText(model.getPickup_date());
        holder.rentPickUpTime.setText(model.getPickup_time());
        holder.rentDestination.setText(model.getDestination());
        holder.rentDropOffLocation.setText(model.getDropoff_location());
        holder.rentDropOffDate.setText(model.getDropoff_date());
        holder.rentDropOffTime.setText(model.getDropoff_time());

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), UserRentConversationActivity.class);
                intent.putExtra("rentalId", getSnapshots().getSnapshot(position).getReference().getId());
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public ChatMessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rent_chat_conversation_item_layout, parent, false);
        return new ChatMessageHolder(view);
    }

    class ChatMessageHolder extends RecyclerView.ViewHolder {
        LinearLayout item;
        TextView rentTransportCompany,
                rentContactNumber,
                rentPickUpLocation,
                rentPickUpDate,
                rentPickUpTime,
                rentDestination,
                rentDropOffLocation,
                rentDropOffDate,
                rentDropOffTime,
                rentalReferenceNumber;

        public ChatMessageHolder(View view) {
            super(view);
            item = view.findViewById(R.id.item);
            rentTransportCompany = view.findViewById(R.id.rentTransportCompany);
            rentContactNumber = view.findViewById(R.id.rentContactNumber);
            rentPickUpLocation = view.findViewById(R.id.rentPickUpLocation);
            rentPickUpDate = view.findViewById(R.id.rentPickUpDate);
            rentPickUpTime = view.findViewById(R.id.rentPickUpTime);
            rentDestination = view.findViewById(R.id.rentDestination);
            rentDropOffLocation = view.findViewById(R.id.rentDropOffLocation);
            rentDropOffDate = view.findViewById(R.id.rentDropOffDate);
            rentDropOffTime = view.findViewById(R.id.rentDropOffTime);
            rentalReferenceNumber = view.findViewById(R.id.rentalReferenceNumber);
        }
    }

}
