package com.example.custom_view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.bt_01)
    Button mBt01;
    @BindView(R.id.bt_02)
    Button mBt02;
    @BindView(R.id.bt_03)
    Button mBt03;
    @BindView(R.id.bt_04)
    Button mBt04;
    @BindView(R.id.bt_05)
    Button mBt05;
    @BindView(R.id.bt_06)
    Button mBt06;
    @BindView(R.id.bt_07)
    Button mBt07;
    @BindView(R.id.bt_08)
    Button mBt08;
    @BindView(R.id.bt_09)
    Button mBt09;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mBt01.setOnClickListener(this);
        mBt02.setOnClickListener(this);
        mBt03.setOnClickListener(this);
        mBt04.setOnClickListener(this);
        mBt05.setOnClickListener(this);
        mBt06.setOnClickListener(this);
        mBt07.setOnClickListener(this);
        mBt08.setOnClickListener(this);
        mBt01.setTextColor(Color.parseColor("#ff0000"));
        mBt01.setBackgroundResource(R.color.colorPrimaryDark);
    }

    @Override
    public void onClick(View v) {
        Intent intent ;
       switch (v.getId()){
           case R.id.bt_01:
               intent = new Intent(this, CustomView1Activity.class);
               startActivity(intent);
               break;
           case R.id.bt_02:

               Log.d("ËEE","EE0000000000000000000.......EE");
               intent = new Intent(this, TextSummaryWithImgCheckBoxActivity.class);
               startActivity(intent);
               break;
           case R.id.bt_03:
               intent = new Intent(this, ProgressBarActivity.class);
               startActivity(intent);
               break;
           case R.id.bt_04:
               intent = new Intent(this, SwitchToggleViewActivity.class);
               startActivity(intent);
               break;
           case R.id.bt_05:
               intent = new Intent(this, CrossLayoutActivity.class);
               startActivity(intent);
               break;
           case R.id.bt_06:
               intent = new Intent(this, SlideUnlockActivity.class);
               startActivity(intent);
               break;
           case R.id.bt_07:
               intent = new Intent(this, SlidingMenuActivity.class);
               startActivity(intent);
               break;
           case R.id.bt_08:
               intent = new Intent(this, MagicViewPager.class);
               startActivity(intent);
               break;
           case R.id.bt_09:
               break;
       }
    }
}
