package com.unccr.zclh.dsdps.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.unccr.zclh.dsdps.R;
import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.fragment.view.CustomVideoView;

import java.io.File;
import java.util.ArrayList;

public class PlayFragment extends BaseFragment {

    private static final String TAG = "PlayFragment";
    private BaseHandler mBaseHandler;
    private View rootView;
    //    private VideoView videoView;
    private CustomVideoView videoView;
    private ImageView imageView;
    private int playIndex = -1;
    private int videoIndex = -1;
    private ArrayList<String> programmeList = new ArrayList<>();
    private boolean isPicProgramme = false;
    private boolean isAnfu = false;
    private String playPicFile;
    private String videoPrmPath;

    public static PlayFragment newInstance(ArrayList<String> fileList, boolean isPicProgramme, boolean isAnfu) {
        Bundle args = new Bundle();
        PlayFragment fragment = new PlayFragment();
        args.putStringArrayList("programmeList", fileList);
        args.putBoolean("isPicProgramme", isPicProgramme);
        args.putBoolean("isAnfu", isAnfu);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == rootView) {
            rootView = inflater.inflate(R.layout.fragment_play, container, false);
            videoView = rootView.findViewById(R.id.fragment_video);
            imageView = rootView.findViewById(R.id.fragment_picture);
        }
        programmeList = getArguments().getStringArrayList("programmeList");
        isPicProgramme = getArguments().getBoolean("isPicProgramme");
        isAnfu = getArguments().getBoolean("isAnfu");
        mBaseHandler = new BaseHandler(this);
        if(programmeList != null){
            Log.d(TAG, "playPrmVideo. " + programmeList.size());
        }
        return rootView;
    }

    @Override
    protected void startLoadingTask() {
        mBaseHandler.removeCallbacksAndMessages(null);
        playProgramme();
    }

    private void playProgramme() {
        if (isPicProgramme) {
            getNextPic();
        } else {
            playPrmVideo();
        }
    }

    private void getNextPic() {
        playIndex++;
        if (programmeList.size() <= playIndex) {
            playIndex = 0;
        }
        playPicFile = programmeList.get(playIndex);
        Log.d(TAG, "getNextPic materialName: " + playPicFile);
        handlerPicPlay();
    }

    private void handlerPicPlay() {
        imageView.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.GONE);
        Glide.with(DsdpsApp.getDsdpsApp())
                .load(new File(playPicFile))
//              .asGif()
                .asBitmap()//取消GIF播放
                .dontAnimate()//取消动画
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存
                .into(imageView);

        mBaseHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getNextPic();
            }
        }, 10 * 1000);
    }

    @Override
    public void pausePlayback() {

    }

    @Override
    public void stopPlayback() {
        videoView.pause();
        videoView.stopPlayback();
        imageView.setImageBitmap(null);
        if (mBaseHandler != null) {
            mBaseHandler.recycle();
        }
    }

    @Override
    public void resumePlayback() {

    }

//    private void requestSDpermission() {
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//        } else {
//            playPrmVideo();
//        }
//    }

    /**
     * 播放视频(电梯）
     */
//    @RequiresApi(api = Build.VERSION_CODES.M)
    private void playPrmVideo() {
        Log.d(TAG, "playPrmVideo......");
//        mBaseHandler.removeCallbacksAndMessages(null);
        if(isAnfu){
            startPlayVideo();
        }else {
            Log.d(TAG, "onReceive switchPrmReceiver: update program" + "7777777777777-------->" + getVideoDuration(videoPrmPath));
            nextVideoIndex();
            startPlayVideo();
            mBaseHandler.postDelayed(switchVideoPlay, getVideoDuration(videoPrmPath));
        }
    }

    private Runnable switchVideoPlay = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void run() {
            playPrmVideo();
        }
    };

    private void startPlayVideo() {
        Log.d(TAG, "onReceive switchPrmReceiver: update program" + "5555555555555 start play");
        if (isAnfu) {
            videoPrmPath = "android.resource://" + getActivity().getPackageName() + "/" + R.raw.anfu;
            imageView.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);

            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    videoView.stopPlayback(); //播放异常，则停止播放，防止弹窗使界面阻塞
                    Log.e(TAG, "Video Play Failed ");
                    return true;
                }
            });
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setVolume(0f, 0f);
                    mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                        @Override
                        public boolean onInfo(MediaPlayer mp, int what, int extra) {
                            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                                videoView.setBackgroundColor(Color.TRANSPARENT);
                            }
                            return false;
                        }
                    });
                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    startPlayVideo();
                }
            });
            videoView.setVideoURI(Uri.parse(videoPrmPath));
            videoView.setMeasure(getActivity().getWindowManager().getDefaultDisplay().getWidth(), 822);
            videoView.start();
        } else {
            videoPrmPath = programmeList.get(videoIndex);
            imageView.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
//        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
////                startPlayVideo();
//                startPlay();
//                Log.e(TAG, "succeed succeed ");
//            }
//        });
//        videoView.setMediaController(new MediaController(getActivity()));
            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    videoView.stopPlayback(); //播放异常，则停止播放，防止弹窗使界面阻塞
                    Log.e(TAG, "Video Play Failed ");
                    return true;
                }
            });
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                        @Override
                        public boolean onInfo(MediaPlayer mp, int what, int extra) {
                            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                                videoView.setBackgroundColor(Color.TRANSPARENT);
                            }
                            return false;
                        }
                    });
                }
            });
            if (new File(videoPrmPath).exists()) {
                videoView.setVideoPath(videoPrmPath);
                videoView.setMeasure(getActivity().getWindowManager().getDefaultDisplay().getWidth(), 822);
                videoView.start();
            } else {
                Log.d(TAG, "文件不存在");
            }
        }
    }

//    private void startPlay() {
//        videoView.setVideoPath(videoPrmPath);
//        videoView.start();
//    }


    private void nextVideoIndex() {
        videoIndex++;
        if(programmeList!=null){
            if (programmeList.size() <= videoIndex) {
                videoIndex = 0;
            }
        }
    }

    /*
     * 获取视频时长
     */
    private long getVideoDuration(String videoPrmPath) {
        int duration;
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(videoPrmPath);
            duration = Integer.parseInt(mmr.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_DURATION));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return duration;
    }
}
