package com.xunixianshi.vrshow.util.cache;

import android.content.Context;
import android.graphics.Bitmap;

import com.xunixianshi.vrshow.util.StringUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by duan on 2016/10/25.
 */

public class CacheUtil {

    private static  CacheUtil mInstance;
    private  FileManager fileManager;
    private  ThreadExecutor threadExecutor;
    private  String  cacheDir;

    public static CacheUtil getInstance(){
        if(mInstance == null){
            mInstance = new CacheUtil();
        }
        return mInstance;
    }

    public  void initCacheUtil(Builder builder){
        this.cacheDir = builder.cacheDir  + File.separator+ "cache" + File.separator;
        this.fileManager = builder.fileManager;
        this.threadExecutor = builder.threadExecutor;
        initDir();
    }

    private void initDir(){
        mkDirCacheDir(CacheType.IMAGE);
        mkDirCacheDir(CacheType.JSON);
    }

    private boolean mkDirCacheDir(CacheType cacheType){
        String cacheDir = this.cacheDir +
                File.separator +
                cacheType.getValue();
        File file = new File(cacheDir);
        return (file.exists() || file.mkdir());
    }

    public void putImageToCache(Bitmap bitmap, String key){
        if(bitmap != null && !isCached(CacheType.IMAGE, key)){
            File saveFile = buildFile(CacheType.IMAGE,key);
            threadExecutor.execute(new CacheWriter(this.fileManager,saveFile,new Cache(bitmap)));
        }
    }

    public void putImageToCache(Bitmap bitmap, String key,CacheCallBack cacheCallBack){
        if(bitmap != null && !isCached(CacheType.IMAGE, key)){
            File saveFile = buildFile(CacheType.IMAGE,key);
            threadExecutor.execute(new CacheWriter(this.fileManager,saveFile,new Cache(bitmap,cacheCallBack)));
        }
    }

    public File getFromCacheImage(String key){
        if(isCached(CacheType.IMAGE, key)){
            return buildFile(CacheType.IMAGE, key);
        }
        return  null;
    }

    public boolean isCached(CacheType cacheType,String key) {
        File cachedFile = this.buildFile(cacheType,key);
        return this.fileManager.exists(cachedFile);
    }

    private File buildFile(CacheType cacheType,String key) {
        String fileNameBuilder = this.cacheDir +
                File.separator +
                cacheType.getValue() +
                File.separator +
                md5(key);
        return new File(fileNameBuilder);
    }

    private static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }


    public static class  Builder{
        String cacheDir;
        Context context;
        ThreadExecutor threadExecutor;
        FileManager fileManager;
        public Builder(Context context){
            this.context = context.getApplicationContext();
            cacheDir = this.context.getCacheDir().getAbsolutePath();
            threadExecutor = new JobExecutor();
            fileManager = new FileManager();
        }

        public Builder setCacheDir(String cacheDir){
            if(!StringUtil.isEmpty(cacheDir)){
                this.cacheDir = cacheDir;
            }
            return this;
        }

        public Builder setThreadExecutor(ThreadExecutor threadExecutor){
            this.threadExecutor = threadExecutor;
            return this;
        }

        public Builder setFileManager(FileManager fileManager){
            this.fileManager  = fileManager;
            return this;
        }
    }
}
