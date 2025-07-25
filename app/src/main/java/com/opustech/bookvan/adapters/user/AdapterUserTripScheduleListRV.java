package com.opustech.bookvan.adapters.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.TripSchedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdapterUserTripScheduleListRV extends FirestoreRecyclerAdapter<TripSchedule, AdapterUserTripScheduleListRV.TripScheduleHolder> {

    private FirebaseFirestore firebaseFirestore;

    private final Context context;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterUserTripScheduleListRV(@NonNull FirestoreRecyclerOptions<TripSchedule> options, Context context) {
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

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            Date date_queue = simpleDateFormat.parse(time_queue);
            Date date_depart = simpleDateFormat.parse(time_depart);
            holder.timeQueue.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(date_queue));
            holder.timeDepart.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(date_depart));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @NonNull
    @Override
    public TripScheduleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_route_trip_schedule_item_layout, parent, false);
        return new TripScheduleHolder(view);
    }

    static class TripScheduleHolder extends RecyclerView.ViewHolder {
        final TextView timeQueue;
        final TextView timeDepart;

        public TripScheduleHolder(View view) {
            super(view);
            timeQueue = view.findViewById(R.id.timeQueue);
            timeDepart = view.findViewById(R.id.timeDepart);
        }
    }

}
