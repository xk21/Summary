package com.example.custom_view;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.custom_view.view.ProgressBarView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProgressBarActivity extends AppCompatActivity {

    @BindView(R.id.my_view)
    ProgressBarView mMyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar);
        ButterKnife.bind(this);
    }
    public void onStartUpdateProgress(View v){
        //更新进度条
//    	mMyView.updateProgress(80);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i =1; i <= 100; i++) {
                    mMyView.updateProgress(i);
                    SystemClock.sleep(50);
                }
            }
        }).start();
    }
}
