package com.opustech.bookvan.ui.adapters;

import android.app.AlertDialog;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.Booking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterBookingPendingAdminListRV extends FirestoreRecyclerAdapter<Booking, AdapterBookingPendingAdminListRV.BookingHolder> {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;

    private String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterBookingPendingAdminListRV(@NonNull FirestoreRecyclerOptions<Booking> options) {
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

        holder.bookingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                final AlertDialog alertDialog = builder.create();
                if (!alertDialog.isShowing()) {
                    final View dialogView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.dialog_confirm_booking, null);
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.setCancelable(true);
                    alertDialog.setView(dialogView);

                    ArrayList<String> transportArray = new ArrayList<>(Arrays.asList(holder.itemView.getContext().getResources().getStringArray(R.array.transport_companies)));
                    ArrayAdapter<String> transportArrayAdapter = new ArrayAdapter<>(holder.itemView.getContext(), R.layout.support_simple_spinner_dropdown_item, transportArray);

                    TextView bookingCustomerNameId = dialogView.findViewById(R.id.confirmBookingCustomerNameId);
                    TextInputLayout inputTransportName = dialogView.findViewById(R.id.inputTransportName);
                    AutoCompleteTextView inputTransportNameACT = dialogView.findViewById(R.id.inputTransportNameACT);
                    TextInputLayout inputDriverName = dialogView.findViewById(R.id.inputDriverName);
                    TextInputLayout inputVanPlate = dialogView.findViewById(R.id.inputVanPlate);

                    String customerNameId = "for " + customerName + " (" + bookingReferenceNumber + ")";
                    bookingCustomerNameId.setText(customerNameId);

                    MaterialButton btnCancelBooking = dialogView.findViewById(R.id.btnCancelBooking);
                    MaterialButton btnConfirmBooking = dialogView.findViewById(R.id.btnConfirmBooking);

                    inputTransportNameACT.setAdapter(transportArrayAdapter);
                    inputTransportNameACT.setThreshold(1);

                    btnCancelBooking.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                    btnConfirmBooking.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            btnConfirmBooking.setEnabled(false);
                            String transport_name = inputTransportName.getEditText().getText().toString();
                            String driver_name = inputDriverName.getEditText().getText().toString();
                            String plate_number = inputVanPlate.getEditText().getText().toString();

                            if (transport_name.isEmpty()) {
                                inputTransportName.getEditText().setError("Please select a transport company.");
                                btnConfirmBooking.setEnabled(true);
                            } else if (driver_name.isEmpty()) {
                                inputDriverName.getEditText().setError("Please enter the name of the van driver.");
                                btnConfirmBooking.setEnabled(true);
                            } else if (plate_number.isEmpty()) {
                                inputVanPlate.getEditText().setError("Please enter the van plate number.");
                                btnConfirmBooking.setEnabled(true);
                            } else {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("transport_name", transport_name);
                                hashMap.put("driver_name", driver_name);
                                hashMap.put("plate_number", plate_number);
                                hashMap.put("status", "confirmed");
                                getSnapshots().getSnapshot(position).getReference()
                                        .set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            btnConfirmBooking.setEnabled(true);
                                            alertDialog.dismiss();
                                            Toast.makeText(v.getContext(), "Successfully confirmed this booking.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                    alertDialog.show();
                }
            }
        });

    }

    @NonNull
    @Override
    public BookingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_pending_item_layout, parent, false);
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
