package com.opustech.bookvan;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.opustech.bookvan.model.Booking;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class AdapterBookingListRV extends FirestoreRecyclerAdapter<Booking, AdapterBookingListRV.BookingHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterBookingListRV(@NonNull FirestoreRecyclerOptions<Booking> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull BookingHolder holder, int position, @NonNull Booking model) {
        String customerName = model.getCustomer_name();
        String customerEmail = model.getCustomer_email();
        String bookingContactNumber = model.getBooking_contact_number();
        String bookingLocationFrom = model.getBooking_location_from();
        String bookingLocationTo = model.getBooking_location_to();
        String bookingScheduleDate = model.getBooking_schedule_date();
        String bookingScheduleTime = model.getBooking_schedule_time();
        String bookingCountAdult = model.getBooking_count_adult();
        String bookingCountChild = model.getBooking_count_child();

        holder.bookingCustomerName.setText(customerName);
        holder.bookingCustomerEmail.setText(customerEmail);
        holder.bookingContactNumber.setText(bookingContactNumber);
        holder.bookingLocationFrom.setText(bookingLocationFrom);
        holder.bookingLocationTo.setText(bookingLocationTo);
        holder.bookingScheduleDate.setText(bookingScheduleDate);
        holder.bookingScheduleTime.setText(bookingScheduleTime);
        holder.bookingCountAdult.setText(bookingCountAdult);
        holder.bookingCountChild.setText(bookingCountChild);

        holder.bookingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.btnConfirmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(holder.itemView.getContext(), "Booking confirmed.", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnCancelBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(holder.itemView.getContext(), "Booking cancelled.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @NonNull
    @Override
    public BookingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_item_layout, parent, false);
        return new BookingHolder(view);
    }

    public void deleteBooking(int position, Context context) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(context)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(context.getResources().getColor(R.color.white))
                .text("Processing...")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        getSnapshots().getSnapshot(position).getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
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

    class BookingHolder extends RecyclerView.ViewHolder {
        TextView bookingCustomerName,
                bookingCustomerEmail,
                bookingContactNumber,
                bookingLocationFrom,
                bookingLocationTo,
                bookingScheduleDate,
                bookingScheduleTime,
                bookingCountAdult,
                bookingCountChild;
        CardView bookingCard;
        Button btnCancelBooking, btnConfirmBooking;

        public BookingHolder(View view) {
            super(view);
            bookingCard = view.findViewById(R.id.bookingCard);
            btnCancelBooking = view.findViewById(R.id.btnCancelBooking);
            btnConfirmBooking = view.findViewById(R.id.btnConfirmBooking);
            bookingCustomerName = view.findViewById(R.id.bookingCustomerName);
            bookingCustomerEmail = view.findViewById(R.id.bookingCustomerEmail);
            bookingContactNumber = view.findViewById(R.id.bookingContactNumber);
            bookingLocationFrom = view.findViewById(R.id.bookingLocationFrom);
            bookingLocationTo = view.findViewById(R.id.bookingLocationTo);
            bookingScheduleDate = view.findViewById(R.id.bookingScheduleDate);
            bookingScheduleTime = view.findViewById(R.id.bookingScheduleTime);
            bookingCountAdult = view.findViewById(R.id.bookingCountAdult);
            bookingCountChild = view.findViewById(R.id.bookingCountChild);

        }
    }

}
