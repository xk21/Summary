package com.example.threadcommunication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    
    private TextView mTextView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.text_ee);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);
    }
    private static int batteryPercent = 100;
    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            batteryPercent = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            Log.d("aaaaaaaaaaaaa","  "+batteryPercent);
//            updateTextViewImg();
            mTextView.setText(""+batteryPercent);
        }
    
        
    };
    private void updateTextViewImg() {
        
    }
}
