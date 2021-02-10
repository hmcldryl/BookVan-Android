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

import java.util.ArrayList;
import java.util.Arrays;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterBookingListRV extends FirestoreRecyclerAdapter<Booking, AdapterBookingListRV.BookingHolder> {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference usersReference = firebaseFirestore.collection("users");

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
        usersReference.document(model.getCustomer_uid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String customerPhoto = task.getResult().getString("photo_url");
                            Glide.with(holder.itemView.getContext())
                                    .load(customerPhoto)
                                    .into(holder.customerPhoto);
                        }
                    }
                });
        String customerName = model.getCustomer_name();
        String customerEmail = model.getCustomer_email();
        String bookingContactNumber = model.getBooking_contact_number();
        String bookingLocationFrom = model.getBooking_location_from();
        String bookingLocationTo = model.getBooking_location_to();
        String bookingScheduleDate = model.getBooking_schedule_date();
        String bookingScheduleTime = model.getBooking_schedule_time();
        String bookingCountAdult = model.getBooking_count_adult();
        String bookingCountChild = model.getBooking_count_child();
        String checkoutTotal = model.getBooking_price();

        holder.bookingCustomerName.setText(customerName);
        holder.bookingCustomerEmail.setText(customerEmail);
        holder.bookingContactNumber.setText(bookingContactNumber);
        holder.bookingLocationFrom.setText(bookingLocationFrom);
        holder.bookingLocationTo.setText(bookingLocationTo);
        holder.bookingScheduleDate.setText(bookingScheduleDate);
        holder.bookingScheduleTime.setText(bookingScheduleTime);
        holder.bookingCountAdult.setText(bookingCountAdult);
        holder.bookingCountChild.setText(bookingCountChild);
        holder.bookingPrice.setText(checkoutTotal);

        holder.bookingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                final AlertDialog alertDialog = builder.create();
                if (!alertDialog.isShowing()) {
                    final View dialogView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.dialog_confirm_booking, null);
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    alertDialog.setCancelable(true);
                    alertDialog.setView(dialogView);

                    ArrayList<String> transportArray = new ArrayList<>(Arrays.asList(holder.itemView.getContext().getResources().getStringArray(R.array.transport_companies)));
                    ArrayAdapter<String> transportArrayAdapter = new ArrayAdapter<>(holder.itemView.getContext(), R.layout.support_simple_spinner_dropdown_item, transportArray);

                    TextInputLayout inputTransportName = dialogView.findViewById(R.id.inputTransportName);
                    AutoCompleteTextView inputTransportNameACT = dialogView.findViewById(R.id.inputTransportNameACT);
                    TextInputLayout inputDriverName = dialogView.findViewById(R.id.inputDriverName);
                    TextInputLayout inputVanPlate = dialogView.findViewById(R.id.inputVanPlate);

                    MaterialButton btnCancelBooking = dialogView.findViewById(R.id.btnCancelBooking);
                    MaterialButton btnConfirmBooking = dialogView.findViewById(R.id.btnConfirmBooking);

                    alertDialog.show();
                }
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
