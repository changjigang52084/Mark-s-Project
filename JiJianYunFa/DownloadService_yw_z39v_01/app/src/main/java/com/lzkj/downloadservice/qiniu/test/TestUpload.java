//package com.lzkj.downloadservice.qiniu.test;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//
//import org.json.JSONObject;
//
//import com.lzkj.downloadservice.bean.HttpRequestBean;
//import com.lzkj.downloadservice.interfaces.IRequestCallback;
//import com.lzkj.downloadservice.util.FileUtil;
//import com.lzkj.downloadservice.util.HttpUtil;
//import com.lzkj.downloadservice.util.LogUtils;
//import com.lzkj.downloadservice.util.LogUtils.LogTag;
//import com.qiniu.android.http.ResponseInfo;
//import com.qiniu.android.storage.UpCancellationSignal;
//import com.qiniu.android.storage.UpCompletionHandler;
//import com.qiniu.android.storage.UpProgressHandler;
//import com.qiniu.android.storage.UploadManager;
//import com.qiniu.android.storage.UploadOptions;
//
//import android.test.AndroidTestCase;
//
///**
// * @author kchang Email:changkai@lz-mr.com
// * @date   创建时间：2015年6月5日 下午3:12:58
// * @version 1.0
// * @parameter
// */
//public class TestUpload extends AndroidTestCase {
//	private static final LogTag TAG = LogUtils.getLogTag(TestUpload.class.getSimpleName(), true);
//
//
//	private volatile boolean isCancelled;
//	/**取消上传的接口*/
//	private UpCancellationSignal cancellationSignal = new UpCancellationSignal(){
//        public boolean isCancelled(){
//            return isCancelled;
//        }
//    };
//    /**
//     * 上传进度
//     */
//    private UpProgressHandler upProgressHandler = new UpProgressHandler(){
//        public void progress(String key, double percent){
//            LogUtils.d(TAG, "initUpload.complete",key + ", progress:" + percent);
//        }
//	};
//	/**
//	 * 上传完成
//	 */
//	private UpCompletionHandler completionHandler =  new UpCompletionHandler() {
//	    @Override
//	    public void complete(String key, ResponseInfo info, JSONObject response) {
//	    	LogUtils.d(TAG, "initUpload.complete", info.toString());
//	    }
//	};
//	private UploadOptions uploadOptions = new UploadOptions(null, null, false,upProgressHandler,null);
//
//	private IRequestCallback requestCallback = new IRequestCallback() {
//		@Override
//		public void onSuccess(String result, String requestHttpUrl, String requestTag) {
//			System.out.println("result:"+result);
//			UploadManager uploadManager = new UploadManager();
//			String uploadFilePath = FileUtil.getInstance().getPicFolder()+File.separator+"meinv.jpg";
//			try {
//				FileInputStream fileInputStream = new FileInputStream(new File(uploadFilePath));
//				int length = 0 ;
//				byte[] data = new byte[1024];
//				ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
//				while((length = fileInputStream.read(data)) != -1) {
//					arrayOutputStream.write(data, 0, length);
//				}
//				fileInputStream.close();
//				arrayOutputStream.flush();
//				byte[] fileData = arrayOutputStream.toByteArray();
//				arrayOutputStream.close();
//				String key = "iEsZrOUCO0NkwTx9mCdLQ66XHK4I4QNQJSFJ2Jpe";
//				String token = result;
//				uploadManager.put(fileData, key, token,completionHandler, uploadOptions);
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//
//		@Override
//		public void onFaile(String errMsg, String requestHttpUrl, String requestTag) {
//			System.out.println("errMsg:"+errMsg);
//		}
//	};
//
//	@SuppressWarnings("unused")
//	private void initUpload() {
//		/*	Configuration config = new Configuration.Builder()
//			                    .chunkSize(256 * 1024)  //分片上传时，每片的大小。 默认 256K
//			                    .putThreshhold(512 * 1024)  // 启用分片上传阀值。默认 512K
//			                    .connectTimeout(10) // 链接超时。默认 10秒
//			                    .responseTimeout(60) // 服务器响应超时。默认 60秒
//			                    .recorder(recorder)  // recorder 分片上传时，已上传片记录器。默认 null
//			                    .recorder(recorder, keyGen)  // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
//			                    .build();
//			// 重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
//		UploadManager uploadManager = new UploadManager(config);*/
//		upload();
//	}
//
//	private void upload() {
//		HttpRequestBean httpRequest = new HttpRequestBean();
//		httpRequest.setRequestCallback(requestCallback);
//		httpRequest.setRequestParm("uploadType=1");
//		httpRequest.setRequestRestry(5);
//		httpRequest.setRequestTag("TestUpload");
//		httpRequest.setRequestUrl("");
//		HttpUtil.newInstance().sendGetRequest(httpRequest);
//	}
//
//}
