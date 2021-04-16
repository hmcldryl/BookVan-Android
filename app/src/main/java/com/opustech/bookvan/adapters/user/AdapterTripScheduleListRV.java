package com.opustech.bookvan.adapters.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.TripSchedule;

public class AdapterTripScheduleListRV extends FirestoreRecyclerAdapter<TripSchedule, AdapterTripScheduleListRV.TripScheduleHolder> {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference partnersReference;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterTripScheduleListRV(@NonNull FirestoreRecyclerOptions<TripSchedule> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TripScheduleHolder holder, int position, @NonNull TripSchedule model) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        partnersReference = firebaseFirestore.collection("partners");

        String time_queue = model.getTime_queue();
        String time_depart = model.getTime_depart();

        holder.timeQueue.setText(time_queue);
        holder.timeDepart.setText(time_depart);

    }

    @NonNull
    @Override
    public TripScheduleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_route_schedule_item_layout, parent, false);
        return new TripScheduleHolder(view);
    }


    class TripScheduleHolder extends RecyclerView.ViewHolder {
        TextView timeQueue, timeDepart, vanCompany;

        public TripScheduleHolder(View view) {
            super(view);
            timeQueue = view.findViewById(R.id.timeQueue);
            timeDepart = view.findViewById(R.id.timeDepart);
            vanCompany = view.findViewById(R.id.vanCompany);
        }
    }

}
