package com.unccr.zclh.dsdps.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

import com.unccr.zclh.dsdps.app.DsdpsApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

public class CONS {

    public static final int LOGIN = 200;
    public static final int LOGINSHOW = 201;
    public static final int CALLSTATE = 202;
    public static final int MEDIASTATE = 203;
    public static final int CALLDOWN = 204;
    public static final int FINISH = 205;
    public static final int SETSPEN = 206;
    public static final int SETTEXT = 207;
    public static final int INCOMINGMESSAGE = 208;//来短信了
    public static final int showpic = 209;
    public static final int CALLING = 210;
    public static void SENDMESSAGETO(Handler handler, int menu, Object object) {
        if (handler != null) {
            Message.obtain(handler, menu, object).sendToTarget();
        }
    }

    /**
     * Java文件操作 获取文件扩展名
     * IMG_20170905_100435.jpg.mp3-->mp3
     *  Created on: 2017-9-28
     *      Author: lyf
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * Java文件操作 获取不带扩展名的文件名
     * IMG_20170905_100435.jpg.mp3-->IMG_20170905_100435.jpg
     *  Created on: 2017-9-28
     *      Author: lyf
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }


    public static final int REQUEST_CODE_ASK_WRITE_SETTINGS = 101;//允许程序读取或写入系统设置
    public static void requestPermission_ACTION_MANAGE_WRITE_SETTINGS(Activity c){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if( !Settings.System.canWrite(c)){
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + DsdpsApp.getDsdpsApp().getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                c.startActivityForResult(intent, REQUEST_CODE_ASK_WRITE_SETTINGS);
            }else{
                //有了权限，你要做什么呢？具体的动作
            }
        }
    }


    public static final int REQUEST_OVERLAY = 102;//悬浮窗口权限申请
    public static void requestPermission_ACTION_MANAGE_OVERLAY_PERMISSION(Activity c) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(c)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + DsdpsApp.getDsdpsApp().getPackageName()));
                c.startActivityForResult(intent, REQUEST_OVERLAY);
            } else {

            }
        }
    }


    public static long forChannel(File f1, File f2) throws Exception{
        long time=new Date().getTime();
        int length=2097152;
        FileInputStream in=new FileInputStream(f1);
        FileOutputStream out=new FileOutputStream(f2);
        FileChannel inC=in.getChannel();
        FileChannel outC=out.getChannel();
        ByteBuffer b=null;
        while(true){
            if(inC.position()==inC.size()){
                inC.close();
                outC.close();
                return new Date().getTime()-time;
            }
            if((inC.size()-inC.position())<length){
                length=(int)(inC.size()-inC.position());
            }else
                length=2097152;
            b=ByteBuffer.allocateDirect(length);
            inC.read(b);
            b.flip();
            outC.write(b);
            outC.force(false);
        }
    }

    public static String getFileSuffix(File f) {
        String name = f.getName();
        if (name != null && !"".equals(name)) {
            if (name.contains(".")) {
                return name.substring(name.lastIndexOf("."));
            }
        }
        return name;
    }




    //防止华为机型未加入白名单时按返回键回到桌面再锁屏后几秒钟进程被杀
//	public static void onBackPressed(Activity activity) {
//		IntentWrapper.onBackPressed(activity);
//	}
}
