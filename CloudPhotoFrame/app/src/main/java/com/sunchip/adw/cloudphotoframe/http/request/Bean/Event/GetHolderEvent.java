package com.sunchip.adw.cloudphotoframe.http.request.Bean.Event;

/**
 * Created by yingmuliang on 2020/1/8.
 */
public class GetHolderEvent {
//    {
//        "result": "success",
//            "err_code": 200,
//            "data": {
//        "frameInfo": {
//            "insert_id": null,
//                    "id": 3005,
//                    "name": "多谢多谢",
//                    "location": "fff-",
//                    "serial": 1000000000000001,
//                    "mac": "00:E0:4C:F0:A0:42",
//                    "ip": "192.168.43.229",
//                    "model": null,
//                    "userId": 8,
//                    "spaceTotal": 4174176256,
//                    "spaceFree": 4174790656,
//                    "softwareVersion": "1.0",
//                    "hardwareVersion": null,
//                    "active_time": "2020-01-08T11:02:01.000+0000",
//                    "paired_time": "2020-01-08T11:47:17.000+0000"
//        },
//        "frameConfig": {
//            "name": "多谢多谢",
//                    "location": "fff-",
//                    "alwaysShowRecent": 4,
//                    "sleepInterval": 1,
//                    "sleepIntervalStartTime": null,
//                    "sleepIntervalEndTime": null,
//                    "motion": 1,
//                    "sleepWhenNoMotionDetected": 1,
//                    "mostRecentTimeSpan": 1,
//                    "sleepClock": 2,
//                    "timezone": 5,
//                    "volume": 1,
//                    "brightness": 1
//        },
//        "playlistConfig": {
//            "shuffle": 1,
//                    "clockSlideshow": 1,
//                    "captions": 1,
//                    "photoFit": 1,
//                    "transitionType": 1,
//                    "displayDuration": 1,
//                    "showAlert": 1
//        },
//        "configTimestamp": 1578550999588,
//                "playlistsTimestamp": 1578484993289
//    }
//    }

    private frameInfoEvent frameInfo;

    private FrameSettingsEvent frameConfig;

    private FrameSlideshowEvent playlistConfig;

    public FrameSettingsEvent getFrameConfig() {
        return frameConfig;
    }

    public void setFrameConfig(FrameSettingsEvent frameConfig) {
        this.frameConfig = frameConfig;
    }

    public FrameSlideshowEvent getPlaylistConfig() {
        return playlistConfig;
    }

    public void setPlaylistConfig(FrameSlideshowEvent playlistConfig) {
        this.playlistConfig = playlistConfig;
    }

    public frameInfoEvent getFrameInfo() {
        return frameInfo;
    }

    public void setFrameInfo(frameInfoEvent frameInfo) {
        this.frameInfo = frameInfo;
    }
}
