package com.opustech.bookvan.adapters.transport;

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
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.TransportCompany;
import com.opustech.bookvan.ui.transport.TransportCompanyProfileActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterTransportPartnerListRV extends FirestoreRecyclerAdapter<TransportCompany, AdapterTransportPartnerListRV.TransportCompanyHolder> {

    private final Context context;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterTransportPartnerListRV(@NonNull FirestoreRecyclerOptions<TransportCompany> options, Context context) {
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
    protected void onBindViewHolder(@NonNull TransportCompanyHolder holder, int position, @NonNull TransportCompany model) {
        holder.setIsRecyclable(false);

        String uid = model.getUid();
        String photo_url = model.getPhoto_url();
        String banner_url = model.getBanner_url();
        String name = model.getName();
        String description = model.getDescription();

        if (photo_url != null) {
            if (!photo_url.isEmpty()) {
                Glide.with(context)
                        .load(photo_url)
                        .into(holder.companyPhoto);
            }
        }

        if (banner_url != null) {
            if (!banner_url.isEmpty()) {
                Glide.with(context)
                        .load(banner_url)
                        .into(holder.companyBanner);
            }
        }

        holder.companyName.setText(name);
        holder.companyDescription.setText(description);

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TransportCompanyProfileActivity.class);
                intent.putExtra("uid", uid);
                view.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public TransportCompanyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transport_partners_item_layout, parent, false);
        return new TransportCompanyHolder(view);
    }

    static class TransportCompanyHolder extends RecyclerView.ViewHolder {

        final TextView companyName;
        final TextView companyDescription;
        final ImageView companyBanner;
        final CircleImageView companyPhoto;
        final MaterialCardView item;

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
