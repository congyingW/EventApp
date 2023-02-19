package com.example.eventapp;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeThread extends Thread{
    public TextView tvTime;
    public TextView tvWeek;
    public TextView tvDate;
    SimpleDateFormat sdf;
    Calendar calendar = Calendar.getInstance();

    public TimeThread(TextView tvTime, TextView tvWeek, TextView tvDate){
        this.tvTime = tvTime;
        this.tvWeek = tvWeek;
        this.tvDate = tvDate;
    }

    @Override
    public void run() {
        super.run();
        while (true){
            Message msg = new Message();
            msg.what = 1;
            // 线程发送消息
            handler.sendMessage(msg);
            try {
                Thread.sleep(1000);  // 线程睡眠 1s
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // 创建消息处理器，接收主线程发出的消息
    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                tvTime.setText(getTime());
                tvWeek.setText(getWeek());
                tvDate.setText(getDate());
            }
            return false;
        }
    });

    public String getTime(){
         sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);
        return sdf.format(new Date(System.currentTimeMillis()));
    }
    public String getDate(){
        sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.CHINA);
        return sdf.format(new Date(System.currentTimeMillis()));
    }
    public String getWeek(){
        calendar = Calendar.getInstance();
        int i = calendar.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case 1:
                return "周日";
            case 2:
                return "周一";
            case 3:
                return "周二";
            case 4:
                return "周三";
            case 5:
                return "周四";
            case 6:
                return "周五";
            case 7:
                return "周六";
            default:
                return "";
        }
    }
}
