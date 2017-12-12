package com.example.custom_view;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.custom_view.viewgroup.AlphaPageTransformer;
import com.example.custom_view.viewgroup.NonPageTransformer;
import com.example.custom_view.viewgroup.RotateDownPageTransformer;
import com.example.custom_view.viewgroup.RotateUpPageTransformer;
import com.example.custom_view.viewgroup.RotateYTransformer;
import com.example.custom_view.viewgroup.ScaleInTransformer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MagicViewPager extends AppCompatActivity {

    @BindView(R.id.id_viewpager)
    ViewPager mViewPager;
    private PagerAdapter mAdapter;

    int[] imgRes = {R.mipmap.a, R.mipmap.b, R.mipmap.c, R.mipmap.d,
            R.mipmap.e, R.mipmap.f, R.mipmap.g, R.mipmap.h, R.mipmap.i};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magic_view_pager);
        ButterKnife.bind(this);
        mViewPager.setPageMargin(20);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mAdapter = new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView view = new ImageView(MagicViewPager.this);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                view.setLayoutParams(lp);
                view.setScaleType(ImageView.ScaleType.FIT_XY);
                view.setImageResource(imgRes[position]);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public int getCount() {
                return imgRes.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        String[] effects = this.getResources().getStringArray(R.array.magic_effect);
        for (String effect : effects) {
            menu.add(effect);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String title = item.getTitle().toString();
        if ("RotateDown".equals(title)) {
            Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
            mViewPager.setPageTransformer(true, new RotateDownPageTransformer());
        } else if ("RotateUp".equals(title)) {
            Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
            mViewPager.setPageTransformer(true, new RotateUpPageTransformer());
        } else if ("RotateY".equals(title)) {
            Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
            mViewPager.setPageTransformer(true, new RotateYTransformer(45));
        } else if ("Standard".equals(title)) {
            Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
           mViewPager.setClipChildren(false);
            mViewPager.setPageTransformer(true, NonPageTransformer.INSTANCE);
        } else if ("Alpha".equals(title)) {
            Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
            mViewPager.setClipChildren(false);
            mViewPager.setPageTransformer(true, new AlphaPageTransformer());
        } else if ("ScaleIn".equals(title)) {
            Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
            mViewPager.setPageTransformer(true, new ScaleInTransformer());
        } else if ("RotateDown and Alpha".equals(title)) {
            Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
            mViewPager.setPageTransformer(true, new RotateDownPageTransformer(new AlphaPageTransformer()));
        } else if ("RotateDown and Alpha And ScaleIn".equals(title)) {
            Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
            mViewPager.setPageTransformer(true, new RotateDownPageTransformer(new AlphaPageTransformer(new ScaleInTransformer())));
        }
        setTitle(title);
        return true;
    }
}
