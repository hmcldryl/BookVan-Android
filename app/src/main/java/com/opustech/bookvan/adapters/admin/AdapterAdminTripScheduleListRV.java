package com.opustech.bookvan.adapters.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.Schedule;

import java.util.Locale;

public class AdapterAdminTripScheduleListRV extends FirestoreRecyclerAdapter<Schedule, AdapterAdminTripScheduleListRV.ScheduleHolder> {

    private FirebaseFirestore firebaseFirestore;

    private Context context;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterAdminTripScheduleListRV(@NonNull FirestoreRecyclerOptions<Schedule> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ScheduleHolder holder, int position, @NonNull Schedule model) {
        firebaseFirestore = FirebaseFirestore.getInstance();

        String route_from = model.getRoute_from().equals("Puerto Princesa City") ? "PPC" : model.getRoute_from();
        String route_to = model.getRoute_to().equals("Puerto Princesa City") ? "PPC" : model.getRoute_to();

        String description = route_from + " to " + route_to;
        String category = model.getCategory();
        String price = context.getResources().getString(R.string.peso_sign) + String.format(Locale.ENGLISH, "%.2f", model.getPrice());

        holder.routeDescription.setText(description);
        holder.routePrice.setText(price);
        holder.routeCategory.setText(capitalizeWords(category));

        holder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.transport_trip_schedule_item_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.btnDelete) {
                            getSnapshots().getSnapshot(position)
                                    .getReference()
                                    .delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, "Success.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
    }

    public static String capitalizeWords(String str) {
        String[] words = str.split("\\s");
        String result = "";
        for (String w : words) {
            String a = w.substring(0, 1);
            String b = w.substring(1);
            result += a.toUpperCase() + b + " ";
        }
        return result.trim();
    }

    @NonNull
    @Override
    public ScheduleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_route_schedule_item_layout, parent, false);
        return new ScheduleHolder(view);
    }

    class ScheduleHolder extends RecyclerView.ViewHolder {
        TextView routeDescription, routePrice, routeCategory;
        ImageButton options;

        public ScheduleHolder(View view) {
            super(view);
            routeDescription = view.findViewById(R.id.routeDescription);
            routeCategory = view.findViewById(R.id.routeCategory);
            routePrice = view.findViewById(R.id.routePrice);
            options = view.findViewById(R.id.options);
        }
    }

}
