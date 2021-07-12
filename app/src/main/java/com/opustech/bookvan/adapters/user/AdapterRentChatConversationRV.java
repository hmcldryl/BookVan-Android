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
import android.widget.RelativeLayout;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.Rental;
import com.opustech.bookvan.ui.user.UserRentConversationActivity;
import com.opustech.bookvan.ui.user.UserRentMessageActivity;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRentChatConversationRV extends FirestoreRecyclerAdapter<Rental, AdapterRentChatConversationRV.ChatMessageHolder> {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference rentalsReference, partnersReference;

    private String uid;
    private Context context;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterRentChatConversationRV(@NonNull FirestoreRecyclerOptions<Rental> options, String uid, Context context) {
        super(options);
        this.uid = uid;
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatMessageHolder holder, int position, @NonNull Rental model) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        rentalsReference = firebaseFirestore.collection("rentals");
        partnersReference = firebaseFirestore.collection("partners");

        partnersReference.document(model.getTransport_uid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            holder.rentTransportCompany.setText(task.getResult().getString("name"));
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
        holder.rentStatus.setText(capitalize(model.getStatus()));
        holder.itemNumber.setText(String.valueOf(position + 1));

        if (model.getPrice() > 0.0) {
            String price = context.getString(R.string.peso_sign) + String.format(Locale.ENGLISH, "%.2f", model.getPrice());
            holder.rentPrice.setText(price);
            holder.rentPrice.setVisibility(View.VISIBLE);
            holder.rentPriceLabel.setVisibility(View.VISIBLE);
        }

        if (model.getStatus().equals("pending")) {
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(holder.itemView.getContext(), UserRentMessageActivity.class);
                    intent.putExtra("rentalId", getSnapshots().getSnapshot(position).getReference().getId());
                    holder.itemView.getContext().startActivity(intent);
                }
            });

            holder.item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu popup = new PopupMenu(context, v);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.user_rent_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.btnCancel) {
                                cancelRent(position, model.getReference_number(), holder.rentalReferenceNumber);
                            }
                            return false;
                        }
                    });
                    popup.show();
                    return false;
                }
            });
        }
    }

    public void cancelRent(int position, String reference_number, TextView textView) {
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
                                        String text = textView.getText().toString() + " (Cancelled)";
                                        textView.setText(text);
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

    private String capitalize(String status) {
        if (status != null) {
            if (!status.isEmpty()) {
                return status.substring(0, 1).toUpperCase() + status.substring(1);
            }
        }
        return status;
    }

    @NonNull
    @Override
    public ChatMessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rent_chat_conversation_item_layout, parent, false);
        return new ChatMessageHolder(view);
    }

    class ChatMessageHolder extends RecyclerView.ViewHolder {
        LinearLayout item;
        TextView rentTransportCompany,
                rentContactNumber,
                rentPickUpLocation,
                rentPickUpDate,
                rentPickUpTime,
                rentDestination,
                rentDropOffLocation,
                rentDropOffDate,
                rentDropOffTime,
                rentalReferenceNumber,
                rentStatus,
                rentPrice,
                rentPriceLabel,
                itemNumber;

        public ChatMessageHolder(View view) {
            super(view);
            item = view.findViewById(R.id.item);
            rentTransportCompany = view.findViewById(R.id.rentTransportCompany);
            rentContactNumber = view.findViewById(R.id.rentContactNumber);
            rentPickUpLocation = view.findViewById(R.id.rentPickUpLocation);
            rentPickUpDate = view.findViewById(R.id.rentPickUpDate);
            rentPickUpTime = view.findViewById(R.id.rentPickUpTime);
            rentDestination = view.findViewById(R.id.rentDestination);
            rentDropOffLocation = view.findViewById(R.id.rentDropOffLocation);
            rentDropOffDate = view.findViewById(R.id.rentDropOffDate);
            rentDropOffTime = view.findViewById(R.id.rentDropOffTime);
            rentalReferenceNumber = view.findViewById(R.id.rentalReferenceNumber);
            rentStatus = view.findViewById(R.id.rentStatus);
            rentPrice = view.findViewById(R.id.rentPrice);
            rentPriceLabel = view.findViewById(R.id.rentPriceLabel);
            itemNumber = view.findViewById(R.id.itemNumber);
        }
    }

}
