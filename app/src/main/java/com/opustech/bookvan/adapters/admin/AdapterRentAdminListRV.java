package com.opustech.bookvan.adapters.admin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.opustech.bookvan.R;
import com.opustech.bookvan.model.Rental;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.ui.user.UserRentConversationActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRentAdminListRV extends FirestoreRecyclerAdapter<Rental, AdapterRentAdminListRV.RentHolder> {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, rentalsReference;

    private Context context;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterRentAdminListRV(@NonNull FirestoreRecyclerOptions<Rental> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull RentHolder holder, int position, @NonNull Rental model) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        rentalsReference = firebaseFirestore.collection("rentals");
        usersReference = firebaseFirestore.collection("users");

        String uid = model.getUid();
        String reference_number = model.getReference_number();
        String name = model.getName();
        String contact_number = model.getContact_number();
        String rent_type = model.getRent_type();
        String pickup_location = model.getPickup_location();
        String pickup_date = model.getPickup_date();
        String pickup_time = model.getPickup_time();
        String destination = model.getDestination();
        String dropoff_location = model.getDropoff_location();
        String dropoff_date = model.getDropoff_date();
        String dropoff_time = model.getDropoff_time();

        usersReference.document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String customerEmail = task.getResult().getString("email");
                            holder.customerEmail.setText(customerEmail);
                            String customerPhoto = task.getResult().getString("photo_url");
                            if (customerPhoto != null) {
                                Glide.with(context)
                                        .load(customerPhoto)
                                        .into(holder.customerPhoto);
                            }
                        }
                    }
                });

        holder.rentContactNumber.setText(contact_number);
        holder.rentPickUpLocation.setText(pickup_location);
        holder.rentPickUpDate.setText(pickup_date);
        holder.rentPickUpTime.setText(pickup_time);
        holder.rentDestination.setText(destination);
        holder.rentDropOffLocation.setText(dropoff_location);
        holder.rentDropOffDate.setText(dropoff_date);
        holder.rentDropOffTime.setText(dropoff_time);

        holder.rentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserRentConversationActivity.class);
                intent.putExtra("reference_number", reference_number);
                context.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public RentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rent_admin_item_layout, parent, false);
        return new RentHolder(view);
    }

    class RentHolder extends RecyclerView.ViewHolder {

        TextView customerName,
                customerEmail,
                rentContactNumber,
                rentType,
                rentPickUpLocation,
                rentPickUpDate,
                rentPickUpTime,
                rentDestination,
                rentDropOffLocation,
                rentDropOffDate,
                rentDropOffTime;
        LinearLayout item;
        CardView rentCard;
        CircleImageView customerPhoto;

        public RentHolder(View view) {
            super(view);
            item = view.findViewById(R.id.item);
            rentCard = view.findViewById(R.id.rentCard);
            customerPhoto = view.findViewById(R.id.customerPhoto);
            customerName = view.findViewById(R.id.customerName);
            customerEmail = view.findViewById(R.id.customerEmail);
            rentContactNumber = view.findViewById(R.id.rentContactNumber);
            rentType = view.findViewById(R.id.rentType);
            rentPickUpLocation = view.findViewById(R.id.rentPickUpLocation);
            rentPickUpDate = view.findViewById(R.id.rentPickUpDate);
            rentPickUpTime = view.findViewById(R.id.rentPickUpTime);
            rentDestination = view.findViewById(R.id.rentDestination);
            rentDropOffLocation = view.findViewById(R.id.rentDropOffLocation);
            rentDropOffDate = view.findViewById(R.id.rentDropOffDate);
            rentDropOffTime = view.findViewById(R.id.rentDropOffTime);
        }
    }
}
