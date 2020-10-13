package com.xunixianshi.vrshow.my.assemble.addAssemble;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hch.utils.OkhttpConstant;
import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.FileUtil;
import com.hch.viewlib.util.ScreenUtils;
import com.hch.viewlib.widget.PullToRefreshBase;
import com.hch.viewlib.widget.PullToRefreshBase.Mode;
import com.hch.viewlib.widget.PullToRefreshRecyclerView;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.my.assemble.AssembleBaseFragment;
import com.xunixianshi.vrshow.my.assemble.AssembleDetailsObject;
import com.xunixianshi.vrshow.my.assemble.EditCoverFragment;
import com.xunixianshi.vrshow.obj.ResourcesNameIsRepeatObj;
import com.xunixianshi.vrshow.obj.UserIconTokenObj;
import com.xunixianshi.vrshow.obj.assembleObj.AssembleCollectionListObj;
import com.xunixianshi.vrshow.obj.assembleObj.AssembleContentItemObj;
import com.xunixianshi.vrshow.recyclerview.BindHeadViewHolderListener;
import com.xunixianshi.vrshow.recyclerview.RecycleViewDivider;
import com.xunixianshi.vrshow.recyclerview.RecyclerBaseItemOnClickListener;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.MaxLengthWatcher;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.interfaces.HttpError;
import com.zhy.http.okhttp.interfaces.HttpSuccess;
import com.zhy.http.okhttp.utils.PicassoUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by duan on 2016/9/24.
 * 创建合集
 */

public class EditAssembleFragment extends AssembleBaseFragment {

    private static final int ASSEMBLE_SUBMIT_CHECK_ASSEMBLE_NAME = 1;  //检查名称合法性   服务器暂时不用检查
    private static final int ASSEMBLE_SUBMIT_GET_TOKEN = 2;  //获取token
    private static final int ASSEMBLE_SUBMIT_UPDATE_QI_NIU_COVER = 3;  //上传到七牛
    private static final int ASSEMBLE_CREATE_SUBMIT_SERVER = 4;  //创建合集提交
    private static final int ASSEMBLE_EDIT_SUBMIT_SERVER = 5; //编辑提交
    private static final int ASSEMBLE_EDIT_ADD_SUBMIT_LIST_SERVER = 6; //编辑提交添加的列表
    private static final int ASSEMBLE_EDIT_DELETE_SUBMIT_LIST_SERVER = 7; //编辑提交删除的列表

    public static final String COVER_URI_TAG = "coverUri";
    public static final String COVER_URL_TAG = "coverUrl";
    private static final int ASSEMBLE_SUBMIT_OK = 8;

    private static final int ASSEMBLE_UI_CLOSE_ERROR_DESC = 10;

    @Bind(R.id.title_center_name_tv)
    TextView title_center_name_tv;

    @Bind(R.id.add_assemble_content_list)
    PullToRefreshRecyclerView add_assemble_content_list;

    @Bind(R.id.add_assemble_action_fail_rl)
    RelativeLayout add_assemble_action_fail_rl;

    @Bind(R.id.add_assemble_action_fail_desc_tv)
    TextView add_assemble_action_fail_desc_tv;


    private EditAssembleHeadHolder mEditAssembleHeadHolder;

    private LoadingAnimationDialog progressDialog;

    private List<AssembleContentItemObj> mNetAllContentList;  //网络所有已加载集合
    private List<AssembleContentItemObj> mUnBindContentList;   //准备添加的 未绑定集合
    private List<AssembleContentItemObj> mRemoveBoundContentList; //准备删除的已绑定集合

    private String mAssembleName; //集合名称
    private String mAssembleIntro; //集合简介
    private String mCoverUrl;  //传进来的url 或者用户上传的url
    private Uri mCoverUri;  //获取到的图片Uri

    //添加内容
    private EditCoverFragment mEditCoverFragment;
    private EditContentFragment mEditContentFragment;

    private AssembleBaseFragment mCurrentChildFragment;
    private EditContentAdapter mEditContentAdapter;

