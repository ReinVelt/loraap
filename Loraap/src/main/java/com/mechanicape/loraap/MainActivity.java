package com.mechanicape.loraap;

import android.app.Activity;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.content.Context;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import android.hardware.usb.UsbDeviceConnection;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends Activity {

    private final String TAG = MainActivity.class.getSimpleName();

    /**
     * The device currently in use, or {@code null}.
     */
    private static UsbSerialPort sPort = null;
    private UsbSerialDriver mSerialDevice;

    private UsbManager mUsbManager;
    private TextView mTitleTextView;
    private ListView mListview;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private SerialInputOutputManager mSerialIoManager;
    private LoraManager lora;
    private boolean isUpdated=true;
    private LoraMessage selectedMessage;
    private Button button;
    private ListView detailView;
    private ArrayList<LoraMessage> messages;


    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    Log.d(TAG, "Runner stopped.");
                }

                @Override
                public void onNewData(final byte[] data) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.updateReceivedData(data);
                        }
                    });
                }
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);


        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        lora=new LoraManager(this);
        messages = lora.getMessages();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            return rootView;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopIoManager();
        if (sPort != null) {
            try {
                sPort.close();
            } catch (IOException e) {
                // Ignore.
            }
            sPort = null;
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        messages = lora.getMessages();
        //myLocationOverlay.enableMyLocation();
        try{
            sPort=UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager).get(0).getPorts().get(0);
        } catch(Exception e){ }
        mTitleTextView = (TextView) findViewById(R.id.textTitle);
        mListview = (ListView) findViewById(R.id.messageList);
        mTitleTextView.setText("Loraap");

        Log.d(TAG, "Resumed, mSerialDevice=" + mSerialDevice);
        if (sPort == null) {
            mTitleTextView.setText("No Lora receiver connected. Please plug it in.");
            updateDisplay();
        } else {
            final UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

            UsbDeviceConnection connection = usbManager.openDevice(sPort.getDriver().getDevice());
            if (connection == null) {
                mTitleTextView.setText("Opening device failed");
                return;
            }

            try {
                sPort.open(connection);
                sPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);


            } catch (IOException e) {
                Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
                mTitleTextView.setText("Error opening device: " + e.getMessage());
                try {
                    sPort.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                sPort = null;
                return;
            }
            mTitleTextView.setText("Serial device: " + sPort.getClass().getSimpleName());
        }
        onDeviceStateChange();


    }



    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (sPort != null) {
            Log.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(sPort, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    //private void showErrorHardwareRequired()
    //{
    //    Intent intent = new Intent(getBaseContext(),ErrorNoHardwareActivity.class);
    //    startActivity(intent);
    //}

    private void updateDisplay()
    {
       if (isUpdated)
       {

           messages = lora.getMessages();

           if (messages.size()>-1)
           {
               ListView listView = (ListView) findViewById(R.id.messageList);
               MessagesAdapter adapter=new MessagesAdapter(getApplicationContext(),messages);
               listView.setAdapter(adapter);

               ArrayList<LoraMessage> messagesDetail=new ArrayList<LoraMessage>();
               detailView = (ListView) findViewById(R.id.messageDetailList);
               MessagesHilightAdapter hlAdapter=new MessagesHilightAdapter(getApplicationContext(),messagesDetail);
               if (messages.size()>0)
               {
                   hlAdapter.add(messages.get(messages.size()-1));
               }

               detailView.setAdapter(hlAdapter);

               listView.setVisibility(View.VISIBLE);
               detailView.setVisibility(View.VISIBLE);



               listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                   public void onItemClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                       ArrayList<LoraMessage> messages = lora.getMessages();
                       ArrayList<LoraMessage> messagesDetail=new ArrayList<LoraMessage>();
                       //ListView detailView = (ListView) findViewById(R.id.messageDetailList);
                       MessagesHilightAdapter hlAdapter=new MessagesHilightAdapter(getApplicationContext(),messagesDetail);
                       selectedMessage=messages.get(position);
                       hlAdapter.add(selectedMessage);
                       detailView.setAdapter(hlAdapter);
                   }
               });

               listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                   public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                       ArrayList<LoraMessage> messages = lora.getMessages();
                       selectedMessage=messages.get(position);
                       Long messageId=selectedMessage.id;
                       Intent intent = new Intent(getBaseContext(), DetailMsgActivity.class);
                       intent.putExtra("messageId", messageId);
                       startActivityForResult(intent,0);
                       return true;
                   }
               });



           }
           //else
           //{
           //     showErrorHardwareRequired();
           //}

           //adapter.clear();
          // adapter.addAll(messages);
       }
       isUpdated=false;
    }




    private void updateReceivedData(byte[] data) {

        try {
            String Data=new String(data);
            if (Data.charAt(0)=='{')
            {
                JSONObject jsonObj = new JSONObject(Data);
                if (jsonObj.length()>0)
                {
                    lora.addMessage(jsonObj);
                    mTitleTextView.append(".");
                    isUpdated=true;
                    updateDisplay();
                }
            }
            else
            {
                mTitleTextView.append(Data);
            }
        }
        catch (final JSONException e) {
            Log.e(TAG, "Json parsing error: " + e.getMessage());


        }



    }

}
