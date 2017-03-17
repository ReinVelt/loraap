package com.mechanicape.loraap;

import org.json.JSONObject;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by rein on 1-2-17.
 */
public class LoraManager {

    private DatabaseHelper db;
    private ArrayList<LoraMessage> messages=new ArrayList<LoraMessage>();



    public LoraManager(Context context) {
        db = new DatabaseHelper(context);
        messages=getMessages();
    }



    public void addMessage(JSONObject jsonMessage)
    {
        LoraMessage message=new LoraMessage(jsonMessage);
        long id=db.saveRxpk(message.getContentValues());
        messages.add(message);
        //return id;
    }

    public LoraMessage getMessage(long messageId)
    {
        return db.getMessage(messageId);
    }


    public ArrayList<LoraMessage> getMessages()
    {
        return messages;
    }

}
