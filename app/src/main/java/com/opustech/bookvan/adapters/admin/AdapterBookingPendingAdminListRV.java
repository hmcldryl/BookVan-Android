package com.opustech.bookvan.adapters.admin;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.Booking;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected void onBindViewHolder(@NonNull BookingHolder holder, int position, @NonNull Booking model) {
        holder.setIsRecyclable(false);

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
        List<String> seat = model.getSeat();

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
                                    Glide.with(context.getApplicationContext())
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
        holder.bookingCountAdult.setText(String.valueOf(count_adult));
        holder.bookingCountChild.setText(String.valueOf(count_child));
        holder.bookingPrice.setText(String.format(Locale.ENGLISH, "%.2f", price));

        holder.seatChip.removeAllViews();
        for (int i = 0; i < seat.size(); i++) {
            final Chip chip = new Chip(context);
            chip.setTextAppearance(R.style.ChipTextAppearance);
            chip.setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary)));
            chip.setTextColor(context.getResources().getColor(R.color.white));
            chip.setChipIcon(context.getResources().getDrawable(R.drawable.ic_van_seat));
            chip.setText(seat.get(i));
            holder.seatChip.addView(chip);
        }

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

        holder.bookingPrice.setText(String.valueOf(price));

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            String outputText = new PrettyTime().format(simpleDateFormat.parse(model.getTimestamp()));
            holder.timestamp.setText(outputText);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public BookingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_admin_pending_item_layout, parent, false);
        return new BookingHolder(view);
    }

    static class BookingHolder extends RecyclerView.ViewHolder {
        final TextView bookingCustomerName;
        final TextView bookingCustomerEmail;
        final TextView bookingContactNumber;
        final TextView bookingReferenceNumber;
        final TextView bookingTripRoute;
        final TextView bookingScheduleDate;
        final TextView bookingScheduleTime;
        final TextView bookingCountAdult;
        final TextView bookingCountChild;
        final TextView bookingCountSpecial;
        final TextView bookingTransportName;
        final TextView labelCountAdult;
        final TextView labelCountChild;
        final TextView labelCountSpecial;
        final TextView bookingPrice;
        final TextView timestamp;
        final TextView itemNumber;
        final LinearLayout item;
        final CircleImageView customerPhoto;
        final ChipGroup seatChip;

        public BookingHolder(View view) {
            super(view);
            item = view.findViewById(R.id.item);
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
            labelCountAdult = view.findViewById(R.id.labelCountAdult);
            bookingCountChild = view.findViewById(R.id.bookingCountChild);
            labelCountChild = view.findViewById(R.id.labelCountChild);
            bookingCountSpecial = view.findViewById(R.id.bookingCountSpecial);
            labelCountSpecial = view.findViewById(R.id.labelCountSpecial);
            bookingTransportName = view.findViewById(R.id.bookingTransportName);
            bookingPrice = view.findViewById(R.id.price);
            timestamp = view.findViewById(R.id.timestamp);
            seatChip = view.findViewById(R.id.seatChip);
        }
    }
}
