package com.lzkj.aidlservice.api.sync;

import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.common.constant.protocol.CommandStateConstant;
import com.baize.adpress.core.protocol.bo.AdpressDataPackage;
import com.baize.adpress.core.protocol.bo.protocol.push.command.CommandProgramSynchronizeSetup;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Material;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Program;
import com.baize.adpress.core.protocol.dto.ProgramListPackage;
import com.baize.lz.core.utils.MD5Utils;
import com.lzkj.aidlservice.R;
import com.lzkj.aidlservice.api.interfaces.IRequestCallback;
import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.bo.HttpRequestBean;
import com.lzkj.aidlservice.bo.ResponseContent;
import com.lzkj.aidlservice.manager.CommandReceiptManager;
import com.lzkj.aidlservice.util.AppUtil;
import com.lzkj.aidlservice.util.ConfigSettings;
import com.lzkj.aidlservice.util.Constant;
import com.lzkj.aidlservice.util.FileUtile;
import com.lzkj.aidlservice.util.HttpConfigSetting;
import com.lzkj.aidlservice.util.HttpConstants;
import com.lzkj.aidlservice.util.HttpUtil;
import com.lzkj.aidlservice.util.ListUitl;
import com.lzkj.aidlservice.util.ParsePrmUitl;
import com.lzkj.aidlservice.util.SharedUtil;
import com.lzkj.aidlservice.util.StringUtil;
import com.lzkj.aidlservice.util.Util;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2016年1月12日 上午10:27:01
 * @parameter 请求更新节目的线程
 */
public class RequestSyncUpdateProgram implements Runnable, IRequestCallback {

    private static final String TAG = "RequestSyncUpdate";

    private String mRequestParam;

    public RequestSyncUpdateProgram(String requestParam) {
        mRequestParam = requestParam;
    }

    @Override
    public void run() {
        requestSyncDevicePrm();
    }

    /**
     * 请求同步节目信息
     */
    private void requestSyncDevicePrm() {
        if (!StringUtil.isNullStr(mRequestParam)) {
            String requestUrl = HttpConfigSetting.getSyncUpdateDevicePrmUrl(ConfigSettings.getDid());
            Log.d(TAG, "requestUrl: " + requestUrl);
            HttpRequestBean httpRequestBean = new HttpRequestBean();
            httpRequestBean.setRequestUrl(requestUrl);
            httpRequestBean.setRequestCallback(this);
            httpRequestBean.setRequestParm(mRequestParam);
            httpRequestBean.setRequestTag(RequestSyncUpdateProgram.class.getSimpleName());
            HttpUtil.newInstance().postRequest(httpRequestBean);
        }
    }

