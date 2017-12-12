package com.example.custom_view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.custom_view.view.SlidingMenuView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SlidingMenuActivity extends AppCompatActivity {


    @BindView(R.id.sliding_menu)
    SlidingMenuView mSlidingMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_menu);
        ButterKnife.bind(this);
    }
    // 4. 点击菜单列表，弹出选项对应的文本，同时关闭侧滑菜单
    public void onTabClick(View v) {
        //关闭侧滑菜单
        mSlidingMenu.toggle();
        Toast.makeText(this, ((TextView)v).getText(), Toast.LENGTH_SHORT).show();
    }

}
