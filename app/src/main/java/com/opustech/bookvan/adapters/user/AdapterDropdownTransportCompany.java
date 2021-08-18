package com.opustech.bookvan.adapters.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.TransportCompany;

import java.util.ArrayList;

public class AdapterDropdownTransportCompany extends ArrayAdapter<TransportCompany> {
    public AdapterDropdownTransportCompany(@NonNull Context context, ArrayList<TransportCompany> transportArrayList) {
        super(context, 0, transportArrayList);
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.transport_spinner_dropdown_item, parent, false);
        }
        ImageView transportPhoto = convertView.findViewById(R.id.transportPhoto);
        TextView transportName = convertView.findViewById(R.id.transportName);
        TransportCompany transportCompany = getItem(position);
        if (transportCompany != null) {
            String name = transportCompany.getName();
            String photoUrl = transportCompany.getPhoto_url();
            transportName.setText(name);
            if (photoUrl != null) {
                if (!photoUrl.isEmpty()) {
                    Glide.with(convertView.getContext())
                            .load(photoUrl)
                            .into(transportPhoto);
                }
            }
        }
        return convertView;

    }
}
