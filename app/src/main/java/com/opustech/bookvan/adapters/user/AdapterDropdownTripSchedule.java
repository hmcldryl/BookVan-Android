package com.opustech.bookvan.adapters.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.opustech.bookvan.R;
import com.opustech.bookvan.model.Schedule;

import java.util.ArrayList;
import java.util.Locale;

public class AdapterDropdownTripSchedule extends ArrayAdapter<Schedule> {
    public AdapterDropdownTripSchedule(@NonNull Context context, ArrayList<Schedule> scheduleArrayList) {
        super(context, 0, scheduleArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.route_spinner_dropdown_item, parent, false);
        }
        TextView routeDescription = convertView.findViewById(R.id.routeDescription);
        TextView routeDepart = convertView.findViewById(R.id.routeDepart);
        TextView routePrice = convertView.findViewById(R.id.routePrice);
        Schedule schedule = getItem(position);
        if (schedule != null) {
            String route = schedule.getRoute_from() + " to " + schedule.getRoute_to();
            String price = getContext().getResources().getString(R.string.peso_sign) + String.format(Locale.ENGLISH, "%.2f", schedule.getPrice());
            routeDescription.setText(route);
            routeDepart.setText(schedule.getTime_depart());
            routePrice.setText(price);
        }
        return convertView;

    }
}
