package com.mechanicape.loraap;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailMsgActivity extends Activity {

    private LoraManager lora;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Long messageId = getIntent().getLongExtra("messageId",1);
        updateDisplay(messageId);
    }

    protected void updateDisplay(long messageId)
    {
        lora=new LoraManager(this);
        LoraMessage message=lora.getMessage(messageId);
        setContentView(R.layout.activity_detail);
        TextView x1 = (TextView) findViewById(R.id.msgdChn); x1.setText(Integer.toString(message.chan));
        TextView x2 = (TextView) findViewById(R.id.msgdDevAddr); x2.setText(toHex(message.RXPKObject.DevAddr));
        TextView x3 = (TextView) findViewById(R.id.msgdCodr); x3.setText(message.codr);
        TextView x4 = (TextView) findViewById(R.id.msgdDatr); x4.setText(message.datr);
        //TextView x5 = (TextView) findViewById(R.id.msgdDatum); x5.setText(Long.toString(message.datum));
        TextView x6 = (TextView) findViewById(R.id.msgdFcnt);x6.setText(toHex(message.RXPKObject.Fcnt));
        TextView x7 = (TextView) findViewById(R.id.msgdFctrl);x7.setText(Integer.toString(message.RXPKObject.Fctrl));
        TextView x8 = (TextView) findViewById(R.id.msgdFport);x8.setText(Integer.toString(message.RXPKObject.FPort));
        TextView x9 = (TextView) findViewById(R.id.msgdFreq);x9.setText(Double.toString(message.freq));
        TextView xA = (TextView) findViewById(R.id.msgdFRMPayload);xA.setText(toHex(message.RXPKObject.FRMPayload));
        TextView xB = (TextView) findViewById(R.id.msgdLsnr);xB.setText(Integer.toString(message.lsnr));
        TextView xC = (TextView) findViewById(R.id.msgdMHDR);xC.setText(Integer.toString(message.RXPKObject.MHDR));
        TextView xD = (TextView) findViewById(R.id.msgdMIC);xD.setText(toHex(message.RXPKObject.MIC));
        TextView xE = (TextView) findViewById(R.id.msgdModu);xE.setText(message.modu);
        TextView xF = (TextView) findViewById(R.id.msgdPHYPayload);xF.setText(toHex(message.RXPKObject.PHYPayload));
        TextView xG = (TextView) findViewById(R.id.msgdRfch);xG.setText(Integer.toString(message.rfch));
        TextView xH = (TextView) findViewById(R.id.msgdRSSI);xH.setText(Integer.toString(message.rssi));
        TextView xI = (TextView) findViewById(R.id.msgdSize);xI.setText(Integer.toString(message.size));
        TextView xJ = (TextView) findViewById(R.id.msgdStat);xJ.setText(Integer.toString(message.stat));
        TextView xK = (TextView) findViewById(R.id.msgdTmst);xK.setText(Long.toString(message.tmst));




    }
    public static String toHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : in) {
            builder.append(String.format("%02X ", b));
        }
        return builder.toString();
    }
}
