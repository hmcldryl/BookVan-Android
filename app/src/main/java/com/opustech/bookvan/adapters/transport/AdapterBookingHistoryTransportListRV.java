package com.opustech.bookvan.adapters.transport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.opustech.bookvan.model.Booking;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterBookingHistoryTransportListRV extends FirestoreRecyclerAdapter<Booking, AdapterBookingHistoryTransportListRV.BookingHolder> {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;

    private String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";

    private Context context;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterBookingHistoryTransportListRV(@NonNull FirestoreRecyclerOptions<Booking> options, Context context) {
        super(options);
        this.context = context;

    }

    @Override
    protected void onBindViewHolder(@NonNull BookingHolder holder, int position, @NonNull Booking model) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");

        String uid = model.getUid();
        String name = model.getName();
        String contact_number = model.getContact_number();
        String reference_number = model.getReference_number();
        String location_from = model.getLocation_from();
        String location_to = model.getLocation_to();
        String schedule_date = model.getSchedule_date();
        String schedule_time = model.getSchedule_time();
        int count_adult = model.getCount_adult();
        int count_child = model.getCount_child();
        String transport_name = model.getTransport_name();
        String driver_name = model.getDriver_name();
        String plate_number = model.getPlate_number();
        float price = model.getPrice();

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

        holder.bookingCustomerName.setText(name);
        holder.bookingContactNumber.setText(contact_number);
        holder.bookingReferenceNumber.setText(reference_number);
        holder.bookingLocationFrom.setText(location_from);
        holder.bookingLocationTo.setText(location_to);
        holder.bookingScheduleDate.setText(schedule_date);
        holder.bookingScheduleTime.setText(schedule_time);
        holder.bookingTransportName.setText(transport_name);
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

        holder.bookingPrice.setText(String.valueOf(price));
    }

    @NonNull
    @Override
    public BookingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_admin_history_item_layout, parent, false);
        return new BookingHolder(view);
    }

    class BookingHolder extends RecyclerView.ViewHolder {
        TextView bookingCustomerName,
                bookingCustomerEmail,
                bookingContactNumber,
                bookingReferenceNumber,
                bookingLocationFrom,
                bookingLocationTo,
                bookingScheduleDate,
                bookingScheduleTime,
                bookingCountAdult,
                bookingCountChild,
                bookingTransportName,
                bookingDriverName,
                bookingPlateNumber,
                bookingPrice,
                labelCountAdult,
                labelCountChild;
        LinearLayout item;
        CircleImageView customerPhoto;

        public BookingHolder(View view) {
            super(view);
            item = view.findViewById(R.id.item);
            customerPhoto = view.findViewById(R.id.customerPhoto);
            bookingCustomerName = view.findViewById(R.id.bookingCustomerName);
            bookingCustomerEmail = view.findViewById(R.id.bookingCustomerEmail);
            bookingContactNumber = view.findViewById(R.id.bookingContactNumber);
            bookingReferenceNumber = view.findViewById(R.id.bookingReferenceNumber);
            bookingLocationFrom = view.findViewById(R.id.bookingLocationFrom);
            bookingLocationTo = view.findViewById(R.id.bookingLocationTo);
            bookingScheduleDate = view.findViewById(R.id.bookingScheduleDate);
            bookingScheduleTime = view.findViewById(R.id.bookingScheduleTime);
            bookingCountAdult = view.findViewById(R.id.bookingCountAdult);
            labelCountAdult = view.findViewById(R.id.labelCountAdult);
            bookingCountChild = view.findViewById(R.id.bookingCountChild);
            labelCountChild = view.findViewById(R.id.labelCountChild);
            bookingTransportName = view.findViewById(R.id.bookingTransportName);
            bookingDriverName = view.findViewById(R.id.bookingDriverName);
            bookingPlateNumber = view.findViewById(R.id.bookingPlateNumber);
            bookingPrice = view.findViewById(R.id.price);
        }
    }
}
