package com.opustech.bookvan.ui.adapters.transport;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.TransportCompany;
import com.opustech.bookvan.transport.TransportProfileActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterTransportPartnerListRV extends FirestoreRecyclerAdapter<TransportCompany, AdapterTransportPartnerListRV.TransportCompanyHolder> {

    private Context context;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterTransportPartnerListRV(@NonNull FirestoreRecyclerOptions<TransportCompany> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TransportCompanyHolder holder, int position, @NonNull TransportCompany model) {

        String uid = model.getUid();
        String name = model.getName();
        String description = model.getDescription();
        String email = model.getEmail();
        String contact_number = model.getContact_number();
        String address = model.getAddress();
        String photo_url = model.getPhoto_url();
        String banner_url = model.getBanner_url();

        if (photo_url != null) {
            if (!photo_url.isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(photo_url)
                        .into(holder.companyPhoto);
            }
        }
        if (banner_url != null) {
            if (!banner_url.isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(banner_url)
                        .into(holder.companyBanner);
            }
        }
        holder.companyName.setText(name);
        holder.companyDescription.setText(description);

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TransportProfileActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("name", name);
                intent.putExtra("description", description);
                intent.putExtra("email", email);
                intent.putExtra("contact_number", contact_number);
                intent.putExtra("address", address);
                intent.putExtra("photo_url", photo_url);
                intent.putExtra("banner_url", banner_url);
                view.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public TransportCompanyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_confirmed_item_layout, parent, false);
        return new TransportCompanyHolder(view);
    }

    class TransportCompanyHolder extends RecyclerView.ViewHolder {

        TextView companyName, companyDescription;
        ImageView companyBanner;
        CircleImageView companyPhoto;
        MaterialCardView item;

        public TransportCompanyHolder(View view) {
            super(view);

            companyName = view.findViewById(R.id.companyName);
            companyDescription = view.findViewById(R.id.companyDescription);
            companyPhoto = view.findViewById(R.id.companyPhoto);
            companyBanner = view.findViewById(R.id.companyBanner);
            item = view.findViewById(R.id.item);

        }
    }
}
