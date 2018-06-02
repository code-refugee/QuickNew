package com.quicknew.android.other;

/**
 * Created by lenovo on 2018/6/2.
 * 该类是一个股票的实体类
 */

public class Share {
    private String day;
    //开盘价
    private double open;
    private double high;
    private double low;
    //收盘价
    private double close;
    //成交量
    private int volume;

    public String getDay() {
        return day;
    }

    public double getOpen() {
        return open;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getClose() {
        return close;
    }

    public int getVolume() {
        return volume;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
