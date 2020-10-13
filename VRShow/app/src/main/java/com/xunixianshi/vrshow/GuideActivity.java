package com.xunixianshi.vrshow;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.mobstat.StatService;
import com.hch.viewlib.util.ViewlibConstant;
import com.hch.viewlib.util.MLog;
import com.hch.viewlib.util.SimpleSharedPreferences;
import com.xunixianshi.vrshow.util.AppContent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * app 引导页
 *@author HeChuang
 *@time 2016/11/1 15:19
 */
public class GuideActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {

    private ViewPager vp;
    private ViewPagerAdapter vpAdapter;
    private List<View> views;

    private ImageView image_iv, movement_iv, beauty_iv, fashion_iv, science_fiction_iv, tourism_iv, terrorist_iv, comi_iv, dance_iv, funny_iv, literary_iv, music_iv, plot_iv;
    private TextView change_tv, goto_home_tv;
    private AnimationDrawable animationDrawable = null;
    private Animation animationScience;
    private Animation animationMovement;
    private Animation animationBeauty;
    private Animation animationFashion;
    private Animation animationTourism;
    private Animation animationTerrorist;

    private long durationMillis = 1000;
    private boolean firstPage = true;
    private boolean animationEnd = false;
    private float density;
    private Map<Integer, Integer> recordSelect = new LinkedHashMap<Integer, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        StatService.setSessionTimeOut(30);
        StatService.setOn(this,StatService.EXCEPTION_LOG);
        StatService.setLogSenderDelayed(0);
        StatService.setSendLogStrategy(this, SendStrategyEnum.SET_TIME_INTERVAL,1,false);
        StatService.setDebugOn(true);
        initView();
        initData();
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;  // 屏幕宽度（像素）
        int height = metric.heightPixels;  // 屏幕高度（像素）
        density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi;
//        width:720  height:1280  density:2.0  densityDpi:240
//        width:1080  height:1920  density:3.0  densityDpi:480
//        width:1536  height:2560  density:4.0  densityDpi:640
        MLog.d("width:" + width + "  height:" + height + "  density:" + density + "  densityDpi:" + densityDpi);

    }

    protected void initView() {
        LayoutInflater inflater = LayoutInflater.from(this);

        views = new ArrayList<View>();
        View pageView1 = inflater.inflate(R.layout.what_new_one, null);
        initPageViews(pageView1);
        views.add(pageView1);
//		views.add(inflater.inflate(R.layout.what_new_two, null));
//		views.add(inflater.inflate(R.layout.what_new_three, null));
//
//		views.add(inflater.inflate(R.layout.what_new_four, null));
//		views.add(inflater.inflate(R.layout.what_new_five, null));

        vpAdapter = new ViewPagerAdapter(views, this);

        vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(vpAdapter);

        vp.setOnPageChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    private void initPageViews(View pageView1) {
        change_tv = (TextView) pageView1.findViewById(R.id.change_tv);
        goto_home_tv = (TextView) pageView1.findViewById(R.id.goto_home_tv);
        image_iv = (ImageView) pageView1.findViewById(R.id.image_iv);
        movement_iv = (ImageView) pageView1.findViewById(R.id.movement_iv);
        beauty_iv = (ImageView) pageView1.findViewById(R.id.beauty_iv);
        fashion_iv = (ImageView) pageView1.findViewById(R.id.fashion_iv);
        science_fiction_iv = (ImageView) pageView1.findViewById(R.id.science_fiction_iv);
        tourism_iv = (ImageView) pageView1.findViewById(R.id.tourism_iv);
        terrorist_iv = (ImageView) pageView1.findViewById(R.id.terrorist_iv);
        comi_iv = (ImageView) pageView1.findViewById(R.id.comi_iv);
        dance_iv = (ImageView) pageView1.findViewById(R.id.dance_iv);
        funny_iv = (ImageView) pageView1.findViewById(R.id.funny_iv);
        literary_iv = (ImageView) pageView1.findViewById(R.id.literary_iv);
        music_iv = (ImageView) pageView1.findViewById(R.id.music_iv);
        plot_iv = (ImageView) pageView1.findViewById(R.id.plot_iv);
        goto_home_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });
        change_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (animationEnd) {
                    movement_iv.setVisibility(View.GONE);
                    beauty_iv.setVisibility(View.GONE);
                    fashion_iv.setVisibility(View.GONE);
                    science_fiction_iv.setVisibility(View.GONE);
                    tourism_iv.setVisibility(View.GONE);
                    terrorist_iv.setVisibility(View.GONE);
                    comi_iv.setVisibility(View.GONE);
                    dance_iv.setVisibility(View.GONE);
                    funny_iv.setVisibility(View.GONE);
                    literary_iv.setVisibility(View.GONE);
                    music_iv.setVisibility(View.GONE);
                    plot_iv.setVisibility(View.GONE);
                    if (firstPage) {
                        slideview(false, movement_iv, 0, -125f, 0, 45f);
                        slideview(false, beauty_iv, 0, -47.5f, 0, -130f);
                        slideview(false, fashion_iv, 0, 120f, 0, 37.5f);
                        slideview(false, science_fiction_iv, 0, -77.5f, 0, -42.5f);
                        slideview(false, tourism_iv, 0, 0, 0, 137.5f);
                        slideview(false, terrorist_iv, 0, 92.5f, 0, -102.5f);
                        //如果是true 说明即将要显示第一页  隐藏第二页标签 并归位
                        slideview(true, comi_iv, 0, 125f, 0, -45f);
                        slideview(true, dance_iv, 0, 47.5f, 0, 130f);
                        slideview(true, funny_iv, 0, -120f, 0, -37.5f);
                        slideview(true, literary_iv, 0, 77.5f, 0, 42.5f);
                        slideview(true, music_iv, 0, 0, 0, -137.5f);
                        slideview(true, plot_iv, 0, -92.5f, 0, 102.5f);
                    } else {
                        slideview(false, comi_iv, 0, -125f, 0, 45f);
                        slideview(false, dance_iv, 0, -47.5f, 0, -130f);
                        slideview(false, funny_iv, 0, 120f, 0, 37.5f);
                        slideview(false, literary_iv, 0, -77.5f, 0, -42.5f);
                        slideview(false, music_iv, 0, 0, 0, 137.5f);
                        slideview(false, plot_iv, 0, 92.5f, 0, -102.5f);
                        //如果是false 说明即将要显示第二页  隐藏第一页标签 并归位
                        slideview(true, movement_iv, 0, 125f, 0, -45f);
                        slideview(true, beauty_iv, 0, 47.5f, 0, 130f);
                        slideview(true, fashion_iv, 0, -120f, 0, -37.5f);
                        slideview(true, science_fiction_iv, 0, 77.5f, 0, 42.5f);
                        slideview(true, tourism_iv, 0, 0, 0, -137.5f);
                        slideview(true, terrorist_iv, 0, -92.5f, 0, 102.5f);
                    }
                    animationEnd = false;
                    startTranslateAnimation();
                }
                Log.d("TAG", "==============================");
            }
        });
        //点击标签 把如果是选中 则把选中的标签临时保存
        movement_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "==============================" + movement_iv.isSelected());
                if (movement_iv.isSelected()) {
                    movement_iv.setSelected(false);
                    recordSelect.remove(6);
                } else {
                    if(selectIsAmple()){
                        return;
                    }
                    movement_iv.setSelected(true);
                    recordSelect.put(6, 6);
                }
            }
        });
        beauty_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "==============================");
                if (beauty_iv.isSelected()) {
                    beauty_iv.setSelected(false);
                    recordSelect.remove(3);
                } else {
                    if(selectIsAmple()){
                        return;
                    }
                    beauty_iv.setSelected(true);
                    recordSelect.put(3, 3);
                }
            }
        });
        fashion_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fashion_iv.isSelected()) {
                    fashion_iv.setSelected(false);
                    recordSelect.remove(8);
                } else {
                    if(selectIsAmple()){
                        return;
                    }
                    fashion_iv.setSelected(true);
                    recordSelect.put(8, 8);
                }
            }
        });
        science_fiction_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (science_fiction_iv.isSelected()) {
                    science_fiction_iv.setSelected(false);
                    recordSelect.remove(5);
                } else {
                    if(selectIsAmple()){
                        return;
                    }
                    science_fiction_iv.setSelected(true);
                    recordSelect.put(5, 5);
                }
            }
        });
        tourism_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "==============================");
                if (tourism_iv.isSelected()) {
                    tourism_iv.setSelected(false);
                    recordSelect.remove(7);
                } else {
                    if(selectIsAmple()){
                        return;
                    }
                    tourism_iv.setSelected(true);
                    recordSelect.put(7, 7);
                }
            }
        });
        terrorist_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "==============================");
                if (terrorist_iv.isSelected()) {
                    terrorist_iv.setSelected(false);
                    recordSelect.remove(4);
                } else {
                    if(selectIsAmple()){
                        return;
                    }
                    terrorist_iv.setSelected(true);
                    recordSelect.put(4, 4);
                }
            }
        });
        comi_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "==============================");
                if (comi_iv.isSelected()) {
                    comi_iv.setSelected(false);
                    recordSelect.remove(10);
                } else {
                    if(selectIsAmple()){
                        return;
                    }
                    comi_iv.setSelected(true);
                    recordSelect.put(10, 10);
                }
            }
        });
        dance_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "==============================");
                if (dance_iv.isSelected()) {
                    dance_iv.setSelected(false);
                    recordSelect.remove(33);
                } else {
                    if(selectIsAmple()){
                        return;
                    }
                    dance_iv.setSelected(true);
                    recordSelect.put(33, 33);
                }
            }
        });
        funny_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "==============================");
                if (funny_iv.isSelected()) {
                    funny_iv.setSelected(false);
                    recordSelect.remove(9);
                } else {
                    if(selectIsAmple()){
                        return;
                    }
                    funny_iv.setSelected(true);
                    recordSelect.put(9, 9);
                }
            }
        });
        literary_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "==============================");
                if (literary_iv.isSelected()) {
                    literary_iv.setSelected(false);
                    recordSelect.remove(58);
                } else {
                    if(selectIsAmple()){
                        return;
                    }
                    literary_iv.setSelected(true);
                    recordSelect.put(58, 58);
                }
            }
        });
        music_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "==============================");
                if (music_iv.isSelected()) {
                    music_iv.setSelected(false);
                    recordSelect.remove(11);
                } else {
                    if(selectIsAmple()){
                        return;
                    }
                    music_iv.setSelected(true);
                    recordSelect.put(11, 11);
                }
            }
        });
        plot_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "==============================");
                if (plot_iv.isSelected()) {
                    plot_iv.setSelected(false);
                    recordSelect.remove(59);
                } else {
                    if(selectIsAmple()){
                        return;
                    }
                    plot_iv.setSelected(true);
                    recordSelect.put(59, 59);
                }
            }
        });
    }

    private boolean selectIsAmple() {
        if(recordSelect.size() >= 5){
            showToastMsg("最多只能选5个标签！");
            return true;
        }else{
            return false;
        }
    }

    protected void initData() {
        initAnim();
    }

    private void initAnim() {
        image_iv.setBackgroundResource(R.drawable.anim);
        animationDrawable = (AnimationDrawable) image_iv.getBackground();
        startAnimation();
    }

    public void slideview(boolean iscancel, final View view, final float fromXDelta, final float toXDelta, final float fromYDelta, final float toYDelta) {
        TranslateAnimation animation = new TranslateAnimation(fromXDelta*density, toXDelta*density, fromYDelta*density, toYDelta*density);
        animation.setInterpolator(new OvershootInterpolator());
        if (iscancel) {
            animation.setDuration(0);
        } else {
            animation.setDuration(1000);
        }
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                animationEnd = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                int left = view.getLeft() + (int) (toXDelta*density - fromXDelta*density);
                int top = view.getTop() + (int) (toYDelta*density - fromYDelta*density);
                int width = view.getWidth();
                int height = view.getHeight();
                view.clearAnimation();
                view.layout(left, top, left + width, top + height);
                animationEnd = true;
            }
        });
        view.startAnimation(animation);
    }

    private void startTranslateAnimation() {
        if (firstPage) {
            //第一页6个标签 显示 并做动画
            movement_iv.setVisibility(View.VISIBLE);
            beauty_iv.setVisibility(View.VISIBLE);
            fashion_iv.setVisibility(View.VISIBLE);
            science_fiction_iv.setVisibility(View.VISIBLE);
            tourism_iv.setVisibility(View.VISIBLE);
            terrorist_iv.setVisibility(View.VISIBLE);
            slideview(false, movement_iv, 0, -125f, 0, 45f);
            slideview(false, beauty_iv, 0, -47.5f, 0, -130f);
            slideview(false, fashion_iv, 0, 120f, 0, 37.5f);
            slideview(false, science_fiction_iv, 0, -77.5f, 0, -42.5f);
            slideview(false, tourism_iv, 0, 0, 0, 137.5f);
            slideview(false, terrorist_iv, 0, 92.5f, 0, -102.5f);
            firstPage = false;
        } else {
            //第二页6个标签显示并作动画
            comi_iv.setVisibility(View.VISIBLE);
            dance_iv.setVisibility(View.VISIBLE);
            funny_iv.setVisibility(View.VISIBLE);
            literary_iv.setVisibility(View.VISIBLE);
            music_iv.setVisibility(View.VISIBLE);
            plot_iv.setVisibility(View.VISIBLE);
            slideview(false, comi_iv, 0, -125f, 0, 45f);
            slideview(false, dance_iv, 0, -47.5f, 0, -130f);
            slideview(false, funny_iv, 0, 120f, 0, 37.5f);
            slideview(false, literary_iv, 0, -77.5f, 0, -42.5f);
            slideview(false, music_iv, 0, 0, 0, 137.5f);
            slideview(false, plot_iv, 0, 92.5f, 0, -102.5f);
            firstPage = true;
        }
        initLabelData();
    }

    /**
     * 初始化标签数据
     */
    private void initLabelData() {
        // 从临时数组中取出已选中的标签  如果此显示的页面有之前选中的 则手动 setSelect(true);
        for (Integer key : recordSelect.keySet()) {
            if (firstPage) { //true 是第二页   false 是第一页
                switch (key) {
                    case 10:
                        comi_iv.setSelected(true);
                        break;
                    case 33:
                        dance_iv.setSelected(true);
                        break;
                    case 9:
                        funny_iv.setSelected(true);
                        break;
                    case 58:
                        literary_iv.setSelected(true);
                        break;
                    case 11:
                        music_iv.setSelected(true);
                        break;
                    case 59:
                        plot_iv.setSelected(true);
                        break;
                }
            } else {
                switch (key) {
                    case 6:
                        movement_iv.setSelected(true);
                        break;
                    case 3:
                        beauty_iv.setSelected(true);
                        break;
                    case 8:
                        fashion_iv.setSelected(true);
                        break;
                    case 5:
                        science_fiction_iv.setSelected(true);
                        break;
                    case 7:
                        tourism_iv.setSelected(true);
                        break;
                    case 4:
                        terrorist_iv.setSelected(true);
                        break;
                }
            }
        }

    }


    private void startAnimation() {
        image_iv.post(new Runnable() {
            @Override
            public void run() {
                animationDrawable.start();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        startTranslateAnimation();
                    }
                }, 1000);
            }
        });
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        Log.d("TAG", "arg0" + arg0);
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int arg0) {

    }

    /**
     * @return
     */
    private String assemblingString() {
        if(recordSelect.size() == 0){
            return "0";
        }
        StringBuffer stringBuffer = new StringBuffer();
        int i = 0;
        for (Integer key : recordSelect.keySet()) {
            if (i == 0) {
                stringBuffer.append(key);
            } else {
                stringBuffer.append("_"+ key);
            }
            i++;
        }
        return stringBuffer.toString();
    }

    private void goHome() {
        Intent intent = new Intent(GuideActivity.this, MainActivity.class);
        intent.putExtra("assemblingString",assemblingString());
        GuideActivity.this.startActivity(intent);
        overridePendingTransition(R.anim.fade, R.anim.hold);
        SimpleSharedPreferences.putBoolean("isFirstIn", true, GuideActivity.this);
        AppContent.fromWelcome = true;
        GuideActivity.this.finish();
    }
    /**
     * @Title: showToast
     * @Description: TODO 消息提示
     * @author hechuang
     * @date 2015-11-12
     * @param @param message    设定文件
     * @return void    返回类型
     */
    public void showToastMsg(String msg) {
        Toast.makeText(GuideActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}

