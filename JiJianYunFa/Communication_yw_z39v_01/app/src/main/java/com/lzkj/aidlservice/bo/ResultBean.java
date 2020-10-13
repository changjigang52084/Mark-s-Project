package com.lzkj.aidlservice.bo;

import java.util.List;

/**
 * Created by Administrator on 2017/12/29.
 */

public class ResultBean {

    /**
     * {
     * "code": 0,
     * "msg": "SUCCESS",
     * "data": {
     * "request_id": "e8dcdcc2fcbdc444eb675e0966b5168e",
     * "ad_key": 972,
     * "ad_tracking": [
     * [],
     * [],
     * [
     * "http://testapi.kuaifa.tv/ad/report?version=v1&_AD_=1431656419.1431651472&_SCREEN_=1431583227&_T_=415258312069&_C_=%06%08%09%00%01%02%1D%05%07%02%19%5CJ%40PV%5E%5D%5B"
     * ]
     * ],
     * "material": {
     * "title": "元祖食品济南承德端午节广告方案_1920_1080",
     * "type": "video",
     * "width": 1920,
     * "height": 1080,
     * "url": "http://resources.kuaifa.tv/upload/aa/df494d2f1b493343936a489c91a9b7.mp4",
     * "show_time": 15,
     * "md5": "aadf494d2f1b493343936a489c91a9b7"
     * }
     * }
     * }
     */

    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * request_id : e8dcdcc2fcbdc444eb675e0966b5168e
         * ad_key : 972
         * ad_tracking : [[],[],["http://testapi.kuaifa.tv/ad/report?version=v1&_AD_=1431656419.1431651472&_SCREEN_=1431583227&_T_=415258312069&_C_=%06%08%09%00%01%02%1D%05%07%02%19%5CJ%40PV%5E%5D%5B"]]
         * material : {"title":"元祖食品济南承德端午节广告方案_1920_1080","type":"video","width":1920,"height":1080,"url":"http://resources.kuaifa.tv/upload/aa/df494d2f1b493343936a489c91a9b7.mp4","show_time":15,"md5":"aadf494d2f1b493343936a489c91a9b7"}
         */

        private String request_id;
        private int ad_key;
        private MaterialBean material;
        private List<List<?>> ad_tracking;

        public String getRequest_id() {
            return request_id;
        }

        public void setRequest_id(String request_id) {
            this.request_id = request_id;
        }

        public int getAd_key() {
            return ad_key;
        }

        public void setAd_key(int ad_key) {
            this.ad_key = ad_key;
        }

        public MaterialBean getMaterial() {
            return material;
        }

        public void setMaterial(MaterialBean material) {
            this.material = material;
        }

        public List<List<?>> getAd_tracking() {
            return ad_tracking;
        }

        public void setAd_tracking(List<List<?>> ad_tracking) {
            this.ad_tracking = ad_tracking;
        }

        public static class MaterialBean {
            /**
             * title : 元祖食品济南承德端午节广告方案_1920_1080
             * type : video
             * width : 1920
             * height : 1080
             * url : http://resources.kuaifa.tv/upload/aa/df494d2f1b493343936a489c91a9b7.mp4
             * show_time : 15
             * md5 : aadf494d2f1b493343936a489c91a9b7
             */

            private String title;
            private String type;
            private int width;
            private int height;
            private String url;
            private int show_time;
            private String md5;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public int getShow_time() {
                return show_time;
            }

            public void setShow_time(int show_time) {
                this.show_time = show_time;
            }

            public String getMd5() {
                return md5;
            }

            public void setMd5(String md5) {
                this.md5 = md5;
            }
        }
    }

}
