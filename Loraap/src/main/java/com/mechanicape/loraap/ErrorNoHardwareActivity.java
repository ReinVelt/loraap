package com.mechanicape.loraap;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by rein on 20-2-17.
 */
public class ErrorNoHardwareActivity extends Activity {

    private LoraManager lora;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.error_required);
    }
}