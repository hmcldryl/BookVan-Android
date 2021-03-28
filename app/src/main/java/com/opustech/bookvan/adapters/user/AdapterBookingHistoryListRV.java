package com.opustech.bookvan.adapters.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.Booking;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterBookingHistoryListRV extends FirestoreRecyclerAdapter<Booking, AdapterBookingHistoryListRV.BookingHolder> {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, partnersReference;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterBookingHistoryListRV(@NonNull FirestoreRecyclerOptions<Booking> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull BookingHolder holder, int position, @NonNull Booking model) {
        holder.itemNumber.setText(String.valueOf(position + 1));

        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        partnersReference = firebaseFirestore.collection("partners");

        String uid = model.getUid();
        String name = model.getName();
        String contact_number = model.getContact_number();
        String reference_number = model.getReference_number();
        String route_from = model.getRoute_from();
        String route_to = model.getRoute_to();
        String trip_route = route_from + " to " + route_to;
        String schedule_date = model.getSchedule_date();
        String schedule_time = model.getSchedule_time();
        int count_adult = model.getCount_adult();
        int count_child = model.getCount_child();
        int count_special = model.getCount_special();
        String transport_uid = model.getTransport_uid();
        String driver_name = model.getDriver_name();
        String plate_number = model.getPlate_number();
        double price = model.getPrice();

        usersReference.document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String customerEmail = task.getResult().getString("email");
                            holder.bookingCustomerEmail.setText(customerEmail);
                            String customerPhoto = task.getResult().getString("photo_url");
                            if (customerPhoto != null) {
                                Glide.with(holder.itemView.getContext())
                                        .load(customerPhoto)
                                        .into(holder.customerPhoto);
                            }
                        }
                    }
                });

        partnersReference.document(transport_uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String transport_name = task.getResult().getString("name");
                            holder.bookingTransportName.setText(transport_name);
                        }
                    }
                });

        holder.bookingCustomerName.setText(name);
        holder.bookingContactNumber.setText(contact_number);
        holder.bookingReferenceNumber.setText(reference_number);
        holder.bookingTripRoute.setText(trip_route);
        holder.bookingScheduleDate.setText(schedule_date);
        holder.bookingScheduleTime.setText(schedule_time);
        holder.bookingDriverName.setText(driver_name);
        holder.bookingPlateNumber.setText(plate_number);

        if (count_adult >= 1) {
            holder.bookingCountAdult.setText(String.valueOf(count_adult));
        } else {
            holder.bookingCountAdult.setVisibility(View.GONE);
            holder.labelCountAdult.setVisibility(View.GONE);
        }

        if (count_child >= 1) {
            holder.bookingCountChild.setText(String.valueOf(count_child));
        } else {
            holder.bookingCountChild.setVisibility(View.GONE);
            holder.labelCountChild.setVisibility(View.GONE);
        }

        if (count_special >= 1) {
            holder.bookingCountSpecial.setText(String.valueOf(count_special));
        } else {
            holder.bookingCountSpecial.setVisibility(View.GONE);
            holder.labelCountSpecial.setVisibility(View.GONE);
        }

        holder.bookingPrice.setText(String.format(Locale.ENGLISH, "%.2f", price));
    }

    @NonNull
    @Override
    public BookingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_user_history_item_layout, parent, false);
        return new BookingHolder(view);
    }

    class BookingHolder extends RecyclerView.ViewHolder {
        TextView bookingCustomerName,
                bookingCustomerEmail,
                bookingContactNumber,
                bookingReferenceNumber,
                bookingTripRoute,
                bookingScheduleDate,
                bookingScheduleTime,
                bookingCountAdult,
                bookingCountChild,
                bookingCountSpecial,
                bookingTransportName,
                bookingDriverName,
                bookingPlateNumber,
                bookingPrice,
                labelCountAdult,
                labelCountChild,
                labelCountSpecial,
                itemNumber;
        MaterialCardView bookingCard;
        CircleImageView customerPhoto;

        public BookingHolder(View view) {
            super(view);
            itemNumber = view.findViewById(R.id.itemNumber);
            customerPhoto = view.findViewById(R.id.customerPhoto);
            bookingCard = view.findViewById(R.id.bookingCard);
            bookingCustomerName = view.findViewById(R.id.bookingCustomerName);
            bookingCustomerEmail = view.findViewById(R.id.bookingCustomerEmail);
            bookingContactNumber = view.findViewById(R.id.bookingContactNumber);
            bookingReferenceNumber = view.findViewById(R.id.bookingReferenceNumber);
            bookingTripRoute = view.findViewById(R.id.bookingTripRoute);
            bookingScheduleDate = view.findViewById(R.id.bookingScheduleDate);
            bookingScheduleTime = view.findViewById(R.id.bookingScheduleTime);
            bookingCountAdult = view.findViewById(R.id.bookingCountAdult);
            labelCountAdult = view.findViewById(R.id.labelCountAdult);
            bookingCountChild = view.findViewById(R.id.bookingCountChild);
            labelCountChild = view.findViewById(R.id.labelCountChild);
            bookingCountSpecial = view.findViewById(R.id.bookingCountSpecial);
            labelCountSpecial = view.findViewById(R.id.labelCountSpecial);
            bookingTransportName = view.findViewById(R.id.bookingTransportName);
            bookingDriverName = view.findViewById(R.id.bookingDriverName);
            bookingPlateNumber = view.findViewById(R.id.bookingPlateNumber);
            bookingPrice = view.findViewById(R.id.price);
        }
    }
}
