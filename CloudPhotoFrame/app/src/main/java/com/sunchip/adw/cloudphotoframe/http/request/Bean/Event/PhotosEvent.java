package com.sunchip.adw.cloudphotoframe.http.request.Bean.Event;

public class PhotosEvent {

//    	"key": "38/38_eff4892e65742856d248d010664ca30e.bmp",
//                "url": "https://vucatimes.s3.us-east-2.amazonaws.com/38/38_eff4892e65742856d248d010664ca30e.bmp?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20191218T024238Z&X-Amz-SignedHeaders=host&X-Amz-Expires=259200&X-Amz-Credential=AKIAIYEQWSILAQUVZ4QQ%2F20191218%2Fus-east-2%2Fs3%2Faws4_request&X-Amz-Signature=b61b9a2e97ec2fb43eee34798bb9a580db396e88cb4960b83a8860ea932af60a",
//                "md5": "eff4892e65742856d248d010664ca30e",
//                "caption": null,
//                "time": "2019-12-17T01:43:48.000+0000"

    private String key;
    private String url;
    private String md5;
    private Long size;
    private String caption;

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
