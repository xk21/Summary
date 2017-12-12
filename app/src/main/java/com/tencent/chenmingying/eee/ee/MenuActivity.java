package com.tencent.chenmingying.eee.ee;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.chenmingying.eee.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.butt1)
    Button mButt1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initData() {
        mButt1.setOnClickListener(this);
    }

    private void initView() {
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_item :
                Toast.makeText(this,"add",Toast.LENGTH_SHORT).show();
                break;
            case R.id.remove_item:
                Toast.makeText(this,"remove",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}
