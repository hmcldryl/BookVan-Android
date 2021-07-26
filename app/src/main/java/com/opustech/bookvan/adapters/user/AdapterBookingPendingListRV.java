package com.opustech.bookvan.adapters.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
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
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.Booking;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterBookingPendingListRV extends FirestoreRecyclerAdapter<Booking, AdapterBookingPendingListRV.BookingHolder> {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, partnersReference;

    private final Context context;
    Activity someActivity;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterBookingPendingListRV(@NonNull FirestoreRecyclerOptions<Booking> options, Context context, Activity someActivity) {
        super(options);
        this.context = context;
        this.someActivity = someActivity;
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
        String route_from = model.getRoute_from().equals("Puerto Princesa City") ? "PPC" : model.getRoute_from();
        String route_to = model.getRoute_to().equals("Puerto Princesa City") ? "PPC" : model.getRoute_to();
        String trip_route = route_from + " to " + route_to;
        String schedule_date = model.getSchedule_date();
        String schedule_time = model.getSchedule_time();
        int count_adult = model.getCount_adult();
        int count_child = model.getCount_child();
        int count_special = model.getCount_special();
        String transport_uid = model.getTransport_uid();
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
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && someActivity.isDestroyed()) {
                                    return;
                                }
                                Glide.with(context)
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


        if (count_adult > 1) {
            String outputAdult = count_adult + " adults.";
            holder.bookingCountAdult.setText(outputAdult);
        } else if (count_adult == 1) {
            String outputAdult = count_adult + " adult.";
            holder.bookingCountAdult.setText(outputAdult);
        } else {
            holder.bookingCountAdult.setVisibility(View.GONE);
        }

        if (count_child > 1) {
            String outputChild = count_child + " children.";
            holder.bookingCountChild.setText(outputChild);
        } else if (count_child == 1) {
            String outputChild = count_child + " child.";
            holder.bookingCountChild.setText(outputChild);
        } else {
            holder.bookingCountChild.setVisibility(View.GONE);
        }

        if (count_special >= 1) {
            String outputChild = count_child + " PWD/Senior/Student.";
            holder.bookingCountSpecial.setText(outputChild);
        } else {
            holder.bookingCountSpecial.setVisibility(View.GONE);
        }

        holder.bookingPrice.setText(String.format(Locale.ENGLISH, "%.2f", price));

        holder.bookingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.booking_pending_user_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.btnCancelBooking) {
                            cancelBooking(position, reference_number);
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            String outputText = new PrettyTime().format(simpleDateFormat.parse(model.getTimestamp()));
            holder.timestamp.setText(outputText);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void cancelBooking(int position, String reference_number) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog alertDialog = builder.create();
        if (!alertDialog.isShowing()) {
            final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_cancel_booking, null);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setCancelable(true);
            alertDialog.setView(dialogView);
            TextView bookingReferenceNo = dialogView.findViewById(R.id.bookingReferenceNo);
            TextInputLayout inputRemarks = dialogView.findViewById(R.id.inputRemarks);

            bookingReferenceNo.setText(reference_number);

            MaterialButton btnConfirm = dialogView.findViewById(R.id.btnConfirm);

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ACProgressFlower dialog = new ACProgressFlower.Builder(context)
                            .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                            .themeColor(context.getResources().getColor(R.color.white))
                            .text("Processing...")
                            .fadeColor(Color.DKGRAY).build();
                    dialog.show();

                    inputRemarks.setEnabled(false);
                    btnConfirm.setEnabled(false);

                    String remarks = inputRemarks.getEditText().getText().toString();

                    if (remarks.isEmpty()) {
                        dialog.dismiss();
                        inputRemarks.setEnabled(true);
                        btnConfirm.setEnabled(true);
                        inputRemarks.getEditText().setError("Please enter reason for cancellation.");
                    } else {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("status", "cancelled");
                        hashMap.put("remarks", remarks);
                        getSnapshots().getSnapshot(position)
                                .getReference()
                                .update(hashMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            alertDialog.dismiss();
                                            dialog.dismiss();
                                            Toast.makeText(context, "Success.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            });
            alertDialog.show();
        }
    }

    @NonNull
    @Override
    public BookingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_user_pending_item_layout, parent, false);
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
                bookingPrice,
                timestamp,
                itemNumber;
        CircleImageView customerPhoto;
        MaterialCardView bookingCard;

        public BookingHolder(View view) {
            super(view);
            bookingCard = view.findViewById(R.id.bookingCard);
            itemNumber = view.findViewById(R.id.itemNumber);
            customerPhoto = view.findViewById(R.id.customerPhoto);
            bookingCustomerName = view.findViewById(R.id.bookingCustomerName);
            bookingCustomerEmail = view.findViewById(R.id.bookingCustomerEmail);
            bookingContactNumber = view.findViewById(R.id.bookingContactNumber);
            bookingReferenceNumber = view.findViewById(R.id.bookingReferenceNumber);
            bookingTripRoute = view.findViewById(R.id.bookingTripRoute);
            bookingScheduleDate = view.findViewById(R.id.bookingScheduleDate);
            bookingScheduleTime = view.findViewById(R.id.bookingScheduleTime);
            bookingCountAdult = view.findViewById(R.id.bookingCountAdult);
            bookingCountChild = view.findViewById(R.id.bookingCountChild);
            bookingCountSpecial = view.findViewById(R.id.bookingCountSpecial);
            bookingTransportName = view.findViewById(R.id.bookingTransportName);
            bookingPrice = view.findViewById(R.id.price);
            timestamp = view.findViewById(R.id.timestamp);
        }
    }

}
