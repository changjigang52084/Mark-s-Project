package com.unccr.zclh.dsdps.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.unccr.zclh.dsdps.R;


public class StreamMediaFragment extends BaseFragment {

    private static final String TAG = "StreamMediaFragment";
    private View view = null;
//    private VideoView videoView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (null == view) {
            view = inflater.inflate(R.layout.fragment_stream, container, false);
//            videoView = view.findViewById(R.id.vitamio_videoView);
        }
        return view;
    }


    @Override
    protected void startLoadingTask() {
//        if (!LibsChecker.checkVitamioLibs(getActivity())){
//            return;
//        }
//        videoView.setVideoURI(Uri.parse(getPath()));
//        videoView.setMediaController(new MediaController(getActivity()));
//        videoView.requestFocus();
//
//        videoView.setOnPreparedListener(mediaPlayer -> mediaPlayer.setPlaybackSpeed(1.0f));
//        videoView.start();
    }

    @Override
    public void pausePlayback() {

    }

    @Override
    public void stopPlayback() {

    }

    @Override
    public void resumePlayback() {

    }

    private String getPath() {
        return "http://hls01open.ys7.com/openlive/29eba3f8b2ca4b3eb3bee4550997d824.m3u8";
    }
}
