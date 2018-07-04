package com.example.root.securityalert;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class UsersAdapter extends ArrayAdapter<ViewHolder> {


    public UsersAdapter(Context context, ArrayList<ViewHolder> users) {

        super(context, 0, users);

    }

    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position

        ViewHolder user = getItem(position);


        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bt_details, parent, false);

        }

        // Lookup view for data population

        TextView tvName = (TextView) convertView.findViewById(R.id.DeviceName);

        TextView tvHome = (TextView) convertView.findViewById(R.id.DeviceAddress);

        // Populate the data into the template view using the data object

        tvName.setText(user.deviceName);

        tvHome.setText(user.deviceAddress);

        // Return the completed view to render on screen

        return convertView;

    }

}