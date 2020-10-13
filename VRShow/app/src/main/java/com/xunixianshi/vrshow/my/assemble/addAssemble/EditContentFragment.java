package com.xunixianshi.vrshow.my.assemble.addAssemble;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.ScreenUtils;
import com.hch.viewlib.widget.PullToRefreshBase;
import com.hch.viewlib.widget.PullToRefreshRecyclerView;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.my.assemble.AssembleBaseFragment;
import com.xunixianshi.vrshow.obj.assembleObj.AssembleCollectionListObj;
import com.xunixianshi.vrshow.obj.assembleObj.AssembleContentItemObj;
import com.xunixianshi.vrshow.recyclerview.RecycleViewDivider;
import com.xunixianshi.vrshow.recyclerview.RecyclerBaseItemOnClickListener;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * 添加內容
 * 这里只能添加 用户未关联到合集的内容, 如果已选择,但未提交的内容不在此页面显示
 *
 * @author DuanChunLin
 * @time 2016/9/29 18:17
 */
public class EditContentFragment extends AssembleBaseFragment {

    public static final String CONTENT_LIST_SELECT_TAG = "contentSelectNotSubmittedList";  //被选中要 绑定的集合
    public static final String BIND_DELETED_CONTENT_TAG = "bindDeletedContentList";//已绑定被删除的集合
    public static final String SELECT_UNBIND_CONTENT_TAG = "selectUnBindContentList"; //选中但未绑定的集合

    @Bind(R.id.my_all_content_list_rv)
    PullToRefreshRecyclerView my_all_content_list_rv;

    @Bind(R.id.title_center_name_tv)
    TextView title_center_name_tv;

    @Bind(R.id.my_assemble_empty_rl)
    RelativeLayout my_assemble_empty_rl;

    @Bind(R.id.my_assemble_net_fail_rl)
    RelativeLayout my_assemble_net_fail_rl;

    @Bind(R.id.edit_assemble_search_rl)
    RelativeLayout edit_assemble_search_rl;

    @Bind(R.id.edit_assemble_search_recycler_view)
    RecyclerView edit_assemble_search_recycler_view;

    @Bind(R.id.edit_assemble_edit_text)
    EditText edit_assemble_edit_text;

    @Bind(R.id.edit_assemble_search_btn)
    Button edit_assemble_search_btn;

    private LoadingAnimationDialog mLoadingAnimationDialog;
    private EditContentAdapter mEditContentAdapter;

    private EditContentAdapter mSearchAdapter;

    private List<AssembleContentItemObj> mAllNetList;
    private List<AssembleContentItemObj> mSelectUnBindContentList; //选中未绑定的数据
    private List<AssembleContentItemObj> mBoundNotSubmittedList;  //所有已被添加， 但未提交的数据 需要去重的数据 主要做去重。不会被改变
    private List<AssembleContentItemObj> mBindDeletedContentList; //绑定但被删除的数据  主要做比较。不会被改变数值

    private int thisPage = 1;
    private int searchPage = 1;
    private boolean isPullToRefresh = false;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View rootView = inflater.inflate(R.layout.fragment_edit_assemble_content, container, false);
        rootView.setClickable(true);// 防止点击穿透，底层的fragment响应上层点击触摸事件
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initData() {
        mAllNetList = new ArrayList<>();
        mSelectUnBindContentList = new ArrayList<>();
        mBoundNotSubmittedList = new ArrayList<>();
        mBindDeletedContentList = new ArrayList<>();
        Bundle bundle = getArguments();
        if (bundle != null) {
            //获取已经选取的内容
            List<AssembleContentItemObj> contentItemObjList = (List<AssembleContentItemObj>) bundle.getSerializable(CONTENT_LIST_SELECT_TAG);
            if (contentItemObjList != null) {
                mBoundNotSubmittedList.addAll(contentItemObjList);
            }
            List<AssembleContentItemObj> bindDeletedContentList = (List<AssembleContentItemObj>) bundle.getSerializable(BIND_DELETED_CONTENT_TAG);
            if (bindDeletedContentList != null) {
                mBindDeletedContentList.addAll(bindDeletedContentList);
            }
            mEditContentAdapter.addItems(mBindDeletedContentList);
        }
        isPullToRefresh = false;
        postHttpUnboundContentListData(thisPage);
    }

