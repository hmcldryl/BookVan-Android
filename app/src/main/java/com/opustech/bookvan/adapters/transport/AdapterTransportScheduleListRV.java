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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.Schedule;
import com.opustech.bookvan.model.TripSchedule;

import java.util.Locale;

public class AdapterTransportScheduleListRV extends FirestoreRecyclerAdapter<Schedule, AdapterTransportScheduleListRV.ScheduleHolder> {

    private FirebaseFirestore firebaseFirestore;

    private final Context context;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterTransportScheduleListRV(@NonNull FirestoreRecyclerOptions<Schedule> options, Context context) {
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
    protected void onBindViewHolder(@NonNull ScheduleHolder holder, int position, @NonNull Schedule model) {
        holder.setIsRecyclable(false);

        firebaseFirestore = FirebaseFirestore.getInstance();

        String route_from = model.getRoute_from().equals("Puerto Princesa City") ? "PPC" : model.getRoute_from();
        String route_to = model.getRoute_to().equals("Puerto Princesa City") ? "PPC" : model.getRoute_to();

        String description = route_from + " to " + route_to;
        String price = context.getResources().getString(R.string.peso_sign) + String.format(Locale.ENGLISH, "%.2f", model.getPrice());

        holder.routeDescription.setText(description);
        holder.routePrice.setText(price);

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final AlertDialog alertDialog = builder.create();
                if (!alertDialog.isShowing()) {
                    final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View view = inflater.inflate(R.layout.dialog_trip_schedule_list, null);
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.setCancelable(true);
                    alertDialog.setView(view);

                    TextView tripRoute = view.findViewById(R.id.tripRoute);
                    tripRoute.setText(description);

                    Query query = getSnapshots().getSnapshot(holder.getAdapterPosition())
                            .getReference()
                            .collection("schedules")
                            .orderBy("time_queue", Query.Direction.ASCENDING)
                            .orderBy("time_depart", Query.Direction.ASCENDING);

                    FirestoreRecyclerOptions<TripSchedule> options = new FirestoreRecyclerOptions.Builder<TripSchedule>()
                            .setQuery(query, TripSchedule.class)
                            .build();

                    AdapterTransportTripScheduleListRV adapterTransportTripScheduleListRV = new AdapterTransportTripScheduleListRV(options, context);
                    LinearLayoutManager manager = new LinearLayoutManager(context);

                    RecyclerView tripScheduleListRV = view.findViewById(R.id.tripScheduleListRV);

                    tripScheduleListRV.setHasFixedSize(true);
                    tripScheduleListRV.setLayoutManager(manager);
                    tripScheduleListRV.setAdapter(adapterTransportTripScheduleListRV);

                    adapterTransportTripScheduleListRV.startListening();

                    alertDialog.show();

                    alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            adapterTransportTripScheduleListRV.stopListening();
                        }
                    });
                }
            }
        });
    }

    @NonNull
    @Override
    public ScheduleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transport_route_schedule_item_layout, parent, false);
        return new ScheduleHolder(view);
    }

    static class ScheduleHolder extends RecyclerView.ViewHolder {
        final LinearLayout item;
        final TextView routeDescription;
        final TextView routePrice;
        final TextView timeQueue;
        final TextView timeDepart;

        public ScheduleHolder(View view) {
            super(view);
            item = view.findViewById(R.id.item);
            routeDescription = view.findViewById(R.id.routeDescription);
            routePrice = view.findViewById(R.id.routePrice);
            timeQueue = view.findViewById(R.id.timeQueue);
            timeDepart = view.findViewById(R.id.timeDepart);
        }
    }

}
