package com.lzkj.aidlservice.util;

import android.os.SystemClock;
import android.widget.Toast;

import com.lzkj.aidlservice.app.CommunicationApp;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;


/**
 * 执行linux底层命令类
 *
 * @author 陈龙文
 */
public class Cmd {

    private static final LogUtils.LogTag TAG = LogUtils.getLogTag(Cmd.class.getSimpleName(), true);

    /**
     * 执行 linux 命令回调接口
     *
     * @author 陈龙文
     */
    public interface CmdResultCallback {
        /**
         * 执行 linux 命令回执
         *
         * @param result      执行命令后返回的消息
         * @param timeConsume 执行命令消耗的时间（单位：毫秒）
         */
        void callback(String result, long timeConsume);
    }

    public static void rootCmd(final String cmd, final CmdResultCallback callback) {
        new Thread() {
            public void run() {

                long l1 = SystemClock.uptimeMillis();
                Process process = null;
                DataOutputStream os = null;

                // 命令执行结果
                InputStream is = null;
                DataInputStream dis = null;

                StringBuilder sb = new StringBuilder();
                try {
                    process = Runtime.getRuntime().exec("su");
                    LogUtils.d(TAG, "rootCmd", "su");
                    os = new DataOutputStream(process.getOutputStream());
                    os.writeBytes(cmd + "\n");
                    LogUtils.d(TAG, "rootCmd", cmd);

//					dis = new DataInputStream(is = process.getInputStream());
//					
//					String cmdResult;
//					while ((cmdResult = dis.readUTF()) != null) {
//						sb.append(cmdResult);
//					}
//					
                    os.writeBytes("exit\n");
                    LogUtils.d(TAG, "rootCmd", "exit");
                    os.flush();
                    process.waitFor();
                    LogUtils.d(TAG, "rootCmd", "exec done!");
                    // flag = true;
                } catch (Exception e) {
                    // flag = false;
                    LogUtils.e(TAG, "rootCmd", e);
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (dis != null) {
                            dis.close();
                        }
                        if (os != null) {
                            os.close();
                        }
                        process.destroy();
                    } catch (Exception e) {
                        // flag = false;
                        LogUtils.e(TAG, "rootCmd", e);
                    }
                }
                long l2 = SystemClock.uptimeMillis();
                LogUtils.timeConsume(TAG, "cmd", l2 - l1);

                if (callback != null) {
                    callback.callback(sb.toString(), l2 - l1);
                }

            }
        }.start();

    }

    /**
     * 执行带参数和返回值的命令
     *
     * @param args 命令执行需要的参数，包括命令本身
     */
    public static void cmd(final String[] args, final CmdResultCallback callback) {

        new Thread() {
            @Override
            public void run() {
                long l1 = SystemClock.uptimeMillis();
                String result = "";
                ProcessBuilder processBuilder = new ProcessBuilder(args);
                Process process = null;
                InputStream errIs = null;
                InputStream inIs = null;
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int read = -1;
                    process = processBuilder.start();
                    errIs = process.getErrorStream();
                    while ((read = errIs.read()) != -1) {
                        baos.write(read);
                    }
                    baos.write("\n".getBytes());
                    inIs = process.getInputStream();
                    while ((read = inIs.read()) != -1) {
                        baos.write(read);
                    }
                    byte[] data = baos.toByteArray();
                    result = new String(data);
                    LogUtils.d(TAG, "intall", result);

                } catch (IOException e) {
                    LogUtils.e(TAG, "cmd", e);
                } catch (Exception e) {
                    LogUtils.e(TAG, "cmd", e);
                } finally {
                    try {
                        if (errIs != null) {
                            errIs.close();
                        }
                        if (inIs != null) {
                            inIs.close();
                        }
                    } catch (IOException e) {
                        LogUtils.e(TAG, "cmd", e);
                    }
                    if (process != null) {
                        process.destroy();
                    }
                }
                long l2 = SystemClock.uptimeMillis();
                LogUtils.timeConsume(TAG, "cmd", l2 - l1);
                if (callback != null) {
                    callback.callback(result, l2 - l1);
                }
            }
        }.start();
    }

    /**
     * 打开看门狗，写入命令
     *
     * @param path
     * @param command
     */
    public static void openWatchDog(String path, String command) {
        try {
            LogUtils.d(TAG, "openWatchDog", "path: " + path + "\r" + " command: " + command);
            File writeName = new File(path);
            BufferedWriter out = new BufferedWriter(new FileWriter(writeName));
            out.write(command);
            out.flush();
            out.close();
        } catch (Exception e) {
            LogUtils.d(TAG, "openWatchDog", "写入命令失败 : " + command);
            LogUtils.d(TAG, "openWatchDog", "写入命令b失败 : " + e.toString());
            Toast.makeText(CommunicationApp.get().getApplicationContext(), "写入命令失败 : " + command, Toast.LENGTH_SHORT).show();
        }
    }
}