    @Override
    protected void initView(View parentView) {
        title_center_name_tv.setText("添加内容");
        mLoadingAnimationDialog = new LoadingAnimationDialog(getActivity());
        my_all_content_list_rv.getRefreshableView().setLayoutManager(new LinearLayoutManager(getActivity()));
        mEditContentAdapter = new EditContentAdapter(getActivity());
        my_all_content_list_rv.getRefreshableView().setAdapter(mEditContentAdapter);
        my_all_content_list_rv.getRefreshableView().addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.VERTICAL, ScreenUtils.dip2px(getActivity(), 1), ContextCompat.getColor(getActivity(), R.color.color_e6e6e6)));

        mSearchAdapter = new EditContentAdapter(getActivity());
        edit_assemble_search_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        edit_assemble_search_recycler_view.setAdapter(mSearchAdapter);
        edit_assemble_search_recycler_view.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.VERTICAL, ScreenUtils.dip2px(getActivity(), 1), ContextCompat.getColor(getActivity(), R.color.color_e6e6e6)));
        initListener();
    }

    /**
     * 初始化监听
     *
     * @author DuanChunLin
     * @time 2016/10/19 15:49
     */
    private void initListener() {
        my_all_content_list_rv.setMode(PullToRefreshBase.Mode.BOTH);
        my_all_content_list_rv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<RecyclerView> refreshView) {
                // 上拉刷新 重置所有数据
                thisPage = 1;
                isPullToRefresh = true;
                postHttpUnboundContentListData(thisPage);
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<RecyclerView> refreshView) {
                // 下拉加载更多 数据获取成功后thispage自动加1 这里不做++操作
                isPullToRefresh = true;
                postHttpUnboundContentListData(thisPage);
            }
        });

        /**
         * 设置编辑内容列表按钮点击监听，如果选中了判断是否是在 上一个页面传进来的item 如果是，则这是一个绑定被删除，但又不需要删除的item.
         *@author DuanChunLin
         *@time 2016/10/19 17:21
         */
        mEditContentAdapter.setRecyclerBaseItemOnClickListener(new RecyclerBaseItemOnClickListener() {
            @Override
            public void onClick(int position) {
                AssembleContentItemObj assembleContentItemObj = mEditContentAdapter.getItem(position);
                if (!assembleContentItemObj.isSelect()) {
                    mSelectUnBindContentList.add(assembleContentItemObj);
                } else {
                    mSelectUnBindContentList.remove(mSearchAdapter.getSameAssemble(mSelectUnBindContentList, assembleContentItemObj));
                }
                assembleContentItemObj.setSelect(!assembleContentItemObj.isSelect());
                mEditContentAdapter.notifyItemChanged(position);
            }
        });


        mSearchAdapter.setRecyclerBaseItemOnClickListener(new RecyclerBaseItemOnClickListener() {
            @Override
            public void onClick(int position) {
                AssembleContentItemObj assembleContentItemObj = mSearchAdapter.getItem(position);

                if (!assembleContentItemObj.isSelect()) {
                    mSelectUnBindContentList.add(assembleContentItemObj);
                } else {
                    mSelectUnBindContentList.remove(mSearchAdapter.getSameAssemble(mSelectUnBindContentList, assembleContentItemObj));
                }
                assembleContentItemObj.setSelect(!assembleContentItemObj.isSelect());
                mSearchAdapter.notifyItemChanged(position);

                synchronizeState(mSearchAdapter.getGroup(), mEditContentAdapter.getGroup());
                notifyContentListAndRemoveRepeat();
            }
        });

        edit_assemble_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSearchAdapter.clearGroup(true);
                synchronizeSearchAdapterList(retrievalBindDeletedContentList(s.toString()));
                postHttpSearchContentKeyword(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    @OnClick({R.id.title_left_back_iv, R.id.edit_assemble_search_btn, R.id.edit_assemble_submit_btn, R.id.edit_assemble_search_close_tv})
    public void OnClickButton(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.edit_assemble_search_btn: //搜索
                showSearchView();
                break;
            case R.id.edit_assemble_search_close_tv: //关闭搜索
                closeSearchView();
                break;
            case R.id.edit_assemble_submit_btn: //选择
                intent = new Intent();
                intent.putExtra(SELECT_UNBIND_CONTENT_TAG, (Serializable) mSelectUnBindContentList);
                backResult(intent);
                break;
            case R.id.title_left_back_iv:
                intent = new Intent();
                setNetContentListState(mSelectUnBindContentList, false);
                backResult(intent);
                break;
        }
    }

    private void synchronizeSearchAdapterList(List<AssembleContentItemObj> list) {
        mSearchAdapter.addItemsNoNotify(list);
        mSearchAdapter.removeItems(mSearchAdapter.getSameSetAssembleList(mSearchAdapter.getGroup(), mBoundNotSubmittedList), false);
        synchronizeState(mEditContentAdapter.getGroup(), mSearchAdapter.getGroup());
        mSearchAdapter.notifyDataSetChanged();
    }


    private List<AssembleContentItemObj> synchronizeState(List<AssembleContentItemObj> synchronizeList, List<AssembleContentItemObj> needSynchronizeList) {
        List<AssembleContentItemObj> list = new ArrayList<>();
        for (Iterator<AssembleContentItemObj> synchronizeIterator = synchronizeList.iterator(); synchronizeIterator.hasNext(); ) {
            AssembleContentItemObj synchronizeItemObj = synchronizeIterator.next();
            for (Iterator<AssembleContentItemObj> allIterator = needSynchronizeList.iterator(); allIterator.hasNext(); ) {
                AssembleContentItemObj needSynchronizeItemObj = allIterator.next();
                if (synchronizeItemObj.getResourceId() == needSynchronizeItemObj.getResourceId()) {
                    needSynchronizeItemObj.setSelect(synchronizeItemObj.isSelect());
                } else if (needSynchronizeItemObj.isSelect()) {
                    list.add(needSynchronizeItemObj);
                }
            }
        }
        return list;
    }

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
     * 检索绑定但要被删除的集合
     * keyword 搜索关键字
     *
     * @author DuanChunLin
     * @time 2016/10/19 14:52
     */
    private List<AssembleContentItemObj> retrievalBindDeletedContentList(String keyword) {
        List<AssembleContentItemObj> list = new ArrayList<>();
        if (StringUtil.isEmpty(keyword)) {
            return list;
        }
        for (Iterator<AssembleContentItemObj> retrievalIterator = mBindDeletedContentList.iterator(); retrievalIterator.hasNext(); ) {
            AssembleContentItemObj needRemoveItemObj = retrievalIterator.next();
            if (needRemoveItemObj.getResourceTitle().contains(keyword)) {
                list.add(needRemoveItemObj);
            }
        }
        return list;
    }

    /**
     * 添加合集
     *
     * @author DuanChunLin
     * @time 2016/10/19 11:20
     */
    private void notifyContentListAndRemoveRepeat() {
        mEditContentAdapter.clearGroup(false);
        mEditContentAdapter.addItemsNoNotify(mBindDeletedContentList);
        mEditContentAdapter.addItemsNoNotify(mAllNetList);
        mEditContentAdapter.removeItems(mEditContentAdapter.getSameSetAssembleList(mEditContentAdapter.getGroup(), mBoundNotSubmittedList), true);
    }

    /**
     * 重新刷新网络请求数据结果界面
     *
     * @author DuanChunLin
     * @time 2016/10/19 14:52
     */
    private void attachPostHttpResultView() {
        my_assemble_net_fail_rl.setVisibility(View.GONE);
        my_assemble_empty_rl.setVisibility(View.GONE);
        if (mEditContentAdapter.getGroup().size() == 0) {
            my_assemble_empty_rl.setVisibility(View.VISIBLE);
            edit_assemble_search_btn.setVisibility(View.GONE);
        }
        mLoadingAnimationDialog.dismiss();
    }

    /**
     * 显示数据请求结果错误界面
     *
     * @author DuanChunLin
     * @time 2016/10/19 14:51
     */
    private void showPostHttpResultError(String msg) {
        my_assemble_empty_rl.setVisibility(View.GONE);
        my_assemble_net_fail_rl.setVisibility(View.GONE);
        if (mEditContentAdapter.getGroup().size() == 0) {
            my_assemble_net_fail_rl.setVisibility(View.VISIBLE);
            showToastMsg(msg);
        }
        mLoadingAnimationDialog.dismiss();
    }


    /**
     * 显示搜索界面
     *
     * @author DuanChunLin
     * @time 2016/10/19 14:51
     */
    private void showSearchView() {
        mSearchAdapter.clearGroup(true);
        edit_assemble_edit_text.setText(null);
        edit_assemble_search_rl.setVisibility(View.VISIBLE);
        edit_assemble_edit_text.postDelayed(new Runnable() {
            @Override
            public void run() {
                edit_assemble_edit_text.setFocusable(true);
                edit_assemble_edit_text.setFocusableInTouchMode(true);
                edit_assemble_edit_text.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(edit_assemble_edit_text, 0);
                }
            }
        }, 200);
    }

    /**
     * 关闭搜索界面
     *
     * @author DuanChunLin
     * @time 2016/10/19 14:51
     */
    private void closeSearchView() {
        edit_assemble_search_rl.setVisibility(View.GONE);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edit_assemble_edit_text.getWindowToken(), 0);

    }

    /**
     * 获取未綁定集合的內容
     *
     * @author hechuang
     * @time 2016/9/21 10:40
     */
    private void postHttpUnboundContentListData(final int page) {
        if (page == 1 && !isPullToRefresh) {
            mLoadingAnimationDialog.show();
        }
        String uid;
        if (AppContent.UID.equals("")) {
            uid = "-1";
        } else {
            uid = AppContent.UID;
        }
        // 加密手机号
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(uid);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("currPage", page);
        hashMap.put("pageSize", 10);
        hashMap.put("userId", Ras_uid);
        hashMap.put("bySort", 1);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        setCancelHttpTag("/user/compilation/unbound/resource/list/service");
        OkHttpAPI.postHttpData("/user/compilation/unbound/resource/list/service", jsonData, new GenericsCallback<AssembleCollectionListObj>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                my_all_content_list_rv.onRefreshComplete();
                showPostHttpResultError("网络连接失败，请检查网络！");
            }

            @Override
            public void onResponse(AssembleCollectionListObj result, int id) {
                my_all_content_list_rv.onRefreshComplete();
                if (result.getResCode().equals("0")) {
                    if (result.getList() != null && result.getList().size() > 0) {

                        mAllNetList.clear();
                        mAllNetList.addAll(result.getList());
                        notifyContentListAndRemoveRepeat();
                        thisPage++;
                    }
                    attachPostHttpResultView();
                } else {
                    showPostHttpResultError(result.getResDesc());
                }
            }
        });
    }


    /**
     * 网络获取 关键字搜索列表
     *
     * @author DuanChunLin
     * @time 2016/10/19 15:49
     */
    private void postHttpSearchContentKeyword(String keyword) {
        if (StringUtil.isEmpty(keyword)) {
            return;
        }
        String uid;
        if (AppContent.UID.equals("")) {
            uid = "-1";
        } else {
            uid = AppContent.UID;
        }
        // 加密手机号
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(uid);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("currPage", 1);
        hashMap.put("pageSize", 50); //感觉够够了。如果单独页面可以下啦刷新
        hashMap.put("userId", Ras_uid);
        hashMap.put("bySort", 1);
        hashMap.put("keyWord", keyword);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/user/compilation/unbound/resource/search/service", jsonData, new GenericsCallback<AssembleCollectionListObj>() {
            @Override
            public void onError(Call call, Exception e, int id) {
//                showToastMsg("网络连接失败，请检查网络！");
            }

            @Override
            public void onResponse(AssembleCollectionListObj result, int id) {
                if (result.getResCode().equals("0")) {
                    if (result.getList() != null && result.getList().size() > 0) {
                        synchronizeSearchAdapterList(result.getList());
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