    private String mAssembleId;

    private float mErrorDescAlpha = 1.0f;
    private int thisPage = 1;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View rootView = inflater.inflate(R.layout.fragment_add_assemble, container, false);
        rootView.setClickable(true);// 防止点击穿透，底层的fragment响应上层点击触摸事件
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initData() {
        mUnBindContentList = new ArrayList<>();
        mNetAllContentList = new ArrayList<>();
        mRemoveBoundContentList = new ArrayList<>();
        progressDialog = new LoadingAnimationDialog(getActivity());
        mEditContentAdapter = new EditContentAdapter(getActivity());
        mEditContentAdapter.setContentListType(2);
        add_assemble_content_list.getRefreshableView().setAdapter(mEditContentAdapter);
        add_assemble_content_list.getRefreshableView().setLayoutManager(new LinearLayoutManager(getActivity()));
        add_assemble_content_list.getRefreshableView().addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.VERTICAL, ScreenUtils.dip2px(getActivity(), 1), ContextCompat.getColor(getActivity(), R.color.color_e6e6e6)));


        mEditContentAdapter.setRecyclerBaseItemOnClickListener(new RecyclerBaseItemOnClickListener() {
            @Override
            public void onClick(int position) {
                AssembleContentItemObj assembleContentItemObj = mEditContentAdapter.getItem(position);
                assembleContentItemObj.setSelect(false);
                if (mEditContentAdapter.getSameAssemble(mUnBindContentList, assembleContentItemObj) != null) {
                    mUnBindContentList.remove(mEditContentAdapter.getSameAssemble(mUnBindContentList, assembleContentItemObj));
                } else {
                    mRemoveBoundContentList.add(assembleContentItemObj);
                }
                mEditContentAdapter.removeItem(position);
            }
        });

        mEditContentAdapter.setHeaderViewInterface(new BindHeadViewHolderListener() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateHeadViewHolder(ViewGroup parent, int viewType) {
                mEditAssembleHeadHolder = new EditAssembleHeadHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_framgent_add_assemble_head, add_assemble_content_list.getRefreshableView(), false));
                return mEditAssembleHeadHolder;
            }

            @Override
            public void onBindHeadViewHolder(RecyclerView.ViewHolder viewHolder) {
                ((EditAssembleHeadHolder) viewHolder).attachCoverImageView();
                ((EditAssembleHeadHolder) viewHolder).initEditText(mAssembleName, mAssembleIntro);
            }
        });
        attachIntentDate();
    }

    private void attachIntentDate() {
        AssembleDetailsObject detailsObject = (AssembleDetailsObject) getArguments().getSerializable(EditAssembleActivity.ASSEMBLE_DETAIL_INTENT_KEY);
        if (detailsObject != null) {
            thisPage = detailsObject.getTotalPage();
            mAssembleId = detailsObject.getAssembleDetailObj().getCompilationId();
            mCoverUrl = detailsObject.getAssembleDetailObj().getCoverImgUrl();
            mNetAllContentList = detailsObject.getContentList();
            mAssembleName = detailsObject.getAssembleDetailObj().getCompilationName();
            mAssembleIntro = detailsObject.getAssembleDetailObj().getCompilationIntro();
            initListener();
            setNetContentListState(mNetAllContentList, true);
            contentDateNotify();
            title_center_name_tv.setText("编辑合集");
        } else {
            add_assemble_content_list.setMode(Mode.DISABLED);
            title_center_name_tv.setText("创建合集");
        }
    }


    /**
     * 初始化监听， 下拉刷新
     *
     * @author DuanChunLin
     * @time 2016/10/27 15:51
     */
    private void initListener() {
        add_assemble_content_list.setMode(Mode.PULL_FROM_END);
        add_assemble_content_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                postHttpCollectionContentListData(mAssembleId, thisPage);
            }
        });

    }

    /**
     * 流程handler
     *
     * @author DuanChunLin
     * @time 2016/10/27 15:52
     */
    private Handler mAssembleSubmitHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ASSEMBLE_SUBMIT_CHECK_ASSEMBLE_NAME:
                    assembleSubmitCheckAssembleName();
                    break;
                case ASSEMBLE_SUBMIT_GET_TOKEN:
                    getToken();
                    break;
                case ASSEMBLE_SUBMIT_UPDATE_QI_NIU_COVER:
                    String token = msg.getData().getString("token");
                    String fileName = msg.getData().getString("fileName");
                    assembleSubmitUpdateQiNiuCover(token, fileName);
                    break;
                case ASSEMBLE_CREATE_SUBMIT_SERVER:
                    assembleCreateSubmitServer();
                    break;
                case ASSEMBLE_EDIT_SUBMIT_SERVER:
                    assembleEditSubmitServer();
                    break;
                case ASSEMBLE_EDIT_ADD_SUBMIT_LIST_SERVER:
                    assembleEditAddListSubmitServer();
                    break;
                case ASSEMBLE_EDIT_DELETE_SUBMIT_LIST_SERVER:
                    assembleEditDeleteListSubmitServer();
                    break;
                case ASSEMBLE_SUBMIT_OK:
                    exitBack();
                    break;
                case ASSEMBLE_UI_CLOSE_ERROR_DESC:
                    dismissAssembleActionFail();
                    break;
            }
        }
    };

    @OnClick({R.id.add_assemble_close, R.id.add_assemble_submit_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_assemble_close: //关闭
                exitBack();
                break;
            case R.id.add_assemble_submit_btn: //提交
                mErrorDescAlpha = 0;
                dismissAssembleActionFail();
                startAssembleSubmit();
                break;
        }
    }

    /**
     * 设置数据状态
     *
     * @author DuanChunLin
     * @time 2016/10/27 15:52
     */
    private void setNetContentListState(List<AssembleContentItemObj> list, boolean state) {
        if (list == null) {
            return;
        }
        for (Iterator<AssembleContentItemObj> allIterator = list.iterator(); allIterator.hasNext(); ) {
            AssembleContentItemObj assembleContentItemObj = allIterator.next();
            assembleContentItemObj.setSelect(state);
        }
    }

    /**
     * 合集内容数据发生改变   要自己做遍历
     * *@author DuanChunLin
     *
     * @time 2016/10/12 14:10
     */
    private void contentDateNotify() {
        mEditContentAdapter.clearGroup(false);
        mEditContentAdapter.addItemsNoNotify(mUnBindContentList);  //添加未绑定合集所有内容
        mEditContentAdapter.addItemsNoNotify(mNetAllContentList); //添加网络合集所有内容
        mEditContentAdapter.removeItems(mEditContentAdapter.getSameSetAssembleList(mNetAllContentList, mRemoveBoundContentList), true);
    }

    //显示 添加/编辑 封面界面
    private void showAddAssembleAddCoverFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        mEditCoverFragment = new EditCoverFragment();
        //设置传递参数
        Bundle bundle = new Bundle();
        bundle.putParcelable(COVER_URI_TAG, mCoverUri);
        bundle.putString(COVER_URL_TAG, mCoverUrl);
        mEditCoverFragment.setArguments(bundle);

        mEditCoverFragment.setTargetFragment(this, REQUEST_CODE); //设置接受数据

        fragmentTransaction.setCustomAnimations(R.anim.activity_right_in, R.anim.activity_left_out)
                .add(R.id.empty_attach_fragment, mEditCoverFragment, "EditCoverFragment");

        mCurrentChildFragment = mEditCoverFragment;
        //实例化fragment事务管理器
        fragmentTransaction.addToBackStack("EditCoverFragment");
        fragmentTransaction.commit();
    }


    /**
     * 显示添加内容碎片
     *
     * @author DuanChunLin
     * @time 2016/10/27 15:53
     */
    private void showAddAssembleAddContentFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        mEditContentFragment = new EditContentFragment();
        //设置传递参数
        Bundle bundle = new Bundle();
        bundle.putSerializable(EditContentFragment.CONTENT_LIST_SELECT_TAG, (Serializable) mUnBindContentList);
        bundle.putSerializable(EditContentFragment.BIND_DELETED_CONTENT_TAG, (Serializable) mRemoveBoundContentList);

        mEditContentFragment.setArguments(bundle);
        mEditContentFragment.setTargetFragment(this, REQUEST_CODE); //设置接受数据
        fragmentTransaction.setCustomAnimations(R.anim.activity_right_in, R.anim.activity_left_out)
                .add(R.id.empty_attach_fragment, mEditContentFragment, "EditCoverFragment");

        mCurrentChildFragment = mEditContentFragment;
        //实例化fragment事务管理器
        fragmentTransaction.addToBackStack("EditCoverFragment");
        fragmentTransaction.commit();
    }


    /**
     * 隐藏合集提交错误
     *
     * @author DuanChunLin
     * @time 2016/10/27 15:53
     */
    private void dismissAssembleActionFail() {
        mErrorDescAlpha -= 0.05;
        if (mErrorDescAlpha <= 0) {
            mErrorDescAlpha = 0;
            add_assemble_action_fail_rl.setVisibility(View.GONE);
        } else {
            Message msg = mAssembleSubmitHandler.obtainMessage();
            msg.what = ASSEMBLE_UI_CLOSE_ERROR_DESC;
            mAssembleSubmitHandler.sendMessageDelayed(msg, 60);
        }
        add_assemble_action_fail_rl.setAlpha(mErrorDescAlpha);
    }

    /**
     * 展示合集提交错误页面
     *
     * @author DuanChunLin
     * @time 2016/10/27 15:53
     */
    private void showAssembleActionFail(String failMsg) {
        mErrorDescAlpha = 1.0f;
        add_assemble_action_fail_rl.setAlpha(mErrorDescAlpha);
        add_assemble_action_fail_rl.setVisibility(View.VISIBLE);
        add_assemble_action_fail_desc_tv.setText(failMsg);
        progressDialog.dismiss();
//        Toast.makeText(getActivity(), failMsg, Toast.LENGTH_SHORT).show();

        mAssembleSubmitHandler.removeMessages(ASSEMBLE_UI_CLOSE_ERROR_DESC);
        Message msg = mAssembleSubmitHandler.obtainMessage();
        msg.what = ASSEMBLE_UI_CLOSE_ERROR_DESC;
        mAssembleSubmitHandler.sendMessageDelayed(msg, 3000);
    }


    /**
     * 开始提交
     *
     * @author DuanChunLin
     * @time 2016/10/27 15:53
     */
    private void startAssembleSubmit() {
        progressDialog.show();
        String assembleName = "";
        String assembleDesc = "";

        if (mEditAssembleHeadHolder != null) {
            assembleName = mEditAssembleHeadHolder.add_assemble_title_et.getText().toString().trim();
            assembleDesc = mEditAssembleHeadHolder.add_assemble_desc_et.getText().toString().trim();
        }

        if (mCoverUri == null && mCoverUrl == null) {
            showAssembleActionFail("没有选择封面");
            return;
        }

        if (assembleName.length() <= 0) {
            showAssembleActionFail("标题不能为空!");
            return;
        }
        if (assembleDesc.length() <= 0) {
            showAssembleActionFail("简介不能为空!");
            return;
        }
        String resourcesName;
        String resourcesDesc;
        try {
            resourcesName = URLEncoder.encode(assembleName, "utf-8");
            resourcesDesc = URLEncoder.encode(assembleDesc, "utf-8");
        } catch (Exception msg) {
            showAssembleActionFail("标题不合法");
            return;
        }
        mAssembleName = resourcesName;
        mAssembleIntro = resourcesDesc;

        if (mCoverUri != null) {
            mAssembleSubmitHandler.sendEmptyMessage(ASSEMBLE_SUBMIT_GET_TOKEN);
        } else {
            assembleSubmitServer();
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    /**
     * 提交流程 如果有合集id 则表示修改合集 否则创建合集
     *
     * @author DuanChunLin
     * @time 2016/10/27 15:54
     */
    private void assembleSubmitServer() {
        if (StringUtil.isEmpty(mAssembleId)) {
            mAssembleSubmitHandler.sendEmptyMessage(ASSEMBLE_CREATE_SUBMIT_SERVER);
        } else {
            mAssembleSubmitHandler.sendEmptyMessage(ASSEMBLE_EDIT_SUBMIT_SERVER);
        }
    }

    /**
     * 合集名称检查 ， 暂时不需要
     *
     * @author DuanChunLin
     * @time 2016/10/27 15:55
     */
    private void assembleSubmitCheckAssembleName() {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("resourcesName", mAssembleName);
        hashMap.put("resourcesType", 1);// 资源类型：1、表示视频分类。2、表示应用分类。
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        // 获取网络参数
        OkHttpAPI.postHttpData("/resources/title/repeatrvice", jsonData, new GenericsCallback<ResourcesNameIsRepeatObj>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showAssembleActionFail("网络连接失败，请检查网络！");
            }

            @Override
            public void onResponse(ResourcesNameIsRepeatObj response, int id) {
                int isRepeat = response.getIsRepeat();
                if (isRepeat == 0) {
                    // 资源名称不重复可以进行下一步
                    mAssembleSubmitHandler.sendEmptyMessage(ASSEMBLE_SUBMIT_GET_TOKEN);  //开始上传到七牛
                } else if (isRepeat == 1) {
                    showAssembleActionFail("您输入的标题已被占用，请重新输入标题！");
                }
            }
        });
    }

    /**
     * 获取上传的token
     */
    private void getToken() {
        //获取token成功后开始上传图片
        String ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("tokenType", 1); // 1图片token
        hashMap.put("userId", ras_uid);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/qiniu/query/token/service", jsonData, new GenericsCallback<UserIconTokenObj>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showAssembleActionFail("网络连接失败，请检查网络！");
            }

            @Override
            public void onResponse(UserIconTokenObj response, int id) {
                if (response.getResCode().equals("0")) {
                    String token = response.getToken();
                    String fileName = response.getFileName();
                    Message message = mAssembleSubmitHandler.obtainMessage();
                    message.what = ASSEMBLE_SUBMIT_UPDATE_QI_NIU_COVER;
                    Bundle bundle = new Bundle();
                    bundle.putString("token", token);
                    bundle.putString("fileName", fileName);

                    message.setData(bundle);
                    mAssembleSubmitHandler.sendMessage(message);
                } else {
                    showAssembleActionFail(response.getResDesc());
                }
            }
        });
    }

    /**
     * 开始上传到七牛
     *
     * @author DuanChunLin
     * @time 2016/9/29 20:26
     */
    private void assembleSubmitUpdateQiNiuCover(String token, String fileName) {
        String filePath = FileUtil.getRealFilePath(getActivity(), mCoverUri);
        UploadManager uploadManager = new UploadManager();
        uploadManager.put(filePath, fileName, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        if (info.isOK()) {
                            mCoverUrl = OkhttpConstant.pic_url + key;
                            assembleSubmitServer();
                        } else {
                            showAssembleActionFail("图片上传失败!请重新提交");
                        }
                    }
                }, null);
    }


    /**
     * 获取合集列表的 所有id 的集合
     *
     * @author DuanChunLin
     * @time 2016/10/27 15:56
     */
    private List<Integer> getContentIdList(List<AssembleContentItemObj> assembleContentItemList) {
        List<Integer> contentIdList = new ArrayList<>();
        if (assembleContentItemList != null) {
            for (Iterator<AssembleContentItemObj> allIterator = assembleContentItemList.iterator(); allIterator.hasNext(); ) {
                AssembleContentItemObj assembleContentItemObj = allIterator.next();
                contentIdList.add(assembleContentItemObj.getResourceId());
            }
        }
        return contentIdList;
    }


    /**
     * 创建合集 提交到后台
     *
     * @author DuanChunLin
     * @time 2016/10/27 15:56
     */
    private void assembleCreateSubmitServer() {
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("userId", Ras_uid);
        hashMap.put("compilationName", mAssembleName);
        hashMap.put("coverImgUrl", mCoverUrl);
        hashMap.put("clientId", 3); //3 Android端

        hashMap.put("compilationIntro", mAssembleIntro);
        hashMap.put("list", getContentIdList(mUnBindContentList)); //查询已审核通过的集合
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postObjectData("/user/compilation/create/service", jsonData, new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                try {
                    String code = result.getString("resCode");
                    if (code.equals("0")) {
                        //创建成功
                        exitBack();
                    } else {
                        showAssembleActionFail("提交失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showAssembleActionFail("提交失败");
                }
            }
        }, new HttpError() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showAssembleActionFail("提交失败");
            }
        });

    }

    /**
     * 提交编辑的合集信息
     *
     * @author DuanChunLin
     * @time 2016/10/14 11:54
     */
    private void assembleEditSubmitServer() {
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("userCompilationId", mAssembleId);
        hashMap.put("compilationName", mAssembleName);
        hashMap.put("coverImgUrl", mCoverUrl);
        hashMap.put("compilationIntro", mAssembleIntro);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postObjectData("/user/compilation/edit/service", jsonData, new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                try {
                    String code = result.getString("resCode");
                    if (code.equals("0")) {
                        mAssembleSubmitHandler.sendEmptyMessage(ASSEMBLE_EDIT_ADD_SUBMIT_LIST_SERVER);
                    } else {
                        showAssembleActionFail("提交失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showAssembleActionFail("提交失败");
                }
            }
        }, new HttpError() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showAssembleActionFail("提交失败");
            }
        });

    }

    /**
     * 提交添加的编辑的合集列表
     *
     * @author DuanChunLin
     * @time 2016/10/14 11:55
     */
    private void assembleEditAddListSubmitServer() {
        if (mUnBindContentList.size() == 0) {
            mAssembleSubmitHandler.sendEmptyMessage(ASSEMBLE_EDIT_DELETE_SUBMIT_LIST_SERVER);
            return;
        }
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("userCompilationId", mAssembleId);
        hashMap.put("dealType", 1); //1表示添加，2表示删除
        hashMap.put("list", getContentIdList(mUnBindContentList)); //查询已审核通过的集合
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postObjectData("/user/compilation/resource/edit/service", jsonData, new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                try {
                    String code = result.getString("resCode");
                    if (code.equals("0")) {
                        //创建成功
                        mAssembleSubmitHandler.sendEmptyMessage(ASSEMBLE_EDIT_DELETE_SUBMIT_LIST_SERVER);
                    } else {
                        showAssembleActionFail("提交失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showAssembleActionFail("提交失败");
                }
            }
        }, new HttpError() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showAssembleActionFail("提交失败");
            }
        });

    }


    /**
     * 提交添加的编辑的合集列表
     *
     * @author DuanChunLin
     * @time 2016/10/14 11:55
     */
    private void assembleEditDeleteListSubmitServer() {
        if (mRemoveBoundContentList.size() == 0) {
            mAssembleSubmitHandler.sendEmptyMessage(ASSEMBLE_SUBMIT_OK);
            return;
        }

        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("userCompilationId", mAssembleId);

        hashMap.put("dealType", 2); //2表示删除
        hashMap.put("list", getContentIdList(mRemoveBoundContentList)); //查询已审核通过的集合
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postObjectData("/user/compilation/resource/edit/service", jsonData, new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                try {
                    String code = result.getString("resCode");
                    if (code.equals("0")) {
                        //创建成功
                        mAssembleSubmitHandler.sendEmptyMessage(ASSEMBLE_SUBMIT_OK);
                    } else {
                        showAssembleActionFail("提交失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showAssembleActionFail("提交失败");
                }
            }
        }, new HttpError() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showAssembleActionFail("提交失败");
            }
        });

    }

    //数据返回接受
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //如果是从内容页返回 则需要去去重 删除列表中相同的
        //返回合集封面
        if (requestCode == REQUEST_CODE && resultCode == RESULT_CODE) {
            Uri uri = data.getParcelableExtra(COVER_URI_TAG);
            if (uri != null) {  //数据回来
                mCoverUri = uri;
                mEditContentAdapter.notifyItemChanged(0);
                return;
            }
            List<AssembleContentItemObj> selectUnBindContentList = (ArrayList<AssembleContentItemObj>) data.getSerializableExtra(EditContentFragment.SELECT_UNBIND_CONTENT_TAG);
            if (selectUnBindContentList != null && selectUnBindContentList.size() > 0) {
                List<AssembleContentItemObj> samSetRemoveBoundList = mEditContentAdapter.getSameSetAssembleList(mRemoveBoundContentList, selectUnBindContentList);
                List<AssembleContentItemObj> sameUnBindList = mEditContentAdapter.getSameSetAssembleList(selectUnBindContentList, mRemoveBoundContentList);
                selectUnBindContentList.removeAll(sameUnBindList);
                mRemoveBoundContentList.removeAll(samSetRemoveBoundList);
                mUnBindContentList.addAll(selectUnBindContentList);
                contentDateNotify();
            }
            return;
        }
        if (mCurrentChildFragment != null) {
            mCurrentChildFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    //点击返回键
    public void onBackPressed() {
        if (mCurrentChildFragment != null && !mCurrentChildFragment.isRemoving()) {
            mCurrentChildFragment.exitBack();
            mCurrentChildFragment = null;
        } else {
            exitBack();
        }
    }

    @Override
    public void exitBack() {
        getActivity().finish();
    }

    /**
     * 获取合集下资源列表
     *
     * @author hechuang
     * @time 2016/9/21 10:40
     */
    private void postHttpCollectionContentListData(String compilationId, int page) {
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("compilationId", compilationId);
        hashMap.put("currPage", page);
        hashMap.put("pageSize", 10);
        hashMap.put("bySort", 1);
        hashMap.put("queryType", 1);
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID); //16.12.28 接口改动
        hashMap.put("userId", Ras_uid);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        setCancelHttpTag("/user/compilation/resource/list/service");
        OkHttpAPI.postHttpData("/user/compilation/resource/list/service", jsonData, new GenericsCallback<AssembleCollectionListObj>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                add_assemble_content_list.onRefreshComplete();
//                showToastMsg("网络连接失败，请检查网络！");
            }

            @Override
            public void onResponse(AssembleCollectionListObj result, int id) {
                progressDialog.dismiss();
                add_assemble_content_list.onRefreshComplete();
                if (result.getResCode().equals("0")) {
                    if (thisPage == 1) {
                        mNetAllContentList.clear();
                    }
                    if (result.getList() != null && result.getList().size() > 0) {
                        setNetContentListState(result.getList(), true);
                        mNetAllContentList.addAll(result.getList());
                        contentDateNotify();
                        thisPage++;
                    }
                }
            }
        });
    }

    /**
     * 合集头部
     *
     * @author DuanChunLin
     * @time 2016/10/27 15:56
     */
    public class EditAssembleHeadHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.add_assemble_cover_rl)
        RelativeLayout add_assemble_cover_rl;

        @Bind(R.id.add_assemble_cover_iv)
        ImageView add_assemble_cover_iv;

        @Bind(R.id.add_assemble_edit_cover_btn)
        Button add_assemble_edit_cover_btn;

        @Bind(R.id.add_assemble_title_et)
        EditText add_assemble_title_et;

        @Bind(R.id.add_assemble_desc_et)
        EditText add_assemble_desc_et;

        @Bind(R.id.add_assemble_add_cover_rl)
        RelativeLayout add_assemble_add_cover_rl; //添加封面布局

        @Bind(R.id.add_assemble_edit_cover_rl)
        RelativeLayout add_assemble_edit_cover_rl; //编辑封面布局

        private MaxLengthWatcher mTitleWatcher;
        private MaxLengthWatcher mDescWatcher;

        public EditAssembleHeadHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setViewSize();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(getActivity());
                }
            });

            add_assemble_title_et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 50) {
                        showToastMsg("标题最多只能输入50个字");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            add_assemble_desc_et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 500) {
                        showToastMsg("简介最多只能输入500个字");
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

//            mTitleWatcher = new MaxLengthWatcher(add_assemble_title_et,50);
//            mTitleWatcher.addTextChangedListener();
//            mTitleWatcher.setOutOfRangeListener(new MaxLengthWatcher.OutOfRangeListener() {
//                @Override
//                public void outOfRange(int count) {
//                    showToastMsg("已超出最大限定字符数!");
//                }
//            });
//
//            mDescWatcher = new MaxLengthWatcher(add_assemble_desc_et,500);
//            mDescWatcher.addTextChangedListener();
//            mDescWatcher.setOutOfRangeListener(new MaxLengthWatcher.OutOfRangeListener() {
//                @Override
//                public void outOfRange(int count) {
//                    showToastMsg("已超出最大限定字符数!");
//                }
//            });
        }

        public void initEditText(String title, String desc) {
            if (!StringUtil.isEmpty(title) && add_assemble_title_et.getText().length() == 0) {
                add_assemble_title_et.setText(title);
            }
            if (!StringUtil.isEmpty(desc) && add_assemble_desc_et.getText().length() == 0) {
                add_assemble_desc_et.setText(desc);
            }
        }

        private void setViewSize() {
            int screenWith = ScreenUtils.getScreenWidth(getActivity());
            ViewGroup.LayoutParams lp;
            lp = add_assemble_cover_iv.getLayoutParams();
            lp.width = screenWith;
            lp.height = (int) (screenWith * 0.56);  //4：9
            add_assemble_cover_iv.setLayoutParams(lp);

            ViewGroup.LayoutParams edit_cover_lp;
            edit_cover_lp = add_assemble_edit_cover_rl.getLayoutParams();
            edit_cover_lp.height = (int) ((screenWith / 2) * 0.56);  //4：9
            add_assemble_edit_cover_rl.setLayoutParams(edit_cover_lp);
        }

        @OnClick({R.id.add_assemble_add_cover_rl, R.id.add_assemble_content_rl, R.id.add_assemble_edit_cover_btn})
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.add_assemble_add_cover_rl: //添加封面
                case R.id.add_assemble_edit_cover_btn:
                    mAssembleName = add_assemble_title_et.getText().toString().trim();
                    mAssembleIntro = add_assemble_desc_et.getText().toString().trim();
                    showAddAssembleAddCoverFragment();
                    break;
                case R.id.add_assemble_content_rl: //添加内容
                    mAssembleName = add_assemble_title_et.getText().toString().trim();
                    mAssembleIntro = add_assemble_desc_et.getText().toString().trim();
                    showAddAssembleAddContentFragment();
                    break;
            }
        }

        public void attachCoverImageView() {
            if (mCoverUrl == null && mCoverUri == null) {
                add_assemble_edit_cover_rl.setVisibility(View.GONE);
                add_assemble_add_cover_rl.setVisibility(View.VISIBLE);
            } else {
                add_assemble_edit_cover_rl.setVisibility(View.VISIBLE);
                add_assemble_add_cover_rl.setVisibility(View.GONE);
                if (mCoverUri != null) {
                    add_assemble_cover_iv.setImageBitmap(getBitmapFromUri(mCoverUri));
                } else {
                    PicassoUtil.loadImage(getActivity(), mCoverUrl + "?imageView2/2/w/" + (ScreenUtils.getScreenWidth(getActivity()) + 120), add_assemble_cover_iv);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
