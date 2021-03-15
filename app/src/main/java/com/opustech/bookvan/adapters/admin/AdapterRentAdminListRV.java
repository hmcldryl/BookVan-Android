package com.opustech.bookvan.adapters.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
        String name = model.getName();
        String contact_number = model.getContact_number();
        String rent_type = model.getRent_type();
        String location_pickup = model.getLocation_pickup();
        String location_dropoff = model.getLocation_dropoff();
        String destination = model.getDestination();
        String schedule_start_date = model.getSchedule_start_date();
        String schedule_end_date = model.getSchedule_end_date();
        String timestamp = model.getTimestamp();

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

        holder.rentalsContactNumber.setText(contact_number);
        holder.rentalsLocationPickup.setText(location_pickup);
        holder.rentalsLocationDropoff.setText(location_dropoff);
        holder.rentalsDestination.setText(destination);
        holder.rentalsScheduleDateStart.setText(schedule_start_date);
        holder.rentalsScheduleDateEnd.setText(schedule_end_date);
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
                rentalsContactNumber,
                rentalsLocationPickup,
                rentalsLocationDropoff,
                rentalsDestination,
                rentalsScheduleDateStart,
                rentalsScheduleDateEnd;
        LinearLayout item;
        CircleImageView customerPhoto;

        public RentHolder(View view) {
            super(view);
            item = view.findViewById(R.id.item);
            customerPhoto = view.findViewById(R.id.customerPhoto);
            customerName = view.findViewById(R.id.customerName);
            customerEmail = view.findViewById(R.id.customerEmail);
            rentalsContactNumber = view.findViewById(R.id.rentalsContactNumber);
            rentalsLocationPickup = view.findViewById(R.id.rentalsLocationPickup);
            rentalsLocationDropoff = view.findViewById(R.id.rentalsLocationDropoff);
            rentalsDestination = view.findViewById(R.id.rentalsDestination);
            rentalsScheduleDateStart = view.findViewById(R.id.rentalsScheduleDateStart);
            rentalsScheduleDateEnd = view.findViewById(R.id.rentalsScheduleDateEnd);
        }
    }
}
