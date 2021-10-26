package com.opustech.bookvan.adapters.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRentalPendingAdminListRV extends FirestoreRecyclerAdapter<Rental, AdapterRentalPendingAdminListRV.RentalHolder> {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;

    private final Context context;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterRentalPendingAdminListRV(@NonNull FirestoreRecyclerOptions<Rental> options, Context context) {
        super(options);
        this.context = context;
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
    protected void onBindViewHolder(@NonNull RentalHolder holder, int position, @NonNull Rental model) {
        holder.setIsRecyclable(false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");

        usersReference.document(model.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            holder.customerName.setText(task.getResult().getString("name"));
                            holder.customerEmail.setText(task.getResult().getString("email"));

                            String customerPhoto = task.getResult().getString("photo_url");
                            if (customerPhoto != null) {
                                Glide.with(context.getApplicationContext())
                                        .load(customerPhoto)
                                        .into(holder.customerPhoto);
                            }
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
        holder.rentalReferenceNumber.setText(model.getReference_number());
        holder.itemNumber.setText(String.valueOf(holder.getAdapterPosition() + 1));

        if (model.getPrice() > 0.0) {
            String price = context.getString(R.string.peso_sign) + String.format(Locale.ENGLISH, "%.2f", model.getPrice());
            holder.rentPrice.setText(price);
            holder.rentPrice.setVisibility(View.VISIBLE);
            holder.rentPriceLabel.setVisibility(View.VISIBLE);
        }

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            String outputText = new PrettyTime().format(simpleDateFormat.parse(model.getTimestamp()));
            holder.timestamp.setText(outputText);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public RentalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rental_admin_pending_item_layout, parent, false);
        return new RentalHolder(view);
    }

    static class RentalHolder extends RecyclerView.ViewHolder {
        final LinearLayout item;
        final TextView customerName;
        final TextView customerEmail;
        final TextView rentContactNumber;
        final TextView rentPickUpLocation;
        final TextView rentPickUpDate;
        final TextView rentPickUpTime;
        final TextView rentDestination;
        final TextView rentDropOffLocation;
        final TextView rentDropOffDate;
        final TextView rentDropOffTime;
        final TextView rentalReferenceNumber;
        final TextView rentPrice;
        final TextView rentPriceLabel;
        final TextView timestamp;
        final TextView itemNumber;
        final CircleImageView customerPhoto;

        public RentalHolder(View view) {
            super(view);
            item = view.findViewById(R.id.item);
            customerName = view.findViewById(R.id.customerName);
            customerEmail = view.findViewById(R.id.customerEmail);
            customerPhoto = view.findViewById(R.id.customerPhoto);
            rentContactNumber = view.findViewById(R.id.rentContactNumber);
            rentPickUpLocation = view.findViewById(R.id.rentPickUpLocation);
            rentPickUpDate = view.findViewById(R.id.rentPickUpDate);
            rentPickUpTime = view.findViewById(R.id.rentPickUpTime);
            rentDestination = view.findViewById(R.id.rentDestination);
            rentDropOffLocation = view.findViewById(R.id.rentDropOffLocation);
            rentDropOffDate = view.findViewById(R.id.rentDropOffDate);
            rentDropOffTime = view.findViewById(R.id.rentDropOffTime);
            rentalReferenceNumber = view.findViewById(R.id.rentalReferenceNumber);
            rentPrice = view.findViewById(R.id.rentPrice);
            rentPriceLabel = view.findViewById(R.id.rentPriceLabel);
            itemNumber = view.findViewById(R.id.itemNumber);
            timestamp = view.findViewById(R.id.timestamp);
        }
    }

}
