package com.xunixianshi.vrshow.player;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hch.utils.MLog;
import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.customview.RoundProgressBar;
import com.hch.viewlib.util.SimpleSharedPreferences;
import com.hch.viewlib.widget.NoScrollGridView;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.customview.CustomImageView;
import com.xunixianshi.vrshow.customview.NetWorkDialog;
import com.xunixianshi.vrshow.interfaces.ImageViewInterface;
import com.xunixianshi.vrshow.interfaces.NetWorkDialogButtonListener;
import com.xunixianshi.vrshow.interfaces.PlayLayoutInterface;
import com.xunixianshi.vrshow.obj.AllVideoResult;
import com.xunixianshi.vrshow.obj.AllVideoResultDataList;
import com.xunixianshi.vrshow.obj.ClassifyResultResourceList;
import com.xunixianshi.vrshow.obj.ClassifyVideoTypeResult;
import com.xunixianshi.vrshow.obj.ClassifyVideoTypeResultResourcesList;
import com.xunixianshi.vrshow.obj.DownLoadAddressResult;
import com.xunixianshi.vrshow.obj.PlayHistoryResult;
import com.xunixianshi.vrshow.obj.PlayHistoryResultDataList;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * User: hch
 * Date: 2016-06-27
 * Time: 11:19
 * FIXME
 */
public class PlayerLayoutGridView {
    public static final int UPDATE_PROGRESS = 200;

    @Bind(R.id.eyes_controller_ll)
    LinearLayout eyes_controller_ll;
    @Bind(R.id.eyes_controller_gridview_rl)
    RelativeLayout eyes_controller_gridview_rl;
    @Bind(R.id.eyes_controller_bootom_bt)
    Button eyes_controller_bootom_bt;
    @Bind(R.id.eyes_controller_bootom_rl)
    RelativeLayout eyes_controller_bootom_rl;

    @Bind(R.id.eyes_controller_video_gv)
    NoScrollGridView eyes_controller_video_gv;

    @Bind(R.id.eyes_controller_classify_ll)
    LinearLayout eyes_controller_classify_ll;
    @Bind(R.id.eyes_controller_video_ll)
    LinearLayout eyes_controller_video_ll;
    @Bind(R.id.eyes_controller_video_iv)
    ImageView eyes_controller_video_iv;

    @Bind(R.id.eyes_controller_game_ll)
    LinearLayout eyes_controller_game_ll;
    @Bind(R.id.eyes_controller_game_iv)
    ImageView eyes_controller_game_iv;
    @Bind(R.id.eyes_controller_classify_1)
    ImageView eyes_controller_classify_1;
    @Bind(R.id.eyes_controller_classify_2)
    ImageView eyes_controller_classify_2;
    @Bind(R.id.eyes_controller_classify_3)
    ImageView eyes_controller_classify_3;
    @Bind(R.id.eyes_controller_classify_4)
    ImageView eyes_controller_classify_4;
    @Bind(R.id.eyes_controller_classify_5)
    ImageView eyes_controller_classify_5;
    @Bind(R.id.eyes_controller_classify_6)
    ImageView eyes_controller_classify_6;
    @Bind(R.id.eyes_controller_classify_7)
    ImageView eyes_controller_classify_7;
    @Bind(R.id.eyes_controller_classify_8)
    ImageView eyes_controller_classify_8;
    @Bind(R.id.eyes_controller_classify_9)
    ImageView eyes_controller_classify_9;
    @Bind(R.id.eyes_controller_classify_10)
    ImageView eyes_controller_classify_10;
    @Bind(R.id.eyes_controller_classify_11)
    ImageView eyes_controller_classify_11;
    @Bind(R.id.eyes_controller_classify_12)
    ImageView eyes_controller_classify_12;
    @Bind(R.id.eyes_controller_classify_13)
    ImageView eyes_controller_classify_13;
    @Bind(R.id.eyes_controller_classify_14)
    ImageView eyes_controller_classify_14;
    @Bind(R.id.eyes_controller_classify_15)
    ImageView eyes_controller_classify_15;

    @Bind(R.id.eyes_controller_vrui_content_rl)
    RelativeLayout eyes_controller_vrui_content_rl;
    @Bind(R.id.eyes_controller_loading_rl)
    RelativeLayout eyes_controller_loading_rl;
    @Bind(R.id.eyes_controller_loading_iv)
    ImageView eyes_controller_loading_iv;
    @Bind(R.id.eyes_controller_emity_rl)
    RelativeLayout eyes_controller_emity_rl;
    @Bind(R.id.eyes_controller_emity_tv)
    TextView eyes_controller_emity_tv;

    @Bind(R.id.eyes_controller_retract_iv)
    ImageView eyes_controller_retract_iv;

    @Bind(R.id.eyes_controller_video_bootom_rl)
    RelativeLayout eyes_controller_video_bootom_rl;

    @Bind(R.id.eyes_controller_video_back_iv)
    ImageView eyes_controller_video_back_iv;

    @Bind(R.id.eyes_controller_video_page_rl)
    RelativeLayout eyes_controller_video_page_rl;

    @Bind(R.id.eyes_controller_video_last_iv)
    ImageView eyes_controller_video_last_iv;

    @Bind(R.id.eyes_controller_video_page_num_tv)
    TextView eyes_controller_video_page_num_tv;

    @Bind(R.id.eyes_controller_video_next_iv)
    ImageView eyes_controller_video_next_iv;

    @Bind(R.id.eyes_controller_center_iv)
    CustomImageView eyes_controller_center_iv;

    @Bind(R.id.eyes_controller_center_rpb)
    RoundProgressBar eyes_controller_center_rpb;

    @Bind(R.id.player_vr_loading_iv)
    ImageView player_loading_iv;

    @Bind(R.id.eyes_controller_back_bt)
    Button eyes_controller_back_bt;

    @Bind(R.id.eyes_controller_volume_big_bt)
    Button eyes_controller_volume_big_bt;

    @Bind(R.id.eyes_controller_volume_small_bt)
    Button eyes_controller_volume_small_bt;

    @Bind(R.id.eyes_controller_show_down_bt)
    Button eyes_controller_show_down_bt;

    private ImageView item_eyelayout_gridview_video_image1_iv;
    private ImageView item_eyelayout_gridview_video_image2_iv;
    private ImageView item_eyelayout_gridview_video_image3_iv;
    private ImageView item_eyelayout_gridview_video_image4_iv;
    private ImageView item_eyelayout_gridview_video_image5_iv;
    private ImageView item_eyelayout_gridview_video_image6_iv;
    private ImageView item_eyelayout_gridview_video_image7_iv;
    private ImageView item_eyelayout_gridview_video_image8_iv;
    private ImageView item_eyelayout_gridview_video_image9_iv;
    private ImageView item_eyelayout_gridview_video_play1_iv;
    private ImageView item_eyelayout_gridview_video_play2_iv;
    private ImageView item_eyelayout_gridview_video_play3_iv;
    private ImageView item_eyelayout_gridview_video_play4_iv;
    private ImageView item_eyelayout_gridview_video_play5_iv;
    private ImageView item_eyelayout_gridview_video_play6_iv;
    private ImageView item_eyelayout_gridview_video_play7_iv;
    private ImageView item_eyelayout_gridview_video_play8_iv;
    private ImageView item_eyelayout_gridview_video_play9_iv;
    private TextView item_eyelayout_gridview_video_name1_tv;
    private TextView item_eyelayout_gridview_video_name2_tv;
    private TextView item_eyelayout_gridview_video_name3_tv;
    private TextView item_eyelayout_gridview_video_name4_tv;
    private TextView item_eyelayout_gridview_video_name5_tv;
    private TextView item_eyelayout_gridview_video_name6_tv;
    private TextView item_eyelayout_gridview_video_name7_tv;
    private TextView item_eyelayout_gridview_video_name8_tv;
    private TextView item_eyelayout_gridview_video_name9_tv;

    private static final int REGIST_VIDEO = 200;

    private Context mContext;
    private EyeLayoutGridViewAdapter eyeLayoutGridViewAdapterClassify;
    private EyeLayoutGridViewAdapter eyeLayoutGridViewAdapterVideo;
    private List<String> urlsClassify = new ArrayList<String>();//显示图片iconList
    private List<String> urlsVideo = new ArrayList<String>();//显示图片iconList
    private ArrayList<ClassifyResultResourceList> classifyResultResourceLists = new ArrayList<ClassifyResultResourceList>();//视频分类列表
    private ArrayList<ClassifyVideoTypeResultResourcesList> classifyVideoTypeResultResourcesLists = new ArrayList<ClassifyVideoTypeResultResourcesList>();//视频列表
    private boolean isRegist = true;
    private boolean checkGridItem = false;
    private boolean checkBootomView = true;
    boolean prigressComplete = false;//确认进度完成
    private int centerProgressNum = 0;// 确认进度控件的进度
    private Handler mHandler;
    private PlayLayoutInterface mPlayLayoutInterface;
    private int gridViewType = 0;  //  默认为0  0：显示视频的分类; 1：显示每个分类下视频数据; 2：继续下一层 待开发（视频详情等）
    private int thisClassifyPage = 2;
    private int gridNum = 1;    //  默认为1  0：不检查vrui; 1：检查分类界面; 2：分类下视频视频列表界面  3： 游戏列表界面
    private NetWorkDialog netWorkDialog;
    private int typeId;
    private int thisVideoPage = 1;
    private int videoMaxPage = 10;
    private int listMode = 0; // 1、视频分类  2、播放历史  3、 全部视频
    private Animation operatingAnim;

