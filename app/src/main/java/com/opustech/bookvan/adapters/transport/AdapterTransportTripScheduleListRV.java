package com.opustech.bookvan.adapters.transport;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.opustech.bookvan.model.TripSchedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdapterTransportTripScheduleListRV extends FirestoreRecyclerAdapter<TripSchedule, AdapterTransportTripScheduleListRV.TripScheduleHolder> {

    private FirebaseFirestore firebaseFirestore;

    private Context context;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterTransportTripScheduleListRV(@NonNull FirestoreRecyclerOptions<TripSchedule> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull TripScheduleHolder holder, int position, @NonNull TripSchedule model) {
        firebaseFirestore = FirebaseFirestore.getInstance();

        String time_queue = model.getTime_queue();
        String time_depart = model.getTime_depart();

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            Date date_queue = simpleDateFormat.parse(time_queue);
            Date date_depart = simpleDateFormat.parse(time_depart);
            holder.timeQueue.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(date_queue));
            holder.timeDepart.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(date_depart));
        } catch (ParseException e) {
            e.printStackTrace();
        }

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
                            getSnapshots().getSnapshot(position).getReference()
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

    @NonNull
    @Override
    public TripScheduleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transport_route_trip_schedule_item_layout, parent, false);
        return new TripScheduleHolder(view);
    }

    class TripScheduleHolder extends RecyclerView.ViewHolder {
        ImageButton options;
        TextView timeQueue, timeDepart;

        public TripScheduleHolder(View view) {
            super(view);
            options = view.findViewById(R.id.options);
            timeQueue = view.findViewById(R.id.timeQueue);
            timeDepart = view.findViewById(R.id.timeDepart);
        }
    }

}
