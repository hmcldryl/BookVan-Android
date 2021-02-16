package com.opustech.bookvan.ui.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.Booking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterBookingConfirmedListRV extends FirestoreRecyclerAdapter<Booking, AdapterBookingConfirmedListRV.BookingHolder> {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;

    private String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterBookingConfirmedListRV(@NonNull FirestoreRecyclerOptions<Booking> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull BookingHolder holder, int position, @NonNull Booking model) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");

        String customerId = model.getUid();
        String customerName = model.getName();
        String bookingContactNumber = model.getContact_number();
        String bookingReferenceNumber = model.getReference_number();
        String bookingLocationFrom = model.getLocation_from();
        String bookingLocationTo = model.getLocation_to();
        String bookingScheduleDate = model.getSchedule_date();
        String bookingScheduleTime = model.getSchedule_time();
        String bookingCountAdult = model.getCount_adult();
        String bookingCountChild = model.getCount_child();
        String checkoutTotal = model.getPrice();

        usersReference.document(customerId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String customerEmail = task.getResult().getString("email");
                            holder.bookingCustomerEmail.setText(customerEmail);
                            String customerPhoto = task.getResult().getString("photo_url");
                            Glide.with(holder.itemView.getContext())
                                    .load(customerPhoto)
                                    .into(holder.customerPhoto);
                        }
                    }
                });

        holder.bookingCustomerName.setText(customerName);
        holder.bookingContactNumber.setText(bookingContactNumber);
        holder.bookingReferenceNumber.setText(bookingReferenceNumber);
        holder.bookingLocationFrom.setText(bookingLocationFrom);
        holder.bookingLocationTo.setText(bookingLocationTo);
        holder.bookingScheduleDate.setText(bookingScheduleDate);
        holder.bookingScheduleTime.setText(bookingScheduleTime);
        holder.bookingCountAdult.setText(bookingCountAdult);
        holder.bookingCountChild.setText(bookingCountChild);
        holder.bookingPrice.setText(checkoutTotal);
    }

    @NonNull
    @Override
    public BookingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_confirmed_item_layout, parent, false);
        return new BookingHolder(view);
    }

    public void deleteBooking(int position, Context context) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(context)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(context.getResources().getColor(R.color.white))
                .text("Processing...")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        getSnapshots().getSnapshot(position).getReference().delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
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
                bookingReferenceNumber,
                bookingLocationFrom,
                bookingLocationTo,
                bookingScheduleDate,
                bookingScheduleTime,
                bookingCountAdult,
                bookingCountChild,
                bookingPrice;
        CardView bookingCard;
        Button btnCancelBooking, btnConfirmBooking;
        CircleImageView customerPhoto;

        public BookingHolder(View view) {
            super(view);
            customerPhoto = view.findViewById(R.id.customerPhoto);
            bookingCard = view.findViewById(R.id.bookingCard);
            btnCancelBooking = view.findViewById(R.id.btnCancelBooking);
            btnConfirmBooking = view.findViewById(R.id.btnConfirmBooking);
            bookingCustomerName = view.findViewById(R.id.bookingCustomerName);
            bookingCustomerEmail = view.findViewById(R.id.bookingCustomerEmail);
            bookingContactNumber = view.findViewById(R.id.bookingContactNumber);
            bookingReferenceNumber = view.findViewById(R.id.bookingReferenceNumber);
            bookingLocationFrom = view.findViewById(R.id.bookingLocationFrom);
            bookingLocationTo = view.findViewById(R.id.bookingLocationTo);
            bookingScheduleDate = view.findViewById(R.id.bookingScheduleDate);
            bookingScheduleTime = view.findViewById(R.id.bookingScheduleTime);
            bookingCountAdult = view.findViewById(R.id.bookingCountAdult);
            bookingCountChild = view.findViewById(R.id.bookingCountChild);
            bookingPrice = view.findViewById(R.id.checkoutTotal);

        }
    }

}
