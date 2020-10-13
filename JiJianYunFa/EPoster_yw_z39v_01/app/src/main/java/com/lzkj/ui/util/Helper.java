package com.lzkj.ui.util;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;

import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Material;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Program;
import com.baize.lz.core.utils.MD5Utils;
import com.lzkj.downloadservice.bean.DownloadBo;
import com.lzkj.ui.app.EPosterApp;
import com.lzkj.ui.log.LogManager;
import com.lzkj.ui.play.ProgramPlayManager;
import com.lzkj.ui.util.LogUtils.LogTag;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Helper {

    private static final LogTag TAG = LogUtils.getLogTag(Helper.class.getSimpleName(), true);
    private static ArrayList<String> saveRecoveryMaterials = new ArrayList<String>();

    /**
     * get sub folder name
     *
     * @param filePath detailed path
     * @return sub folder name
     */
    public static String getFolderNameByPath(String filePath) {
        if (null == filePath) {
            return null;
        }
//		String subFilePath = filePath.substring(0, filePath.lastIndexOf("/"));
        String subFolderName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
        return subFolderName;
    }

    /**
     * zoom dial Image
     *
     * @param bitmap source bitmap
     * @param width  zoom width
     * @param height zoom height
     * @return zoom bitmap
     */
    public static Bitmap zoomImage(Bitmap bitmap, float width, float height) {
        float xBig = ((float) width / bitmap.getWidth());
        float yBig = ((float) height / bitmap.getHeight());
        Matrix matrix = new Matrix();
        matrix.postScale(xBig, yBig);
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    /**
     * 处理素材错误的方法
     *
     * @param material
     */
    public static void handlerMaterialError(Material material, Program program) {
        if (null == material) {
            return;
        }
        if (saveRecoveryMaterials.contains(FileStore.getFileName(material.getU()))) {
            return;
        }
        saveRecoveryMaterials.add(FileStore.getFileName(material.getU()));
        delErrMaterial(material, program);
        //首先得到文件的下载路径和当前节目的id
//		String prmId  = ProgramPlayManager.newInstance().getProgramSchedule().getCurrentProgram().getKey();
    }

    public static ArrayList<String> getRecoveryList() {
        return saveRecoveryMaterials;
    }

    /**
     * 删除素材
     *
     * @param material
     */
    private static void delErrMaterial(final Material material, final Program program) {
        ThreadPoolManager.get().addRunnable(new Runnable() {
            @Override
            public void run() {
                String filePath = null;
                switch (material.getT()) {
                    case Constants.PIC_FRAGMENT:
                        filePath = FileStore.getInstance()
                                .getImageFilePath(FileStore.getFileName(material.getU()));
                        break;
                    case Constants.VIDEO_FRAGMENT:
                        filePath = FileStore.getInstance()
                                .getImageFilePath(FileStore.getFileName(material.getU()));
                        break;
                    default:
                        break;
                }
                File materialFile = new File(filePath);
                if (materialFile.exists()) {//判断md5是否正确
                    try {
                        String md5Value = MD5Utils.getHashToFilePath(filePath);
                        if (md5Value.equals(material.getM())) {
                            return;
                        } else {
                            LogUtils.d(TAG, "delErrMaterial", "filePath : " + filePath);
                            StringBuffer stringBuffer = new StringBuffer(8);
                            stringBuffer.append("Eposter (delErrMaterial) Program is not validity program key is :");
                            stringBuffer.append(program.getKey());
                            stringBuffer.append(", program name is :");
                            stringBuffer.append(program.getN());
                            stringBuffer.append(",programErroMsg: del play program file :");
                            stringBuffer.append(filePath);
                            stringBuffer.append(",_");
                            stringBuffer.append(DateTimeUtil.getStringTimeToFormat("yyyy_MM_dd_HH_mm_ss"));
                            LogManager.get().insertOperationMessage(stringBuffer.toString());

                            materialFile.delete();
                            String materialUrl = material.getU();
                            Intent intent = new Intent(Constants.RECOVERY_DAMAGE_FILE_ACTION);
                            ArrayList<String> list = new ArrayList<String>();
                            list.add(materialUrl);
                            intent.putStringArrayListExtra(Constants.RECOVERY_DAMAGE_FILE_LIST, list);
                            intent.putExtra(Constants.MATERIAL_TYPE, material.getT());
                            intent.putExtra(Constants.MATERIAL_NAME, FileStore.getFileName(material.getU()));
                            intent.putExtra(Constants.MATERIAL_MD5, material.getM());
                            intent.putExtra(Constants.PROGRAM_KEY, program.getKey());
                            EPosterApp.getApplication().sendBroadcast(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 删除掉已恢复的素材名字
     *
     * @param materialName
     */
    public static void delRecoveryMaterials(String materialName) {
        if (saveRecoveryMaterials.contains(materialName)) {
            saveRecoveryMaterials.remove(materialName);
        }
        if (saveRecoveryMaterials.isEmpty()) {
            ProgramPlayManager.getInstance().playProgramList("delRecoveryMaterials");
        }
    }

    /**
     * 绑定失败 使用广播的方式去下载素材
     *
     * @param httpUrls
     * @param fileType
     * @param prmId
     */
    public static void addDownload(List<String> httpUrls, int fileType, String prmId) {
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.addAll(httpUrls);
        ArrayList<DownloadBo> downloadBos = new ArrayList<DownloadBo>();
        DownloadBo downloadBo = new DownloadBo();
        downloadBo.setHttpUrls(arrayList);
        downloadBos.add(downloadBo);
        downloadBo.setPrmId(prmId);
        downloadBo.setType(fileType);
        Bundle extras = new Bundle();
        extras.putParcelableArrayList(Constants.DOWNLOAD_LIST, downloadBos);
        startDownloadService(extras);
    }

    /**
     * 启动下载服务
     *
     * @param extras 启动服务附加的值
     */
    private static void startDownloadService(Bundle extras) {
        if (null != extras) {
            ComponentName downloadComponentName = new ComponentName(Constants.DOWNLOAD_PKG,
                    Constants.DOWNLOAD_SERVICE_CLS);
            Intent downloadIntent = new Intent();
            downloadIntent.setComponent(downloadComponentName);
            downloadIntent.putExtra(Constants.DOWNLOAD_TYPE_KEY, Constants.APPEND_DOWNLOAD);//添加下载
            downloadIntent.putExtras(extras);

            EPosterApp.getApplication().startService(downloadIntent);
        }
        LogUtils.d(TAG, "startDownloadService", "startDownloadService");
    }
}