    @Override
    public void onSuccess(String result, String httpTag, String requestUrl) {
        //同步节目成功以后，判断本地节目是否一样，
        //如果是一样的则不做任何处理，如果不一样则将不一样的节目进行更新，
        //调用下载apk进行下载最新的素材。
        Log.d(TAG, "RequestSyncUpdateProgram onSuccess result: " + result);
        if (!isUnBind()) {
            try {
                isUpdatePrm(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isUpdatePrm(String remotePrm) throws Exception {
        boolean isUpdate = false;
        if (StringUtil.isNullStr(remotePrm)) {
            return isUpdate;
        }
        //解析当前节目
        ResponseContent responseContent = JSON.parseObject(remotePrm, ResponseContent.class);
        String adptressData = (String) responseContent.getData();
        AdpressDataPackage adpressDataPackage = AdpressDataPackage.parser(adptressData);
        if (responseContent.isSuccess()) {
            String prmValue = adpressDataPackage.getData().toJson();
            Log.d(TAG, "isUpdatePrm prmValue: " + prmValue);
            if (null == prmValue) {
                return isUpdate;
            }
            //读取.temp节目，返回给服务器告诉他未下载成功
            List<Program> localUnDownloadProgramList = Util.getLocalProgramList(true);//读取本地所有的未下载的节目
            if (ListUitl.isNotEmpty(localUnDownloadProgramList)) {
                downloadTempPrm(localUnDownloadProgramList); //下载素材
                sendReceipt(adpressDataPackage, Util.getPrmKey(localUnDownloadProgramList), new ArrayList<String>());//将所有同步的节目重新发送一遍下载成功的回执
            }
            CommandProgramSynchronizeSetup commandProgramSynchronizeSetup = JSON.parseObject(prmValue, CommandProgramSynchronizeSetup.class);
            //同步最新的节目列表
            List<Program> remoteProgramList = commandProgramSynchronizeSetup.getProgramList();
            if (ConfigSettings.DEFAULT_MAX_FOLW != -1 && ParsePrmUitl.checkIsExceedFolw(remoteProgramList, false)) {
                Log.w(TAG, "isUpdatePrm folw exceed.");
                String tipMsg = String.format(StringUtil.getString(R.string.tip_folw_over), ConfigSettings.getMaxFolw() + "");
                List<Program> localProgramList = Util.getLocalProgramList(false);//读取本地所有的节目
                List<String> localProgramKeyList = Util.getPrmKey(localProgramList);
                List<String> localWaitProgramKeyList = Util.getWaitPrmList(localProgramKeyList);
                //getPrmKeyList(program.getProgramList())
                //返回回执，告诉服务器失败
                CommandReceiptManager.responseDownloadState(adpressDataPackage,
                        CommandStateConstant.COMMAND_STATE_EXECUTED_SUCCESS, tipMsg,
                        localProgramKeyList, localWaitProgramKeyList);
                return isUpdate;
            }
//		    delPrm(adpressDataPackage, remoteProgramList);
            int size = remoteProgramList.size();
            Log.d(TAG, "size : " + size);
            if (ListUitl.isEmpty(remoteProgramList)) {//远程无任何节目 删除本地所有的节目
                isUpdate = true;
                delLocalAllProgramAndNotifyPlayList();
                return isUpdate;
            }

            File file = new File(FileUtile.getInstance().getTempFolder() + File.separator + SharedUtil.CACHE_LOCAL_PRM);
            String localPrm = FileUtile.readPrmToFile(file);
            Log.d(TAG, "localPrm: " + localPrm);
            if (null != localPrm && prmValue.equals(localPrm)) {
                return isUpdate;
            }
            FileUtile.writePrmContent(file, prmValue);

            List<Program> localProgramList = Util.getLocalProgramList(false);//读取本地所有的节目

//		    delPrm(remoteProgramList, localProgramList);//删除节目

            addDownloadPrm(adpressDataPackage, remoteProgramList, localProgramList);//添加下载节目

            /*以下代码需要注释 注释者cjg*/
//			CommandReceiptManager.commandReceipt(adpressDataPackage,
//					CommandStateConstant.COMMAND_STATE_EXECUTED_SUCCESS, null);
//			List<String> remotePrmKeyList = Util.getPrmKey(remoteProgramList);
//			//将所有同步的节目重新发送一遍下载成功的回执
//			sendReceipt(adpressDataPackage, remotePrmKeyList, Util.getWaitPrmList(remotePrmKeyList));
        } else {
            failedReceipt(adpressDataPackage, responseContent.getMessage());
        }

        return isUpdate;
    }

    private void delPrm(AdpressDataPackage adpressDataPackage, List<Program> remoteProgramList) throws IOException {
        List<Program> delPrmList = new ArrayList<Program>();
        List<Program> localProgramList = Util.getLocalProgramList(false);//读取本地所有的节目
        for (Program localProgram : localProgramList) {
            boolean isDel = true;
            for (Program remoteProgram : remoteProgramList) {
                if (null != localProgram && null != remoteProgram) {
                    if (localProgram.getKey().equals(remoteProgram.getKey())) {
                        isDel = false;
                    }
                }
            }
            if (isDel) {
                delPrmList.add(localProgram);
            }
        }
        delProgramList(delPrmList);

        addDownloadPrm(adpressDataPackage, remoteProgramList, localProgramList);//添加下载节目
    }

    /**
     * 下载未下载完的节目
     */
    private void downloadTempPrm(List<Program> localUnDownloadProgramList) {
        ParsePrmUitl.savePrmAndDownloads(localUnDownloadProgramList);//保存节目并且下载素材
    }

    private void delPrm(List<Program> remoteProgramList, List<Program> localProgramList) {
        //得到要删除的节目列表
        List<Program> delProgramList = ListUitl.getFilterProgramList(false, localProgramList,
                remoteProgramList, Program.class, "getKey", null, new Object[0]);
        delProgramList(delProgramList);
    }

    private void delProgramList(List<Program> delProgramList) {
        if (ListUitl.isNotEmpty(delProgramList)) {
            for (Program delProgram : delProgramList) {
                if (null != delProgram) {
                    FileUtile.getInstance().delProgramToPrmId(delProgram.getKey());
                }
            }
            delProgramList.clear();
            //延迟2秒切换视频
            CommunicationApp.get().mAppHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppUtil.notifyProgramPlayList();
                }
            }, 2000);
        }
    }

    private void addDownloadPrm(AdpressDataPackage adpressDataPackage,
                                List<Program> remoteProgramList, List<Program> localProgramList) {
        //得到要下载的节目列表
        List<Program> downloadProgramList = ListUitl.getFilterProgramList(false, remoteProgramList,
                localProgramList, Program.class, "getKey", null, new Object[0]);
        if (ListUitl.isNotEmpty(downloadProgramList)) {
            setDownloadPlanApackage(adpressDataPackage);
            //下载新节目
            ParsePrmUitl.savePrmAndDownloads(downloadProgramList);//保存节目并且下载素材
        }
    }

    private void sendReceipt(AdpressDataPackage adpressDataPackage,
                             List<String> remoteProgramList, List<String> downloadFiledList) {
        List<Program> localProgramList = null;//读取本地所有的节目
        try {
            localProgramList = Util.getLocalProgramList(false);
            List<String> localProgramKeyList = Util.getPrmKey(localProgramList);
            CommandReceiptManager.responseDownloadState(adpressDataPackage,
                    CommandStateConstant.COMMAND_STATE_EXECUTED_SUCCESS, null,
                    localProgramKeyList, downloadFiledList);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 终端是否已解绑解绑的话删除所有的节目
     *
     * @return boolean true表示已解绑
     */
    private boolean isUnBind() {
        if (StringUtil.isNullStr(ConfigSettings.getDid())) {
            FileUtile.delete(FileUtile.getInstance().getLayoutFolderPath());
            FileUtile.delete(FileUtile.getInstance().getPicFolder());
            FileUtile.delete(FileUtile.getInstance().getVideoFolder());
            FileUtile.delete(FileUtile.getInstance().getLogFolder());
            FileUtile.delete(FileUtile.getInstance().getScaledImageFolderPath());
            Log.d(TAG, "delete all prm.");
            return true;
        }
        return false;
    }

    /**
     * 删除本地所有的节目,并且提示更新节目播放列表
     */
    private void delLocalAllProgramAndNotifyPlayList() {
        File prmFolder = new File(FileUtile.getInstance().getLayoutFolderPath());
        File[] programFiles = prmFolder.listFiles();
        for (File programFile : programFiles) {
            //删除本地所有节目和素材
            FileUtile.getInstance().delFile(programFile);
        }
        AppUtil.notifyProgramPlayList();
    }

    /**
     * 根据最新的节目列表，筛选出需要删除的本地节目
     *
     * @param newPrograms 最新的节目播放列表
     * @return boolean 返回true表示有删除
     */
    private boolean delInvalidPrmToNewPrograms(List<Program> newPrograms) {
        List<String> newProgramKeys = new ArrayList<String>();
        for (Program program : newPrograms) {//遍历当前最新节目的所有列表，保存节目的key
            newProgramKeys.add(program.getKey());
        }
        boolean isDelProgram = false;
        File[] programFiles = new File(FileUtile.getInstance().getLayoutFolderPath()).listFiles();
        for (File programFile : programFiles) {
            String programFileName = programFile.getName();
            String programKey = programFileName.substring(0, programFileName.lastIndexOf("."));
            Log.d(TAG, "programFileName: " + programFileName + " ,programKey: " + programKey);
            if (!newProgramKeys.contains(programKey)) {
                Log.d(TAG, "del programKey: " + programKey);
                isDelProgram = true;
            }
        }
        return isDelProgram;
    }

    /**
     * 根据传入的新的节目列表和节目文件名列表 筛选出最新的节目列表
     *
     * @param programList    节目文件名的列表
     * @param newProgramList 新节目列表
     * @return 返回最新的节目列表
     */
    private List<Program> getNewProgramList(List<String> programList, List<Program> newProgramList) {
        List<Program> existProgramList = new ArrayList<Program>();//已存在的节目
        for (Program program : newProgramList) {//新节目列表
            for (String oldProgram : programList) {
                if (oldProgram.contains(program.getKey())) {
                    existProgramList.add(program);
                }
            }
        }
        newProgramList.removeAll(existProgramList);
        return newProgramList;
    }

    /**
     * 获取本机节目名称
     *
     * @return
     */
    private List<String> getLocalPrmName() {
        File prmFile = new File(FileUtile.getInstance().getLayoutFolderPath());//本地所有节目
        String[] prmList = prmFile.list();
        return Arrays.asList(prmList);
    }

    /**
     * 校验本地的temp节目文件中的素材是否已经下载完成
     *
     * @return true表示temp文件下载完成
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    private boolean checkTempPrmMaterialIsComplete() throws NoSuchAlgorithmException, IOException {
        //首先读取节目文件目录里面所有的节目
        File prmFile = new File(FileUtile.getInstance().getLayoutFolderPath());//本地所有节目
        File[] files = prmFile.listFiles();
        boolean flag = false;
        for (File file : files) {
            if (file.getName().endsWith(FileUtile.TEMP_SUFFIX_NAME)) {
                Log.d(TAG, "prm name:" + file.getName());
                String programJson = FileUtile.readPrmToFile(file);
                Program localPrm = JSON.parseObject(programJson, Program.class);
                List<Material> prmFiles = ParsePrmUitl.parsePrmToMaterials(localPrm);// 获取素材名称列表
                if (checkMaterialIsComplete(prmFiles)) {
                    //将名字改成prm
                    File prmPathFile = new File(file.getParent()
                            + File.separator
                            + StringUtil.getNameRemoveSuffix(file.getName())
                            + FileUtile.PRM_SUFFIX_NAME);
                    flag = file.renameTo(prmPathFile);
                    Log.d(TAG, "prm rename is: " + flag + " ,file name: " + file.getName());
                }
            }
        }
        return flag;
    }

    /**
     * 判断素材是否完整
     *
     * @param prmFiles
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    private boolean checkMaterialIsComplete(List<Material> prmFiles) throws NoSuchAlgorithmException, IOException {
        for (Material material : prmFiles) {
            //得到节目列表中的素材名称
            String filePath = getFilePathToMaterial(material);
            if (!checkFileMd5(material, filePath)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 根据素材对象获取素材的详细路径
     *
     * @param material 素材对象
     * @return 返回素材的详细路径
     */
    private String getFilePathToMaterial(Material material) {
        String filePath = null;
        if (null != material) {
            if (null == material.getT()) {
                return filePath;
            }
        }
        switch (material.getT()) {
            case Constant.VIDEO_FRAGMENT:
                filePath = FileUtile.getInstance().getVideoFolder() + File.separator + FileUtile.getFileName(material.getU());
                break;
            case Constant.PIC_FRAGMENT:
                filePath = FileUtile.getInstance().getPicFolder() + File.separator + FileUtile.getFileName(material.getU());
                break;
            default:
                filePath = FileUtile.getInstance().getVideoFolder() + File.separator + FileUtile.getFileName(material.getU());
                break;
        }
        return filePath;
    }

    /**
     * 判断文件是否合法，对文件进行MD5校验
     *
     * @param filePath 文件路径
     * @return 返回true表示合法, false表示不合法
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    private boolean checkFileMd5(Material material, String filePath) throws NoSuchAlgorithmException, IOException {
        boolean fileIsValid = false;
        if (null == filePath) {
            return true;
        }
        File localFile = new File(filePath);
        if (localFile.exists()) {
            String md5 = MD5Utils.getHashToFilePath(filePath);
            String fileMd5 = material.getM();
            if (!fileMd5.equals(md5)) {//校验md5
                long modifiedTime = localFile.lastModified();//最后修改的时间
                long currentTime = System.currentTimeMillis();//当前时间
                long differenceTime = currentTime - modifiedTime;
                Log.d(TAG, "materialFile modifiedTime: " + modifiedTime + " ,differenceTime: " + differenceTime);
                if (differenceTime > HttpConstants.THREE_MINUTE_LONG) {//3分钟
                    localFile.delete();
                    Log.w(TAG, "delete filePath: " + filePath);
                }
                return fileIsValid;
            } else {
                fileIsValid = true;
                return fileIsValid;
            }
        } else {
            return fileIsValid;
        }

    }

    /**
     * 判断本地节目是否存在
     *
     * @return true表示节目存在，false表示不存在
     */
    private boolean checkLocalPrmExits(ProgramListPackage program) {
        //首先读取节目文件目录里面所有的节目
        File programFolde = new File(FileUtile.getInstance().getLayoutFolderPath());//本地所有节目
        String[] programFileNameList = programFolde.list();
        int programFileSize = programFileNameList.length;
        List<Program> newPrmList = program.getProgramList();//最新的节目列表
        int newPrmSize = newPrmList.size();
        if (programFileSize != newPrmSize) {
            return false;
        }
        int index = 0;
        for (Program newPrm : newPrmList) {
            for (String programFileName : programFileNameList) {
                if ((newPrm.getKey() + FileUtile.PRM_SUFFIX_NAME).contains(programFileName)) {
                    index++;
                }
            }
        }
        return index == newPrmSize;
    }

    /**
     * 验证节目素材是否完整
     *
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    private void checkPrmMaterialIsComplete(ProgramListPackage programListPackage) throws NoSuchAlgorithmException, IOException {
        ArrayList<String> materialUrlList = new ArrayList<String>();
        //判断当前节目里面是否存在
        List<Program> programList = programListPackage.getProgramList();
        for (Program program : programList) {
            List<Material> materialList = ParsePrmUitl.parsePrmToMaterials(program);// 获取素材名称列表
            if (null == materialList || materialList.isEmpty()) {
                Log.d(TAG,"materialList is empty, program key: " + program.getKey());
                continue;
            }
            for (Material material : materialList) {
                String filePath = getFilePathToMaterial(material);
                if (!checkFileMd5(material, filePath)) {//不合法的文件 加入到下载列表中
                    materialUrlList.add(material.getU());
                }
                Log.d(TAG,"materialFile: " + filePath);
            }
        }
        sendRecoveryFileDownload(materialUrlList);
    }

    /**
     * 发送恢复素材 下载
     *
     * @param materialUrlList st
     *                        素材下载列表
     */
    private void sendRecoveryFileDownload(ArrayList<String> materialUrlList) {
        Log.d(TAG,"materialUrlList size: " + materialUrlList.size());
        if (null != materialUrlList && !materialUrlList.isEmpty()) {
            Intent recoveryDownloadIntent = new Intent(Constant.RECOVERY_DAMAGE_FILE_ACTION);
            recoveryDownloadIntent.putStringArrayListExtra(Constant.RECOVERY_DAMAGE_FILE_LIST,
                    materialUrlList);
            CommunicationApp.get().sendBroadcast(recoveryDownloadIntent);
        }
    }


    /**
     * 获取本地缓存的节目列表
     *
     * @return
     */
    private String getCacheLocalProgramList() {
        return SharedUtil.newInstance().getString(SharedUtil.CACHE_LOCAL_PRM);
    }

    /**
     * 保存同步节目列表到本地
     *
     * @param localPrm
     */
    private void setCacheLocalProgramList(String localPrm) {
        SharedUtil.newInstance().setString(SharedUtil.CACHE_LOCAL_PRM, localPrm);
    }

    /**
     * 保存下载计划列表到本地
     *
     * @param adpressDataPackage
     */
    private void setDownloadPlanApackage(AdpressDataPackage adpressDataPackage) {
        SharedUtil.newInstance().setString(SharedUtil.DWONLOADPLAN_ADPRESSDATAPACKAGE, adpressDataPackage.toString());
    }

    /**
     * 同步失败回执
     *
     * @param adpressDataPackage
     * @param errMsg             错误消息
     */
    private void failedReceipt(AdpressDataPackage adpressDataPackage, String errMsg) {
        CommandReceiptManager.commandReceipt(adpressDataPackage,
                CommandStateConstant.COMMAND_STATE_EXECUTED_ERROR,
                errMsg);
    }

    @Override
    public void onFaile(String errMsg, String httpTag, String requestUrl) {
        Log.d(TAG, "onFaile result: " + errMsg);
//		if (null != mISyncCallBack) {
//			mISyncCallBack.syncToTag(httpTag);
//		}
        isUnBind();
    }
}
