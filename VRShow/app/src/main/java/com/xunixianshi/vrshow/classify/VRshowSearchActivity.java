package com.xunixianshi.vrshow.classify;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.hch.viewlib.widget.DeletableEditText;
import com.hch.viewlib.widget.MyListView;
import com.hch.viewlib.widget.Tag;
import com.hch.viewlib.widget.TagListView;
import com.hch.viewlib.widget.TagView;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.database.RecordSQLiteOpenHelper;
import com.xunixianshi.vrshow.obj.search.SearchHotWordListObj;
import com.xunixianshi.vrshow.obj.search.SearchHotWordObj;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * 搜索页
 *
 * @author HeChuang
 * @ClassName VRshowSearchActivity
 * @time 2016/11/1 15:38
 */
public class VRshowSearchActivity extends BaseAct {

    private TagListView mTagListView;
    private final List<Tag> mTags = new ArrayList<Tag>();
    private ArrayList<SearchHotWordListObj> list;
    private TextView tab_bar_cancel_tv;
    private DeletableEditText tab_bar_keyword_et;
    private LinearLayout search_history_ll;

    private MyListView listView;

    private TextView tv_tip;

    private RelativeLayout delete_clear_rl;

    private RecordSQLiteOpenHelper helper = new RecordSQLiteOpenHelper(this);
    private SQLiteDatabase db;
    private BaseAdapter adapter;


    @Override
    public void setRootView() {
        setContentView(R.layout.activity_vrshow_search);
        initView();
        getSearchHotWord();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryData("");
    }

    public void initView() {
        listView = (MyListView) findViewById(R.id.listView);
        search_history_ll = (LinearLayout) findViewById(R.id.search_history_ll);
        delete_clear_rl = (RelativeLayout) findViewById(R.id.delete_clear_rl);
        delete_clear_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
                queryData("");
            }
        });
        tv_tip = (TextView) findViewById(R.id.tv_tip);
        tab_bar_keyword_et = (DeletableEditText) findViewById(R.id.tab_bar_keyword_et);
        mTagListView = (TagListView) findViewById(R.id.tagview);
        tab_bar_cancel_tv = (TextView) findViewById(R.id.tab_bar_cancel_tv);
        tab_bar_cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VRshowSearchActivity.this.finish();
            }
        });
    }

    // 从网络获取搜索热词
    private void getSearchHotWord() {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("currPage", 1);
        hashMap.put("pageSize", 10);
        hashMap.put("clientId", 3); // 3表示android热词
        hashMap.put("queryType", 1); // 1表示根据热词排序方式顺序排序(排序值越小数据越靠前)
        String jsonData = StringUtil.MapToJson(hashMap);
        setCancelHttpTag("/resources/search/hotkey/list/service");
        OkHttpAPI.postHttpData("/resources/search/hotkey/list/service", jsonData, new GenericsCallback<SearchHotWordObj>() {

            @Override
            public void onError(Call call, Exception e, int id) {
                showMsg("网络连接失败,请检查网络！");
            }

            @Override
            public void onResponse(SearchHotWordObj response, int id) {
                list = new ArrayList<SearchHotWordListObj>();
                SearchHotWordListObj item;
                if (response.getResCode().equals("0")) {
                    if (response.getList() != null && response.getList().size() > 0) {
                        list = response.getList();
                        mTags.clear();
                        for (int i = 0; i < list.size(); i++) {
                            item = list.get(i);
                            Tag tag = new Tag();
                            String keyWord = item.getKeyWord();
                            tag.setChecked(true);
                            tag.setTitle(keyWord);
                            mTags.add(tag);
                        }
                        mTagListView.setTags(mTags);
                    }
                } else {
                    showMsg(response.getResDesc());
                }
            }
        });
    }

    private void initListener() {
        // 搜索框的键盘搜索键点击回调
        tab_bar_keyword_et.setOnKeyListener(new View.OnKeyListener() {// 输入完后按键盘上的搜索键
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {// 修改回车键功能
                    String searchStr = tab_bar_keyword_et.getText().toString().trim();
                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    if (StringUtil.isEmpty(searchStr)) {
                        showToastMsg("请输入搜索关键字");
                        return false;
                    }
                    // 按完搜索键后将当前查询的关键字保存起来,如果该关键字已经存在就不执行保存
                    boolean hasData = hasData(searchStr);
                    if (!hasData) {
                        insertData(searchStr);
                    }
                    Intent intent = new Intent();
                    intent.setClass(VRshowSearchActivity.this, SearchResultActivity.class);
                    intent.putExtra("mHistoryKeyword", searchStr);
                    startActivity(intent);
                }
                return false;
            }
        });
        tab_bar_keyword_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    search_history_ll.setVisibility(View.VISIBLE);
                }
            }
        });

        // 搜索框的文本变化实时监听
        tab_bar_keyword_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() != 0) {
                    search_history_ll.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() == 0) {
                    tv_tip.setText("搜索历史");
                }
                String tempName = tab_bar_keyword_et.getText().toString();
                // 根据tempName去模糊查询数据库中有没有数据
                queryData(tempName);

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                String mHistoryKeyword = textView.getText().toString();
                Intent intent = new Intent();
                intent.setClass(VRshowSearchActivity.this, SearchResultActivity.class);
                intent.putExtra("mHistoryKeyword", mHistoryKeyword);
                startActivity(intent);
            }
        });

        // 第一次进入查询所有的历史记录
        queryData("");

        mTagListView.setTags(mTags);
        mTagListView.setOnTagClickListener(new TagListView.OnTagClickListener() {
            @Override
            public void onTagClick(TagView tagView, Tag tag) {
                Intent intent = new Intent();
                intent.setClass(VRshowSearchActivity.this, SearchResultActivity.class);
                intent.putExtra("mHistoryKeyword", tag.getTitle());
                boolean hasData = hasData(tag.getTitle().toString().trim());
                if (!hasData) {
                    insertData(tag.getTitle().toString().trim());
                }
                startActivity(intent);
            }
        });
    }

    /**
     * 插入数据
     */
    private void insertData(String tempName) {
        db = helper.getWritableDatabase();
        db.execSQL("insert into records(name) values('" + tempName + "')");
        db.close();
    }

    /**
     * 模糊查询数据
     */
    private void queryData(String tempName) {
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name like '%" + tempName + "%' order by id desc ", null);
        // 创建adapter适配器对象
        adapter = new SimpleCursorAdapter(this, R.layout.simples_list_item_1, cursor, new String[]{"name"},
                new int[]{android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        // 设置适配器
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * 检查数据库中是否已经有该条记录
     */
    private boolean hasData(String tempName) {
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name =?", new String[]{tempName});
        //判断是否有下一个
        return cursor.moveToNext();
    }

    /**
     * 清空数据
     */
    private void deleteData() {
        db = helper.getWritableDatabase();
        db.execSQL("delete from records");
        db.close();
    }

    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

