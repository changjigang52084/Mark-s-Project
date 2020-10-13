package com.sunchip.adw.cloudphotoframe.show;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.lang.reflect.Type;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Launcher.Downling.image.NetworkUtil;
import com.example.SunchipFile.File_Message;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunchip.adw.cloudphotoframe.BaseActivity;
import com.sunchip.adw.cloudphotoframe.MainActivity;
import com.sunchip.adw.cloudphotoframe.R;
import com.sunchip.adw.cloudphotoframe.Timer.MyCountTimer;
import com.sunchip.adw.cloudphotoframe.adapter.LeftMenuListAdapter;
import com.sunchip.adw.cloudphotoframe.adapter.RightContentListAdapter;
import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;
import com.sunchip.adw.cloudphotoframe.http.HttpURLUtils;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.BaseErrarEvent;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.Event.HttpErrorEvent;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.Event.PhotoListEvent;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.Event.PhotosEvent;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode;
import com.sunchip.adw.cloudphotoframe.http.request.HttpErrarCode;
import com.sunchip.adw.cloudphotoframe.http.request.HttpRequestAuxiliary;
import com.sunchip.adw.cloudphotoframe.manager.MusicPlayManger;
import com.sunchip.adw.cloudphotoframe.play.PlayVideoPicActivity;
import com.sunchip.adw.cloudphotoframe.util.FileUtils;
import com.sunchip.adw.cloudphotoframe.util.StatusBarUtils;
import com.sunchip.adw.cloudphotoframe.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import me.zhouzhuo.zzsecondarylinkage.ZzSecondaryLinkage;
import me.zhouzhuo.zzsecondarylinkage.model.ILinkage;

import static com.sunchip.adw.cloudphotoframe.app.CloudFrameApp.BASE;

public class ShowVideoPicActivity extends BaseActivity {

    private static final String TAG = "ShowVideoPicActivity";

    private ZzSecondaryLinkage zzSecondaryLinkage;
    private Gson gson = new Gson();

    //获取本地照片保存的地址
    private List<File> mFileList = new ArrayList<>();

    private List<PhotoListEvent> entities = new ArrayList<>();

    private LeftMenuListAdapter leftAdapter;

    private RightContentListAdapter rightAdapter;

    private List<PhotosEvent> rightEntity = new ArrayList<>();

    //用于统计数据
    PhotoListEvent mPhotoListEvent = new PhotoListEvent();
    public static List<PhotosEvent> mPhotosEvent = new ArrayList<>();

    //照片正在下载的张数
    private int PhotoNumber = 0;

    //当前选中的列表图片
    public static int Selection = 0;

    //当前要播放的地址列表
    public static List<String> PhotoPhth = new ArrayList<>();

    //当前选中的列表图片要播放的圖片
    public static int PhotoSelection = 0;

    //判断是否正在下载
    private boolean AreYouDownloading = false;

    //保存每次得到的图片Key
    Set<String> SetPath = new TreeSet<>();


    //本地保存的图片列表
    private String mData = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.getInstance().ImageStausBar(this, R.color.white);

