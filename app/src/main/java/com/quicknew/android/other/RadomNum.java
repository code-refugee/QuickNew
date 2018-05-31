package com.quicknew.android.other;

import java.util.Arrays;

/**
 * Created by lenovo on 2018/5/29.
 */
/*该类用于随机产生一个用户名*/
public class RadomNum {
    //参数a数组存的是SQL数据库中的用户名
    public int account(int[] a){
        //先将数组a按升序排好
        Arrays.sort(a);
        //随机产生六位数
        int num=(int)((Math.random()*9+1)*100000);
        while(hasnum(a,num)){
            num=(int)((Math.random()*9+1)*100000);
        }
        return num;
    }
    //二分查找算法，查询该账号是否已经存在于数据库中
    private boolean hasnum(int[] a,int num){
        return hasnum(a,0,a.length-1,num);
    }

    private boolean hasnum(int[] a,int lo,int hi,int num){
        if(hi<lo)
            return false;
        int mid=lo+(hi-lo)/2;
        if(a[mid]>num) return hasnum(a,lo,mid-1,num);
        else if(a[mid]<num) return hasnum(a,mid+1,hi,num);
        else  return true;
    }
}
