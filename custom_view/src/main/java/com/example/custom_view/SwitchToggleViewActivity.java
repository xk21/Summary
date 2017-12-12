package com.example.custom_view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.custom_view.view.SwitchToggleView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SwitchToggleViewActivity extends AppCompatActivity {

    @BindView(R.id.switch_toggle)
    SwitchToggleView mSwitchToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_toggle_view);
        ButterKnife.bind(this);
        mSwitchToggle.setOnSwitchChangeListener(mOnSwitchChangeListener);
    }

    private SwitchToggleView.OnSwitchChangeListener mOnSwitchChangeListener = new SwitchToggleView.OnSwitchChangeListener() {

        @Override
        public void onSwitch(boolean isClose) {
            String text = isClose ? "关闭" : "打开";
            Toast.makeText(SwitchToggleViewActivity.this, text, Toast.LENGTH_SHORT).show();
        }
    };
}
