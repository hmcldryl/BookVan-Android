package com.opustech.bookvan.adapters.transport;

import android.content.Context;
import android.graphics.Color;
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
import com.opustech.bookvan.model.TripSchedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class AdapterTransportTripScheduleListRV extends FirestoreRecyclerAdapter<TripSchedule, AdapterTransportTripScheduleListRV.TripScheduleHolder> {

    private FirebaseFirestore firebaseFirestore;

    private final Context context;

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
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected void onBindViewHolder(@NonNull TripScheduleHolder holder, int position, @NonNull TripSchedule model) {
        holder.setIsRecyclable(false);

        firebaseFirestore = FirebaseFirestore.getInstance();

        String time_queue = model.getTime_queue();
        String time_depart = model.getTime_depart();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

        if (time_queue.equals("00:00")) {
            holder.timeQueue.setText("12:00 MN");
        } else if (time_queue.equals("12:00")) {
            holder.timeQueue.setText("12:00 NN");
        } else {
            try {
                Date date_queue = simpleDateFormat.parse(time_queue);
                holder.timeQueue.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(date_queue));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (time_depart.equals("00:00")) {
            holder.timeDepart.setText("12:00 MN");
        } else if (time_depart.equals("12:00")) {
            holder.timeDepart.setText("12:00 NN");
        } else {
            try {
                Date date_depart = simpleDateFormat.parse(time_depart);
                holder.timeDepart.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(date_depart));
            } catch (ParseException e) {
                e.printStackTrace();
            }
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
                            final ACProgressFlower dialog = new ACProgressFlower.Builder(context)
                                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                                    .themeColor(context.getResources().getColor(R.color.white))
                                    .fadeColor(Color.DKGRAY).build();
                            dialog.show();
                            getSnapshots().getSnapshot(holder.getAdapterPosition()).getReference()
                                    .delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                dialog.dismiss();
                                                Toast.makeText(context, "Success.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                dialog.dismiss();
                                                Toast.makeText(context, "Failed.", Toast.LENGTH_SHORT).show();
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

    static class TripScheduleHolder extends RecyclerView.ViewHolder {
        final ImageButton options;
        final TextView timeQueue;
        final TextView timeDepart;

        public TripScheduleHolder(View view) {
            super(view);
            options = view.findViewById(R.id.options);
            timeQueue = view.findViewById(R.id.timeQueue);
            timeDepart = view.findViewById(R.id.timeDepart);
        }
    }

}
