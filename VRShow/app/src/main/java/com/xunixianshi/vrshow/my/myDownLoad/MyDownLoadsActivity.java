package com.xunixianshi.vrshow.my.myDownLoad;

import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hch.filedownloader.download.DownloaderManager;
import com.hch.filedownloader.download.FileDownloaderModel;
import com.hch.utils.OkhttpConstant;
import com.hch.viewlib.util.ScreenUtils;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.customview.HintDialog;
import com.xunixianshi.vrshow.customview.NetWorkDialog;
import com.xunixianshi.vrshow.interfaces.DownLoadInterface;
import com.xunixianshi.vrshow.interfaces.NetWorkDialogButtonListener;
import com.xunixianshi.vrshow.recyclerview.RecycleViewDivider;
import com.xunixianshi.vrshow.recyclerview.RecyclerBaseItemOnClickListener;
import com.xunixianshi.vrshow.util.SearchFileUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by duan on 2016/9/22.
 */
public class MyDownLoadsActivity extends BaseAct  {

    @Bind(R.id.my_down_load_recycler_view)
    RecyclerView my_down_load_recycler_view;

    @Bind(R.id.page_empty_rl)
    RelativeLayout page_empty_rl;

    @Bind(R.id.title_center_name_tv)
    TextView title_center_name_tv;

    @Bind(R.id.title_right_function_tv)
    TextView title_right_function_tv;


    private DownLoadAdapter mDownLoadAdapter;
    private NetWorkDialog netWorkDialog;

    private HintDialog mHintDialog;

    private List<DownLoadItem> mSelectList;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_my_downloads);
        ButterKnife.bind(this);
    }

    @Override
    protected void initView() {
        super.initView();
        mHintDialog = new HintDialog(this);
    }

    @Override
    protected void initData() {
        title_center_name_tv.setText(R.string.user_label_my_download);
        mDownLoadAdapter = new DownLoadAdapter(this, new DownLoadInterface() {
            @Override
            public boolean download(String downLoadType, int resourceId, String downLoadName, String downLoadIconUrl, String packageName, String fileSize) {
                return false;
            }
            @Override
            public boolean reDownload(String downLoadType, int resourceId, final int downloadId) {
                if(NetWorkDialog.getIsShowNetWorkDialog(MyDownLoadsActivity.this)){
                    if(netWorkDialog == null){
                        netWorkDialog = new NetWorkDialog(MyDownLoadsActivity.this);
                    }
                    netWorkDialog.showNetWorkChangeDownLoadWarning(new NetWorkDialogButtonListener() {
                        @Override
                        public void okClick() {
                            DownloaderManager.getInstance().startTask(downloadId);
                            delayNotifyDataSetChanged(20);
                        }

                        @Override
                        public void cancelClick() {
                            delayNotifyDataSetChanged(20);
                        }
                    });
                }
                else{
                    DownloaderManager.getInstance().startTask(downloadId);
                    delayNotifyDataSetChanged(20);
                }
                return true;
            }
        });

        mDownLoadAdapter.setRecyclerBaseItemOnClickListener(new RecyclerBaseItemOnClickListener() {
            @Override
            public void onClick(int position) {
                mSelectList.clear();
                mSelectList.add(mDownLoadAdapter.getItem(position));
                showDialog();
            }
        });

        mSelectList = new ArrayList<>();

        my_down_load_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        my_down_load_recycler_view.setAdapter(mDownLoadAdapter);
        my_down_load_recycler_view.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL,  ScreenUtils.dip2px(this,5), ContextCompat.getColor(this,R.color.c_layout_blue_bg)));
        getDownLoadedVideoData();
    }

    @OnClick({R.id.title_left_back_iv,R.id.title_right_function_tv})
    public void clickBack(View view) {
        switch (view.getId()){
            case R.id.title_left_back_iv:
                finish();
                break;
            case R.id.title_right_function_tv:
                mSelectList.clear();
                mSelectList.addAll(mDownLoadAdapter.getGroup());
                showDialog();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.delayNotifyDataSetChanged(100);
    }

    private void showDialog() {
        mHintDialog.setContextText("是否确定删除");
        mHintDialog.setOkButText("确定");
        mHintDialog.setCancelText("取消");
        mHintDialog.setOkClickListaner(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteSelect();
                mHintDialog.dismiss();
            }
        });
        mHintDialog.setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHintDialog.dismiss();
            }
        });
        mHintDialog.show();
    }

    private void deleteSelect() {
        String result = deleteDownLoadItems(mSelectList);
        mSelectList.clear();
        showToastMsg(result);
        delayNotifyDataSetChanged(20);
    }

    public String deleteDownLoadItems(List<DownLoadItem> downloads) {
        // 删除操作
        for (int j = 0; j < downloads.size(); j++) {
            DownLoadItem downloaditem = downloads.get(j);
            int downloadID = downloaditem.getId();
            if(downloadID == -1){
                final FileDownloaderModel model = DownloaderManager.getInstance()
                        .getFileDownloaderModelByUrl(downloaditem.getDownLoadUrl());
                if(model !=null){
                    downloadID = model.getId();
                }
            }
            if(downloadID != 1){
                DownloaderManager.getInstance().deleteTask(downloadID);
            }
        }
        return "已删除";
    }

    private void getDownLoadedVideoData() {
        page_empty_rl.setVisibility(View.GONE);
        title_right_function_tv.setVisibility(View.VISIBLE);
        mDownLoadAdapter.setGroup(SearchFileUtil.getInstance(this).getDownLoadModeList(OkhttpConstant.DOWN_LOAD_NET_VIDEO_TYPE_NAME));  //添加网络端的普通视频
        if(mDownLoadAdapter.getGroup().size()==0 ){
            page_empty_rl.setVisibility(View.VISIBLE);
            title_right_function_tv.setVisibility(View.INVISIBLE);
        }
    }

    private void delayNotifyDataSetChanged(long delayMillis){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                getDownLoadedVideoData();
            }
        }, delayMillis);
    }

}