    private final Handler mGridHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case REGIST_VIDEO:
                    registGridviewInterface();
                    break;
            }
            return true;
        }
    });

    public PlayerLayoutGridView(Context context, View viewID) {
        ButterKnife.bind(this, viewID);
        this.mContext = context;
        isRegist = false;
        init();
    }

    private void init() {
        eyes_controller_video_gv.setEnabled(false);
        eyeLayoutGridViewAdapterClassify = new EyeLayoutGridViewAdapter(mContext);
        eyeLayoutGridViewAdapterVideo = new EyeLayoutGridViewAdapter(mContext);
        operatingAnim = AnimationUtils.loadAnimation(mContext, R.anim.dialog_loading_animation);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
    }

    public void setHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public void setInterface(PlayLayoutInterface playLayoutInterface) {
        this.mPlayLayoutInterface = playLayoutInterface;
    }

    public void setData(ArrayList<ClassifyVideoTypeResultResourcesList> videoLists) {
        if (videoLists != null) {
            classifyVideoTypeResultResourcesLists.addAll(videoLists);
            eyeLayoutGridViewAdapterClassify.setGroup(classifyVideoTypeResultResourcesLists);
        }
//        MLog.d("eyes_controller_video_gv:" + eyes_controller_video_gv);
        eyes_controller_video_gv.setAdapter(eyeLayoutGridViewAdapterClassify);
    }

    public void setMaxPage(int maxPage) {
        videoMaxPage = maxPage;
        eyes_controller_video_page_num_tv.setText(thisVideoPage + "/" + maxPage);
    }

    public void updateGridview(ArrayList<ClassifyVideoTypeResultResourcesList> videoLists) {
//        mPlayLayoutInterface.showVrUILoading(false);
        if (videoLists != null) {
            classifyVideoTypeResultResourcesLists.clear();
            classifyVideoTypeResultResourcesLists.addAll(videoLists);
            eyeLayoutGridViewAdapterClassify.setGroup(classifyVideoTypeResultResourcesLists);
//            Message msg = mGridHandler.obtainMessage(REGIST_VIDEO);
//            mGridHandler.sendMessageDelayed(msg, 5000);
        }
    }

    /**
     * 初始化移动视图  player调用
     */
    public void initEyeLayout() {
        eyes_controller_center_iv.addLookObject(eyes_controller_back_bt, new ImageViewInterface() {
            boolean canRun = false;

            @Override
            public void calback(boolean calback) {
                canRun = true;
            }

            @Override
            public void onFocus(boolean isFocus) {
                if (canRun) {//开始显示确认进度
                    if (isFocus) {//中心点在控件上
                        if (prigressComplete) {//确认进度是否完成
                            //做响应的操作
                            centerProgressNum = 0;
                            prigressComplete = false;
                            canRun = false;
                            MLog.d("gridViewType::" + gridViewType);
                            switch (gridViewType) {
                                case 0:
                                    mPlayLayoutInterface.finishActivity();
                                    break;
                                case 1:
                                    eyes_controller_video_gv.setVisibility(View.VISIBLE);
                                    gridViewType = 0;
                                    gridNum = 1;
                                    break;

                            }
                        } else {//确认进度未完成 继续等待
                            updateProgressBar();
                        }
                        mPlayLayoutInterface.onFocusViewNum(7, true);
                    } else {//中心点离开控件
                        mPlayLayoutInterface.onFocusViewNum(7, false);
                        centerProgressNum = 0;
                        prigressComplete = false;
                        canRun = false;
                        goneProgressBar();
                    }
                }
            }
        });
        eyes_controller_center_iv.addLookObject(eyes_controller_volume_big_bt, new ImageViewInterface() {
            boolean canRun = false;

            @Override
            public void calback(boolean calback) {
                canRun = true;
            }

            @Override
            public void onFocus(boolean isFocus) {
                if (canRun) {//开始显示确认进度
                    if (isFocus) {//中心点在控件上
                        if (prigressComplete) {//确认进度是否完成
                            //做响应的操作
                            centerProgressNum = 0;
                            prigressComplete = false;
                            canRun = false;
                            mPlayLayoutInterface.volumeAdd();
                        } else {//确认进度未完成 继续等待
                            updateProgressBar();
                        }
                        mPlayLayoutInterface.onFocusViewNum(9, true);
                    } else {//中心点离开控件
                        mPlayLayoutInterface.onFocusViewNum(9, false);
                        centerProgressNum = 0;
                        prigressComplete = false;
                        canRun = false;
                        goneProgressBar();
                    }
                }
            }
        });
        eyes_controller_center_iv.addLookObject(eyes_controller_volume_small_bt, new ImageViewInterface() {
            boolean canRun = false;

            @Override
            public void calback(boolean calback) {
                canRun = true;
            }

            @Override
            public void onFocus(boolean isFocus) {
                if (canRun) {//开始显示确认进度
                    if (isFocus) {//中心点在控件上
                        if (prigressComplete) {//确认进度是否完成
                            //做响应的操作
                            centerProgressNum = 0;
                            prigressComplete = false;
                            canRun = false;
                            mPlayLayoutInterface.volumeSub();
                        } else {//确认进度未完成 继续等待
                            updateProgressBar();
                        }
                        mPlayLayoutInterface.onFocusViewNum(8, true);
                    } else {//中心点离开控件
                        mPlayLayoutInterface.onFocusViewNum(8, false);
                        centerProgressNum = 0;
                        prigressComplete = false;
                        canRun = false;
                        goneProgressBar();
                    }
                }
            }
        });
        eyes_controller_center_iv.addLookObject(eyes_controller_show_down_bt, new ImageViewInterface() {
            boolean canRun = false;

            @Override
            public void calback(boolean calback) {
                canRun = true;
            }

            @Override
            public void onFocus(boolean isFocus) {
                if (canRun) {//开始显示确认进度
                    if (isFocus) {//中心点在控件上
                        if (prigressComplete) {//确认进度是否完成
                            //做响应的操作
                            centerProgressNum = 0;
                            prigressComplete = false;
                            canRun = false;
                            mPlayLayoutInterface.showGridView(true);
                        } else {//确认进度未完成 继续等待
                            updateProgressBar();
                        }
                        mPlayLayoutInterface.onFocusViewNum(10, true);
                    } else {//中心点离开控件
                        mPlayLayoutInterface.onFocusViewNum(10, false);
                        centerProgressNum = 0;
                        prigressComplete = false;
                        canRun = false;
                        goneProgressBar();
                    }
                }
            }
        });
        eyes_controller_center_iv.setBottomViewFocus(eyes_controller_bootom_bt, new ImageViewInterface() {

            @Override
            public void calback(boolean calback) {
            }

            @Override
            public void onFocus(boolean isFocus) {
                if (isFocus) {//中心点在控件上
                    mPlayLayoutInterface.onFocusViewNum(29, true);
                } else {//中心点离开控件
                    mPlayLayoutInterface.onFocusViewNum(29, false);
                }
            }
        });
        eyes_controller_center_iv.addGridView(eyes_controller_video_gv, new ImageViewInterface() {
            @Override
            public void calback(boolean calback) {

            }

            @Override
            public void onFocus(boolean isFocus) {
                if (!isRegist) {
                    isRegist = true;
                    mPlayLayoutInterface.initVideoView();
//                    registGridviewInterface();
//                    registGridVideoInterface();
                }
            }
        });
        eyes_controller_center_iv.addGridControlObject(eyes_controller_video_ll, new ImageViewInterface() {
            boolean canRun = false;

            @Override
            public void calback(boolean calback) {
                canRun = true;
            }

            @Override
            public void onFocus(boolean isFocus) {
                if (canRun) {//开始显示确认进度
                    if (isFocus) {//中心点在控件上
                        if (prigressComplete) {//确认进度是否完成
                            //做响应的操作
                            centerProgressNum = 0;
                            prigressComplete = false;
                            canRun = false;

                        } else {//确认进度未完成 继续等待
                            updateProgressBar();
                        }
                        mPlayLayoutInterface.onFocusViewNum(1, true);
                    } else {//中心点离开控件
                        mPlayLayoutInterface.onFocusViewNum(1, false);
                        centerProgressNum = 0;
                        prigressComplete = false;
                        canRun = false;
                        goneProgressBar();
                    }
                }
            }
        });
        eyes_controller_center_iv.addGridControlObject(eyes_controller_game_ll, new ImageViewInterface() {
            boolean canRun = false;

            @Override
            public void calback(boolean calback) {
                canRun = true;
            }

            @Override
            public void onFocus(boolean isFocus) {
                if (canRun) {//开始显示确认进度
                    if (isFocus) {//中心点在控件上
                        if (prigressComplete) {//确认进度是否完成
                            //做响应的操作
                            centerProgressNum = 0;
                            prigressComplete = false;
                            canRun = false;

//                            mPlayLayoutInterface.showGridView(false);
                        } else {//确认进度未完成 继续等待
                            updateProgressBar();
                        }
                        mPlayLayoutInterface.onFocusViewNum(2, true);
                    } else {//中心点离开控件
                        mPlayLayoutInterface.onFocusViewNum(2, false);
                        centerProgressNum = 0;
                        prigressComplete = false;
                        canRun = false;
                        goneProgressBar();
                    }
                }
            }
        });
        eyes_controller_center_iv.addClassifyObject(eyes_controller_classify_1, new ImageViewInterface() {
            boolean canRun = false;

            @Override
            public void calback(boolean calback) {
                canRun = true;
            }

            @Override
            public void onFocus(boolean isFocus) {
                if (canRun) {//开始显示确认进度
                    if (isFocus) {//中心点在控件上
                        if (prigressComplete) {//确认进度是否完成
                            //做响应的操作
                            canRun = false;
                            videoClassifySelect(3);
//                            mPlayLayoutInterface.showVrUILoading(true);
//                            mPlayLayoutInterface.showGridView(false);
                        } else {//确认进度未完成 继续等待
                            updateProgressBar();
                        }

                    } else {//中心点离开控件
                        centerProgressNum = 0;
                        prigressComplete = false;
                        canRun = false;
                        goneProgressBar();
                    }
                }
            }
        });
        eyes_controller_center_iv.addClassifyObject(eyes_controller_classify_2, new ImageViewInterface() {
            boolean canRun = false;

            @Override
            public void calback(boolean calback) {
                canRun = true;
            }

            @Override
            public void onFocus(boolean isFocus) {
                if (canRun) {//开始显示确认进度
                    if (isFocus) {//中心点在控件上
                        if (prigressComplete) {//确认进度是否完成
                            //做响应的操作
                            canRun = false;
                            videoClassifySelect(33);
//                            mPlayLayoutInterface.showVrUILoading(true);
//                            mPlayLayoutInterface.showGridView(false);
                        } else {//确认进度未完成 继续等待
                            updateProgressBar();
                        }

                    } else {//中心点离开控件
                        centerProgressNum = 0;
                        prigressComplete = false;
                        canRun = false;
                        goneProgressBar();
                    }
                }
            }
        });
        eyes_controller_center_iv.addClassifyObject(eyes_controller_classify_3, new ImageViewInterface() {
            boolean canRun = false;

            @Override
            public void calback(boolean calback) {
                canRun = true;
            }

            @Override
            public void onFocus(boolean isFocus) {
                if (canRun) {//开始显示确认进度
                    if (isFocus) {//中心点在控件上
                        if (prigressComplete) {//确认进度是否完成
                            //做响应的操作
                            canRun = false;
                            videoClassifySelect(9);
//                            mPlayLayoutInterface.showVrUILoading(true);
//                            mPlayLayoutInterface.showGridView(false);
                        } else {//确认进度未完成 继续等待
                            updateProgressBar();
                        }

                    } else {//中心点离开控件
                        centerProgressNum = 0;
                        prigressComplete = false;
                        canRun = false;
                        goneProgressBar();
                    }
                }
            }
        });
        eyes_controller_center_iv.addClassifyObject(eyes_controller_classify_4, new ImageViewInterface() {
            boolean canRun = false;

            @Override
            public void calback(boolean calback) {
                canRun = true;
            }

            @Override
            public void onFocus(boolean isFocus) {
                if (canRun) {//开始显示确认进度
                    if (isFocus) {//中心点在控件上
                        if (prigressComplete) {//确认进度是否完成
                            //做响应的操作
                            canRun = false;
                            videoClassifySelect(8);
//                            mPlayLayoutInterface.showVrUILoading(true);
//                            mPlayLayoutInterface.showGridView(false);
                        } else {//确认进度未完成 继续等待
                            updateProgressBar();
                        }

                    } else {//中心点离开控件
                        centerProgressNum = 0;
                        prigressComplete = false;
                        canRun = false;
                        goneProgressBar();
                    }
                }
            }
        });
        eyes_controller_center_iv.addClassifyObject(eyes_controller_classify_5, new ImageViewInterface() {
            boolean canRun = false;

            @Override
            public void calback(boolean calback) {
                canRun = true;
            }

            @Override
            public void onFocus(boolean isFocus) {
                if (canRun) {//开始显示确认进度
                    if (isFocus) {//中心点在控件上
                        if (prigressComplete) {//确认进度是否完成
                            //做响应的操作
                            canRun = false;
                            videoClassifySelect(7);
//                            mPlayLayoutInterface.showVrUILoading(true);
//                            mPlayLayoutInterface.showGridView(false);
                        } else {//确认进度未完成 继续等待
                            updateProgressBar();
                        }

                    } else {//中心点离开控件
                        centerProgressNum = 0;
                        prigressComplete = false;
                        canRun = false;
                        goneProgressBar();
                    }
                }
            }
        });
        eyes_controller_center_iv.addClassifyObject(eyes_controller_classify_6, new ImageViewInterface() {
            boolean canRun = false;

            @Override
            public void calback(boolean calback) {
                canRun = true;
            }

            @Override
            public void onFocus(boolean isFocus) {
                if (canRun) {//开始显示确认进度
                    if (isFocus) {//中心点在控件上
                        if (prigressComplete) {//确认进度是否完成
                            //做响应的操作
                            canRun = false;
                            videoClassifySelect(10);
//                            mPlayLayoutInterface.showVrUILoading(true);
//                            mPlayLayoutInterface.showGridView(false);
                        } else {//确认进度未完成 继续等待
                            updateProgressBar();
                        }

                    } else {//中心点离开控件
                        centerProgressNum = 0;
                        prigressComplete = false;
                        canRun = false;
                        goneProgressBar();
                    }
                }
            }
        });
        eyes_controller_center_iv.addClassifyObject(eyes_controller_classify_7, new ImageViewInterface() {
            boolean canRun = false;

            @Override
            public void calback(boolean calback) {
                canRun = true;
            }

            @Override
            public void onFocus(boolean isFocus) {
                if (canRun) {//开始显示确认进度
                    if (isFocus) {//中心点在控件上
                        if (prigressComplete) {//确认进度是否完成
                            //做响应的操作
                            canRun = false;
                            videoClassifySelect(11);
//                            mPlayLayoutInterface.showVrUILoading(true);
//                            mPlayLayoutInterface.showGridView(false);
                        } else {//确认进度未完成 继续等待
                            updateProgressBar();
                        }

                    } else {//中心点离开控件
                        centerProgressNum = 0;
                        prigressComplete = false;
                        canRun = false;
                        goneProgressBar();
                    }
                }
            }
        });
        eyes_controller_center_iv.addClassifyObject(eyes_controller_classify_8, new ImageViewInterface() {
            boolean canRun = false;

            @Override
            public void calback(boolean calback) {
                canRun = true;
            }

            @Override
            public void onFocus(boolean isFocus) {
                if (canRun) {//开始显示确认进度
                    if (isFocus) {//中心点在控件上
                        if (prigressComplete) {//确认进度是否完成
                            //做响应的操作
                            canRun = false;
                            videoClassifySelect(6);
//                            mPlayLayoutInterface.showVrUILoading(true);
//                            mPlayLayoutInterface.showGridView(false);
                        } else {//确认进度未完成 继续等待
                            updateProgressBar();
                        }

                    } else {//中心点离开控件
                        centerProgressNum = 0;
                        prigressComplete = false;
                        canRun = false;
                        goneProgressBar();
                    }
                }
            }
        });
        eyes_controller_center_iv.addClassifyObject(eyes_controller_classify_9, new ImageViewInterface() {
            boolean canRun = false;

            @Override
            public void calback(boolean calback) {
                canRun = true;
            }

            @Override
            public void onFocus(boolean isFocus) {
                if (canRun) {//开始显示确认进度
                    if (isFocus) {//中心点在控件上
                        if (prigressComplete) {//确认进度是否完成
                            //做响应的操作
                            canRun = false;
                            videoClassifySelect(5);
//                            mPlayLayoutInterface.showVrUILoading(true);
//                            mPlayLayoutInterface.showGridView(false);
                        } else {//确认进度未完成 继续等待
                            updateProgressBar();
                        }

                    } else {//中心点离开控件
                        centerProgressNum = 0;
                        prigressComplete = false;
                        canRun = false;
                        goneProgressBar();
                    }
                }
            }
        });
        eyes_controller_center_iv.addClassifyObject(eyes_controller_classify_10, new ImageViewInterface() {
            boolean canRun = false;

            @Override
            public void calback(boolean calback) {
                canRun = true;
            }

            @Override
            public void onFocus(boolean isFocus) {
                if (canRun) {//开始显示确认进度
                    if (isFocus) {//中心点在控件上
                        if (prigressComplete) {//确认进度是否完成
                            //做响应的操作
                            canRun = false;
                            videoClassifySelect(59);
//                            mPlayLayoutInterface.showVrUILoading(true);
//                            mPlayLayoutInterface.showGridView(false);
                        } else {//确认进度未完成 继续等待
                            updateProgressBar();
                        }

                    } else {//中心点离开控件
                        centerProgressNum = 0;
                        prigressComplete = false;
                        canRun = false;
                        goneProgressBar();
                    }
                }
            }
        });
        eyes_controller_center_iv.addClassifyObject(eyes_controller_classify_11, new ImageViewInterface() {
            boolean canRun = false;

            @Override
            public void calback(boolean calback) {
                canRun = true;
            }

            @Override
            public void onFocus(boolean isFocus) {
                if (canRun) {//开始显示确认进度
                    if (isFocus) {//中心点在控件上
                        if (prigressComplete) {//确认进度是否完成
                            //做响应的操作
                            canRun = false;
                            videoClassifySelect(58);
//                            mPlayLayoutInterface.showVrUILoading(true);
//                            mPlayLayoutInterface.showGridView(false);
                        } else {//确认进度未完成 继续等待
                            updateProgressBar();
                        }

                    } else {//中心点离开控件
                        centerProgressNum = 0;
                        prigressComplete = false;
                        canRun = false;
                        goneProgressBar();
                    }
                }
            }
        });
        eyes_controller_center_iv.addClassifyObject(eyes_controller_classify_12, new ImageViewInterface() {
            boolean canRun = false;

            @Override
            public void calback(boolean calback) {
                canRun = true;
            }

            @Override
            public void onFocus(boolean isFocus) {
                if (canRun) {//开始显示确认进度
                    if (isFocus) {//中心点在控件上
                        if (prigressComplete) {//确认进度是否完成
                            //做响应的操作
                            canRun = false;
                            videoClassifySelect(4);
//                            mPlayLayoutInterface.showVrUILoading(true);
//                            mPlayLayoutInterface.showGridView(false);
                        } else {//确认进度未完成 继续等待
                            updateProgressBar();
                        }

                    } else {//中心点离开控件
                        centerProgressNum = 0;
                        prigressComplete = false;
                        canRun = false;
                        goneProgressBar();
                    }
                }
            }
        });
        eyes_controller_center_iv.addClassifyObject(eyes_controller_classify_13, new ImageViewInterface() {
            boolean canRun = false;

            @Override
            public void calback(boolean calback) {
                canRun = true;
            }

            @Override
            public void onFocus(boolean isFocus) {
                if (canRun) {//开始显示确认进度
                    if (isFocus) {//中心点在控件上
//                        if (prigressComplete) {//确认进度是否完成
//                            //做响应的操作
//                            centerProgressNum = 0;
//                            prigressComplete = false;
//                            canRun = false;
//
////                            mPlayLayoutInterface.showGridView(false);
//                        } else {//确认进度未完成 继续等待
//                            updateProgressBar();
//                        }
//
//                    } else {//中心点离开控件
//                        centerProgressNum = 0;
//                        prigressComplete = false;
//                        canRun = false;
//                        goneProgressBar();
                    }
                }
            }
        });
        eyes_controller_center_iv.addClassifyObject(eyes_controller_classify_14, new ImageViewInterface() {
            boolean canRun = false;

            @Override
            public void calback(boolean calback) {
                canRun = true;
            }

            @Override
            public void onFocus(boolean isFocus) {
                if (canRun) {//开始显示确认进度
                    if (isFocus) {//中心点在控件上
                        if (prigressComplete) {//确认进度是否完成
                            //做响应的操作
                            centerProgressNum = 0;
                            prigressComplete = false;
                            canRun = false;
                            gridNum = 2;
                            getAllVideoList();
//                            mPlayLayoutInterface.showVrUILoading(true);
                        } else {//确认进度未完成 继续等待
                            updateProgressBar();
                        }

                    } else {//中心点离开控件
                        centerProgressNum = 0;
                        prigressComplete = false;
                        canRun = false;
                        goneProgressBar();
                    }
                }
            }
        });
        eyes_controller_center_iv.addClassifyObject(eyes_controller_classify_15, new ImageViewInterface() {
            boolean canRun = false;

            @Override
            public void calback(boolean calback) {
                canRun = true;
            }

            @Override
            public void onFocus(boolean isFocus) {
                if (canRun) {//开始显示确认进度
                    if (isFocus) {//中心点在控件上
                        if (prigressComplete) {//确认进度是否完成
                            //做响应的操作
                            centerProgressNum = 0;
                            prigressComplete = false;
                            canRun = false;
                            thisVideoPage = 1;
                            getHistoryVideoList();
                        } else {//确认进度未完成 继续等待
                            updateProgressBar();
                        }

                    } else {//中心点离开控件
                        centerProgressNum = 0;
                        prigressComplete = false;
                        canRun = false;
                        goneProgressBar();
                    }
                }
            }
        });
        eyes_controller_center_iv.addGridControlObject(eyes_controller_video_back_iv, new ImageViewInterface() {
            boolean canRun = false;

            @Override
            public void calback(boolean calback) {
                canRun = true;
            }

            @Override
            public void onFocus(boolean isFocus) {
                if (canRun) {//开始显示确认进度
                    if (isFocus) {//中心点在控件上
                        if (prigressComplete) {//确认进度是否完成
                            //做响应的操作
                            centerProgressNum = 0;
                            prigressComplete = false;
                            canRun = false;
                            checkGridItem = true;
                            checkBootomView = false;
                            thisVideoPage = 1;
                            mPlayLayoutInterface.showEmptyPage(false, "");
                            mPlayLayoutInterface.showVideoList(false);
                        } else {//确认进度未完成 继续等待
                            updateProgressBar();
                        }
                        mPlayLayoutInterface.onFocusViewNum(3, true);
                    } else {//中心点离开控件
                        mPlayLayoutInterface.onFocusViewNum(3, false);
                        centerProgressNum = 0;
                        prigressComplete = false;
                        canRun = false;
                        goneProgressBar();
                    }
                }
            }
        });
        eyes_controller_center_iv.addGridControlObject(eyes_controller_video_last_iv, new ImageViewInterface() {
            boolean canRun = false;

            @Override
            public void calback(boolean calback) {
                canRun = true;
            }

            @Override
            public void onFocus(boolean isFocus) {
                if (canRun) {//开始显示确认进度
                    if (thisVideoPage != 1) {
                        if (isFocus) {//中心点在控件上
                            if (prigressComplete) {//确认进度是否完成
                                //做响应的操作
                                centerProgressNum = 0;
                                prigressComplete = false;
                                canRun = false;
                                checkGridItem = true;
                                checkBootomView = false;
                                thisVideoPage--;
                                switch (listMode) {
                                    case 1:
                                        getVideoList();
                                        break;
                                    case 2:
                                        getHistoryVideoList();
                                        break;
                                    case 3:
                                        getAllVideoList();
                                        break;
                                }
                                mPlayLayoutInterface.updateVideoPage(thisVideoPage + "/" + videoMaxPage);
                            } else {//确认进度未完成 继续等待
                                updateProgressBar();
                            }
                            mPlayLayoutInterface.onFocusViewNum(4, true);
                        } else {//中心点离开控件
                            mPlayLayoutInterface.onFocusViewNum(4, false);
                            centerProgressNum = 0;
                            prigressComplete = false;
                            canRun = false;
                            goneProgressBar();
                        }
                    } else {
                        mPlayLayoutInterface.onFocusViewNum(4, false);
                    }
                }
            }
        });
        eyes_controller_center_iv.addGridControlObject(eyes_controller_video_next_iv, new ImageViewInterface() {
            boolean canRun = false;

            @Override
            public void calback(boolean calback) {
                canRun = true;
            }

            @Override
            public void onFocus(boolean isFocus) {
                if (canRun) {//开始显示确认进度
                    if (thisVideoPage != videoMaxPage) {
                        if (isFocus) {//中心点在控件上
                            if (prigressComplete) {//确认进度是否完成
                                //做响应的操作
                                centerProgressNum = 0;
                                prigressComplete = false;
                                canRun = false;
                                checkGridItem = true;
                                checkBootomView = false;
                                thisVideoPage++;
                                switch (listMode) {
                                    case 1:
                                        getVideoList();
                                        break;
                                    case 2:
                                        getHistoryVideoList();
                                        break;
                                    case 3:
                                        getAllVideoList();
                                        break;
                                }
                                mPlayLayoutInterface.updateVideoPage(thisVideoPage + "/" + videoMaxPage);
                            } else {//确认进度未完成 继续等待
                                updateProgressBar();
                            }
                            mPlayLayoutInterface.onFocusViewNum(5, true);
                        } else {//中心点离开控件
                            mPlayLayoutInterface.onFocusViewNum(5, false);
                            centerProgressNum = 0;
                            prigressComplete = false;
                            canRun = false;
                            goneProgressBar();
                        }
                    } else {
                        mPlayLayoutInterface.onFocusViewNum(5, false);
                    }
                }
            }
        });
        eyes_controller_center_iv.addGridControlObject(eyes_controller_retract_iv, new ImageViewInterface() {
            boolean canRun = false;

            @Override
            public void calback(boolean calback) {
                canRun = true;
            }

            @Override
            public void onFocus(boolean isFocus) {
                if (canRun) {//开始显示确认进度
                    if (isFocus) {//中心点在控件上
                        if (prigressComplete) {//确认进度是否完成
                            //做响应的操作
                            centerProgressNum = 0;
                            prigressComplete = false;
                            canRun = false;
                            checkGridItem = false;
                            checkBootomView = true;
                            mPlayLayoutInterface.showGridView(false);
                        } else {//确认进度未完成 继续等待
                            updateProgressBar();
                        }
                        mPlayLayoutInterface.onFocusViewNum(6, true);
                    } else {//中心点离开控件
                        mPlayLayoutInterface.onFocusViewNum(6, false);
                        centerProgressNum = 0;
                        prigressComplete = false;
                        canRun = false;
                        goneProgressBar();
                    }
                }
            }
        });
    }

    public void videoClassifySelect(int videoTypeNum) {
        centerProgressNum = 0;
        prigressComplete = false;
        typeId = videoTypeNum;
        thisVideoPage = 1;
        getVideoList();
    }


    //造成卡顿  需要优化
    public void setOnFocusView(int num, boolean onFocus) {
        if (onFocus) {
            switch (num) {
                case 1:
                    if (eyes_controller_video_iv != null) {
                        eyes_controller_video_iv.setBackgroundResource(R.drawable.eyes_vrui_videos_on);
                    }
                    break;
                case 2:
                    if (eyes_controller_game_iv != null) {
                        eyes_controller_game_iv.setBackgroundResource(R.drawable.eyes_vrui_games_on);
                    }
                    break;
                case 3:
                    if (eyes_controller_video_back_iv != null) {
                        eyes_controller_video_back_iv.setBackgroundResource(R.drawable.eyes_vrui_back_bt_on);
                    }
                    break;
                case 4:
                    if (eyes_controller_video_last_iv != null) {
                        eyes_controller_video_last_iv.setBackgroundResource(R.drawable.eyes_vrui_left_flip_page_on);
                    }
                    break;
                case 5:
                    if (eyes_controller_video_next_iv != null) {
                        eyes_controller_video_next_iv.setBackgroundResource(R.drawable.eyes_vrui_right_flip_page_on);
                    }
                    break;
                case 6:
                    if (eyes_controller_retract_iv != null) {
                        eyes_controller_retract_iv.setBackgroundResource(R.drawable.eyes_vrui_hide_on);
                    }
                    break;
                case 7:
                    if (eyes_controller_back_bt != null) {
                        eyes_controller_back_bt.setBackgroundResource(R.drawable.eyes_vrui_back_bt_on);
                    }
                    break;
                case 8:
                    if (eyes_controller_volume_small_bt != null) {
                        eyes_controller_volume_small_bt.setBackgroundResource(R.drawable.eyes_sound_small_on);
                    }
                    break;
                case 9:
                    if (eyes_controller_volume_big_bt != null) {
                        eyes_controller_volume_big_bt.setBackgroundResource(R.drawable.eyes_sound_big_on);
                    }
                    break;
                case 10:
                    if (eyes_controller_show_down_bt != null) {
                        eyes_controller_show_down_bt.setBackgroundResource(R.drawable.eyes_vrui_show_on);
                    }
                    break;
                case 11:
                    if (item_eyelayout_gridview_video_play1_iv != null) {
                        item_eyelayout_gridview_video_play1_iv.setBackgroundResource(R.drawable.eyes_vrui_video_play_on);
                    }
                    break;
                case 12:
                    if (item_eyelayout_gridview_video_play2_iv != null) {
                        item_eyelayout_gridview_video_play2_iv.setBackgroundResource(R.drawable.eyes_vrui_video_play_on);
                    }
                    break;
                case 13:
                    if (item_eyelayout_gridview_video_play3_iv != null) {
                        item_eyelayout_gridview_video_play3_iv.setBackgroundResource(R.drawable.eyes_vrui_video_play_on);
                    }
                    break;
                case 14:
                    if (item_eyelayout_gridview_video_play4_iv != null) {
                        item_eyelayout_gridview_video_play4_iv.setBackgroundResource(R.drawable.eyes_vrui_video_play_on);
                    }
                    break;
                case 15:
                    if (item_eyelayout_gridview_video_play5_iv != null) {
                        item_eyelayout_gridview_video_play5_iv.setBackgroundResource(R.drawable.eyes_vrui_video_play_on);
                    }
                    break;
                case 16:
                    if (item_eyelayout_gridview_video_play6_iv != null) {
                        item_eyelayout_gridview_video_play6_iv.setBackgroundResource(R.drawable.eyes_vrui_video_play_on);
                    }
                    break;
                case 17:
                    if (item_eyelayout_gridview_video_play7_iv != null) {
                        item_eyelayout_gridview_video_play7_iv.setBackgroundResource(R.drawable.eyes_vrui_video_play_on);
                    }
                    break;
                case 18:
                    if (item_eyelayout_gridview_video_play8_iv != null) {
                        item_eyelayout_gridview_video_play8_iv.setBackgroundResource(R.drawable.eyes_vrui_video_play_on);
                    }
                    break;
                case 19:
                    if (item_eyelayout_gridview_video_play9_iv != null) {
                        item_eyelayout_gridview_video_play9_iv.setBackgroundResource(R.drawable.eyes_vrui_video_play_on);
                    }
                    break;
//                case 20:
//                    if (item_eyelayout_gridview_video_name1_tv != null) {
//                        item_eyelayout_gridview_video_name1_tv.setVisibility(View.VISIBLE);
//                    }
//                    break;
//                case 21:
//                    if (item_eyelayout_gridview_video_name2_tv != null) {
//                        item_eyelayout_gridview_video_name2_tv.setVisibility(View.VISIBLE);
//                    }
//                    break;
//                case 22:
//                    if (item_eyelayout_gridview_video_name3_tv != null) {
//                        item_eyelayout_gridview_video_name3_tv.setVisibility(View.VISIBLE);
//                    }
//                    break;
//                case 23:
//                    if (item_eyelayout_gridview_video_name4_tv != null) {
//                        item_eyelayout_gridview_video_name4_tv.setVisibility(View.VISIBLE);
//                    }
//                    break;
//                case 24:
//                    if (item_eyelayout_gridview_video_name5_tv != null) {
//                        item_eyelayout_gridview_video_name5_tv.setVisibility(View.VISIBLE);
//                    }
//                    break;
//                case 25:
//                    if (item_eyelayout_gridview_video_name6_tv != null) {
//                        item_eyelayout_gridview_video_name6_tv.setVisibility(View.VISIBLE);
//                    }
//                    break;
//                case 26:
//                    if (item_eyelayout_gridview_video_name7_tv != null) {
//                        item_eyelayout_gridview_video_name7_tv.setVisibility(View.VISIBLE);
//                    }
//                    break;
//                case 27:
//                    if (item_eyelayout_gridview_video_name8_tv != null) {
//                        item_eyelayout_gridview_video_name8_tv.setVisibility(View.VISIBLE);
//                    }
//                    break;
//                case 28:
//                    if (item_eyelayout_gridview_video_name9_tv != null) {
//                        item_eyelayout_gridview_video_name9_tv.setVisibility(View.VISIBLE);
//                    }
//                    break;
                case 29:
                    if (eyes_controller_bootom_rl != null) {
                        eyes_controller_bootom_rl.setAlpha(1.0f);
                    }
                    break;
            }
        } else {
            switch (num) {
                case 1:
                    if (eyes_controller_video_iv != null) {
                        eyes_controller_video_iv.setBackgroundResource(R.drawable.eyes_vrui_videos);
                    }
                    break;
                case 2:
                    if (eyes_controller_game_iv != null) {
                        eyes_controller_game_iv.setBackgroundResource(R.drawable.eyes_vrui_games);
                    }
                    break;
                case 3:
                    if (eyes_controller_video_back_iv != null) {
                        eyes_controller_video_back_iv.setBackgroundResource(R.drawable.eyes_vrui_back_bt);
                    }
                    break;
                case 4:
                    if (eyes_controller_video_last_iv != null) {
                        eyes_controller_video_last_iv.setBackgroundResource(R.drawable.eyes_vrui_left_flip_page);
                    }
                    break;
                case 5:
                    if (eyes_controller_video_next_iv != null) {
                        eyes_controller_video_next_iv.setBackgroundResource(R.drawable.eyes_vrui_right_flip_page);
                    }
                    break;
                case 6:
                    if (eyes_controller_retract_iv != null) {
                        eyes_controller_retract_iv.setBackgroundResource(R.drawable.eyes_vrui_hide);
                    }
                    break;
                case 7:
                    if (eyes_controller_back_bt != null) {
                        eyes_controller_back_bt.setBackgroundResource(R.drawable.eyes_vrui_back_bt);
                    }
                    break;
                case 8:
                    if (eyes_controller_volume_small_bt != null) {
                        eyes_controller_volume_small_bt.setBackgroundResource(R.drawable.eyes_sound_small);
                    }
                    break;
                case 9:
                    if (eyes_controller_volume_big_bt != null) {
                        eyes_controller_volume_big_bt.setBackgroundResource(R.drawable.eyes_sound_big);
                    }
                    break;
                case 10:
                    if (eyes_controller_show_down_bt != null) {
                        eyes_controller_show_down_bt.setBackgroundResource(R.drawable.eyes_vrui_show);
                    }
                    break;
                case 11:
                    if (item_eyelayout_gridview_video_play1_iv != null) {
                        item_eyelayout_gridview_video_play1_iv.setBackgroundResource(R.drawable.eyes_vrui_video_play);
                    }
                    break;
                case 12:
                    if (item_eyelayout_gridview_video_play2_iv != null) {
                        item_eyelayout_gridview_video_play2_iv.setBackgroundResource(R.drawable.eyes_vrui_video_play);
                    }
                    break;
                case 13:
                    if (item_eyelayout_gridview_video_play3_iv != null) {
                        item_eyelayout_gridview_video_play3_iv.setBackgroundResource(R.drawable.eyes_vrui_video_play);
                    }
                    break;
                case 14:
                    if (item_eyelayout_gridview_video_play4_iv != null) {
                        item_eyelayout_gridview_video_play4_iv.setBackgroundResource(R.drawable.eyes_vrui_video_play);
                    }
                    break;
                case 15:
                    if (item_eyelayout_gridview_video_play5_iv != null) {
                        item_eyelayout_gridview_video_play5_iv.setBackgroundResource(R.drawable.eyes_vrui_video_play);
                    }
                    break;
                case 16:
                    if (item_eyelayout_gridview_video_play6_iv != null) {
                        item_eyelayout_gridview_video_play6_iv.setBackgroundResource(R.drawable.eyes_vrui_video_play);
                    }
                    break;
                case 17:
                    if (item_eyelayout_gridview_video_play7_iv != null) {
                        item_eyelayout_gridview_video_play7_iv.setBackgroundResource(R.drawable.eyes_vrui_video_play);
                    }
                    break;
                case 18:
                    if (item_eyelayout_gridview_video_play8_iv != null) {
                        item_eyelayout_gridview_video_play8_iv.setBackgroundResource(R.drawable.eyes_vrui_video_play);
                    }
                    break;
                case 19:
                    if (item_eyelayout_gridview_video_play9_iv != null) {
                        item_eyelayout_gridview_video_play9_iv.setBackgroundResource(R.drawable.eyes_vrui_video_play);
                    }
                    break;
//                case 20:
//                    if (item_eyelayout_gridview_video_name1_tv != null) {
//                        item_eyelayout_gridview_video_name1_tv.setVisibility(View.INVISIBLE);
//                    }
//                    break;
//                case 21:
//                    if (item_eyelayout_gridview_video_name2_tv != null) {
//                        item_eyelayout_gridview_video_name2_tv.setVisibility(View.INVISIBLE);
//                    }
//                    break;
//                case 22:
//                    if (item_eyelayout_gridview_video_name3_tv != null) {
//                        item_eyelayout_gridview_video_name3_tv.setVisibility(View.INVISIBLE);
//                    }
//                    break;
//                case 23:
//                    if (item_eyelayout_gridview_video_name4_tv != null) {
//                        item_eyelayout_gridview_video_name4_tv.setVisibility(View.INVISIBLE);
//                    }
//                    break;
//                case 24:
//                    if (item_eyelayout_gridview_video_name5_tv != null) {
//                        item_eyelayout_gridview_video_name5_tv.setVisibility(View.INVISIBLE);
//                    }
//                    break;
//                case 25:
//                    if (item_eyelayout_gridview_video_name6_tv != null) {
//                        item_eyelayout_gridview_video_name6_tv.setVisibility(View.INVISIBLE);
//                    }
//                    break;
//                case 26:
//                    if (item_eyelayout_gridview_video_name7_tv != null) {
//                        item_eyelayout_gridview_video_name7_tv.setVisibility(View.INVISIBLE);
//                    }
//                    break;
//                case 27:
//                    if (item_eyelayout_gridview_video_name8_tv != null) {
//                        item_eyelayout_gridview_video_name8_tv.setVisibility(View.INVISIBLE);
//                    }
//                    break;
//                case 28:
//                    if (item_eyelayout_gridview_video_name9_tv != null) {
//                        item_eyelayout_gridview_video_name9_tv.setVisibility(View.INVISIBLE);
//                    }
//                    break;
                case 29:
                    if (eyes_controller_bootom_rl != null) {
                        eyes_controller_bootom_rl.setAlpha(0.3f);
                    }
                    break;
            }
        }
    }

    /**
     * 更新移动布局位置    player调用
     */
    public void updateLayoutLocation(int left, int top) {
        RelativeLayout.LayoutParams btnLp1 = (RelativeLayout.LayoutParams) eyes_controller_ll.getLayoutParams();
        left = left - (eyes_controller_ll.getWidth());
        //设置控件的margin值 完成控件移动功能
        btnLp1.setMargins(left, top, -eyes_controller_ll.getWidth(), -eyes_controller_ll.getHeight());
        eyes_controller_ll.requestLayout();
        //获取中心点的坐标值
        int[] location = new int[2];
        eyes_controller_center_iv.getLocationOnScreen(location);
        int centerX = location[0];
        int centerY = location[1];
        eyes_controller_center_iv.checkOnView(centerX, centerY, isRegist, checkGridItem, checkBootomView, gridNum);
    }

    public void initVideoView() {
        if (eyes_controller_video_gv == null) {
            return;
        }
        item_eyelayout_gridview_video_image1_iv = (ImageView) eyes_controller_video_gv.getChildAt(0).findViewById(R.id.item_eyelayout_gridview_video_image_iv);
        item_eyelayout_gridview_video_image2_iv = (ImageView) eyes_controller_video_gv.getChildAt(1).findViewById(R.id.item_eyelayout_gridview_video_image_iv);
        item_eyelayout_gridview_video_image3_iv = (ImageView) eyes_controller_video_gv.getChildAt(2).findViewById(R.id.item_eyelayout_gridview_video_image_iv);
        item_eyelayout_gridview_video_image4_iv = (ImageView) eyes_controller_video_gv.getChildAt(3).findViewById(R.id.item_eyelayout_gridview_video_image_iv);
        item_eyelayout_gridview_video_image5_iv = (ImageView) eyes_controller_video_gv.getChildAt(4).findViewById(R.id.item_eyelayout_gridview_video_image_iv);
        item_eyelayout_gridview_video_image6_iv = (ImageView) eyes_controller_video_gv.getChildAt(5).findViewById(R.id.item_eyelayout_gridview_video_image_iv);
        item_eyelayout_gridview_video_image7_iv = (ImageView) eyes_controller_video_gv.getChildAt(6).findViewById(R.id.item_eyelayout_gridview_video_image_iv);
        item_eyelayout_gridview_video_image8_iv = (ImageView) eyes_controller_video_gv.getChildAt(7).findViewById(R.id.item_eyelayout_gridview_video_image_iv);
        item_eyelayout_gridview_video_image9_iv = (ImageView) eyes_controller_video_gv.getChildAt(8).findViewById(R.id.item_eyelayout_gridview_video_image_iv);
        item_eyelayout_gridview_video_play1_iv = (ImageView) eyes_controller_video_gv.getChildAt(0).findViewById(R.id.item_eyelayout_gridview_video_play_iv);
        item_eyelayout_gridview_video_play2_iv = (ImageView) eyes_controller_video_gv.getChildAt(1).findViewById(R.id.item_eyelayout_gridview_video_play_iv);
        item_eyelayout_gridview_video_play3_iv = (ImageView) eyes_controller_video_gv.getChildAt(2).findViewById(R.id.item_eyelayout_gridview_video_play_iv);
        item_eyelayout_gridview_video_play4_iv = (ImageView) eyes_controller_video_gv.getChildAt(3).findViewById(R.id.item_eyelayout_gridview_video_play_iv);
        item_eyelayout_gridview_video_play5_iv = (ImageView) eyes_controller_video_gv.getChildAt(4).findViewById(R.id.item_eyelayout_gridview_video_play_iv);
        item_eyelayout_gridview_video_play6_iv = (ImageView) eyes_controller_video_gv.getChildAt(5).findViewById(R.id.item_eyelayout_gridview_video_play_iv);
        item_eyelayout_gridview_video_play7_iv = (ImageView) eyes_controller_video_gv.getChildAt(6).findViewById(R.id.item_eyelayout_gridview_video_play_iv);
        item_eyelayout_gridview_video_play8_iv = (ImageView) eyes_controller_video_gv.getChildAt(7).findViewById(R.id.item_eyelayout_gridview_video_play_iv);
        item_eyelayout_gridview_video_play9_iv = (ImageView) eyes_controller_video_gv.getChildAt(8).findViewById(R.id.item_eyelayout_gridview_video_play_iv);
        item_eyelayout_gridview_video_name1_tv = (TextView) eyes_controller_video_gv.getChildAt(0).findViewById(R.id.item_eyelayout_gridview_video_name_tv);
        item_eyelayout_gridview_video_name2_tv = (TextView) eyes_controller_video_gv.getChildAt(1).findViewById(R.id.item_eyelayout_gridview_video_name_tv);
        item_eyelayout_gridview_video_name3_tv = (TextView) eyes_controller_video_gv.getChildAt(2).findViewById(R.id.item_eyelayout_gridview_video_name_tv);
        item_eyelayout_gridview_video_name4_tv = (TextView) eyes_controller_video_gv.getChildAt(3).findViewById(R.id.item_eyelayout_gridview_video_name_tv);
        item_eyelayout_gridview_video_name5_tv = (TextView) eyes_controller_video_gv.getChildAt(4).findViewById(R.id.item_eyelayout_gridview_video_name_tv);
        item_eyelayout_gridview_video_name6_tv = (TextView) eyes_controller_video_gv.getChildAt(5).findViewById(R.id.item_eyelayout_gridview_video_name_tv);
        item_eyelayout_gridview_video_name7_tv = (TextView) eyes_controller_video_gv.getChildAt(6).findViewById(R.id.item_eyelayout_gridview_video_name_tv);
        item_eyelayout_gridview_video_name8_tv = (TextView) eyes_controller_video_gv.getChildAt(7).findViewById(R.id.item_eyelayout_gridview_video_name_tv);
        item_eyelayout_gridview_video_name9_tv = (TextView) eyes_controller_video_gv.getChildAt(8).findViewById(R.id.item_eyelayout_gridview_video_name_tv);
    }

    /**
     * 注册gridview的监听事件
     */
    public void registGridviewInterface() {
        try {
            if (eyes_controller_video_gv == null) {
                return;
            }
            item_eyelayout_gridview_video_image1_iv = (ImageView) eyes_controller_video_gv.getChildAt(0).findViewById(R.id.item_eyelayout_gridview_video_image_iv);
            item_eyelayout_gridview_video_image2_iv = (ImageView) eyes_controller_video_gv.getChildAt(1).findViewById(R.id.item_eyelayout_gridview_video_image_iv);
            item_eyelayout_gridview_video_image3_iv = (ImageView) eyes_controller_video_gv.getChildAt(2).findViewById(R.id.item_eyelayout_gridview_video_image_iv);
            item_eyelayout_gridview_video_image4_iv = (ImageView) eyes_controller_video_gv.getChildAt(3).findViewById(R.id.item_eyelayout_gridview_video_image_iv);
            item_eyelayout_gridview_video_image5_iv = (ImageView) eyes_controller_video_gv.getChildAt(4).findViewById(R.id.item_eyelayout_gridview_video_image_iv);
            item_eyelayout_gridview_video_image6_iv = (ImageView) eyes_controller_video_gv.getChildAt(5).findViewById(R.id.item_eyelayout_gridview_video_image_iv);
            item_eyelayout_gridview_video_image7_iv = (ImageView) eyes_controller_video_gv.getChildAt(6).findViewById(R.id.item_eyelayout_gridview_video_image_iv);
            item_eyelayout_gridview_video_image8_iv = (ImageView) eyes_controller_video_gv.getChildAt(7).findViewById(R.id.item_eyelayout_gridview_video_image_iv);
            item_eyelayout_gridview_video_image9_iv = (ImageView) eyes_controller_video_gv.getChildAt(8).findViewById(R.id.item_eyelayout_gridview_video_image_iv);
            item_eyelayout_gridview_video_play1_iv = (ImageView) eyes_controller_video_gv.getChildAt(0).findViewById(R.id.item_eyelayout_gridview_video_play_iv);
            item_eyelayout_gridview_video_play2_iv = (ImageView) eyes_controller_video_gv.getChildAt(1).findViewById(R.id.item_eyelayout_gridview_video_play_iv);
            item_eyelayout_gridview_video_play3_iv = (ImageView) eyes_controller_video_gv.getChildAt(2).findViewById(R.id.item_eyelayout_gridview_video_play_iv);
            item_eyelayout_gridview_video_play4_iv = (ImageView) eyes_controller_video_gv.getChildAt(3).findViewById(R.id.item_eyelayout_gridview_video_play_iv);
            item_eyelayout_gridview_video_play5_iv = (ImageView) eyes_controller_video_gv.getChildAt(4).findViewById(R.id.item_eyelayout_gridview_video_play_iv);
            item_eyelayout_gridview_video_play6_iv = (ImageView) eyes_controller_video_gv.getChildAt(5).findViewById(R.id.item_eyelayout_gridview_video_play_iv);
            item_eyelayout_gridview_video_play7_iv = (ImageView) eyes_controller_video_gv.getChildAt(6).findViewById(R.id.item_eyelayout_gridview_video_play_iv);
            item_eyelayout_gridview_video_play8_iv = (ImageView) eyes_controller_video_gv.getChildAt(7).findViewById(R.id.item_eyelayout_gridview_video_play_iv);
            item_eyelayout_gridview_video_play9_iv = (ImageView) eyes_controller_video_gv.getChildAt(8).findViewById(R.id.item_eyelayout_gridview_video_play_iv);
            item_eyelayout_gridview_video_name1_tv = (TextView) eyes_controller_video_gv.getChildAt(0).findViewById(R.id.item_eyelayout_gridview_video_name_tv);
            item_eyelayout_gridview_video_name2_tv = (TextView) eyes_controller_video_gv.getChildAt(1).findViewById(R.id.item_eyelayout_gridview_video_name_tv);
            item_eyelayout_gridview_video_name3_tv = (TextView) eyes_controller_video_gv.getChildAt(2).findViewById(R.id.item_eyelayout_gridview_video_name_tv);
            item_eyelayout_gridview_video_name4_tv = (TextView) eyes_controller_video_gv.getChildAt(3).findViewById(R.id.item_eyelayout_gridview_video_name_tv);
            item_eyelayout_gridview_video_name5_tv = (TextView) eyes_controller_video_gv.getChildAt(4).findViewById(R.id.item_eyelayout_gridview_video_name_tv);
            item_eyelayout_gridview_video_name6_tv = (TextView) eyes_controller_video_gv.getChildAt(5).findViewById(R.id.item_eyelayout_gridview_video_name_tv);
            item_eyelayout_gridview_video_name7_tv = (TextView) eyes_controller_video_gv.getChildAt(6).findViewById(R.id.item_eyelayout_gridview_video_name_tv);
            item_eyelayout_gridview_video_name8_tv = (TextView) eyes_controller_video_gv.getChildAt(7).findViewById(R.id.item_eyelayout_gridview_video_name_tv);
            item_eyelayout_gridview_video_name9_tv = (TextView) eyes_controller_video_gv.getChildAt(8).findViewById(R.id.item_eyelayout_gridview_video_name_tv);
//            eyes_controller_center_iv.addGridObject(item_eyelayout_gridview_video_image1_iv, item_eyelayout_gridview_video_play1_iv, new ImageViewInterface() {
//                boolean canRun = false;
//
//                @Override
//                public void calback(boolean calback) {
//                    canRun = calback;
//                }
//
//                @Override
//                public void onFocus(boolean isFocus) {
//                    if (canRun) {
//                        if (isFocus) {//中心点在控件上
//                            if (prigressComplete) {//确认进度是否完成
//                                //做响应的操作
//                                centerProgressNum = 0;
//                                prigressComplete = false;
//                                canRun = false;
//                                gridNum = 0;
//                                getPlayAddress(classifyVideoTypeResultResourcesLists.get(0).getResourceId(), classifyVideoTypeResultResourcesLists.get(0).getResourceName());
////                            getVideoList(0);
//                            } else {//确认进度未完成 继续等待
//                                updateProgressBar();
//                            }
//                            mPlayLayoutInterface.onFocusViewNum(11, true);
//                            mPlayLayoutInterface.onFocusViewNum(20, true);
//                        } else {//中心点离开控件
//
//                        }
//                    } else {
//                        mPlayLayoutInterface.onFocusViewNum(11, false);
//                        if (isFocus) {//中心点在控件上
//                            centerProgressNum = 0;
//                            prigressComplete = false;
//                            goneProgressBar();
//                            mPlayLayoutInterface.onFocusViewNum(20, true);
//                        } else {//中心点离开控件
//                            mPlayLayoutInterface.onFocusViewNum(20, false);
//                        }
//                    }
//
//                }
//            });
//            eyes_controller_center_iv.addGridObject(item_eyelayout_gridview_video_image2_iv, item_eyelayout_gridview_video_play2_iv, new ImageViewInterface() {
//                boolean canRun = false;
//
//                @Override
//                public void calback(boolean calback) {
//                    canRun = calback;
//                }
//
//                @Override
//                public void onFocus(boolean isFocus) {
//                    if (canRun) {
//                        if (isFocus) {//中心点在控件上
//                            if (prigressComplete) {//确认进度是否完成
//                                //做响应的操作
//                                centerProgressNum = 0;
//                                prigressComplete = false;
//                                canRun = false;
//                                gridNum = 0;
//                                getPlayAddress(classifyVideoTypeResultResourcesLists.get(1).getResourceId(), classifyVideoTypeResultResourcesLists.get(1).getResourceName());
////                            getVideoList(0);
//                            } else {//确认进度未完成 继续等待
//                                updateProgressBar();
//                            }
//                            mPlayLayoutInterface.onFocusViewNum(12, true);
//                            mPlayLayoutInterface.onFocusViewNum(21, true);
//                        } else {//中心点离开控件
//
//                        }
//                    } else {
//                        mPlayLayoutInterface.onFocusViewNum(12, false);
//                        if (isFocus) {//中心点在控件上 centerProgressNum = 0;
//                            centerProgressNum = 0;
//                            prigressComplete = false;
//                            goneProgressBar();
//                            mPlayLayoutInterface.onFocusViewNum(21, true);
//                        } else {//中心点离开控件
//                            mPlayLayoutInterface.onFocusViewNum(21, false);
//                        }
//                    }
//
//                }
//            });
//            eyes_controller_center_iv.addGridObject(item_eyelayout_gridview_video_image3_iv, item_eyelayout_gridview_video_play3_iv, new ImageViewInterface() {
//                boolean canRun = false;
//
//                @Override
//                public void calback(boolean calback) {
//                    canRun = calback;
//                }
//
//                @Override
//                public void onFocus(boolean isFocus) {
//                    if (canRun) {
//                        if (isFocus) {//中心点在控件上
//                            if (prigressComplete) {//确认进度是否完成
//                                //做响应的操作
//                                centerProgressNum = 0;
//                                prigressComplete = false;
//                                canRun = false;
//                                gridNum = 0;
//                                getPlayAddress(classifyVideoTypeResultResourcesLists.get(2).getResourceId(), classifyVideoTypeResultResourcesLists.get(2).getResourceName());
////                            getVideoList(0);
//                            } else {//确认进度未完成 继续等待
//                                updateProgressBar();
//                            }
//                            mPlayLayoutInterface.onFocusViewNum(13, true);
//                            mPlayLayoutInterface.onFocusViewNum(23, true);
//                        } else {//中心点离开控件
//
//                        }
//                    } else {
//                        mPlayLayoutInterface.onFocusViewNum(13, false);
//                        if (isFocus) {//中心点在控件上
//                            centerProgressNum = 0;
//                            prigressComplete = false;
//                            goneProgressBar();
//                            mPlayLayoutInterface.onFocusViewNum(22, true);
//                        } else {//中心点离开控件
//                            mPlayLayoutInterface.onFocusViewNum(22, false);
//                        }
//                    }
//
//                }
//            });
//            eyes_controller_center_iv.addGridObject(item_eyelayout_gridview_video_image4_iv, item_eyelayout_gridview_video_play4_iv, new ImageViewInterface() {
//                boolean canRun = false;
//
//                @Override
//                public void calback(boolean calback) {
//                    canRun = calback;
//                }
//
//                @Override
//                public void onFocus(boolean isFocus) {
//                    if (canRun) {
//                        if (isFocus) {//中心点在控件上
//                            if (prigressComplete) {//确认进度是否完成
//                                //做响应的操作
//                                centerProgressNum = 0;
//                                prigressComplete = false;
//                                canRun = false;
//                                gridNum = 0;
//                                getPlayAddress(classifyVideoTypeResultResourcesLists.get(3).getResourceId(), classifyVideoTypeResultResourcesLists.get(3).getResourceName());
////                            getVideoList(0);
//                            } else {//确认进度未完成 继续等待
//                                updateProgressBar();
//                            }
//                            mPlayLayoutInterface.onFocusViewNum(14, true);
//                            mPlayLayoutInterface.onFocusViewNum(23, true);
//                        } else {//中心点离开控件
//
//                        }
//                    } else {
//                        mPlayLayoutInterface.onFocusViewNum(14, false);
//                        if (isFocus) {//中心点在控件上
//                            centerProgressNum = 0;
//                            prigressComplete = false;
//                            goneProgressBar();
//                            mPlayLayoutInterface.onFocusViewNum(23, true);
//                        } else {//中心点离开控件
//                            mPlayLayoutInterface.onFocusViewNum(23, false);
//                        }
//                    }
//
//                }
//            });
//            eyes_controller_center_iv.addGridObject(item_eyelayout_gridview_video_image5_iv, item_eyelayout_gridview_video_play5_iv, new ImageViewInterface() {
//                boolean canRun = false;
//
//                @Override
//                public void calback(boolean calback) {
//                    canRun = calback;
//                }
//
//                @Override
//                public void onFocus(boolean isFocus) {
//                    if (canRun) {
//                        if (isFocus) {//中心点在控件上
//                            if (prigressComplete) {//确认进度是否完成
//                                //做响应的操作
//                                centerProgressNum = 0;
//                                prigressComplete = false;
//                                canRun = false;
//                                gridNum = 0;
//                                getPlayAddress(classifyVideoTypeResultResourcesLists.get(4).getResourceId(), classifyVideoTypeResultResourcesLists.get(4).getResourceName());
////                            getVideoList(0);
//                            } else {//确认进度未完成 继续等待
//                                updateProgressBar();
//                            }
//                            mPlayLayoutInterface.onFocusViewNum(15, true);
//                            mPlayLayoutInterface.onFocusViewNum(24, true);
//                        } else {//中心点离开控件
//
//                        }
//                    } else {
//                        mPlayLayoutInterface.onFocusViewNum(15, false);
//                        if (isFocus) {//中心点在控件上
//                            centerProgressNum = 0;
//                            prigressComplete = false;
//                            goneProgressBar();
//                            mPlayLayoutInterface.onFocusViewNum(24, true);
//                        } else {//中心点离开控件
//                            mPlayLayoutInterface.onFocusViewNum(24, false);
//                        }
//                    }
//
//                }
//            });
//            eyes_controller_center_iv.addGridObject(item_eyelayout_gridview_video_image6_iv, item_eyelayout_gridview_video_play6_iv, new ImageViewInterface() {
//                boolean canRun = false;
//
//                @Override
//                public void calback(boolean calback) {
//                    canRun = calback;
//                }
//
//                @Override
//                public void onFocus(boolean isFocus) {
//                    if (canRun) {
//                        if (isFocus) {//中心点在控件上
//                            if (prigressComplete) {//确认进度是否完成
//                                //做响应的操作
//                                centerProgressNum = 0;
//                                prigressComplete = false;
//                                canRun = false;
//                                gridNum = 0;
//                                getPlayAddress(classifyVideoTypeResultResourcesLists.get(5).getResourceId(), classifyVideoTypeResultResourcesLists.get(5).getResourceName());
////                            getVideoList(0);
//                            } else {//确认进度未完成 继续等待
//                                updateProgressBar();
//                            }
//                            mPlayLayoutInterface.onFocusViewNum(16, true);
//                            mPlayLayoutInterface.onFocusViewNum(25, true);
//                        } else {//中心点离开控件
//
//                        }
//                    } else {
//                        mPlayLayoutInterface.onFocusViewNum(16, false);
//                        if (isFocus) {//中心点在控件上
//                            centerProgressNum = 0;
//                            prigressComplete = false;
//                            goneProgressBar();
//                            mPlayLayoutInterface.onFocusViewNum(25, true);
//                        } else {//中心点离开控件
//                            mPlayLayoutInterface.onFocusViewNum(25, false);
//                        }
//                    }
//
//                }
//            });
//            eyes_controller_center_iv.addGridObject(item_eyelayout_gridview_video_image7_iv, item_eyelayout_gridview_video_play7_iv, new ImageViewInterface() {
//                boolean canRun = false;
//
//                @Override
//                public void calback(boolean calback) {
//                    canRun = calback;
//                }
//
//                @Override
//                public void onFocus(boolean isFocus) {
//                    if (canRun) {
//                        if (isFocus) {//中心点在控件上
//                            if (prigressComplete) {//确认进度是否完成
//                                //做响应的操作
//                                centerProgressNum = 0;
//                                prigressComplete = false;
//                                canRun = false;
//                                gridNum = 0;
//                                getPlayAddress(classifyVideoTypeResultResourcesLists.get(6).getResourceId(), classifyVideoTypeResultResourcesLists.get(6).getResourceName());
////                            getVideoList(0);
//                            } else {//确认进度未完成 继续等待
//                                updateProgressBar();
//                            }
//                            mPlayLayoutInterface.onFocusViewNum(17, true);
//                            mPlayLayoutInterface.onFocusViewNum(26, true);
//                        } else {//中心点离开控件
//
//                        }
//                    } else {
//                        mPlayLayoutInterface.onFocusViewNum(17, false);
//                        if (isFocus) {//中心点在控件上centerProgressNum = 0;
//                            centerProgressNum = 0;
//                            prigressComplete = false;
//                            goneProgressBar();
//                            mPlayLayoutInterface.onFocusViewNum(26, true);
//                        } else {//中心点离开控件
//                            mPlayLayoutInterface.onFocusViewNum(26, false);
//                        }
//                    }
//
//                }
//            });
//            eyes_controller_center_iv.addGridObject(item_eyelayout_gridview_video_image8_iv, item_eyelayout_gridview_video_play8_iv, new ImageViewInterface() {
//                boolean canRun = false;
//
//                @Override
//                public void calback(boolean calback) {
//                    canRun = calback;
//                }
//
//                @Override
//                public void onFocus(boolean isFocus) {
//                    if (canRun) {
//                        if (isFocus) {//中心点在控件上
//                            if (prigressComplete) {//确认进度是否完成
//                                //做响应的操作
//                                centerProgressNum = 0;
//                                prigressComplete = false;
//                                canRun = false;
//                                gridNum = 0;
//                                getPlayAddress(classifyVideoTypeResultResourcesLists.get(7).getResourceId(), classifyVideoTypeResultResourcesLists.get(7).getResourceName());
////                            getVideoList(0);
//                            } else {//确认进度未完成 继续等待
//                                updateProgressBar();
//                            }
//                            mPlayLayoutInterface.onFocusViewNum(18, true);
//                            mPlayLayoutInterface.onFocusViewNum(27, true);
//                        } else {//中心点离开控件
//
//                        }
//                    } else {
//                        mPlayLayoutInterface.onFocusViewNum(18, false);
//                        if (isFocus) {//中心点在控件上
//                            centerProgressNum = 0;
//                            prigressComplete = false;
//                            goneProgressBar();
//                            mPlayLayoutInterface.onFocusViewNum(27, true);
//                        } else {//中心点离开控件
//                            mPlayLayoutInterface.onFocusViewNum(27, false);
//                        }
//                    }
//
//                }
//            });
//            eyes_controller_center_iv.addGridObject(item_eyelayout_gridview_video_image9_iv, item_eyelayout_gridview_video_play9_iv, new ImageViewInterface() {
//                boolean canRun = false;
//
//                @Override
//                public void calback(boolean calback) {
//                    canRun = calback;
//                }
//
//                @Override
//                public void onFocus(boolean isFocus) {
//                    if (canRun) {
//                        if (isFocus) {//中心点在控件上
//                            if (prigressComplete) {//确认进度是否完成
//                                //做响应的操作
//                                centerProgressNum = 0;
//                                prigressComplete = false;
//                                canRun = false;
//                                gridNum = 0;
//                                getPlayAddress(classifyVideoTypeResultResourcesLists.get(8).getResourceId(), classifyVideoTypeResultResourcesLists.get(8).getResourceName());
////                            getVideoList(0);
//                            } else {//确认进度未完成 继续等待
//                                updateProgressBar();
//                            }
//                            mPlayLayoutInterface.onFocusViewNum(19, true);
//                            mPlayLayoutInterface.onFocusViewNum(28, true);
//                        } else {//中心点离开控件
//
//                        }
//                    } else {
//                        mPlayLayoutInterface.onFocusViewNum(19, false);
//                        if (isFocus) {//中心点在控件上
//                            centerProgressNum = 0;
//                            prigressComplete = false;
//                            goneProgressBar();
//                            mPlayLayoutInterface.onFocusViewNum(28, true);
//                        } else {//中心点离开控件
//                            mPlayLayoutInterface.onFocusViewNum(28, false);
//                        }
//                    }
//
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取视频数据
     */
    private void getVideoList() {
        listMode = 1;
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        String action = "/app/tag/resources/list/service";
        hashMap.put("currPage", thisVideoPage);
        hashMap.put("queryCount", 9);
        hashMap.put("tagId", typeId);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData(action, jsonData, new GenericsCallback<ClassifyVideoTypeResult>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showToastMsg("网络连接失败。请检查网络！");
            }

            @Override
            public void onResponse(ClassifyVideoTypeResult result, int id) {
                if (result.getResourcesList() != null
                        && result.getResourcesList().size() > 0) {
                    int totalNum = result.getTotal();
                    if (thisVideoPage == 1) {
                        if (totalNum % 9 == 0 && thisVideoPage == 1) {
                            mPlayLayoutInterface.setMaxPage(totalNum / 9);
                        } else {
                            mPlayLayoutInterface.setMaxPage((totalNum / 9) + 1);
                        }
                    }
                    if (result.getResourcesList().size() > 0) {
                        mPlayLayoutInterface.setVideoList(result.getResourcesList());
                    } else {
                    }
                }
            }
        });
    }

    /**
     * 获取全部视频列表
     */
    private void getAllVideoList() {
        listMode = 3;
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("bySort", 1);
        hashMap.put("typeId", 1);
        hashMap.put("tagId", -1);
        hashMap.put("isFree", 0);
        hashMap.put("clientId", -1);
        hashMap.put("currPage", thisVideoPage);
        hashMap.put("pageSize", 9);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/resources/list/service", jsonData, new GenericsCallback<AllVideoResult>() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(AllVideoResult result, int id) {
                if (result.getResCode().equals("0")) {
                    if (result.getResourcesList() != null && result.getResourcesList().size() > 0) {
                        int totalNum = result.getTotal();
                        if (thisVideoPage == 1) {
                            if (totalNum % 9 == 0 && thisVideoPage == 1) {
                                mPlayLayoutInterface.setMaxPage(totalNum / 9);
                            } else {
                                mPlayLayoutInterface.setMaxPage((totalNum / 9) + 1);
                            }
                        }
                        ArrayList<ClassifyVideoTypeResultResourcesList> videoLists = new ArrayList<ClassifyVideoTypeResultResourcesList>();
                        ArrayList<AllVideoResultDataList> playHistoryResultDataList = result.getResourcesList();
                        for (int i = 0; i < playHistoryResultDataList.size(); i++) {
                            ClassifyVideoTypeResultResourcesList videoInfo = new ClassifyVideoTypeResultResourcesList();
                            videoInfo.setCoverImgUrl(playHistoryResultDataList.get(i).getCoverImgUrl());
                            videoInfo.setResourceId(playHistoryResultDataList.get(i).getResourceId());
                            videoInfo.setResourceName(playHistoryResultDataList.get(i).getResourceName());
                            videoLists.add(videoInfo);
                        }
                        if (videoLists.size() > 0) {
                            mPlayLayoutInterface.setVideoList(videoLists);
                        } else {
//                            showEmptyPage("sssssssss");
                        }
                    }
                }
            }
        });
    }

    /**
     * 播放历史
     */
    private void getHistoryVideoList() {
        listMode = 2;
        if (AppContent.UID.equals("")) {
            mPlayLayoutInterface.showEmptyPage(true, "还未登录！");
        }
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("userId", Ras_uid);
        hashMap.put("currPage", thisVideoPage);
        hashMap.put("pageSize", 9);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/user/play/history/list/service", jsonData, new GenericsCallback<PlayHistoryResult>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showToastMsg("网络连接失败，请检查网络！");
            }

            @Override
            public void onResponse(PlayHistoryResult result, int id) {
                if (result.getResCode().equals("0")) {
                    if (result.getDataList() != null && result.getDataList().size() > 0) {
                        int totalNum = result.getTotal();
                        if (thisVideoPage == 1) {
                            if (totalNum % 9 == 0) {
                                mPlayLayoutInterface.setMaxPage(totalNum / 9);
                            } else {
                                mPlayLayoutInterface.setMaxPage((totalNum / 9) + 1);
                            }
                        }
                        ArrayList<ClassifyVideoTypeResultResourcesList> videoLists = new ArrayList<ClassifyVideoTypeResultResourcesList>();
                        ArrayList<PlayHistoryResultDataList> playHistoryResultDataList = result.getDataList();
                        for (int i = 0; i < playHistoryResultDataList.size(); i++) {
                            ClassifyVideoTypeResultResourcesList videoInfo = new ClassifyVideoTypeResultResourcesList();
                            videoInfo.setCoverImgUrl(playHistoryResultDataList.get(i).getResourcesCoverImg());
                            videoInfo.setResourceId(playHistoryResultDataList.get(i).getResourcesId());
                            videoInfo.setResourceName(playHistoryResultDataList.get(i).getResourcesTitle());
                            videoLists.add(videoInfo);
                        }
                        if (videoLists.size() > 0) {
                            mPlayLayoutInterface.setVideoList(videoLists);
                        } else {
//                                    showEmptyPage("sssssss");
                        }
                    }
                }
            }
        });
    }

    /**
     * 更新确认进度
     */
    private void updateProgressBar() {
        centerProgressNum += 5;
        MLog.d("centerProgressNum:" + centerProgressNum);
        if (centerProgressNum <= 150) {
            Message msg = mHandler.obtainMessage();
            msg.what = UPDATE_PROGRESS;
            msg.arg1 = centerProgressNum;
            mHandler.sendMessage(msg);
        }
    }

    private void goneProgressBar() {
        Message msg =mHandler.obtainMessage();
        msg.what = UPDATE_PROGRESS;
        msg.arg1 = 0;
        mHandler.sendMessage(msg);
    }

    //************************操作方法集********************************//

    /**
     * 隐藏和显示loading    player调用
     */
    public void showLoading(boolean show) {
        if (show) {
            player_loading_iv.setVisibility(View.VISIBLE);
            player_loading_iv.startAnimation(operatingAnim);
        } else {
            player_loading_iv.setVisibility(View.GONE);
            player_loading_iv.clearAnimation();
        }
    }

    /**
     * 隐藏和显示VRUI的loading    player调用
     */
    public void showVrUILoading(boolean show) {
        if (show) {
            eyes_controller_loading_rl.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(eyes_controller_vrui_content_rl.getWidth(), eyes_controller_vrui_content_rl.getHeight());
            eyes_controller_loading_rl.setLayoutParams(params);
            eyes_controller_loading_rl.setBackgroundResource(R.color.color_80000000);
            eyes_controller_loading_iv.startAnimation(operatingAnim);
        } else {
            eyes_controller_loading_rl.setVisibility(View.GONE);
            eyes_controller_loading_iv.clearAnimation();
        }
    }

    public void showEmityPage(boolean isShow, String message) {
        if (isShow) {
            gridNum = 0;
            eyes_controller_emity_rl.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(eyes_controller_vrui_content_rl.getWidth(), eyes_controller_vrui_content_rl.getHeight());
            eyes_controller_emity_rl.setLayoutParams(params);
            eyes_controller_emity_tv.setText(message);
            eyes_controller_video_bootom_rl.setVisibility(View.VISIBLE);
            eyes_controller_video_page_rl.setVisibility(View.GONE);
            eyes_controller_classify_ll.setVisibility(View.GONE);
        } else {
            gridNum = 1;
            eyes_controller_video_page_rl.setVisibility(View.VISIBLE);
            eyes_controller_emity_rl.setVisibility(View.GONE);
        }
    }

    /**
     * 更新确定进度  player调用
     */
    public void updateProgress(int progressNum) {
        if (progressNum == 150) {
            prigressComplete = true;
            eyes_controller_center_rpb.setVisibility(View.INVISIBLE);
        } else {
            if (progressNum < 50) {
                progressNum = 0;
            } else {
                progressNum -= 50;
            }
            eyes_controller_center_rpb.setCricleColor(Color.TRANSPARENT);
            eyes_controller_center_rpb.setVisibility(View.VISIBLE);
            eyes_controller_center_rpb.setProgress(progressNum);
        }
    }

    /**
     * 显示和隐藏 vrUI    player调用
     */
    public void showGridView(boolean show) {
        if (show) {
            checkGridItem = true;
            checkBootomView = false;
            eyes_controller_retract_iv.setBackgroundResource(R.drawable.eyes_vrui_hide);
            eyes_controller_gridview_rl.setVisibility(View.VISIBLE);
            eyes_controller_bootom_rl.setVisibility(View.INVISIBLE);
        } else {
            eyes_controller_show_down_bt.setBackgroundResource(R.drawable.eyes_vrui_show);
            eyes_controller_gridview_rl.setVisibility(View.INVISIBLE);
            eyes_controller_bootom_rl.setVisibility(View.VISIBLE);
        }
    }

    public void showVideoList(boolean show) {
        if (show) {
            gridNum = 2;
            eyes_controller_video_bootom_rl.setVisibility(View.VISIBLE);
            eyes_controller_video_gv.setVisibility(View.VISIBLE);
            eyes_controller_classify_ll.setVisibility(View.INVISIBLE);
        } else {
            gridNum = 1;
            eyes_controller_classify_ll.setVisibility(View.VISIBLE);
            eyes_controller_video_gv.setVisibility(View.INVISIBLE);
            eyes_controller_video_bootom_rl.setVisibility(View.INVISIBLE);
        }
    }

    public void updateVideoPage(String page) {
        eyes_controller_video_page_num_tv.setText(page);
    }


    String videoUrl;
    int videoType;
    int playerLastProgress;

    /**
     * 获取播放地址
     */
    private void getPlayAddress(final int sourceId, final String videoName) {
        String uid;
        if (AppContent.UID.equals("")) {
            // uid = "-1";
            uid = "2147483647";
        } else {
            uid = AppContent.UID;
        }
        // 加密手机号
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(uid);
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("resourcesId", sourceId);
        hashMap.put("userId", Ras_uid);
        hashMap.put("urlType",1); //1是播放，2是下载
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/resources/new/url/service", jsonData, new GenericsCallback<DownLoadAddressResult>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showToastMsg("网络连接失败，请检查网络");
            }

            @Override
            public void onResponse(DownLoadAddressResult result, int id) {
                if (result.getResCode().equals("0")) {
                    videoUrl = result.getUrl();
                    videoType = result.getResourcePlayType();
                    playerLastProgress = result.getPlayTimes();
                    MLog.d("videoUrl:" + videoUrl);
                    if (NetWorkDialog.getIsShowNetWorkDialog(mContext)) {
                        if (netWorkDialog == null) {
                            netWorkDialog = new NetWorkDialog(mContext);
                        }
                        netWorkDialog.showNetWorkChangePlayerVideoWarning(new NetWorkDialogButtonListener() {
                            @Override
                            public void okClick() {
                                openVideoPlayer(videoUrl, playerLastProgress, videoName, videoType, sourceId);
                            }
                        });
                    } else {
                        openVideoPlayer(videoUrl, playerLastProgress, videoName, videoType, sourceId);
                    }
                }
            }
        });
    }


    private void openVideoPlayer(String playerUrl, int playerLastProgress, String videoName, int videoType, int sourceId) {
        if (playerUrl == null && playerUrl.equals("") && playerUrl.length() <= 0) {
            showToastMsg("播放路径不存在");
        }
        int lastProgress = 0;
        if (AppContent.UID.equals("")) {
            lastProgress = (int) SimpleSharedPreferences.getInt(
                    videoName, mContext);
        } else {
            lastProgress = playerLastProgress;
        }

        VRPlayerActivity.intentTo(mContext, videoName, playerUrl, videoType, sourceId, lastProgress, true);
        mPlayLayoutInterface.finishActivity();
    }

    public void releseListener() {
        eyes_controller_center_iv.releseListener();
    }

    /**
     * @param @param message    设定文件
     * @return void    返回类型
     * @Title: showToast
     * @Description: TODO 消息提示
     * @author hechuang
     * @date 2015-11-12
     */
    public void showToastMsg(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

}
