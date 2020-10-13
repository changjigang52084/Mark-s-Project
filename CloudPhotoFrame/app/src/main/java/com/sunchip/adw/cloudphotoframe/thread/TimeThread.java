package com.sunchip.adw.cloudphotoframe.thread;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeThread extends Thread {

    private TextView tvTime;
    private TextView dateTv;
    private TextView dateTx;

    public TimeThread(TextView tvTime, TextView dateTv,TextView dateTx) {
        this.tvTime = tvTime;
        this.dateTv = dateTv;
        this.dateTx = dateTx;
    }

    @Override
    public void run() {
        super.run();
        do {
            try {
                Thread.sleep(1000);
                Message message = new Message();
                message.what = 0x001;
                mHandler.sendMessage(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (true);
    }

    //获取系统当前是星期几
    public static String getCurrentWeekDay() {
        String week = "";
        Calendar c1 = Calendar.getInstance();
        int day = c1.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case 1:
                week = "Sunday";
                break;
            case 2:
                week = "Monday";
                break;
            case 3:
                week = "Tuesdays";
                break;
            case 4:
                week = "Wednesday";
                break;
            case 5:
                week = "Thursday";
                break;
            case 6:
                week = "Fridays";
                break;
            case 7:
                week = "Saturday";
                break;
        }
        return week;
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x001:
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    String time = sdf.format(new Date());
                    tvTime.setText(time);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                    String date = sdf1.format(new Date());
                    dateTv.setText(date);
                    dateTx.setText(getCurrentWeekDay());
                    System.runFinalization();
                    System.gc();
                    break;
            }
        }
    };
}
