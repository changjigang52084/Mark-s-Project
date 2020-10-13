package com.xunixianshi.vrshow.my.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.hch.viewlib.util.MLog;
import com.hch.viewlib.widget.PullToRefreshBase;
import com.hch.viewlib.widget.PullToRefreshListView;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseFra;
import com.xunixianshi.vrshow.actmanager.BaseNetFra;
import com.xunixianshi.vrshow.customview.EditTextDialog;
import com.xunixianshi.vrshow.customview.HintDialog;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.customview.NetWorkDialog;
import com.xunixianshi.vrshow.interfaces.EditDialogTextInterface;
import com.xunixianshi.vrshow.interfaces.NetWorkDialogButtonListener;
import com.xunixianshi.vrshow.interfaces.ReceiverNetworkInterface;
import com.xunixianshi.vrshow.interfaces.UploadActivityToItemInterface;
import com.xunixianshi.vrshow.interfaces.UploadItemToActivityInterface;
import com.xunixianshi.vrshow.my.fragment.database.UploadBean;
import com.xunixianshi.vrshow.my.fragment.database.UploadDatabaseTools;
import com.xunixianshi.vrshow.receiver.ConnectionChangeReceiver;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.StringUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * TODO 内容提交中页面
 *
 * @author MarkChang
 * @ClassName CommittedFragment
 * @time 2016/11/1 15:50
 */
public class CommittedFragment extends BaseNetFra implements UploadItemToActivityInterface {

    @Bind(R.id.committed_ptrlv)
    PullToRefreshListView committed_ptrlv;
    @Bind(R.id.page_error_rl)
    RelativeLayout page_error_rl;
    @Bind(R.id.page_empty_rl)
    RelativeLayout page_emity_rl;

    private CommittedAdapter committedAdatper;
    private LoadingAnimationDialog myProgressDialog;
    private ArrayList<UploadBean> videoBeanArrayList = new ArrayList<UploadBean>();
    private UploadDatabaseTools uploadDatabaseTools;
    private HintDialog mHintDialog;
    private EditTextDialog mEditTextDialog;

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View rootView = inflater.inflate(R.layout.fragment_committed, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initView(View parentView) {
        super.initView(parentView);
        committed_ptrlv.setMode(PullToRefreshBase.Mode.DISABLED);
    }

    @Override
    protected void initData() {
        super.initData();
        mHintDialog = new HintDialog(getActivity());
        mEditTextDialog = new EditTextDialog(getActivity());
        myProgressDialog = new LoadingAnimationDialog(getActivity());
        committedAdatper = new CommittedAdapter(getActivity());
        uploadDatabaseTools = new UploadDatabaseTools(getActivity());
        committedAdatper.setInterface(this);
        if (getContext() != null && NetWorkDialog.getIsShowNetWorkDialog(getContext())) {
            committedAdatper.isWiFi(true);
        } else {
            committedAdatper.isWiFi(false);
        }
        addReceiverNetwork();
    }

    private void initListener() {
        committed_ptrlv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                getData();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        if(AppContent.UID.equals("")){
            //正常情况用户如果能进到这个界面  uid是肯定有的
            showToastMsg("用户id不存在");
            return ;
        }
        videoBeanArrayList = uploadDatabaseTools.selectAllByUid(AppContent.UID);
//        videoBeanArrayList = uploadDatabaseTools.selectAllUploadData();
        if (videoBeanArrayList.size() <= 0) {
            page_emity_rl.setVisibility(View.VISIBLE);
        } else {
            page_emity_rl.setVisibility(View.GONE);
        }
        committedAdatper.setGroup(videoBeanArrayList);
        committed_ptrlv.setAdapter(committedAdatper);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void showDialog(final UploadActivityToItemInterface uploadActivityToItemInterface) {
        mHintDialog.setTitleText("提示");
        mHintDialog.setContextText("只能同时上传一个，需要先暂停另一个上传。确定暂停上一个吗？");
        mHintDialog.setCancelText("取消");
        mHintDialog.setOkButText("确定");
        mHintDialog.setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHintDialog.dismiss();
            }
        });
        mHintDialog.setOkClickListaner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadActivityToItemInterface.okButtonClick();
                mHintDialog.dismiss();
            }
        });
        mHintDialog.show();
    }

    @Override
    public void showHint(final UploadActivityToItemInterface uploadActivityToItemInterface) {
        mHintDialog.setTitleText("提示");
        mHintDialog.setContextText("是否确认删除");
        mHintDialog.setCancelText("取消");
        mHintDialog.setOkButText("确定");
        mHintDialog.setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHintDialog.dismiss();
            }
        });
        mHintDialog.setOkClickListaner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadActivityToItemInterface.okButtonClick();
                mHintDialog.dismiss();
            }
        });
        mHintDialog.show();
    }

    @Override
    public void showLoading() {
        myProgressDialog.show();
    }

    @Override
    public void dissmissLoading() {
        myProgressDialog.dismiss();
    }

    @Override
    public void upData() {
        if(isDestroy()){return;}
        videoBeanArrayList.clear();
        videoBeanArrayList = uploadDatabaseTools.selectAllByUid(AppContent.UID);
        if (videoBeanArrayList.size() <= 0) {
            page_emity_rl.setVisibility(View.VISIBLE);
        } else {
            page_emity_rl.setVisibility(View.GONE);
        }
        committedAdatper.setGroup(videoBeanArrayList);
    }

    @Override
    public void checkNetWork(UploadActivityToItemInterface uploadActivityToItemInterface) {
        if (getContext() != null && NetWorkDialog.getIsShowNetWorkDialog(getContext())) {
            showNetWorkDialog(uploadActivityToItemInterface);
        } else {
            uploadActivityToItemInterface.okButtonClick();
        }
    }

    private void showNetWorkDialog(final UploadActivityToItemInterface uploadActivityToItemInterface) {
        mHintDialog.setTitleText("提示");
        mHintDialog.setContextText("目前在2G网络。是否继续上传");
        mHintDialog.setCancelText("取消");
        mHintDialog.setOkButText("确定");
        mHintDialog.setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHintDialog.dismiss();
            }
        });
        mHintDialog.setOkClickListaner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadActivityToItemInterface.okButtonClick();
                mHintDialog.dismiss();
            }
        });
        mHintDialog.show();
    }

    @Override
    public void editName(final EditDialogTextInterface editDialogTextInterface,String resourceName,String imageUrl) {
        if(!isDestroy()){
            mEditTextDialog.setContextText(resourceName);
            mEditTextDialog.setImageUrl(imageUrl);
            mEditTextDialog.setCancelText("取消");
            mEditTextDialog.setOkButText("确定");
//            mEditTextDialog.setCancelClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mEditTextDialog.dismiss();
//                }
//            });
            mEditTextDialog.setOkClickListaner(new EditDialogTextInterface() {
                @Override
                public void okButtonCarryText(String editText) {
                    editDialogTextInterface.okButtonCarryText(editText);
                    mEditTextDialog.dismiss();
                }
            });
            mEditTextDialog.show();
        }
    }

    private void addReceiverNetwork() {
        registerReceiverNetworkInterface(new ReceiverNetworkInterface() {
            @Override
            public void NetworkChanged(int state) {
                if (state != ConnectionChangeReceiver.NET_STATE__CONNECTED_WIFI ) {
                    committedAdatper.cancelUpload();
                }
            }
        });
    }
}