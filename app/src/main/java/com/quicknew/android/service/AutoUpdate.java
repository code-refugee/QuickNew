package com.quicknew.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.quicknew.android.other.Share;
import com.quicknew.android.util.HttpUtil;
import com.quicknew.android.util.Utility;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdate extends Service {
    public AutoUpdate() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateShare();
        AlarmManager manager=(AlarmManager) getSystemService(ALARM_SERVICE);
        //3小时的毫秒数
        int anHour=3*60*60*1000;
        long triggerTime= SystemClock.elapsedRealtime()+anHour;
        Intent i=new Intent(this,AutoUpdate.class);
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    //更新股票信息
    private void updateShare() {
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        //得到股票名
        String name=prefs.getString("sharename",null);
        if(name!=null){
            String shareUrl="http://money.finance.sina.com.cn/quotes_service" +
                    "/api/json_v2.php/CN_MarketData.getKLineData?symbol="
                    +name+"&scale=60&ma=10&datalen=1023 ";
            HttpUtil.sendOkHttpRequest(shareUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //得到JSON数据
                    String responseText=response.body().string();

                    //解析股票JSON数据
                    List<Share> shares= Utility.handleShareResponse(responseText);
                    if(shares!=null){
                        SharedPreferences.Editor editor=PreferenceManager.
                                getDefaultSharedPreferences(AutoUpdate.this).edit();
                        editor.putString("share",responseText);
                        editor.apply();
                    }
                }
            });
        }
    }
}
