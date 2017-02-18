package com.mechanicape.loraap;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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

    }
}
