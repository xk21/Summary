package com.tencent.chenmingying.eee;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.tencent.chenmingying.eee.ee.MenuActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private GridView mGridView;
    private List datas;
    private AA mAA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGridView = (GridView) findViewById(R.id.icon_group);
       
                Log.d("aadd","bbbbb=1");
                for (int i=0;i<100;i++){
                    for (int j=0;j<100;j++){
                        Log.d("aaa","bbaab=0");
                        Log.d("aaa","bbaab=0");
                    }
                }
                Log.d("aadd","bbbbb=2");
       
        initDAta();
        initView();
    }

    private void initDAta() {
        
        datas = new ArrayList();
        datas.add("ee");
        datas.add("Topgrid");
        datas.add("自定义View");
        datas.add("Activity切换动画");
        datas.add("Activity切换动画");
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("tag","aaaaa="+mAA);
    }
    
    private void initView() {
        mGridView.setAdapter(new MylistAdapter());
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case 0:
                        intent = new Intent(MainActivity.this, MenuActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent("android.intent.action.topnewgrid.act");
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent("android.intent.action.custom_view");
                        startActivity(intent);
                        break;
                }

            }
        });
    }

    
    /**
     * Created by chenmingying on 2017/2/9.
     */

    public class MylistAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                textView = new TextView(MainActivity.this);
            } else {
                textView= (TextView) convertView;
            }
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(15,15,15,15);
            textView.setText((String) datas.get(position));
            return textView;
        }
    }

}
