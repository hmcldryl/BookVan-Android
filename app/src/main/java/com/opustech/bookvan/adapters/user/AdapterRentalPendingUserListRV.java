package com.opustech.bookvan.adapters.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.Rental;
import com.opustech.bookvan.ui.user.RentMessageActivity;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRentalPendingUserListRV extends FirestoreRecyclerAdapter<Rental, AdapterRentalPendingUserListRV.RentalHolder> {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference rentalsReference, usersReference;

    private final String name;
    private final Context context;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterRentalPendingUserListRV(@NonNull FirestoreRecyclerOptions<Rental> options, String name, Context context) {
        super(options);
        this.name = name;
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
    protected void onBindViewHolder(@NonNull RentalHolder holder, int position, @NonNull Rental model) {
        holder.setIsRecyclable(false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        rentalsReference = firebaseFirestore.collection("rentals");
        usersReference = firebaseFirestore.collection("users");

        usersReference.document(model.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            holder.customerName.setText(task.getResult().getString("name"));
                            holder.customerEmail.setText(task.getResult().getString("email"));

                            String customerPhoto = task.getResult().getString("photo_url");
                            if (customerPhoto != null) {
                                Glide.with(context)
                                        .load(customerPhoto)
                                        .into(holder.customerPhoto);
                            }
                        }
                    }
                });

        holder.rentContactNumber.setText(model.getContact_number());
        holder.rentPickUpLocation.setText(model.getPickup_location());
        holder.rentPickUpDate.setText(model.getPickup_date());
        holder.rentPickUpTime.setText(model.getPickup_time());
        holder.rentDestination.setText(model.getDestination());
        holder.rentDropOffLocation.setText(model.getDropoff_location());
        holder.rentDropOffDate.setText(model.getDropoff_date());
        holder.rentDropOffTime.setText(model.getDropoff_time());
        holder.rentalReferenceNumber.setText(model.getReference_number());
        holder.itemNumber.setText(String.valueOf(holder.getAdapterPosition() + 1));

        if (model.getPrice() > 0.0) {
            String price = context.getString(R.string.peso_sign) + String.format(Locale.ENGLISH, "%.2f", model.getPrice());
            holder.rentPrice.setText(price);
            holder.rentPrice.setVisibility(View.VISIBLE);
            holder.rentPriceLabel.setVisibility(View.VISIBLE);
        }

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.rental_pending_user_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.btnChat) {
                            Intent intent = new Intent(holder.itemView.getContext(), RentMessageActivity.class);
                            intent.putExtra("rental_id", getSnapshots().getSnapshot(holder.getAdapterPosition()).getReference().getId());
                            intent.putExtra("user_id", model.getUid());
                            intent.putExtra("name", name);
                            intent.putExtra("reference_id", model.getReference_number());
                            intent.putExtra("transport_id", getSnapshots().get(holder.getAdapterPosition()).getTransport_uid());
                            intent.putExtra("status", model.getStatus());
                            holder.itemView.getContext().startActivity(intent);
                        } else if (item.getItemId() == R.id.btnCancel) {
                            cancelRent(holder.getAdapterPosition(), model.getReference_number());
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

    public void cancelRent(int position, String reference_number) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog alertDialog = builder.create();
        if (!alertDialog.isShowing()) {
            final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_cancel_rent, null);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setCancelable(true);
            alertDialog.setView(dialogView);
            TextView rentReferenceNo = dialogView.findViewById(R.id.rentReferenceNo);

            rentReferenceNo.setText(reference_number);

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

                    btnConfirm.setEnabled(false);

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("status", "cancelled");
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
                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(context, "Failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });
            alertDialog.show();
        }
    }

    @NonNull
    @Override
    public RentalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rental_user_pending_item_layout, parent, false);
        return new RentalHolder(view);
    }

    static class RentalHolder extends RecyclerView.ViewHolder {
        final LinearLayout item;
        final TextView customerName;
        final TextView customerEmail;
        final TextView rentContactNumber;
        final TextView rentPickUpLocation;
        final TextView rentPickUpDate;
        final TextView rentPickUpTime;
        final TextView rentDestination;
        final TextView rentDropOffLocation;
        final TextView rentDropOffDate;
        final TextView rentDropOffTime;
        final TextView rentalReferenceNumber;
        final TextView rentPrice;
        final TextView rentPriceLabel;
        final TextView timestamp;
        final TextView itemNumber;
        final CircleImageView customerPhoto;

        public RentalHolder(View view) {
            super(view);
            item = view.findViewById(R.id.item);
            customerName = view.findViewById(R.id.customerName);
            customerEmail = view.findViewById(R.id.customerEmail);
            customerPhoto = view.findViewById(R.id.customerPhoto);
            rentContactNumber = view.findViewById(R.id.rentContactNumber);
            rentPickUpLocation = view.findViewById(R.id.rentPickUpLocation);
            rentPickUpDate = view.findViewById(R.id.rentPickUpDate);
            rentPickUpTime = view.findViewById(R.id.rentPickUpTime);
            rentDestination = view.findViewById(R.id.rentDestination);
            rentDropOffLocation = view.findViewById(R.id.rentDropOffLocation);
            rentDropOffDate = view.findViewById(R.id.rentDropOffDate);
            rentDropOffTime = view.findViewById(R.id.rentDropOffTime);
            rentalReferenceNumber = view.findViewById(R.id.rentalReferenceNumber);
            rentPrice = view.findViewById(R.id.rentPrice);
            rentPriceLabel = view.findViewById(R.id.rentPriceLabel);
            itemNumber = view.findViewById(R.id.itemNumber);
            timestamp = view.findViewById(R.id.timestamp);
        }
    }

}
