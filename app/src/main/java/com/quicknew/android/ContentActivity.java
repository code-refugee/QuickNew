package com.quicknew.android;
/**
 * Created by yuhang Tao on 2018/5/31.
 *这是一个活动，用于显示股票的信息
 */
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
    //存放布局
    private List<View> views;
    //获取自己在res下写的布局的id（android.R.layout是获取Android SDK自带的布局文件）
    private int[] layouts=new int[]{
            R.layout.aqi,R.layout.suggestion
    };
    //用于存放点的图片
    private ImageView[] dots=new ImageView[2];
    //代表当前页
    private int curPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        initView();
        initData();
        pager.setAdapter(adapter);
    }

    //初始化视图
    private void initView() {
        pager=(ViewPager)findViewById(R.id.view_pager);
        //对滑动操作进行监听
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //改变点的状态
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
            //打上标签
            dots[i].setTag(i);
            //对点击点的操作进行监听，点到该位置，页面也该改变
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

    //初始化数据适配器
    private void initData(){
        views=new ArrayList<>();
        /*从一个Context中，获得一个布局填充器，这样就可以使用这个填充器来
        把xml布局文件转为View对象了。*/
        LayoutInflater inflater=LayoutInflater.from(this);
        for(int i=0;i<layouts.length;i++){
            /*通过inflater.inflate（）方法将布局找出来并赋给view
            * (findViewById则是从布局文件中查找一个控件)*/
            View view=inflater.inflate(layouts[i],null);
            views.add(view);
        }
        adapter=new ViewPageAdapter(views);
    }

    //设置点的状态
    private void setDots(int index){
        if(index<0||index>dots.length||index==curPager)
            return;
//        if(index==curPager)
//            return;
//        if(index<0)
//            index=dots.length;
//        if(index>dots.length)
//            index=0;
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
