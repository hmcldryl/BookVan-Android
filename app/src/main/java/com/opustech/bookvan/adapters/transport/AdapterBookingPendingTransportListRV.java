package com.opustech.bookvan.adapters.transport;

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
import com.opustech.bookvan.notification.APIService;
import com.opustech.bookvan.notification.Client;
import com.opustech.bookvan.notification.Data;
import com.opustech.bookvan.notification.NotificationSender;
import com.opustech.bookvan.notification.RequestResponse;
import com.opustech.bookvan.ui.user.UserBookActivity;
import com.opustech.bookvan.ui.user.UserRentActivity;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterBookingPendingTransportListRV extends FirestoreRecyclerAdapter<Booking, AdapterBookingPendingTransportListRV.BookingHolder> {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, partnersReference;

    private APIService apiService;

    private String uid;

    private Context context;

    String transport_name = "";

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterBookingPendingTransportListRV(@NonNull FirestoreRecyclerOptions<Booking> options, String uid, Context context) {
        super(options);
        this.uid = uid;
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull BookingHolder holder, int position, @NonNull Booking model) {
        holder.itemNumber.setText(String.valueOf(holder.getAdapterPosition() + 1));

        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        partnersReference = firebaseFirestore.collection("partners");

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        String name = model.getName();
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

        partnersReference.document(transport_uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            transport_name = task.getResult().getString("name");
                            holder.bookingTransportName.setText(transport_name);
                        }
                    }
                });

        holder.bookingCustomerName.setText(name);
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

        if (count_special >= 1) {
            String outputSpecial = count_special + " PWD/Senior/Student.";
            holder.bookingCountSpecial.setText(outputSpecial);
        } else {
            holder.bookingCountSpecial.setVisibility(View.GONE);
            holder.labelCountSpecial.setVisibility(View.GONE);
        }

        holder.bookingPrice.setText(String.format(Locale.ENGLISH, "%.2f", price));

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
                    TextView bookingReferenceNo = dialogView.findViewById(R.id.bookingReferenceNo);
                    TextInputLayout inputDriverName = dialogView.findViewById(R.id.inputDriverName);
                    TextInputLayout inputVanPlate = dialogView.findViewById(R.id.inputVanNumber);

                    bookingReferenceNo.setText(reference_number);

                    MaterialButton btnConfirmBooking = dialogView.findViewById(R.id.btnConfirmBooking);
                    MaterialButton btnCancelBooking = dialogView.findViewById(R.id.btnCancelBooking);

                    btnCancelBooking.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
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
                                            btnConfirm.setEnabled(true);
                                            inputRemarks.getEditText().setError("Please enter reason for cancellation.");
                                        } else {
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("status", "cancelled");
                                            hashMap.put("remarks", remarks);
                                            getSnapshots().getSnapshot(holder.getAdapterPosition())
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
                    });

                    btnConfirmBooking.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            inputDriverName.setEnabled(false);
                            inputVanPlate.setEnabled(false);
                            btnConfirmBooking.setEnabled(false);
                            btnCancelBooking.setEnabled(false);

                            String driver_name = inputDriverName.getEditText().getText().toString();
                            String van_number = inputVanPlate.getEditText().getText().toString();

                            if (driver_name.isEmpty()) {
                                btnConfirmBooking.setEnabled(true);
                                btnCancelBooking.setEnabled(true);
                                inputDriverName.setEnabled(true);
                                inputVanPlate.setEnabled(true);
                                inputDriverName.getEditText().setError("Please enter the name of the van driver.");
                            } else if (van_number.isEmpty()) {
                                btnConfirmBooking.setEnabled(true);
                                btnCancelBooking.setEnabled(true);
                                inputDriverName.setEnabled(true);
                                inputVanPlate.setEnabled(true);
                                inputVanPlate.getEditText().setError("Please enter the van plate number.");
                            } else {
                                updateBookingInfo(alertDialog, model.getUid(), driver_name, van_number, holder.getAdapterPosition(), transport_name, reference_number);
                            }
                        }
                    });
                    alertDialog.show();
                }
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

    private void sendNotification(String token, String title, String message) {
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, token);
        apiService.sendNotification(sender).enqueue(new Callback<RequestResponse>() {
            @Override
            public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(context, "Request failed.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RequestResponse> call, Throwable t) {

            }
        });
    }

    private void fetchToken(String uid, String transportName, String referenceNumber) {
        FirebaseFirestore.getInstance().collection("tokens")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            sendNotification(task.getResult().getString("token"), "Booking Confirmed", "Your booking, " + referenceNumber + ", is confirmed by " + transportName + ".");
                        }
                    }
                });
    }

    private void updateBookingInfo(AlertDialog alertDialog, String uid, String driver_name, String van_number, int position, String transportName, String referenceNumber) {
        fetchToken(uid, transportName, referenceNumber);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("driver_name", driver_name);
        hashMap.put("van_number", van_number);
        hashMap.put("status", "confirmed");
        getSnapshots().getSnapshot(position)
                .getReference()
                .update(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_transport_pending_item_layout, parent, false);
        return new BookingHolder(view);
    }

    class BookingHolder extends RecyclerView.ViewHolder {
        TextView bookingCustomerName,
                bookingReferenceNumber,
                bookingTripRoute,
                bookingScheduleDate,
                bookingScheduleTime,
                bookingCountAdult,
                bookingCountChild,
                bookingCountSpecial,
                bookingTransportName,
                labelCountAdult,
                labelCountChild,
                labelCountSpecial,
                bookingPrice,
                timestamp,
                itemNumber;
        LinearLayout item;
        CircleImageView customerPhoto;

        public BookingHolder(View view) {
            super(view);
            item = view.findViewById(R.id.item);
            itemNumber = view.findViewById(R.id.itemNumber);
            customerPhoto = view.findViewById(R.id.customerPhoto);
            bookingCustomerName = view.findViewById(R.id.bookingCustomerName);
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
            bookingPrice = view.findViewById(R.id.price);
            timestamp = view.findViewById(R.id.timestamp);
        }
    }
}
