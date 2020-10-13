//package com.lzkj.aidlservice.service;
//
//import android.app.Service;
//import android.content.Intent;
//import android.os.IBinder;
//
//import com.google.gson.Gson;
//import com.lzkj.aidlservice.DownLoaderTask;
//import com.lzkj.aidlservice.bo.ResultBean;
//import com.lzkj.aidlservice.util.AppUtil;
//import com.lzkj.aidlservice.util.LogUtils;
//import com.lzkj.aidlservice.util.NetworkUtils;
//
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicHeader;
//import org.jetbrains.annotations.Nullable;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.RandomAccessFile;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
///**
// * Created by Administrator on 2018/1/24.
// * 插播广告服务
// */
//
//public class SowingAdvertisementService extends Service {
//
//    private static final LogUtils.LogTag TAG = LogUtils.getLogTag(SowingAdvertisementService.class.getSimpleName(), true);
//
//    // 正式地址
//    public static String FORMAL_URL = "https://api.kuaifa.tv/ad/request?version=v1";
//
//    private Gson gson = new Gson();
//
//    private File videoFile;
//    private File picFile;
//    private File videoPrmFile = null;
//    private File picPrmFile = null;
//    private String kuaiFaUrl = null;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        videoFile = new File("/mnt/internal_sd/mallposter/video");
//        if (!videoFile.exists()) {
//            videoFile.mkdirs();
//        }
//        picFile = new File("/mnt/internal_sd/mallposter/pic");
//        if (!picFile.exists()) {
//            picFile.mkdirs();
//        }
//
//        try {
//            videoPrmFile = new File("/mnt/internal_sd/mallposter/prm/video.prm");
//            if (!videoPrmFile.exists()) {
//                videoPrmFile.createNewFile();
//            }
//            picPrmFile = new File("/mnt/internal_sd/mallposter/prm/picture.prm");
//            if (!picPrmFile.exists()) {
//                picPrmFile.createNewFile();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (NetworkUtils.isNetworkConnected(SowingAdvertisementService.this)) {
//                    kuaiFaPost(createParams());
//                } else {
//                    if (isDeletePrm) {
//                        return;
//                    }
//                    deletePrm();
//                    AppUtil.notifyProgramPlayList();
//                }
//            }
//        }, 0, 1000 * 30);
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return Service.START_STICKY;
//    }
//
//    // 创建请求参数
//    private JSONObject createParams() {
//
//        JSONObject jsonObject1 = new JSONObject();
//        JSONObject jsonObject2 = new JSONObject();
//        JSONObject jsonObject3 = new JSONObject();
//        JSONObject jsonObject4 = new JSONObject();
//        JSONObject jsonObject5 = new JSONObject();
//
//        String appid = "9264";
//        String android_id = "9774d56d682e549c";// 必须，如无可传一个固定值
//        String mac = AppUtil.getMacAddress();
//        LogUtils.d(TAG, "createParams", "mac: " + mac);
//        String imei = "201006089999999"; //必须，如无可传一个固定值
//        int width = 1080;
//        int height = 1920;
//        String connection_type = "WIFI"; // 必须
//        String operator_type = "TELECOM"; // 必须
//        int times = 15; // 必须
//        int type = 0;
//
//        try {
//            // 创建json根对象
//            jsonObject1.put("appid", appid);
//            jsonObject3.put("android_id", android_id);
//            jsonObject3.put("mac", mac);
//            jsonObject3.put("imei", imei);
//            jsonObject5.put("width", width);
//            jsonObject5.put("height", height);
//            jsonObject2.put("udid", jsonObject3);
//            jsonObject2.put("screen_size", jsonObject5);
//            jsonObject1.put("device", jsonObject2);
//            jsonObject4.put("connection_type", connection_type);
//            jsonObject4.put("operator_type", operator_type);
//            jsonObject1.put("network", jsonObject4);
//            jsonObject1.put("times", times);
//            jsonObject1.put("type", type);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        LogUtils.d(TAG, "createParams", "jsonObject1: " + jsonObject1.toString());
//        return jsonObject1;
//    }
//
//    // 快发云接口请求
//    private void kuaiFaPost(JSONObject params) {
//        HttpClient client = new DefaultHttpClient();
//        HttpPost post = new HttpPost(FORMAL_URL);
//        post.setHeader("Content-Type", "application/json");
//        post.addHeader("Authorization", "Basic YWRtaW46");
//        String result = "";
//        try {
//            StringEntity s = new StringEntity(params.toString(), "utf-8");
//            s.setContentEncoding(new BasicHeader(org.apache.http.protocol.HTTP.CONTENT_TYPE, "application/json"));
//            post.setEntity(s);
//            // 发送请求
//            HttpResponse httpResponse = client.execute(post);
//            // 获取响应输入流
//            InputStream inputStream = httpResponse.getEntity().getContent();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
//            StringBuilder sb = new StringBuilder();
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                sb.append(line + "\n");
//            }
//            inputStream.close();
//            result = sb.toString();
//            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//                if (result != null && !"".equals(result)) {
//                    ResultBean resultBean = gson.fromJson(result, ResultBean.class);
//                    int code = resultBean.getCode();
//                    LogUtils.d(TAG, "kuaiFaPost", "result: " + result + " ,code: " + code);
//                    // 如果code返回0说明接口里面有广告
//                    if (code == 104071) {
//                        if (isDeletePrm) {
//                            return;
//                        }
//                        deletePrm();
//                        AppUtil.notifyProgramPlayList();
//                    } else if (code == 0) {
//                        List<List<?>> countInterface = resultBean.getData().getAd_tracking();
//                        String ad_key = String.valueOf(resultBean.getData().getAd_key());
//                        int show_time = resultBean.getData().getMaterial().getShow_time();
//                        if (countInterface.size() > 0) {
//                            String countUrl01 = "";
//                            String countUrl02 = "";
//                            String countUrl03 = "";
//                            LogUtils.d(TAG, "kuaiFaPost", "countInterface.size(): " + countInterface.size());
//                            List<?> countInterface01 = countInterface.get(0);
//                            List<?> countInterface02 = countInterface.get(1);
//                            List<?> countInterface03 = countInterface.get(2);
//                            LogUtils.d(TAG, "kuaiFaPost", "countInterface01: " + countInterface01.size() + " ,countInterface02: " + countInterface02 + " ,countInterface03: " + countInterface03);
//                            if (countInterface01.size() > 0) {
//                                countUrl01 = (String) countInterface01.get(0);
//                                LogUtils.d(TAG, "kuaiFaPost", "countUrl01: " + countUrl01);
//                            }
//                            if (countInterface02.size() > 0) {
//                                countUrl02 = (String) countInterface02.get(0);
//                                LogUtils.d(TAG, "kuaiFaPost", "countUrl02: " + countUrl02);
//                            }
//                            if (countInterface03.size() > 0) {
//                                countUrl03 = (String) countInterface03.get(0);
//                                LogUtils.d(TAG, "kuaiFaPost", "countUrl03: " + countUrl03);
//                            }
//                            Intent intent = new Intent("com.lzkj.aidlservice.AD_TRACKING");
//                            intent.putExtra("countUrl03", countUrl03);
//                            intent.putExtra("ad_key", ad_key);
//                            intent.putExtra("show_time", show_time);
//                            sendBroadcast(intent);
//                        }
//                        String u = resultBean.getData().getMaterial().getUrl();
//                        LogUtils.d(TAG, "kuaiFaPost", "广告节目u: " + u + " ,kuaiFaUrl: " + kuaiFaUrl);
//                        if (u.equals(kuaiFaUrl)) {
//                            return;
//                        }
//                        deletePrm();
//                        kuaiFaUrl = u;
//
//                        if (u != null && !"".equals(u)) {
//                            String suffixType = u.substring(u.lastIndexOf(".") + 1, u.length());
//                            LogUtils.d(TAG, "kuaiFaPost", "suffixType: " + suffixType);
//                            if (suffixType.equals("mp4")) {
//                                try {
//                                    videoPrmFile = new File("/mnt/internal_sd/mallposter/prm/video.prm");
//                                    if (!videoPrmFile.exists()) {
//                                        videoPrmFile.createNewFile();
//                                        isDeletePrm = false;
//                                    }
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                                String videoTitle = "11111111";
//                                int video_show_time = resultBean.getData().getMaterial().getShow_time();
//                                String videoMd5 = resultBean.getData().getMaterial().getMd5();
//
//                                DownLoaderTask downLoaderTask = new DownLoaderTask(u, "/mnt/internal_sd/mallposter/video", SowingAdvertisementService.this);
//                                downLoaderTask.execute();
//                                String videoProgramList = createVideoProgramList(u, videoMd5, ad_key, videoTitle, video_show_time, suffixType).toString();
//                                LogUtils.d(TAG, "", "videoProgramList: " + videoProgramList);
//                                Thread.sleep(5000);
//                                writeTxtFile(videoProgramList, videoPrmFile);
//                            } else if (suffixType.equals("jpg")) {
//                                try {
//                                    picPrmFile = new File("mnt/internal_sd/mallposter/prm/picture.prm");
//                                    if (!picPrmFile.exists()) {
//                                        picPrmFile.createNewFile();
//                                        isDeletePrm = false;
//                                    }
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                                String picTitle = "00000000";
//                                int pic_show_time = resultBean.getData().getMaterial().getShow_time();
//                                String picMd5 = resultBean.getData().getMaterial().getMd5();
//
//                                DownLoaderTask downLoaderTask = new DownLoaderTask(u, "/mnt/internal_sd/mallposter/pic", SowingAdvertisementService.this);
//                                downLoaderTask.execute();
//                                String picProgramList = createPicProgramList(u, picMd5, ad_key, picTitle, pic_show_time, suffixType).toString();
//                                LogUtils.d(TAG, "", "picProgramList: " + picProgramList);
//                                Thread.sleep(5000);
//                                writeTxtFile(picProgramList, picPrmFile);
//                            }
//                            AppUtil.notifyProgramPlayList();
//                        }
//                    } else if (code == 200000) {
//                        if (isDeletePrm) {
//                            return;
//                        }
//                        deletePrm();
//                        AppUtil.notifyProgramPlayList();
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private boolean isDeletePrm = false;
//
//    private void deletePrm() {
//        isDeletePrm = true;
//        kuaiFaUrl = null;
//        try {
//            if (videoPrmFile != null || videoPrmFile.exists()) {
//                LogUtils.d(TAG, "deletePrm", "delete video prm.");
//                videoPrmFile.delete();
//            }
//            if (picPrmFile != null || picPrmFile.exists()) {
//                LogUtils.d(TAG, "deletePrm", "delete pic prm.");
//                picPrmFile.delete();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public boolean writeTxtFile(String content, File fileName) throws Exception {
//        RandomAccessFile mm = null;
//        boolean flag = false;
//        FileOutputStream o = null;
//        try {
//            o = new FileOutputStream(fileName);
//            o.write(content.getBytes("GBK"));
//            o.close();
//            flag = true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (mm != null) {
//                mm.close();
//            }
//        }
//        return flag;
//    }
//
//    // 创建图片节目清单
//    private JSONObject createPicProgramList(String u, String md5, String ad_key, String title, int show_time, String suffixType) {
//        JSONObject jsonObject = new JSONObject();
//        JSONObject jsonObject1 = new JSONObject();
//        JSONObject jsonObject2 = new JSONObject();
//        JSONArray jsonArray1 = new JSONArray();
//        JSONArray jsonArray2 = new JSONArray();
//        try {
//            jsonObject2.put("d", show_time);
//            jsonObject2.put("i", 1);
//            jsonObject2.put("key", ad_key);
//            jsonObject2.put("m", md5);
//            jsonObject2.put("n", title + "." + suffixType);
//            jsonObject2.put("s", 10000000);
//            jsonObject2.put("t", 1);
//            jsonObject2.put("u", u);
//
//            jsonArray2.put(jsonObject2);
//
//            jsonObject1.put("h", 1920);
//            jsonObject1.put("key", 2);
//            jsonObject1.put("mas", jsonArray2);
//            jsonObject1.put("t", 1);
//            jsonObject1.put("w", 1080);
//            jsonObject1.put("x", 0);
//            jsonObject1.put("y", 0);
//            jsonObject1.put("z", 0);
//
//            jsonArray1.put(jsonObject1);
//
//            jsonObject.put("as", jsonArray1);
//            jsonObject.put("d", 2);
//            jsonObject.put("de", 1627673600000.0);
//            jsonObject.put("ds", 1516204800000.0);
//            jsonObject.put("h", 1920);
//            jsonObject.put("key", ad_key);
//            jsonObject.put("n", title);
//            jsonObject.put("p", 0);
//            jsonObject.put("t", 1);
//            jsonObject.put("te", "23:59");
//            jsonObject.put("ts", "00:00");
//            jsonObject.put("w", 1080);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return jsonObject;
//    }
//
//    // 创建视频节目清单
//    private JSONObject createVideoProgramList(String u, String md5, String ad_key, String title, int show_time, String suffixType) {
//        JSONObject jsonObject = new JSONObject();
//        JSONObject jsonObject1 = new JSONObject();
//        JSONObject jsonObject2 = new JSONObject();
//        JSONArray jsonArray1 = new JSONArray();
//        JSONArray jsonArray2 = new JSONArray();
//        try {
//            jsonObject2.put("d", show_time);
//            jsonObject2.put("i", 1);
//            jsonObject2.put("key", ad_key);
//            jsonObject2.put("m", md5);
//            jsonObject2.put("n", title + "." + suffixType);
//            jsonObject2.put("s", 10000000);
//            jsonObject2.put("t", 2);
//            jsonObject2.put("u", u);
//
//            jsonArray2.put(jsonObject2);
//
//            jsonObject1.put("h", 1920);
//            jsonObject1.put("key", 2);
//            jsonObject1.put("mas", jsonArray2);
//            jsonObject1.put("t", 2);
//            jsonObject1.put("w", 1080);
//            jsonObject1.put("x", 0);
//            jsonObject1.put("y", 0);
//            jsonObject1.put("z", 0);
//
//            jsonArray1.put(jsonObject1);
//
//            jsonObject.put("as", jsonArray1);
//            jsonObject.put("d", 2);
//            jsonObject.put("de", 1627673600000.0);
//            jsonObject.put("ds", 1516204800000.0);
//            jsonObject.put("h", 1920);
//            jsonObject.put("key", ad_key);
//            jsonObject.put("n", title);
//            jsonObject.put("p", 0);
//            jsonObject.put("t", 1);
//            jsonObject.put("te", "23:59");
//            jsonObject.put("ts", "00:00");
//            jsonObject.put("w", 1080);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return jsonObject;
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        kuaiFaUrl = null;
//        startService(new Intent(this, SowingAdvertisementService.class));
//    }
//}
