package nl.mechanicape.apeloradroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;

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
        TextView msgTitle = (TextView) convertView.findViewById(R.id.msgTitle);
        TextView msgData = (TextView) convertView.findViewById(R.id.msgData);
        // Populate the data into the template view using the data object
        msgTitle.setText(" "+toHex(message.RXPKObject.DevAddr));
        msgData.setText(" "+toHex(message.RXPKObject.FPort));
        // Return the completed view to render on screen
        return convertView;

    }



    public static String toHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : in) {
            builder.append(String.format("%02x ", b));
        }
        return builder.toString();
    }

    public static String toHex(byte b) {

        return String.format("%02x", b);
    }



}