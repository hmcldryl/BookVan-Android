package com.opustech.bookvan.adapters.user;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import de.hdodenhof.circleimageview.CircleImageView;
import se.simbio.encryption.Encryption;

public class AdapterBookingConfirmedListRV extends FirestoreRecyclerAdapter<Booking, AdapterBookingConfirmedListRV.BookingHolder> {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;

    private String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";

    private static String OT_SALT = "TEST";
    private static String OT_KEY = "TEST";

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
        holder.bookingCountAdult.setText(String.valueOf(count_adult));
        holder.bookingCountChild.setText(String.valueOf(count_child));
        holder.bookingTransportName.setText(transport_name);
        holder.bookingDriverName.setText(driver_name);
        holder.bookingPlateNumber.setText(plate_number);
        holder.bookingPrice.setText(String.valueOf(price));

        holder.bookingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                final AlertDialog alertDialog = builder.create();
                if (!alertDialog.isShowing()) {
                    final View dialogView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.dialog_booking_confirmed_item_layout, null);
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.setCancelable(true);
                    alertDialog.setView(dialogView);

                    ImageView qrPlaceholder = dialogView.findViewById(R.id.qrPlaceholder);

                    String inputValue = encryptString(reference_number);

                    QRGEncoder qrgEncoder = new QRGEncoder(inputValue, null, QRGContents.Type.TEXT, 300);
                    qrgEncoder.setColorBlack(holder.itemView.getContext().getResources().getColor(R.color.colorPrimary));
                    qrgEncoder.setColorWhite(holder.itemView.getContext().getResources().getColor(R.color.white));
                    Bitmap bitmap = qrgEncoder.getBitmap();
                    qrPlaceholder.setImageBitmap(bitmap);

                    alertDialog.show();
                }
            }
        });
    }

    private String encryptString(String data) {
        byte[] iv = new byte[16];
        Encryption encryption = Encryption.getDefault(OT_KEY, OT_SALT, iv);
        String result = encryption.encryptOrNull(data);
        return result;
    }

    @NonNull
    @Override
    public BookingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_confirmed_item_layout, parent, false);
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
                bookingPrice;
        MaterialCardView bookingCard;
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
            bookingTransportName = view.findViewById(R.id.bookingTransportName);
            bookingDriverName = view.findViewById(R.id.bookingDriverName);
            bookingPlateNumber = view.findViewById(R.id.bookingPlateNumber);
            bookingPrice = view.findViewById(R.id.price);
        }
    }
}
