package com.opustech.bookvan.adapters.transport;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.Schedule;

import java.util.Locale;

public class AdapterTransportTripScheduleListRV extends FirestoreRecyclerAdapter<Schedule, AdapterTransportTripScheduleListRV.ScheduleHolder> {

    private FirebaseFirestore firebaseFirestore;

    private Context context;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterTransportTripScheduleListRV(@NonNull FirestoreRecyclerOptions<Schedule> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ScheduleHolder holder, int position, @NonNull Schedule model) {
        firebaseFirestore = FirebaseFirestore.getInstance();

        String description = model.getRoute_from() + " to " + model.getRoute_to();
        String price = context.getResources().getString(R.string.peso_sign) + String.format(Locale.ENGLISH, "%.2f", model.getPrice());
        String time_queue = model.getTime_queue();
        String time_depart = model.getTime_queue();

        holder.routeDescription.setText(description);
        holder.routePrice.setText(price);
        holder.timeQueue.setText(time_queue);
        holder.timeDepart.setText(time_depart);

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
                            Toast.makeText(context, "Deleted.", Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
    }

    @NonNull
    @Override
    public ScheduleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transport_route_schedule_item_layout, parent, false);
        return new ScheduleHolder(view);
    }

    class ScheduleHolder extends RecyclerView.ViewHolder {
        TextView routeDescription, routePrice, timeQueue, timeDepart;
        ImageButton options;

        public ScheduleHolder(View view) {
            super(view);
            routeDescription = view.findViewById(R.id.routeDescription);
            routePrice = view.findViewById(R.id.routePrice);
            timeQueue = view.findViewById(R.id.timeQueue);
            timeDepart = view.findViewById(R.id.timeDepart);
            options = view.findViewById(R.id.options);
        }
    }

}
