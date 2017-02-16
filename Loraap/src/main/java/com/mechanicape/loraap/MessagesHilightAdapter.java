package com.mechanicape.loraap;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by rein on 7-2-17.
 */
public class MessagesHilightAdapter extends ArrayAdapter<LoraMessage> {


    public MessagesHilightAdapter(Context context, ArrayList<LoraMessage> messages) {

        super(context, 0, messages);
    }



    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        final LoraMessage message = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_message_detail, parent, false);
        }
        // Lookup view for data population
        TextView msgDevAddr = (TextView) convertView.findViewById(R.id.msgDevAddr);
        TextView msgPayload = (TextView) convertView.findViewById(R.id.msgPayload);
        TextView msgDatum = (TextView) convertView.findViewById(R.id.msgDatum);
        TextView msgRSSI = (TextView) convertView.findViewById(R.id.msgRSSI);
        // Populate the data into the template view using the data object
        msgDevAddr.setText(toHex(message.RXPKObject.DevAddr));
        msgPayload.setText(toHex(message.RXPKObject.FRMPayload).substring(0,14)+"... (size="+Integer.toString(message.RXPKObject.FRMPayload.length)+")");
        msgDatum.setText(new Date(message.datum*1000).toLocaleString());
        msgRSSI.setText(Integer.toString(message.rssi));

        // Return the completed view to render on screen
        return convertView;

    }



    public static String toHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : in) {
            builder.append(String.format("%02X", b));
        }
        return builder.toString();
    }








}