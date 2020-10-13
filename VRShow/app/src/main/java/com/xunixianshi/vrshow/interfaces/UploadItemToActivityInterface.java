package com.xunixianshi.vrshow.interfaces;

/**
 * item 通知界面接口
 * @ClassName UploadItemToActivityInterface
 *@author HeChuang
 *@time 2016/11/1 15:39
 */
public interface UploadItemToActivityInterface {
    void showDialog(UploadActivityToItemInterface uploadActivityToItemInterface);
    void showHint(UploadActivityToItemInterface uploadActivityToItemInterface);
    void editName(EditDialogTextInterface uploadActivityToItemInterface,String resourceName,String imageUrl);
    void showLoading();
    void dissmissLoading();
    void upData();
    void checkNetWork(UploadActivityToItemInterface uploadActivityToItemInterface);
}
