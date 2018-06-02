package com.quicknew.android.util;

import com.quicknew.android.other.Share;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2018/6/2.
 * 该类是用于解析股票JSON数据的方法
 */

public class Utility {

    /*用于将返回的JSON数据解析成存储Share实体类的集*/
    public static List<Share> handleShareResponse(String response){
        try {
            JSONArray jsonArray=new JSONArray(response);
            List<Share> shares=new ArrayList<>();
            int time=0;
            int j=jsonArray.length()-1;
            for(int i=0;i<7;i++){
                JSONObject jsonObject=jsonArray.getJSONObject(j-time);
                Share share=new Share();
                share.setDay(jsonObject.getString("day"));
                share.setClose(Double.parseDouble(jsonObject.getString("close")));
                share.setHigh(Double.parseDouble(jsonObject.getString("high")));
                share.setLow(Double.parseDouble(jsonObject.getString("low")));
                share.setOpen(Double.parseDouble(jsonObject.getString("open")));
                share.setVolume(Integer.parseInt(jsonObject.getString("volume")));
                shares.add(share);
                time+=4;
            }
            return shares;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
