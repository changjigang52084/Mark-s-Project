package com.xunixianshi.vrshow.my;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.MLog;
import com.hch.viewlib.util.ScreenUtils;
import com.hch.viewlib.util.SimpleSharedPreferences;
import com.hch.viewlib.widget.view.CircleImageView;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.cityList.CityListActivity;
import com.xunixianshi.vrshow.customview.HintDialog;
import com.xunixianshi.vrshow.my.fragment.database.UploadManageUtil;
import com.xunixianshi.vrshow.my.information.ModifyAvatarActivity;
import com.xunixianshi.vrshow.my.information.ModifyNameActivity;
import com.xunixianshi.vrshow.my.information.SignatureEditPageActivity;
import com.xunixianshi.vrshow.my.information.sexSelectDialog.SexSelectDialog;
import com.xunixianshi.vrshow.obj.CityListItemResult;
import com.xunixianshi.vrshow.obj.CityListResult;
import com.xunixianshi.vrshow.obj.QueryUserInformationObj;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.interfaces.HttpError;
import com.zhy.http.okhttp.interfaces.HttpSuccess;
import com.zhy.http.okhttp.utils.PicassoUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * TODO 个人信息页面
 *
 * @author MarkChang
 * @ClassName PersonalInformationActivity
 * @time 2016/11/1 15:40
 */
public class PersonalInformationActivity extends BaseAct {

    @Bind(R.id.my_back_rl)
    RelativeLayout my_back_rl;
    @Bind(R.id.my_content_tv)
    TextView my_content_tv;
    @Bind(R.id.personal_information_icon_civ)
    CircleImageView personal_information_icon_civ;
    @Bind(R.id.personal_information_icon_rl)
    RelativeLayout personal_information_icon_rl;
    @Bind(R.id.personal_information_user_name_tv)
    TextView personal_information_user_name_tv;
    @Bind(R.id.personal_information_name_rl)
    RelativeLayout personal_information_name_rl;
    @Bind(R.id.personal_information_user_gender_tv)
    TextView personal_information_user_gender_tv;
    @Bind(R.id.personal_information_gender_rl)
    RelativeLayout personal_information_gender_rl;
    @Bind(R.id.personal_information_user_region_tv)
    TextView personal_information_user_region_tv;
    @Bind(R.id.personal_information_region_rl)
    RelativeLayout personal_information_region_rl;
    @Bind(R.id.personal_information_input_brief_introduction_tv)
    TextView personal_information_input_brief_introduction_tv;
    //    @Bind(R.id.personal_information_account_rl)
//    RelativeLayout personal_information_account_rl;
    @Bind(R.id.personal_information_exit_login_btn)
    Button personal_information_exit_login_btn;

    private HintDialog mHintDialog;

    public static final int USER_ICON_REQUEST = 1;
    public static final int USER_NAME_REQUEST = 2;
    public static final int USER_PROFILE_REQUEST = 3;
    public static final int USE_CITY_LIST_REQUEST = 4;
    private String userIcon; // 用户图像
    private String userName; // 用户名称
    private String userSex; // 用户性别
    private String residenceAreaName; // 用户地区名称
    private String residenceAreaDetail; // 用户详细地址
    private String userPersonalProfile = ""; // 用户简介
    private String userCity;
    private String userCityCode = "-1";
    private int userNameModifyNum; // 用户名称修改次数
    private boolean iconType = true;//true 代表是网络路径  false代表是本地路径

