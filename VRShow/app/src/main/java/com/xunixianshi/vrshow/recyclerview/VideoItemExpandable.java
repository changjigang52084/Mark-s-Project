package com.xunixianshi.vrshow.recyclerview;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.obj.HttpZanObj;
import com.xunixianshi.vrshow.obj.VideoMoreObj;
import com.xunixianshi.vrshow.util.StringUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by duan on 2016/10/24.
 */

public class VideoItemExpandable extends FrameLayout {

    public static final int ITEM_ZAN_TYPE = 1;
    public static final int ITEM_DOWNLOAD_TYPE = 2;
    public static final int ITEM_SHARE_TYPE = 3;
    public static final int ITEM_DETAIL_TYPE = 4;

    @Bind(R.id.public_video_item_zan_num_tv)
    TextView public_video_item_zan_num_tv;

    @Bind(R.id.public_video_item_zan_add_one_tv)
    TextView public_video_item_zan_add_one_tv;

    @Bind(R.id.public_video_item_zan_ib)
    ImageButton public_video_item_zan_ib;

    private RecyclerItemOnClickListener mRecyclerItemOnClickListener;

    private Context mContext;

    private int mPosition;

    private VideoMoreObj mVideoMoreObj;
    private boolean mIsZanEd;

    private Animation btnAnim;

    public VideoItemExpandable(Context context) {
        super(context);
        initInflateExpandable(context);
    }

    public VideoItemExpandable(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInflateExpandable(context);
    }

    public VideoItemExpandable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInflateExpandable(context);
    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public VideoItemExpandable(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        initInflateExpandable(context);
//    }

    public void initInflateExpandable(Context context){
        mContext = context;
        View view =  LayoutInflater.from(mContext).inflate(R.layout.item_expandable_child_video, this, false);
        ButterKnife.bind(this,view);
        addView(view);

        btnAnim = AnimationUtils.loadAnimation(mContext, R.anim.zan_add_translate);
    }

    public void setRecyclerItemOnClickListener(RecyclerItemOnClickListener recyclerItemOnClickListener){
        mRecyclerItemOnClickListener = recyclerItemOnClickListener;
    }

    public void setItemDate(VideoMoreObj videoMoreObj, int position){
        mPosition = position;
        mVideoMoreObj = videoMoreObj;
        mIsZanEd = videoMoreObj.getIsLike()==1;
        attachView();
    }

    private void attachView(){
        if(mIsZanEd){
            public_video_item_zan_ib.setImageResource(R.drawable.classify_detail_zan_press);
        }
        else{
            public_video_item_zan_ib.setImageResource(R.drawable.classify_detail_zan_normal);
        }
        public_video_item_zan_num_tv.setText("赞"+ StringUtil.getNumToString(mVideoMoreObj.getLikesTotal()));
    }

    @OnClick({R.id.public_video_item_zan_ll,R.id.public_video_item_download_ll,R.id.public_video_item_share_ll,R.id.public_video_item_detail_ll})
    public void itemOnClick(View view){
        switch (view.getId()) {
            case R.id.public_video_item_zan_ll:
                if(mRecyclerItemOnClickListener!=null){
                    mRecyclerItemOnClickListener.onClick(mPosition, ITEM_ZAN_TYPE, new RecyclerItemCallBack() {
                        @Override
                        public void onSuccess(Object o) {
                            HttpZanObj result = (HttpZanObj)o;
                            mVideoMoreObj.setLikesTotal(result.getLikesTotal());
                            mIsZanEd = result.getLikesStatus()== 0; // 服务器没状态，只好模拟上一个状态了。
                            if(!mIsZanEd){
                                public_video_item_zan_add_one_tv.setVisibility(View.VISIBLE);
                                public_video_item_zan_add_one_tv.startAnimation(btnAnim);
                                new Handler().postDelayed(new Runnable() {
                                    public void run() {
                                        public_video_item_zan_add_one_tv.setVisibility(View.GONE);
                                    }
                                }, 1000);
                            }
                            mIsZanEd  = !mIsZanEd;
                            attachView();
                        }
                        @Override
                        public void onFail(Object o) {
                        }
                    });
                }
                break;
            case R.id.public_video_item_download_ll:
                clickItem(ITEM_DOWNLOAD_TYPE,null);
                break;
            case R.id.public_video_item_share_ll:
                clickItem(ITEM_SHARE_TYPE,null);
                break;
            case R.id.public_video_item_detail_ll:
                clickItem(ITEM_DETAIL_TYPE,null);
                break;
        }
    }

    private void clickItem(int itemType,RecyclerItemCallBack callBack){
        if(mRecyclerItemOnClickListener!=null){
            mRecyclerItemOnClickListener.onClick(mPosition, itemType,callBack);
        }
    }
}
