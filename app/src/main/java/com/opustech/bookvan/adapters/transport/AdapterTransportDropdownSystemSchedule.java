package com.opustech.bookvan.adapters.transport;

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
import com.opustech.bookvan.model.SystemSchedule;

import java.util.ArrayList;
import java.util.Locale;

public class AdapterTransportDropdownSystemSchedule extends ArrayAdapter<SystemSchedule> {

    public AdapterTransportDropdownSystemSchedule(@NonNull Context context, ArrayList<SystemSchedule> scheduleArrayList) {
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.transport_route_spinner_dropdown_item, parent, false);
        }
        TextView routeDescription = convertView.findViewById(R.id.routeDescription);
        TextView routePrice = convertView.findViewById(R.id.routePrice);
        SystemSchedule schedule = getItem(position);
        if (schedule != null) {
            String route_from = schedule.getRoute_from().equals("Puerto Princesa City") ? "PPC" : schedule.getRoute_from();
            String route_to = schedule.getRoute_to().equals("Puerto Princesa City") ? "PPC" : schedule.getRoute_to();

            String route = route_from + " to " + route_to;
            String price = getContext().getResources().getString(R.string.peso_sign) + String.format(Locale.ENGLISH, "%.2f", schedule.getPrice());
            routeDescription.setText(route);
            routePrice.setText(price);
        }
        return convertView;

    }
}