        setContentView(R.layout.activity_show_video_pic);
        Initialization();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ShowVideoPicEvent(BaseErrarEvent event) {
        switch (event.getCode()) {
            case InterFaceCode.Get_Playback_Resources:
//                TypeTokenPlaylist(event.getResult(), false);
                break;

            case InterFaceCode.DownLoadDone:
                AreYouDownloading = false;
                Log.e("TAG", "下载成功的地址是：" + event.getResult());
                //上一张下载完成以后开始下一张下载
                PhotoNumber++;
                //如果当前的张数在的话直接加
                if (PhotoNumber > mPhotosEvent.size() - 1)
                    return;
                else Showdownload();

                if (rightAdapter != null)
                    //下载成功直接刷新适配器
                    rightAdapter.notifyDataSetChanged();
                break;
            case InterFaceCode.DownLoadDone_load:
                AreYouDownloading = true;
                break;
            case InterFaceCode.DownLoadDone_load_Upload:
                handler.removeMessages(0x127);
                handler.sendEmptyMessageDelayed(0x127, 30 * 1000);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyCountTimer.setStartMyCount("ShowVideoPicActivity_onResume");
        MusicPlayManger.getInstance().Stop();
    }

    private void TypeTokenPlaylist(String event) {
        entities.clear();
        rightEntity.clear();
        mPhotosEvent.clear();
        Type listType = new TypeToken<HttpErrorEvent<List<PhotoListEvent>>>() {
        }.getType();
        HttpErrorEvent<List<PhotoListEvent>> newsInfos = gson.fromJson(event, listType);
        SetPath.clear();
        if (newsInfos.getErr_code() == HttpErrarCode.RESULT_CODE_SUCCESS) {
            //内容获取成功
            entities = newsInfos.getData();
            if (newsInfos.getData().size() == 0) {
                //没有数据
                entities.add(0, mPhotoListEvent);
            } else {
                //如果有数据的话 那么我们就统计所有的数据放在list里面
                for (int i = 0; i < newsInfos.getData().size(); i++) {
                    for (int i1 = 0; i1 < newsInfos.getData().get(i).getPhotos().size(); i1++) {
                        mPhotosEvent.add(newsInfos.getData().get(i).getPhotos().get(i1));
                        SetPath.add(newsInfos.getData().get(i).getPhotos().get(i1).getMd5());
                    }
                    mPhotoListEvent.setPhotos(mPhotosEvent);
                }
                entities.add(0, mPhotoListEvent);
            }
            LeftMenuList();
            //直接从第一张开始下载
            Showdownload();
            //删除多余照片 省的本地占用
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DeletePhoto();
                }
            });
        } else if (newsInfos.getErr_code() == HttpErrarCode.RESULT_CODE_FRAME_UNPAIR) {
            //解除绑定 删除所有照片
            HttpURLUtils.deleteDirWihtFile(new File(CloudFrameApp.BASE));
            //列表显示一个全部 不过为空而已
            entities.add(0, mPhotoListEvent);
        }
    }

    private void DeletePhoto() {
        if (mFileList != null && mFileList.size() > 0) {
            for (int i = 0; i < mFileList.size(); i++) {
                String path = mFileList.get(i).getAbsolutePath();
                int Sm = path.lastIndexOf(".");
                int Out = path.lastIndexOf("/");
                String Suffix = path.substring(Out + 1, Sm);
                if (!SetPath.contains(Suffix)) {
                    //删除多余的图片
                    FileUtils.getInstance().deleteFile(mFileList.get(i).getAbsolutePath());
                }
            }
        }
    }

    public void LeftMenuList() {
        leftAdapter.setList(entities);
        rightAdapter.setList(entities.get(0).getPhotos());
        //播放列表默认选择第一个
        getListPhotoPath(entities.get(0).getPhotos());
    }


    public void Initialization() {
        ViewUtil.scaleContentView((ViewGroup) findViewById(R.id.act_main_root_layout));

        zzSecondaryLinkage = findViewById(R.id.zz_linkage);
        //默认使用一个全部列表 为甚不查询呢 问题就在这 如果服务器再返回一个全部列表 那么数据查询每次就要大一倍 所以本地做这个统计
        mPhotoListEvent.setId(0);
        mPhotoListEvent.setName(getResources().getStringArray(R.array.photo_setting)[0]);

        leftAdapter = new LeftMenuListAdapter(this, entities);
        zzSecondaryLinkage.setLeftMenuAdapter(leftAdapter);


        rightAdapter = new RightContentListAdapter(this, rightEntity);
        zzSecondaryLinkage.setRightContentAdapter(rightAdapter);

        zzSecondaryLinkage.setOnItemClickListener(new ILinkage.OnItemClickListener() {
            @Override
            public void onLeftClick(View itemView, int position) {
                //设置选择的列表
                Selection = position;
                rightAdapter.setList(entities.get(position).getPhotos());
                getListPhotoPath(entities.get(position).getPhotos());
                MyCountTimer.setStartMyCount("ShowVideoPicActivity_onLeftClick");
            }

            @Override
            public void onRightClick(View itemView, int position) {
                Log.e("TAG", "当前选中的是第" + Selection + "个列表，名字为：" +
                        entities.get(Selection).getName()
                        + "  选中的图片个数是第:" + position + "个");

                PhotoSelection = position;
                CloudFrameApp.IsPlayMoThod = 1;
                IsPlaying(position);
            }
        });

        mFileList.clear();
        //获取本地文件的所有图片
        if (FileUtils.getInstance().fileIsExists(BASE)) {
            mFileList = FileUtils.getInstance().getFile(new File(BASE));
        }
        try {
            mData = File_Message.read("PhotoList", this);
        } catch (Exception e) {
            Log.e("TAG", "找不到文件需要你去设置");
        }
        if (mData != null) {
            handler.sendEmptyMessage(0x126);
        }

        //也刷新一下 不冲突
        handler.sendEmptyMessage(0x123);
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x123:
                    //先获取一下内容
                    HttpRequestAuxiliary.getInstance().getPlayList(InterFaceCode.Get_Playback_Resources);
                    break;
                case 0x124:
                    //本地文件存在的时候要去判断文件大小 是否上一次下载未成功
                    if (mPhotosEvent.size() == 0 || mPhotosEvent.size() < PhotoNumber) {
                        //必须实时的去判断这个数字
                        PhotoNumber = 0;
                        return;
                    }
                    int DownFileSizeDouble = 0;
                    Long FuwuFileSizeDouble = Long.parseLong("0");
                    try {
                        DownFileSizeDouble = (int) FileUtils.getInstance().getFileOrFilesSize((String) msg.obj);
                        FuwuFileSizeDouble = mPhotosEvent.get(PhotoNumber).getSize();
                    } catch (Exception e) {
                        Log.e("TAG", "我觉得你删图片了,导致异常出现了.........");
                    }

                    Log.e("TAG", "本地文件大小:" + DownFileSizeDouble + "  服务器文件大小:" + FuwuFileSizeDouble);
                    if (DownFileSizeDouble != FuwuFileSizeDouble) {
                        File file = new File((String) msg.obj);
                        if (file.exists()) {
                            file.delete();
                        }
                        //刪除当前
                        Log.e("TAG", "刪除当前 PhotoNumber:" + PhotoNumber + "   file     " + file.exists());
                    } else {
                        PhotoNumber++;
                    }
                    if (PhotoNumber > mPhotosEvent.size() - 1) {
                        PhotoNumber = 0;
                        return;
                    } else Showdownload();
                    break;
                case 0x126:
                    TypeTokenPlaylist(mData);
                    break;
                case 0x127:
                    Log.e("TAG", "当前的下载线程三十秒没有下载=======1=======PhotoNumber:" + PhotoNumber);
                    if (rightAdapter != null)
                        //下载成功直接刷新适配器
                        rightAdapter.notifyDataSetChanged();
                    if (PhotoNumber > mPhotosEvent.size() - 1) {
                        Log.e("TAG", "当前的下载线程三十秒没有下载,但是是到了最后一个已经下载完了就不需要再三十秒倒计时了" +
                                "=======4=======PhotoNumber:" + PhotoNumber);
                        PhotoNumber = 0;
                        return;
                    }
                    //三十秒没有开始下载  判断是都有网络 如果有网络接着下载 如果没有网络就不用下载
                    if (NetworkUtil.isNetworkConnected(ShowVideoPicActivity.this)) {
                        //有网
                        Showdownload();
                    } else {
                        //没网
                        Log.e("TAG", "三十秒没有下载,并且没有网络连接哦=======2=======PhotoNumber:" + PhotoNumber);
                        handler.removeMessages(0x127);
                        handler.sendEmptyMessageDelayed(0x127, 30 * 1000);
                        Log.e("TAG", "没网以后接着倒计时30秒=======3=======PhotoNumber:" + PhotoNumber);
                    }
                    break;
            }
        }
    };


    //一张一张下载 否则会出现一张图片多个下载线程导致图片下载失败
    private void Showdownload() {

        //防止正在下载的时候出现异常开启另一个下载线程问题
        if (AreYouDownloading) {
            return;
        }
        if (mPhotosEvent.size() == 0 || mPhotosEvent.size() < PhotoNumber) {
            PhotoNumber = 0;
            return;
        }
        String Key = mPhotosEvent.get(PhotoNumber).getKey();
        int Sm = Key.lastIndexOf(".");
        String Suffix = Key.substring(Sm);
        String PhotoPathName = mPhotosEvent.get(PhotoNumber).getMd5() + Suffix;
        final String Path = CloudFrameApp.BASE + PhotoPathName;
        if (!PinJieIsPhoto(PhotoPathName))
            HttpURLUtils.Download(mPhotosEvent.get(PhotoNumber).getUrl(), new File(Path), 0);
        else {
            Message message = new Message();
            message.what = 0x124;
            message.obj = Path;
            handler.sendMessage(message);
        }
    }


    //判斷是否可以播放
    public void IsPlaying(final int index) {
        String Key = mPhotosEvent.get(index).getKey();
        int Sm = Key.lastIndexOf(".");
        String Suffix = Key.substring(Sm);

        String PhotoPathName = mPhotosEvent.get(index).getMd5() + Suffix;
        final String Path = CloudFrameApp.BASE + PhotoPathName;
        //本地文件存在的时候要去判断文件大小 是否上一次下载未成功
        final int DownFileSizeDouble = (int) FileUtils.getInstance().getFileOrFilesSize(Path);
        final long FuwuFileSizeDouble = mPhotosEvent.get(index).getSize();


        if (DownFileSizeDouble == FuwuFileSizeDouble) {
            Intent intentPlay = new Intent(ShowVideoPicActivity.this, PlayVideoPicActivity.class);
            intentPlay.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentPlay);
        } else {

        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 350) {
            MyCountTimer.setStartMyCount("ShowVideoPicActivity_keyCode != 350");
        }

        if (event.getKeyCode() == 351) {
            staerActibity(0);
        } else if (event.getKeyCode() == 353) {
            staerActibity(2);
        }

        Log.e("TAG", "ShowVideoPicAcvity====" + keyCode);
        return super.onKeyDown(keyCode, event);
    }

    //拼接地址 判断是否存在
    public Boolean PinJieIsPhoto(String PathName) {
        String Path = CloudFrameApp.BASE + PathName;

        return HttpURLUtils.IsFileExists(Path);
    }


    //轮询获取所有的播放地址
    public void getListPhotoPath(List<PhotosEvent> photos) {
        if (photos != null && photos.size() > 0) {
            PhotoPhth.clear();
            for (int i = 0; i < photos.size(); i++) {
                String Key = photos.get(i).getKey();
                int Sm = Key.lastIndexOf(".");
                String Suffix = Key.substring(Sm);
                PhotoPhth.add(CloudFrameApp.BASE + photos.get(i).getMd5() + Suffix);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyCountTimer.setCloseMyCount();
    }
}
