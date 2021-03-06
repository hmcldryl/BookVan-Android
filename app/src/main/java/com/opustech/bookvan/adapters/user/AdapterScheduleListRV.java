package com.opustech.bookvan.adapters.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.Schedule;

public class AdapterScheduleListRV extends FirestoreRecyclerAdapter<Schedule, AdapterScheduleListRV.ScheduleHolder> {

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
        String time_pila = model.getTime_pila();
        String time_alis = model.getTime_alis();
        String van_company_uid = model.getVan_company_uid();

        holder.timePila.setText(time_pila);
        holder.timeAlis.setText(time_alis);
        holder.vanCompany.setText(van_company_uid);
    }

    @NonNull
    @Override
    public ScheduleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_item_layout, parent, false);
        return new ScheduleHolder(view);
    }


    class ScheduleHolder extends RecyclerView.ViewHolder {
        TextView timePila, timeAlis, vanCompany;

        public ScheduleHolder(View view) {
            super(view);
            timePila = view.findViewById(R.id.timePila);
            timeAlis = view.findViewById(R.id.timeAlis);
            vanCompany = view.findViewById(R.id.vanCompany);
        }
    }

}
