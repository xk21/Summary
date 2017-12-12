package com.example.custom_view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.custom_view.view.CrossLayoutView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CrossLayoutActivity extends AppCompatActivity {

    @BindView(R.id.my_view_group)
    CrossLayoutView mMyViewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cross_layout);
        ButterKnife.bind(this);
    }

    public void onRevert(View view) {
        mMyViewGroup.revert();
    }
}
