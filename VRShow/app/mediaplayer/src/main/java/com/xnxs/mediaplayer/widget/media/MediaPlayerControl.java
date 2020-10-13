package com.xnxs.mediaplayer.widget.media;

/**
 * Created by duanchunlin on 2016/8/10.
 */
public interface MediaPlayerControl {

    void    start();
    void    pause();
    int     getDuration();
    int     getCurrentPosition();
    void    seekTo(int pos);
    boolean isPlaying();
    int     getBufferPercentage();
    boolean canPause();
    boolean canSeekBackward();
    boolean canSeekForward();

    boolean isCompletion();
    /**
     * Get the audio session id for the player used by this VideoView. This can be used to
     * apply audio effects to the audio track of a video.
     * @return The audio session, or 0 if there was an error.
     */
    int getAudioSessionId();
}
