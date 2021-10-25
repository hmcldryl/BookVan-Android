package com.opustech.bookvan.adapters.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.Schedule;
import com.opustech.bookvan.model.TripSchedule;

public class AdapterScheduleListRV extends FirestoreRecyclerAdapter<Schedule, AdapterScheduleListRV.ScheduleHolder> {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference partnersReference;

    private final Context context;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterScheduleListRV(@NonNull FirestoreRecyclerOptions<Schedule> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ScheduleHolder holder, int position, @NonNull Schedule model) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        partnersReference = firebaseFirestore.collection("partners");

        String uid = model.getVan_company_uid();
        String route = model.getRoute_from() + " to " + model.getRoute_to();
        String route_from = model.getRoute_from() + " to";
        String route_to = model.getRoute_to();
        double price = model.getPrice();

        partnersReference.document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Glide.with(context)
                                    .load(task.getResult().getString("photo_url"))
                                    .into(holder.vanCompanyPhoto);
                            holder.vanCompany.setText(task.getResult().getString("name"));

                            boolean disabled = task.getResult().getBoolean("account_disabled");

                            if (!disabled) {
                                holder.routeDescriptionFrom.setText(route_from);
                                holder.routeDescriptionTo.setText(route_to);
                                holder.price.setText(String.valueOf(price));

                                holder.item.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        final AlertDialog alertDialog = builder.create();
                                        if (!alertDialog.isShowing()) {
                                            final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                            final View dialogView = inflater.inflate(R.layout.dialog_user_trip_schedule_list, null);
                                            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            alertDialog.setCancelable(true);
                                            alertDialog.setView(dialogView);

                                            TextView tripRoute = dialogView.findViewById(R.id.tripRoute);
                                            RecyclerView tripScheduleListRV = dialogView.findViewById(R.id.tripScheduleListRV);

                                            tripRoute.setText(route);

                                            Query query = getSnapshots().getSnapshot(holder.getAdapterPosition())
                                                    .getReference()
                                                    .collection("schedules")
                                                    .orderBy("time_queue", Query.Direction.ASCENDING)
                                                    .orderBy("time_depart", Query.Direction.ASCENDING);

                                            FirestoreRecyclerOptions<TripSchedule> options = new FirestoreRecyclerOptions.Builder<TripSchedule>()
                                                    .setQuery(query, TripSchedule.class)
                                                    .build();

                                            AdapterUserTripScheduleListRV adapterTransportTripScheduleListRV = new AdapterUserTripScheduleListRV(options, context);
                                            LinearLayoutManager manager = new LinearLayoutManager(context);

                                            tripScheduleListRV.setHasFixedSize(true);
                                            tripScheduleListRV.setLayoutManager(manager);
                                            tripScheduleListRV.setAdapter(adapterTransportTripScheduleListRV);

                                            adapterTransportTripScheduleListRV.startListening();

                                            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                                @Override
                                                public void onCancel(DialogInterface dialog) {
                                                    adapterTransportTripScheduleListRV.stopListening();
                                                }
                                            });

                                            alertDialog.show();
                                        }
                                    }
                                });
                            } else {
                                holder.layout.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }

    @NonNull
    @Override
    public ScheduleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_route_schedule_item_layout, parent, false);
        return new ScheduleHolder(view);
    }


    static class ScheduleHolder extends RecyclerView.ViewHolder {
        final RelativeLayout layout;
        final CardView item;
        final ImageView vanCompanyPhoto;
        final TextView price;
        final TextView routeDescriptionFrom;
        final TextView routeDescriptionTo;
        final TextView vanCompany;

        public ScheduleHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout);
            item = view.findViewById(R.id.item);
            price = view.findViewById(R.id.price);
            routeDescriptionFrom = view.findViewById(R.id.routeDescriptionFrom);
            routeDescriptionTo = view.findViewById(R.id.routeDescriptionTo);
            vanCompany = view.findViewById(R.id.vanCompany);
            vanCompanyPhoto = view.findViewById(R.id.vanCompanyPhoto);
        }
    }

}
