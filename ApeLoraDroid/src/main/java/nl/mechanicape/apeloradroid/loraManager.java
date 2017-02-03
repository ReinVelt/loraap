package nl.mechanicape.apeloradroid;

import org.json.JSONObject;

/**
 * Created by rein on 1-2-17.
 */
public class loraManager {

    private JSONObject messages[];
    private int msgCounter=0;

    public void addMessage(JSONObject jsonMessage)
    {
        messages[msgCounter]=jsonMessage;
        msgCounter++;
    }

    public JSONObject[] getMessages()
    {
        return messages;
    }

}
