package com.opustech.bookvan.ui.adapters.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.Booking;
import com.opustech.bookvan.transport.TransportLoginActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterBookingPendingAdminListRV extends FirestoreRecyclerAdapter<Booking, AdapterBookingPendingAdminListRV.BookingHolder> {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, partnersReference;

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
        partnersReference = firebaseFirestore.collection("partners");

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
                                if (!customerPhoto.isEmpty()) {
                                    Glide.with(holder.itemView.getContext())
                                            .load(customerPhoto)
                                            .into(holder.customerPhoto);
                                }
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
        holder.bookingCountAdult.setText(String.valueOf(count_adult));
        holder.bookingCountChild.setText(String.valueOf(count_child));
        holder.bookingPrice.setText(String.valueOf(price));

        if (count_adult > 1) {
            String outputAdult = count_adult + " adults.";
            holder.bookingCountAdult.setText(outputAdult);
        }
        else if (count_adult == 1) {
            String outputAdult = count_adult + " adult.";
            holder.bookingCountAdult.setText(outputAdult);
        }
        else {
            holder.bookingCountAdult.setVisibility(View.GONE);
        }

        if (count_child > 1) {
            String outputChild = count_child + " children.";
            holder.bookingCountChild.setText(outputChild);
        }
        else if (count_child == 1) {
            String outputChild = count_child + " child.";
            holder.bookingCountChild.setText(outputChild);
        }
        else {
            holder.bookingCountChild.setVisibility(View.GONE);
        }

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                final AlertDialog alertDialog = builder.create();
                if (!alertDialog.isShowing()) {
                    final View dialogView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.dialog_confirm_booking, null);
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.setCancelable(true);
                    alertDialog.setView(dialogView);

                    TextView bookingCustomerNameId = dialogView.findViewById(R.id.confirmBookingCustomerNameId);
                    TextInputLayout inputTransportName = dialogView.findViewById(R.id.inputTransportName);
                    AutoCompleteTextView inputTransportNameACT = dialogView.findViewById(R.id.inputTransportNameACT);
                    TextInputLayout inputDriverName = dialogView.findViewById(R.id.inputDriverName);
                    TextInputLayout inputVanPlate = dialogView.findViewById(R.id.inputVanPlate);
                    MaterialButton btnCancelBooking = dialogView.findViewById(R.id.btnCancelBooking);
                    MaterialButton btnConfirmBooking = dialogView.findViewById(R.id.btnConfirmBooking);

                    String customerNameId = "for " + name + " (" + reference_number + ")";
                    bookingCustomerNameId.setText(customerNameId);

                    ArrayList<String> transportArray = new ArrayList<>();
                    partnersReference.get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                            transportArray.add(i, task.getResult().getDocuments().get(i).getString("name"));
                                        }
                                        ArrayAdapter<String> transportArrayAdapter = new ArrayAdapter<>(holder.itemView.getContext(), R.layout.support_simple_spinner_dropdown_item, transportArray);
                                        inputTransportNameACT.setAdapter(transportArrayAdapter);
                                        inputTransportNameACT.setThreshold(1);
                                    }
                                }
                            });

                    btnCancelBooking.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            new MaterialAlertDialogBuilder(holder.itemView.getContext())
                                    .setTitle("Cancel this booking?")
                                    .setMessage("Note that this action will also delete the booking from your system.")
                                    .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            cancelPendingBooking(holder, position);
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .show();
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
                                getSnapshots().getSnapshot(position)
                                        .getReference()
                                        .update(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            btnConfirmBooking.setEnabled(true);
                                            alertDialog.dismiss();
                                            new MaterialAlertDialogBuilder(holder.itemView.getContext())
                                                    .setTitle("Successfully confirmed this booking.")
                                                    .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.dismiss();
                                                        }
                                                    })
                                                    .show();
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

    public void cancelPendingBooking(BookingHolder holder, int position) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(holder.itemView.getContext())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(holder.itemView.getContext().getResources().getColor(R.color.white))
                .text("Processing...")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        getSnapshots().getSnapshot(position)
                .getReference()
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            new MaterialAlertDialogBuilder(holder.itemView.getContext())
                                    .setTitle("Successfully confirmed this booking.")
                                    .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .show();
                        }
                    }
                });
    }

    @NonNull
    @Override
    public BookingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_admin_pending_item_layout, parent, false);
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
        LinearLayout item;
        Button btnCancelBooking, btnConfirmBooking;
        CircleImageView customerPhoto;

        public BookingHolder(View view) {
            super(view);
            item = view.findViewById(R.id.item);
            customerPhoto = view.findViewById(R.id.customerPhoto);
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
            bookingPrice = view.findViewById(R.id.price);
        }
    }
}
