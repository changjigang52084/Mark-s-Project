package com.airiche.newworktest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executors;

import cn.finalteam.okhttpfinal.FileDownloadCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.OkHttpFinal;
import cn.finalteam.okhttpfinal.OkHttpFinalConfiguration;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

public class MainActivity extends AppCompatActivity {
    VideoView videoView;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionUtils.getInstance().Permission(this);


        OkHttpFinalConfiguration.Builder builder = new OkHttpFinalConfiguration.Builder();
        OkHttpFinal.getInstance().init(builder.build());

        final String url = "http://qnfile.orangelive.tv/iDISPLAY_Gen3_01_8.1_2.1.2_20200824.apk";
        textView = findViewById(R.id.download);

        EditText editText = (EditText)findViewById(R.id.Eddownload);
        editText.setText(url);
        //一般速度 二十多秒 最快七秒 不知道是不是网速问题
        Download(url);

        //最慢 久的几分钟还没下载完成 原生的下载功能
//        DownloadMangers(url);

        //二十多秒
//        Excutors(url);

        //okhttp3 六十多秒
//        downloadFile3(url);

//        initView();
    }

    public void initView() {
        videoView = (VideoView) findViewById(R.id.Videoview);
        playVideo();
    }

    public void playVideo() {
        String uri = "android.resource://" + getPackageName() + "/" + R.raw.bee9983123a62eafc0abd85700a175e4;


        String path = "/sdcard/Download/bee9983123a62eafc0abd85700a175e4.avi";
        Log.e("FlashActivity", "Path==========================" + uri);
//        videoView.setVideoURI(Uri.parse(uri));
        videoView.setVideoPath(path);
        MediaController mc = new MediaController(this);
        //设置控制器 控制的是那一个videoview
        mc.setAnchorView(videoView);
        mc.setVisibility(View.INVISIBLE);
        //设置videoview的控制器为mc
        videoView.setMediaController(mc);
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                String strres = "播放完成";
                //判断视频的总长度与当前长度是否在误差区间内
                if (Math.abs(videoView.getDuration() - videoView.getCurrentPosition()) > 1000) {
                    strres = "播放错误,可能无网络连接";
                }
                Log.e("TAG", strres);
                //网络可用
            }
        });
    }

    DownloadManager downloadManager;
    long mTaskId;

    public void DownloadMangers(final String url) {
        String s = System.currentTimeMillis() + ".mp4";
        //创建下载任务,downloadUrl就是下载链接
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //指定下载路径和下载文件名
        request.setDestinationInExternalPublicDir("/sdcard/", s);
        //获取下载管理器
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        //将下载任务加入下载队列，否则不会进行下载
        downloadManager.enqueue(request);
        mTaskId = downloadManager.enqueue(request);
        registerReceiver(receiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    //广播接受者，接收下载状态
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(mTaskId);//筛选下载任务，传入任务ID，可变参数
            Cursor c = downloadManager.query(query);
            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                switch (status) {
                    case DownloadManager.STATUS_PAUSED:
                        Log.i(TAG, ">>>下载暂停");
                    case DownloadManager.STATUS_PENDING:
                        Log.i(TAG, ">>>下载延迟");
                    case DownloadManager.STATUS_RUNNING:
                        Log.i(TAG, ">>>正在下载");
                        break;
                    case DownloadManager.STATUS_SUCCESSFUL:
                        Log.i(TAG, ">>>下载完成");
                        //下载完成安装APK
                        break;
                    case DownloadManager.STATUS_FAILED:
                        Log.i(TAG, ">>>下载失败");
                        break;
                }
            }
        }
    };


    static String TAG = "Mainactivity";

    public void Download(final String url) {
        Log.e(TAG, "开始下载=========：" + url);
        HttpRequest.download(url, new File("/sdcard/a.apk"), new FileDownloadCallback() {
            //开始下载
            @Override
            public void onStart() {
                super.onStart();
            }

            //下载进度
            @Override
            public void onProgress(int progress, long networkSpeed) {
                super.onProgress(progress, networkSpeed);
                Log.e(TAG, "进度值是：" + progress);
                textView.setText("进度值是：" + progress);
            }

            //下载失败
            @Override
            public void onFailure() {
                super.onFailure();
                Log.e(TAG, "下载失败的地址是:" + url);

            }

            //下载完成（下载成功）
            @Override
            public void onDone() {
                super.onDone();
                Log.e(TAG, "成功下载的地址是：" + "/sdcard/a.mp4");
            }
        });
    }

    public void Excutors(final String url) {
        final String filePath = "/sdcard/" + System.currentTimeMillis() + ".mp4";
        Executors.newCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL webUrl = new URL(url);
                    URLConnection con = webUrl.openConnection();    // 打开连接
                    InputStream in = con.getInputStream();            // 获取InputStream
                    File f = new File(filePath);                    // 创建文件输出流
                    FileOutputStream fo = new FileOutputStream(f);
                    byte[] buffer = new byte[1024 * 1024 * 10];
                    Log.e(TAG, "开始下载========：" + "/sdcard/a.mp4");
                    int len = 0;
                    // 读取文件
                    while ((len = in.read(buffer)) > 0) {
                        Log.e(TAG, "正在下载========：" + buffer);
                        fo.write(buffer, 0, len);            // 写入文件
                    }
                    in.close();
                    fo.flush();
                    fo.close();
                    Log.e(TAG, "下载完成========：");
                } catch (Exception e) {
                    Log.e(TAG, "下载失败========：" + e.getMessage());
                }
            }
        });
    }


    //okhttp3网络请求框架
    private void downloadFile3(final String url) {

        final String filePath = System.currentTimeMillis() + ".mp4";
        final long startTime = System.currentTimeMillis();
        Request request = new Request.Builder().url(url).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                e.printStackTrace();
                Log.i(TAG, "下载失败===============");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Sink sink = null;
                BufferedSink bufferedSink = null;
                try {
                    File dest = new File("/sdcard/", filePath);
                    if (dest.exists()) {
                        dest.mkdirs();
                    }
                    sink = Okio.sink(dest);
                    bufferedSink = Okio.buffer(sink);
                    bufferedSink.writeAll(response.body().source());

                    bufferedSink.close();
                    Log.i(TAG, "下载成功==========");
                    Log.i(TAG, "totalTime=" + (System.currentTimeMillis() - startTime));
                } catch (Exception e) {

                    Log.i(TAG, "下载失败===============" + e.getMessage());
                } finally {
                    if (bufferedSink != null) {
                        bufferedSink.close();
                    }

                }
            }
        });
    }
}
