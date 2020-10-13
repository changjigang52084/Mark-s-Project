package com.lzkj.aidlservice.manager;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.bo.AdpressDataPackage;
import com.baize.adpress.core.protocol.dto.AdpressCommandReceiptPackage;
import com.baize.adpress.core.protocol.dto.AdpressProgramReceiptErrorPackage;
import com.baize.adpress.core.protocol.dto.AdpressProgramReceiptPackage;
import com.lzkj.aidlservice.R;
import com.lzkj.aidlservice.api.interfaces.IRequestCallback;
import com.lzkj.aidlservice.bo.HttpRequestBean;
import com.lzkj.aidlservice.util.ConfigSettings;
import com.lzkj.aidlservice.util.HttpConfigSetting;
import com.lzkj.aidlservice.util.HttpUtil;
import com.lzkj.aidlservice.util.ListUitl;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;
import com.lzkj.aidlservice.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2015年6月25日 上午11:06:51
 * @parameter 命令回执工具类
 */
public class CommandReceiptManager implements IRequestCallback {
    private static final LogTag TAG = LogUtils.getLogTag(CommandReceiptManager.class.getSimpleName(), true);

    /**
     * 执行命令回执
     *
     * @param adpressDataPackage AdpressDataPackage对象
     * @param responseCode       响应码
     * @param jsonData           发送的数据
     */
    public static void commandReceipt(AdpressDataPackage adpressDataPackage, Integer responseCode, String jsonData) {
        try {
            if (null == adpressDataPackage) {
                return;
            }
            AdpressCommandReceiptPackage commandReceiptPagckage = new AdpressCommandReceiptPackage();
            LogUtils.d(TAG, "commandReceipt", JSON.toJSONString(adpressDataPackage));
            if (null != ConfigSettings.getDid()) {
                commandReceiptPagckage.setDeviceId(Long.parseLong(ConfigSettings.getDid()));
            } else {
                commandReceiptPagckage.setDeviceId(null);
            }
            if (null != adpressDataPackage.getData() && null != adpressDataPackage.getData().getOperationId()) {
                commandReceiptPagckage.setOperatorId(adpressDataPackage.getData().getOperationId());
            } else {
                //没有就塞默认值-1
                commandReceiptPagckage.setOperatorId(-1L);
            }
            commandReceiptPagckage.setState(responseCode);
            commandReceiptPagckage.setType(adpressDataPackage.getTypeStr());
            commandReceiptPagckage.setData(jsonData);
            sendPostToServer(JSON.toJSONString(commandReceiptPagckage), "responseReceiver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 响应命令执行状态
     *
     * @param responseCode        命令值
     * @param errMessage          错误信息
     * @param downloadSuccessList 下载成功的节目列表id
     * @param downloadFiledList   下载失败的节目列表id
     */
    public static void responseDownloadState(AdpressDataPackage adpressDataPackage, int responseCode, String errMessage,
                                             List<String> downloadSuccessList,
                                             List<String> downloadFiledList) {
        try {
            if ((null == downloadFiledList && null == downloadSuccessList)
                    || (downloadSuccessList.isEmpty() && downloadFiledList.isEmpty())) {
                return;
            }

//			if (null != adpressDataPackage && null == adpressDataPackage.getData()) {
//				return;
//			}
            Long operationId = -1L;
            String typeStr = "8";//默认为同步节目回执
            if (null != adpressDataPackage && null != adpressDataPackage.getData()) {
                operationId = adpressDataPackage.getData().getOperationId();
                typeStr = adpressDataPackage.getTypeStr();
            }
            if (null == typeStr) {
                typeStr = "8";
            }
            if (null == operationId) {
                operationId = -1L;
            }

            AdpressCommandReceiptPackage commandReceiptPagckage = new AdpressCommandReceiptPackage();
            commandReceiptPagckage.setDeviceId(Long.parseLong(ConfigSettings.getDid()));
            commandReceiptPagckage.setState(responseCode);
            commandReceiptPagckage.setOperatorId(operationId);
            commandReceiptPagckage.setType(typeStr);

            AdpressProgramReceiptPackage adpressProgramReceiptPackage = new AdpressProgramReceiptPackage();
            adpressProgramReceiptPackage.setErrorList(getErrorProgarmList(downloadFiledList, errMessage));
            adpressProgramReceiptPackage.setSuccessList(downloadSuccessList);
            commandReceiptPagckage.setData(JSON.toJSONString(adpressProgramReceiptPackage));
            sendPostToServer(JSON.toJSONString(commandReceiptPagckage), "responseDownloadState");

//			String prmListId = ShreadUtil.newInstance().getString(CommutShardUtil.PROGRAM_LIST_ID);
//			ProtocolPackageAssembler<AdpressProtocolPackage> assembler = AdpressProtocolPackageAssemblerFactory.create(
//			Map<String, Object> materials = new HashMap<String, Object>();
//			materials.put("state", responseCode);
//			materials.put("deviceId", Long.parseLong(ConfigSettings.getDeviceId()));
//			if (null != errMessage) {
//				materials.put("message", errMessage);
//			}
//			materials.put("programListId", prmListId);
            LogUtils.d(TAG, "uploadDownloadPlan", "downloadSuccessList:" + JSON.toJSONString(downloadSuccessList)
                    + ",downloadFiledList:" + JSON.toJSONString(downloadFiledList));
//			AdpressProtocolPackage assembleRes = assembler.assemble(materials);
//			responsePost(adpressDataPackage, assembleRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<AdpressProgramReceiptErrorPackage> getErrorProgarmList(
            List<String> downloadFiledList, String errMessage) {
        List<AdpressProgramReceiptErrorPackage> programReceiptErrorPackages = new ArrayList<>();
        if (ListUitl.isEmpty(downloadFiledList) && StringUtil.isNullStr(errMessage)) {
            LogUtils.d(TAG, "getErrorProgarmList", "errMessage:" + errMessage
                    + ",downloadFiledList:" + JSON.toJSONString(downloadFiledList));
            return programReceiptErrorPackages;
        }

        for (String prmKey : downloadFiledList) {
            LogUtils.d(TAG, "getErrorProgarmList", "prmKey:" + prmKey);
            AdpressProgramReceiptErrorPackage adpressProgramReceiptErrorPackage
                    = new AdpressProgramReceiptErrorPackage();
            adpressProgramReceiptErrorPackage.setKey(prmKey);
            adpressProgramReceiptErrorPackage.setRemark(errMessage);
            programReceiptErrorPackages.add(adpressProgramReceiptErrorPackage);
        }
        return programReceiptErrorPackages;
    }


    /**
     * 执行返回取消节目列表
     *
     * @param adpressDataPackage 命令数据包
     * @param responseCode       回执结果
     * @param delSuccessList     成功删除的节目id集合
     * @param delFiledList       失败删除的节目id集合
     */
    public static void commandReceiptCancelPrm(AdpressDataPackage adpressDataPackage
            , Integer responseCode, List<String> delSuccessList, List<String> delFiledList) {
        try {
            if (null == adpressDataPackage) {
                return;
            }
            AdpressCommandReceiptPackage commandReceiptPagckage = new AdpressCommandReceiptPackage();
            commandReceiptPagckage.setDeviceId(Long.parseLong(ConfigSettings.getDid()));
            commandReceiptPagckage.setState(responseCode);
            Long operationId = adpressDataPackage.getData().getOperationId();
            commandReceiptPagckage.setOperatorId(operationId);
            commandReceiptPagckage.setType(adpressDataPackage.getTypeStr());
            AdpressProgramReceiptPackage adpressProgramReceiptPackage = new AdpressProgramReceiptPackage();
            adpressProgramReceiptPackage.setSuccessList(delSuccessList);
            adpressProgramReceiptPackage.setErrorList(getErrorProgarmList(delFiledList,
                    StringUtil.getString(R.string.prm_del_error)));
            commandReceiptPagckage.setData(JSON.toJSONString(adpressProgramReceiptPackage));
            sendPostToServer(JSON.toJSONString(commandReceiptPagckage), "responseReceiver");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送post请求给服务器
     *
     * @param responseData 要发送的数据
     *                     <p>
     *                     解决上一个版本中写的固定的httpTag,这样就可以把上一个版本中保存在数据库里面请求失败的清除掉
     */
    private static void sendPostToServer(String responseData, String httpTag) {
        HttpRequestBean httpRequestBean = new HttpRequestBean();
        httpRequestBean.setRequestUrl(HttpConfigSetting.getPushReceiptUrl()); // 往HttpRequestBean对象里面添加：消息推送回执的URL地址。
        httpRequestBean.setRequestRestry(15);// 往HttpRequestBean对象里面添加：请求失败重试次数。
        httpRequestBean.setRequestCallback(null);// 没有回调。
        httpRequestBean.setRequestParm(responseData);// 往HttpRequestBean对象里面添加：要发送给后台的数据。
        httpRequestBean.setRequestTag(httpTag);// 往HttpRequestBean对象里面添加：httpTag标签。
        HttpUtil.newInstance().postRequest(httpRequestBean);
    }

    @Override
    public void onSuccess(String result, String httpTag, String requestUrl) {
    }

    @Override
    public void onFaile(String errMsg, String httpTag, String requestUrl) {
    }
}
