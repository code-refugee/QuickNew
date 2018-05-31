package com.quicknew.android.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by yuhang Tao on 2018/5/31.
 * 这是一个滑动窗口viewpager的数据适配器
 */

public class ViewPageAdapter extends PagerAdapter {
    //用于存放自定义布局
    private List<View> views;

    public ViewPageAdapter(List<View> views) {
        this.views = views;
    }

    //获取要滑动的控件的数量，这里是我们的两个布局
    @Override
    public int getCount() {
        return views.size();
    }

    //判断显示的是否为同一个布局
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    //如果滑动的布局超出了缓存的范围，则销毁布局
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    /*当要显示的布局可以进行缓存的时候，会调用这个方法进行布局的初始化，
    我们将要显示的布局加入到ViewGroup中，然后作为返回值返回即可 */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position));
        return views.get(position);
    }

}
