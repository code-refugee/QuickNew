package com.quicknew.android;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.quicknew.android.adapter.ViewPageAdapter;

import java.util.ArrayList;
import java.util.List;

public class ContentActivity extends AppCompatActivity {
    private ViewPager pager;
    private ViewPageAdapter adapter;
    private List<View> views;
    private int[] layouts=new int[]{
            R.layout.aqi,R.layout.suggestion
    };
    private ImageView[] dots=new ImageView[2];
    private int curPager;
    private boolean isFirst;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        initView();
        initData();
        pager.setAdapter(adapter);
    }

    private void initView() {
        pager=(ViewPager)findViewById(R.id.view_pager);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        LinearLayout layout=(LinearLayout)findViewById(R.id.ll_details_top_dot);
        for(int i =0;i<2;i++){
            dots[i]=(ImageView)layout.getChildAt(i);
            dots[i].setEnabled(true);
            dots[i].setTag(i);
            dots[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int) v.getTag();
                    setDots(index);
                    setPagers(index);
                }
            });
        }
        curPager = 0;
        dots[curPager].setEnabled(false);
    }

    private void initData(){
        views=new ArrayList<>();
        LayoutInflater inflater=LayoutInflater.from(this);
        for(int i=0;i<layouts.length;i++){
            View view=inflater.inflate(layouts[i],null);
            views.add(view);
        }
        adapter=new ViewPageAdapter(views);
    }

    //设置点的状态
    private void setDots(int index){
        if(index<0||index>dots.length||index==curPager)
            return;
        dots[index].setEnabled(false);
        dots[curPager].setEnabled(true);
        curPager=index;
    }

    //显示当前视图
    private void setPagers(int index){
        if(index<0||index>layouts.length)
            return;
        pager.setCurrentItem(index);
    }
}
