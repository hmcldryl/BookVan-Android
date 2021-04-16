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
import com.opustech.bookvan.model.TripSchedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AdapterDropdownScheduleTime extends ArrayAdapter<TripSchedule> {
    public AdapterDropdownScheduleTime(@NonNull Context context, ArrayList<TripSchedule> scheduleArrayList) {
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.time_spinner_dropdown_item, parent, false);
        }
        TextView timeDepart = convertView.findViewById(R.id.timeDepart);
        TripSchedule schedule = getItem(position);
        if (schedule != null) {
            String time_depart = schedule.getTime_depart();
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                Date date_depart = simpleDateFormat.parse(time_depart);
                timeDepart.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(date_depart));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return convertView;

    }
}
