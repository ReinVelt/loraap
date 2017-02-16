package com.mechanicape.loraap;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by rein on 7-2-17.
 */
public class MessagesAdapter extends ArrayAdapter<LoraMessage> {


    public MessagesAdapter(Context context, ArrayList<LoraMessage> messages) {

        super(context, 0, messages);
    }



    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        LoraMessage message = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_message, parent, false);
        }
        // Lookup view for data population
        TextView msgDevAddr = (TextView) convertView.findViewById(R.id.msgDevAddr);
        TextView msgPayload = (TextView) convertView.findViewById(R.id.msgPayload);
        TextView msgDatum = (TextView) convertView.findViewById(R.id.msgDatum);
        TextView msgTime = (TextView) convertView.findViewById(R.id.msgTime);
        TextView msgRSSI = (TextView) convertView.findViewById(R.id.msgRSSI);
        // Populate the data into the template view using the data object
        msgDevAddr.setText(toHex(message.RXPKObject.DevAddr));
        msgPayload.setText(toHex(message.RXPKObject.FRMPayload).substring(0,14));
        //msgDatum.setText(Long.toString(message.datum));
        Date date=new Date(message.datum*1000);
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getContext());
        msgDatum.setText(dateFormat.format(date));
        msgTime.setText(timeFormat.format(date));
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