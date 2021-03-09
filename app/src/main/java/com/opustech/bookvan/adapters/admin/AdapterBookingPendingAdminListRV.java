package com.opustech.bookvan.adapters.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.Booking;

import java.util.HashMap;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterBookingPendingAdminListRV extends FirestoreRecyclerAdapter<Booking, AdapterBookingPendingAdminListRV.BookingHolder> {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, partnersReference;

    private final Context context;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterBookingPendingAdminListRV(@NonNull FirestoreRecyclerOptions<Booking> options, Context context) {
        super(options);
        this.context = context;
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
        String transport_uid = model.getTransport_uid();

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
                                    Glide.with(context)
                                            .load(customerPhoto)
                                            .into(holder.customerPhoto);
                                }
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
                            String transport_name = task.getResult().getString("transport_name");
                            holder.bookingTransportName.setText(transport_name);
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

        if (count_adult > 1) {
            String outputAdult = count_adult + " adults.";
            holder.bookingCountAdult.setText(outputAdult);
        } else if (count_adult == 1) {
            String outputAdult = count_adult + " adult.";
            holder.bookingCountAdult.setText(outputAdult);
        } else {
            holder.bookingCountAdult.setVisibility(View.GONE);
            holder.labelCountAdult.setVisibility(View.GONE);
        }

        if (count_child > 1) {
            String outputChild = count_child + " children.";
            holder.bookingCountChild.setText(outputChild);
        } else if (count_child == 1) {
            String outputChild = count_child + " child.";
            holder.bookingCountChild.setText(outputChild);
        } else {
            holder.bookingCountChild.setVisibility(View.GONE);
            holder.labelCountChild.setVisibility(View.GONE);
        }

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final AlertDialog alertDialog = builder.create();
                if (!alertDialog.isShowing()) {
                    final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_booking, null);
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.setCancelable(true);
                    alertDialog.setView(dialogView);
                    TextView bookingCustomerNameId = dialogView.findViewById(R.id.confirmBookingCustomerNameId);
                    TextInputLayout inputDriverName = dialogView.findViewById(R.id.inputDriverName);
                    TextInputLayout inputVanPlate = dialogView.findViewById(R.id.inputVanPlate);
                    TextInputLayout inputPrice = dialogView.findViewById(R.id.inputPrice);

                    String customerNameId = "for " + name + " (" + reference_number + ")";
                    bookingCustomerNameId.setText(customerNameId);

                    MaterialButton btnConfirmBooking = dialogView.findViewById(R.id.btnConfirmBooking);

                    btnConfirmBooking.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            inputDriverName.setEnabled(false);
                            inputVanPlate.setEnabled(false);
                            inputPrice.setEnabled(false);
                            btnConfirmBooking.setEnabled(false);
                            String driver_name = inputDriverName.getEditText().getText().toString();
                            String plate_number = inputVanPlate.getEditText().getText().toString();
                            float price = Float.parseFloat(inputPrice.getEditText().getText().toString());
                            if (driver_name.isEmpty()) {
                                inputDriverName.setEnabled(true);
                                inputVanPlate.setEnabled(true);
                                inputPrice.setEnabled(true);
                                btnConfirmBooking.setEnabled(true);
                                inputDriverName.getEditText().setError("Please enter the name of the van driver.");
                            } else if (plate_number.isEmpty()) {
                                inputDriverName.setEnabled(true);
                                inputVanPlate.setEnabled(true);
                                inputPrice.setEnabled(true);
                                btnConfirmBooking.setEnabled(true);
                                inputVanPlate.getEditText().setError("Please enter the van plate number.");
                            } else if (inputPrice.getEditText().toString().isEmpty()) {
                                inputDriverName.setEnabled(true);
                                inputVanPlate.setEnabled(true);
                                inputPrice.setEnabled(true);
                                btnConfirmBooking.setEnabled(true);
                                inputPrice.getEditText().setError("Please enter price for this booking.");
                            } else {
                                updateBookingInfo(alertDialog, driver_name, plate_number, price, position);
                            }
                        }
                    });
                    alertDialog.show();
                }
            }
        });
    }

    private void updateBookingInfo(AlertDialog alertDialog, String driver_name, String plate_number, float price, int position) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("driver_name", driver_name);
        hashMap.put("plate_number", plate_number);
        hashMap.put("price", price);
        hashMap.put("status", "confirmed");
        getSnapshots().getSnapshot(position)
                .getReference()
                .update(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    alertDialog.dismiss();
                    Toast.makeText(context, "Success.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void cancelBooking(int position) {
        new MaterialAlertDialogBuilder(context)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final ACProgressFlower dialog = new ACProgressFlower.Builder(context)
                                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                                .themeColor(context.getResources().getColor(R.color.white))
                                .text("Processing...")
                                .fadeColor(Color.DKGRAY).build();
                        dialog.show();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("status", "cancelled");
                        getSnapshots().getSnapshot(position)
                                .getReference()
                                .update(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    dialogInterface.dismiss();
                                    dialog.dismiss();
                                    Toast.makeText(context, "Success.", Toast.LENGTH_SHORT).show();
                                } else {
                                    dialogInterface.dismiss();
                                    dialog.dismiss();
                                    Toast.makeText(context, "Failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).show();
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
                bookingTransportName,
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
        }
    }
}
