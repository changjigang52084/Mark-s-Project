package com.xunixianshi.vrshow.player.controller;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.hch.viewlib.util.ScreenUtils;
import com.xunixianshi.vrshow.interfaces.PlayLayoutInterface;
import com.xunixianshi.vrshow.obj.ClassifyVideoTypeResultResourcesList;
import com.xunixianshi.vrshow.player.PlayerLayoutGridView;

import java.util.ArrayList;

/**
 * Created by duanchunlin on 2016/8/15.
 */
public class GridViewController implements PlayLayoutInterface ,SensorEventListener {

    private Activity mContext;
    private SensorManager sensorManager;
    private Sensor orientationSensor;// 旋转矢量传感器

    private PlayerLayoutGridView gridView_left;
    private PlayerLayoutGridView gridView_right;
    private boolean enable = false;


    float[] orientationValues;
    private float[] phoneInWorldSpaceMatrix = new float[16];
    //是否是第一次进入
    private boolean isFirst = false;
    //初始化操作框位置
    private float firstX = 0;
    private float firstY = 0;
    private float firstZ = 0;

    //Volume
    private AudioManager mAudioManager;

    public GridViewController(Activity context){
        mContext = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);// VECTOR 矢量
        mAudioManager=  (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }


    public void initPlayLayoutView(View leftView, View rightView) {
        gridView_left = new PlayerLayoutGridView(mContext, leftView);
        gridView_right = new PlayerLayoutGridView(mContext, rightView);
        gridView_left.initEyeLayout();
        gridView_left.setInterface(this);
        gridView_left.setHandler(new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case PlayerLayoutGridView.UPDATE_PROGRESS:
                        updateProgress(msg.arg1);
                        break;
                }
            }
        });
        gridView_left.setData(null);
        gridView_right.setData(null);
        sensorManager.registerListener(this,orientationSensor,SensorManager.SENSOR_DELAY_UI);
    }

    public void disable(){
        if(enable){
            enable = false;
        }
    }

    public void enable(){
        if(!enable){
            enable = true;
            isFirst = false;
        }
    }

    public void onDestroy(){
        sensorManager.unregisterListener(this,orientationSensor);
    }

    public boolean isEnable(){
        return enable;
    }

    @Override
    public void back() {
    }

    @Override
    public void onFocusViewNum(int num, boolean onFocus) {
        gridView_left.setOnFocusView(num, onFocus);
        gridView_right.setOnFocusView(num, onFocus);
    }

    @Override
    public void finishActivity() {
        mContext.finish();
    }

    @Override
    public void setMaxPage(int maxPage) {
        gridView_left.setMaxPage(maxPage);
        gridView_right.setMaxPage(maxPage);
    }

    @Override
    public void backOnFocus(boolean onFocus) {
    }

    @Override
    public void volumeAdd() {
        autoSetAudioVolume(true);
    }

    @Override
    public void volumeAddOnFocus(boolean onFocus) {
    }

    @Override
    public void volumeSub() {
        autoSetAudioVolume(false);
    }

    @Override
    public void volumeSubOnFocus(boolean onFocus) {
    }


    /**
     * 自动设置音量  true 增大  false 减小
     *
     * @param bigOrSmall
     */
    private void autoSetAudioVolume(boolean bigOrSmall) {
        int vol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (bigOrSmall) {
            vol++;
        } else {
            vol--;
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, AudioManager.FLAG_SHOW_UI);
    }


    @Override
    public void showGridView(boolean show) {
        gridView_left.showGridView(show);
        gridView_right.showGridView(show);
    }

    @Override
    public void showClassifyList(boolean show) {
    }

    @Override
    public void showClassifyOnFocus(boolean onFocus) {
    }

    @Override
    public void showVideoList(boolean show) {
        gridView_left.showVideoList(show);
        gridView_right.showVideoList(show);
    }

    @Override
    public void showVideoOnFocus(boolean onFocus) {
    }

    @Override
    public void setVideoList(ArrayList<ClassifyVideoTypeResultResourcesList> videoLists) {
        gridView_left.updateGridview(videoLists);
        gridView_right.updateGridview(videoLists);
        showVideoList(true);
    }

    @Override
    public void showEmptyPage(boolean isShow, String message) {
        gridView_left.showEmityPage(isShow, message);
        gridView_right.showEmityPage(isShow, message);
    }

    @Override
    public void showVrUILoading(boolean show) {
        gridView_left.showVrUILoading(show);
        gridView_right.showVrUILoading(show);
    }

    @Override
    public void updateVideoPage(String page) {
        gridView_left.updateVideoPage(page);
        gridView_right.updateVideoPage(page);
    }

    @Override
    public void initVideoView() {
        gridView_left.registGridviewInterface();
        gridView_right.initVideoView();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.accuracy != 0) {
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
                orientationValues = event.values;
            sensorHandler.sendEmptyMessage(0);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    Handler sensorHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            calculateOrientation();
        }
    };

    /**
     * 更新位移操作框的位置
     */
    private void calculateOrientation() {
        if(!enable){return;}

        float[] values = new float[3];
        SensorManager.getRotationMatrixFromVector(phoneInWorldSpaceMatrix, orientationValues);
        float[] orientationRadians =
                SensorManager.getOrientation(phoneInWorldSpaceMatrix, values);
        for (int i = 0; i < 3; ++i) {
            values[i] = (float) Math.toDegrees(orientationRadians[i]);
        }
        // 要经 过一次数据格式的转换，转换为度 x y z 的返回是-180<->180； -180去全负 取绝对值可以得出跟移动方向相反的值
        float x = (float) Math.abs(((values[0]) - 180));// 横屏状态下绕y轴旋转
        float y = (float) Math.abs((values[1] - 180));// 横屏状态下绕z轴旋转
        float z = (float) Math.abs(((values[2]) - 180));// 横屏状态下绕x轴旋转
        //记录第一次获取到的xyz坐标 做为初始值
        if (values[0] != 0) {
            if (!isFirst) {
                firstX = x;
                firstY = y;
                firstZ = z;
                isFirst = true;
            }
        }
        int widthPX = ScreenUtils.getScreenWidth(mContext);
        int heightPX = (int) (ScreenUtils.getScreenHeight(mContext));
        int left = (int) ((x - firstX) * (widthPX / 90.0));
        int top = (int) ((z - 270) * (heightPX / 90.0) + (heightPX * 0.20));
        gridView_left.updateLayoutLocation(left, top);
        gridView_right.updateLayoutLocation(left, top);
    }
    /**
     * 显示和隐藏loading
     *
     * @param isShow
     */
    public void showLoading(boolean isShow) {
        gridView_left.showLoading(isShow);
        gridView_right.showLoading(isShow);
    }
    /**
     * 更新完成状态进度
     *
     * @param progress
     */
    public void updateProgress(int progress) {
        gridView_left.updateProgress(progress);
        gridView_right.updateProgress(progress);
    }
}
