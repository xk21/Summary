package com.example.custom_view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.custom_view.view.SlideUnlockView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SlideUnlockActivity extends AppCompatActivity {

    @BindView(R.id.lock_view)
    SlideUnlockView mLockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_unlock);
        ButterKnife.bind(this);
        mLockView.setOnUnlockListener(new SlideUnlockView.OnUnlockListener(){
            @Override
            public void onUnlock() {
                finish();
            }
        });
    }
}
