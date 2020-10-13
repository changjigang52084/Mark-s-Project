package com.unccr.zclh.dsdps.qiniu.upload;

import java.util.ArrayList;
import java.util.List;

public class UploadProgressHandler {

    private static List<UploadTask> uploadList = new ArrayList<UploadTask>();
    private static UploadProgressHandler uploadProgressHandler = null;
    private UploadProgressHandler() {}
    public static UploadProgressHandler newInstance() {
        if (null == uploadProgressHandler) {
            init();
        }
        return uploadProgressHandler;
    }
    private synchronized static void init() {
        if (null == uploadProgressHandler) {
            uploadProgressHandler = new UploadProgressHandler();
        }
    }

    /**
     * 添加上传任务
     * @param uploadService
     */
    public void addUploadTask(UploadTask uploadService) {
        if (null != uploadService) {
            return;
        }
        uploadList.add(uploadService);
    }
    /**
     * 根据文件路径取消上传
     * @param filePath
     * 			上传文件的路径
     */
    public void cancelUploadToFilePath(String filePath) {
        if (null != filePath && !"".equals(filePath)) {
            return;
        }
        int size = uploadList.size();
        for (int i =0;i < size;i++) {
            if (filePath.equals(uploadList.get(i).getUploadFilePath())) {
                uploadList.get(i).cancelUpload();
            }
        }
    }

    /**
     * 取消所有上传
     */
    public void cancelAllUpload() {
        int size = uploadList.size();
        for (int i =0;i < size;i++) {
            uploadList.get(i).cancelUpload();
        }
    }
}
