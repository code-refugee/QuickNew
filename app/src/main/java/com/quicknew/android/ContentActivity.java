package com.quicknew.android;
/**
 * Created by yuhang Tao on 2018/5/31.
 *这是一个活动，用于显示股票的信息
 */
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.quicknew.android.adapter.ViewPageAdapter;
import com.quicknew.android.other.Share;
import com.quicknew.android.util.HttpUtil;
import com.quicknew.android.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ContentActivity extends AppCompatActivity {

    //存放viewpager内的几个布局
    private List<View> views;

    //获取自己在res下写的布局的id（android.R.layout是获取Android SDK自带的布局文件）
    private int[] layouts=new int[]{
            R.layout.aqi,R.layout.suggestion
    };

    //用于存放点的图片
    private ImageView[] dots=new ImageView[2];

    //用于存放收盘价(用于分析7天内最高和最低收盘价)
    private double[] close_price;

    //代表当前页
    private int curPager;

    //用于存储Share对象
    private List<Share> shares;

    //主界面的滑动视图
    private ScrollView content_layout;

    //搜索框
    private EditText search_content;

    //搜素按钮
    private Button search;

    //用于显示股票名
    private TextView share_name;

    private LinearLayout shares_layout;

    //用于实现滑动
    private ViewPager pager;

    //viewpager的适配器
    private ViewPageAdapter adapter;

    //用于存放要查询的股票名
    private String sharename;

    //suggestion布局中的scrollview
    private ScrollView  suggest_scrool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        initView();
        pager.setAdapter(adapter);
    }

    //初始化视图
    private void initView() {
        //控件初始化
        content_layout=(ScrollView)findViewById(R.id.content_layout);
        search_content=(EditText)findViewById(R.id.search_content);
        search=(Button)findViewById(R.id.search);
        share_name=(TextView)findViewById(R.id.share_name);
        shares_layout=(LinearLayout)findViewById(R.id.shares_layout);
        pager=(ViewPager)findViewById(R.id.view_pager);
        //得到当前SharedPreferences对象
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(ContentActivity.this);
        /*这里通过prefs得到的JSON数据*/
        String shareString=prefs.getString("share",null);
        //得到股票名
        String name=prefs.getString("sharename",null);
        if(shareString!=null){
            //有缓存时直接解析股票JSON数据
            shares= Utility.handleShareResponse(shareString);
            sharename=name;
            showShareInfo();
        }else{
            //无缓存时默认查询sz000001股票的信息
            sharename="sz000001";
            //将当前界面隐藏，不然无数据时看上去怪怪的
            content_layout.setVisibility(View.INVISIBLE);
            //根据股票名查询股票的详细信息
            requestShare();
        }
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

    /*根据股票的名字获取股市信息*/
    private void requestShare() {
        //接口地址
        String shareUrl="http://money.finance.sina.com.cn/quotes_service" +
                "/api/json_v2.php/CN_MarketData.getKLineData?symbol="
                +sharename+"&scale=60&ma=10&datalen=1023 ";
        //发起网络请求
        HttpUtil.sendOkHttpRequest(shareUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ContentActivity.this,"亲，股票数据不存在"
                                ,Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //得到JSON数据
                final String responseText=response.body().string();

                //解析股票JSON数据
                shares=Utility.handleShareResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //若查询到了股票信息，则通过键值对的方式存入SharedPreferences
                        if(shares!=null){
                            SharedPreferences.Editor editor=PreferenceManager
                                    .getDefaultSharedPreferences(ContentActivity.this).edit();
                            //存JSON数据
                            editor.putString("share",responseText);
                            //存1股票名
                            editor.putString("sharename",sharename);
                            //调用apply()提交数据
                            editor.apply();
                            /*调用方法，将已得到的数据在活动中显示出来*/
                            showShareInfo();
                        }else{
                            Toast.makeText(ContentActivity.this,"亲，股票数据不存在"
                            ,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    /*处理已经得到的数据的数据，将数据在活动中显示出来*/
    private void showShareInfo(){
        share_name.setText(sharename);
        //清空原先所有的视图
        shares_layout.removeAllViews();
        close_price=new double[shares.size()];
        //用于表示close_price数组的下标
        int index=0;
        //通过遍历取出数据，并在相应的位置显示出来
        for(Share share:shares){
            /*通过inflater.inflate（）方法将布局找出来并赋给view*/
            View view=LayoutInflater.from(this).inflate(R.layout.shares_item,shares_layout,false);
            TextView dayText=(TextView)view.findViewById(R.id.day_text);
            TextView openText=(TextView)view.findViewById(R.id.open_text);
            TextView volumeText=(TextView)view.findViewById(R.id.volume_text);
            dayText.setText(share.getDay());
            openText.setText(share.getOpen()+"");
            volumeText.setText(share.getVolume()+"");
            close_price[index]=share.getClose();
            index++;
            //在另一种方法中初始化滑动窗口，不然代码太长
            initData();
            shares_layout.addView(view);
        }

        //设置可见
        content_layout.setVisibility(View.VISIBLE);
    }

    //初始化数据适配器
    private void initData(){
        views=new ArrayList<>();
        /*从一个Context中，获得一个布局填充器，这样就可以使用这个填充器来
        把xml布局文件转为View对象了。*/
        LayoutInflater inflater=LayoutInflater.from(this);
         /*通过inflater.inflate（）方法将布局找出来并赋给view
            * (findViewById则是从布局文件中查找一个控件)*/
        View view=inflater.inflate(layouts[0],null);
        TextView high_text=(TextView)view.findViewById(R.id.high_text);
        TextView low_text=(TextView)view.findViewById(R.id.low_text);
        Arrays.sort(close_price);
        high_text.setText(close_price[shares.size()-1]+"");
        low_text.setText(close_price[0]+"");
        views.add(view);

        View view1=inflater.inflate(layouts[1],null);
        String suggestion=analyseShare();
        TextView sugg_test1=(TextView)view1.findViewById(R.id.sugg_test1) ;
        suggest_scrool=(ScrollView) view1.findViewById(R.id.suggest_scrool);

        //添加监听，防止与外部的ScrollView产生冲突
        suggest_scrool.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    //允许ScrollView截断点击事件，ScrollView可滑动
                    content_layout.requestDisallowInterceptTouchEvent(false);
                }else{
                    //不允许ScrollView截断点击事件，点击事件由子View处理
                    content_layout.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });
        sugg_test1.setText(suggestion);
        views.add(view1);
        adapter=new ViewPageAdapter(views);
    }

    //分析股票给出建议
    private String analyseShare(){
        return "建议买入1111111111111111111111111111111111111111111" +
                "1111111111111111111111111111111111111111111111111111111" +
                "11111111111111111111111111111111111111111111111111111111111" +
                "11111111111111111111111111111111111111111111111111111111111" +
                "11111111111111111111111111111111111111111111111111111111111111" +
                "11111111111111111111111111111111111111111111111111111111111111111" +
                "11111111111111111111111111111111111111111111111111111111111111111";
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
