package com.opustech.bookvan.adapters.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

public class AdapterScheduleListRV extends FirestoreRecyclerAdapter<Schedule, AdapterScheduleListRV.ScheduleHolder> {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference partnersReference;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public AdapterScheduleListRV(@NonNull FirestoreRecyclerOptions<Schedule> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ScheduleHolder holder, int position, @NonNull Schedule model) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        partnersReference = firebaseFirestore.collection("partners");

        String time_queue = model.getTime_queue();
        String time_depart = model.getTime_queue();
        String van_company_uid = model.getVan_company_uid();

        holder.timeQueue.setText(time_queue);
        holder.timeDepart.setText(time_depart);

        partnersReference.document(van_company_uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            holder.vanCompany.setText(task.getResult().getString("name"));
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


    class ScheduleHolder extends RecyclerView.ViewHolder {
        TextView timeQueue, timeDepart, vanCompany;

        public ScheduleHolder(View view) {
            super(view);
            timeQueue = view.findViewById(R.id.timeQueue);
            timeDepart = view.findViewById(R.id.timeDepart);
            vanCompany = view.findViewById(R.id.vanCompany);
        }
    }

}
