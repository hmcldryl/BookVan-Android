package com.opustech.bookvan.adapters.admin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.Rental;
import com.opustech.bookvan.ui.transport.TransportProfileActivity;

import org.imaginativeworld.whynotimagecarousel.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.ImageCarousel;

import java.util.ArrayList;
import java.util.List;

public class AdapterRentalsAdminListRV extends FirestoreRecyclerAdapter<Rental, AdapterRentalsAdminListRV.RentalHolder> {

    private Context context;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterRentalsAdminListRV(@NonNull FirestoreRecyclerOptions<Rental> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull RentalHolder holder, int position, @NonNull Rental model) {
        List<String> photo_url = model.getPhoto_url();
        String owner = model.getOwner();
        String van_description = model.getVan_description();
        String price = String.valueOf(model.getPrice());

        List<CarouselItem> data = new ArrayList<>();
        for (int i = 0; i < photo_url.size() - 1; i++) {
            data.add(new CarouselItem(photo_url.get(i)));
        }

        holder.rentalsImageCarousel.addData(data);
        holder.rentalsVanDescription.setText(van_description);
        holder.rentalsVanOwner.setText(owner);
        holder.rentalsPrice.setText(price);


        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TransportProfileActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public RentalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rent_item_layout, parent, false);
        return new RentalHolder(view);
    }

    class RentalHolder extends RecyclerView.ViewHolder {

        TextView rentalsVanModel, rentalsVanOwner, rentalsVanDescription, rentalsPrice;
        ImageCarousel rentalsImageCarousel;
        MaterialCardView item;

        public RentalHolder(View view) {
            super(view);
            item = view.findViewById(R.id.item);
            rentalsImageCarousel = view.findViewById(R.id.rentalsImageCarousel);
            rentalsVanModel = view.findViewById(R.id.rentalsVanModel);
            rentalsVanOwner = view.findViewById(R.id.rentalsVanOwner);
            rentalsVanDescription = view.findViewById(R.id.rentalsVanDescription);
            rentalsPrice = view.findViewById(R.id.price);

        }
    }
}