    private int modifyUserSex; // 修改后的用户性别
    private String modifyUserPersonalProfile; // 修改后的用户简介
    private boolean canEdit = true;
    private boolean havePersonal = false;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_personal_information);
        ButterKnife.bind(this);
    }

    @Override
    protected void initView() {
        super.initView();
        mHintDialog = new HintDialog(PersonalInformationActivity.this);
    }

    @Override
    protected void initData() {
        super.initData();
        my_content_tv.setText("个人信息");
        Intent data = getIntent();
        if (data != null) {
            userCity = data.getStringExtra("areaName");
            userCityCode = data.getStringExtra("areaCode");
            if (userCity != null && !userCity.equals("")) {
                personal_information_user_region_tv.setText(userCity);
                commitUserCityData();
            } else {
                getNetData();
            }
        }
    }

    @OnClick({R.id.my_back_rl, R.id.personal_information_icon_rl, R.id.personal_information_name_rl,
            R.id.personal_information_gender_rl, R.id.personal_information_region_rl,
            R.id.personal_information_exit_login_btn, R.id.personal_information_input_brief_introduction_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_back_rl:
                PersonalInformationActivity.this.finish();
                break;
            case R.id.personal_information_icon_rl: // 用户修改图像按钮
                Intent intent1 = new Intent(); // 传一张用户图像到下一个页面
                intent1.setClass(PersonalInformationActivity.this, ModifyAvatarActivity.class);
                intent1.putExtra("userIcon", userIcon);
                intent1.putExtra("iconType", iconType);
                startActivityForResult(intent1, USER_ICON_REQUEST);
                break;
            case R.id.personal_information_name_rl: // 用户修改名字按钮
                Intent intent2 = new Intent();
                intent2.setClass(PersonalInformationActivity.this, ModifyNameActivity.class);
                intent2.putExtra("canEdit", canEdit);
                intent2.putExtra("username", userName);
                startActivityForResult(intent2, USER_NAME_REQUEST);
                break;
            case R.id.personal_information_gender_rl: // 用户修改性别按钮
                SexSelectDialog sexSelectDialog = new SexSelectDialog(PersonalInformationActivity.this);
                sexSelectDialog.setSex(userSex);
                sexSelectDialog.setOnSexClickLister(new SexSelectDialog.OnSexSelectLister() {
                    @Override
                    public void sexSelectClick(String sex) {
                        userSex = sex;
                        personal_information_user_gender_tv.setText(userSex);
                        modifyUserSexInformation();
                    }
                });
                break;
            case R.id.personal_information_region_rl: // 用户修改地区按钮
//                Bundle bundle = new Bundle();
//                bundle.putString("areaCode","0");
//                showActivityForResult(PersonalInformationActivity.this, CityListActivity.class,bundle);
                getCityListData();
                break;
//            case R.id.personal_information_account_rl: // 用户账户按钮
//                startActivity(new Intent(PersonalInformationActivity.this, ModifyUserCountActivity.class));
//                break;
            case R.id.personal_information_exit_login_btn: // 用户退出登录按钮
                showDialog();
                break;
            case R.id.personal_information_input_brief_introduction_tv: // 用户简介
                Intent intent3 = new Intent();
                intent3.setClass(PersonalInformationActivity.this, SignatureEditPageActivity.class);
                if (havePersonal) {
                    intent3.putExtra("userPersonalProfile", userPersonalProfile);
                } else {
                    intent3.putExtra("userPersonalProfile", "");
                }
                startActivityForResult(intent3, USER_PROFILE_REQUEST);
                break;
        }
    }

    private ArrayList<CityListItemResult> items = new ArrayList<CityListItemResult>();

    /**
     * 获取城市列表
     *
     * @ClassName PersonalInformationActivity
     * @author HeChuang
     * @time 2017/1/4 10:27
     */
    private void getCityListData() {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("areaCode", 0);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/config/area/list/service", jsonData, new GenericsCallback<CityListResult>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showToastMsg("没有获取到城市列表");
            }

            @Override
            public void onResponse(CityListResult result, int id) {
                if (result.getResCode().equals("0")) {
                    if (result.getList().size() > 0) {
                        items.clear();
                        Bundle bundle = new Bundle();
                        items.addAll(result.getList());
                        bundle.putSerializable("areaCode", items);
                        showActivity(PersonalInformationActivity.this, CityListActivity.class, bundle);
                    } else {
                        showToastMsg("没有获取到城市列表");
                    }
                }
            }
        });
    }

    /**
     * 修改用户性别信息
     */
    private void modifyUserSexInformation() {
        if (userSex.equals("女")) {
            modifyUserSex = 1;
        } else {
            modifyUserSex = 0;
        }
        commitData();
    }

    /**
     * 获取网络数据
     */
    private void getNetData() {
        if (AppContent.UID.equals("")) {
            return;
        }
        MLog.d("AppContent.UID:" + AppContent.UID);
        String rsa_userId = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("userId", rsa_userId);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        // 获取网络数据
        OkHttpAPI.postHttpData("/user/info/get/service", jsonData, new GenericsCallback<QueryUserInformationObj>() {
            @Override
            public void onError(Call call, Exception e, int id) {
//                showToastMsg("网络连接失败，请检查网络！");
            }

            @Override
            public void onResponse(QueryUserInformationObj response, int id) {
                if (response.getResCode().equals("0")) {
                    userIcon = response.getUserIcon();
                    userName = response.getUserName();
                    if (response.getUserSex() == 0) {
                        userSex = "男";
                        modifyUserSex = 0;
                    } else {
                        userSex = "女";
                        modifyUserSex = 1;
                    }
                    if (response.getUserNameModifyNum() > 0) {
                        canEdit = false;
                    } else {
                        canEdit = true;
                    }
                    residenceAreaName = response.getResidenceAreaName();
                    userPersonalProfile = response.getUserPersonalProfile();
                    userNameModifyNum = response.getUserNameModifyNum();
                    if(personal_information_icon_civ == null){
                        personal_information_icon_civ = findViewById(R.id.personal_information_icon_civ);
                    }
                    PicassoUtil.loadImage(PersonalInformationActivity.this, userIcon, personal_information_icon_civ, R.drawable.registered_status);
                    personal_information_user_name_tv.setText(userName);
                    personal_information_user_gender_tv.setText(userSex);
                    if (!residenceAreaName.equals("")) {
                        personal_information_user_region_tv.setText(residenceAreaName);
                    }

                    if (userPersonalProfile.equals("-1")) {
                        userPersonalProfile = "";
                    }
                    if (userPersonalProfile.equals("") && userPersonalProfile.length() <= 0) {
                        havePersonal = false;
                        personal_information_input_brief_introduction_tv.setTextColor(Color.parseColor("#999999"));
                        userPersonalProfile = "还没有写签名，赶紧去写一个威武霸气的签名吧！";
                    } else {
                        havePersonal = true;
                        personal_information_input_brief_introduction_tv.setTextColor(Color.parseColor("#333333"));
                    }
                    personal_information_input_brief_introduction_tv.setText(userPersonalProfile);
                } else {
//                    showToastMsg(response.getResDesc());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case USER_ICON_REQUEST:
                if (data != null) {
                    String photo = data.getStringExtra("filePath");
                    userIcon = photo;
                    iconType = false;
                    Bitmap photoIcon = BitmapFactory.decodeFile(photo);
                    personal_information_icon_civ.setImageBitmap(photoIcon);
                }
                break;
            case USER_NAME_REQUEST:
                if (data != null) {
                    userName = data.getStringExtra("KEY_USER_NAME");
                    canEdit = data.getBooleanExtra("canEdit", true);
                    personal_information_user_name_tv.setText(userName);
                }
                break;
            case USER_PROFILE_REQUEST:
                if (data != null) {
                    userPersonalProfile = data.getStringExtra("signatureProfile");
                    personal_information_input_brief_introduction_tv.setText(userPersonalProfile);
                    commitData();
                }
                break;
            case USE_CITY_LIST_REQUEST:
                if (data != null) {
                    personal_information_user_region_tv.setText(data.getStringExtra("city"));
                    SimpleSharedPreferences.putString("city", data.getStringExtra("city"), PersonalInformationActivity.this);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取城市地区列表
     *
     * @ClassName PersonalInformationActivity
     * @author HeChuang
     * @time 2017/1/4 10:30
     */
    private void commitUserCityData() {
        if (AppContent.UID.equals("")) {
            return;
        }
        String rsa_userId = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("userId", rsa_userId);
        hashMap.put("userSex", -1);
        hashMap.put("userDateBirth", -1);
        hashMap.put("userResidenceAreaCode", userCityCode);
        hashMap.put("userResidenceAreaDetail", -1);
        hashMap.put("userPersonalProfile", -1);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        // 获取网络数据
        OkHttpAPI.postObjectData("/user/info/edit/service", jsonData, new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                try {
                    if (result.getString("resCode").equals("0")) {
//                        showToastMsg(result.getString("resDesc"));
                        getNetData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new HttpError() {
            @Override
            public void onError(Call call, Exception e, int id) {
//                showToastMsg("网络连接失败，请检查网络！");
            }
        });
    }

    /**
     * 提交数据
     *
     * @author HeChuang
     * @time 2016/10/24 14:03
     */
    private void commitData() {
        if (AppContent.UID.equals("")) {
            return;
        }
        String rsa_userId = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
// 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("userId", rsa_userId);
        hashMap.put("userSex", modifyUserSex);
        hashMap.put("userDateBirth", -1);
        hashMap.put("userResidenceAreaCode", userCityCode);
        hashMap.put("userResidenceAreaDetail", -1);
        hashMap.put("userPersonalProfile", userPersonalProfile);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        // 获取网络数据
        OkHttpAPI.postObjectData("/user/info/edit/service", jsonData, new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                try {
                    if (result.getString("resCode").equals("0")) {
//                        showToastMsg(result.getString("resDesc"));
                        getNetData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new HttpError() {
            @Override
            public void onError(Call call, Exception e, int id) {
//                showToastMsg("网络连接失败，请检查网络！");
            }
        });
    }

    // 退出登录状态
    private void showDialog() {
        mHintDialog.setContextText("是否确定退出");
        mHintDialog.setOkButText("确定");
        mHintDialog.setCancelText("取消");
        mHintDialog.setOkClickListaner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppContent.UID = "";
                SimpleSharedPreferences.putString("loginUid", "",
                        PersonalInformationActivity.this);
                SimpleSharedPreferences.putString("userIconUrl", "", PersonalInformationActivity.this);
                AppContent.fromWelcome = false;
                //用户退出，暂停所有上传任务
                UploadManageUtil.getInstance().cancelUpload();
                PersonalInformationActivity.this.finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
