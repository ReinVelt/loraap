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
        setContentView(R.layout.activity_detail);
        lora=new LoraManager(this);
        Long messageId = getIntent().getLongExtra("messageId",1);
        loadData(messageId);
    }

    protected void loadData(long messageId)
    {
        LoraMessage message=lora.getMessage(messageId);

    }
}
