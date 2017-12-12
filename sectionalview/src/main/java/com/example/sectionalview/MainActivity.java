package com.example.sectionalview;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {


    EditText mEt01;
    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEt01 = (EditText) findViewById(R.id.et_01);
        mEt01.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDialog = new AlertDialog.Builder(MainActivity.this)
                        .setPositiveButton("确定",null)
                        .create();
                mDialog.show();
                mDialog.getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED
                );
                return false;
            }
        });
        /*mEt01.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                   mDialog = new AlertDialog.Builder(MainActivity.this)
                           .setPositiveButton("确定",null)
                           .create();
                    mDialog.show();
                    mDialog.getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED
                    );
                }else {

                }
            }
        });*/
    }
}
