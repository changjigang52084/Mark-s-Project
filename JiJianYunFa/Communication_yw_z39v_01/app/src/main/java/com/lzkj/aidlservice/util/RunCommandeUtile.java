package com.lzkj.aidlservice.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.util.Log;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年8月15日 下午7:38:39 
 * @version 1.0 
 * @parameter  执行shell命令
 */
public class RunCommandeUtile {
	  private static final String COMMAND_SU       = "/system/xbin/su";
	  private static final String COMMAND_SH       = "sh";
	  private static final String COMMAND_EXIT     = "exit\n";
	  private static final String COMMAND_LINE_END = "\n";
	  /**
	   * 执行休眠
	   * @return
	   */
//	  public static boolean execLYSeelp() {
//			String[] commands = new String[]{"echo mem > /sys/power/state"};
//			return execCommand(commands, true, true);
//	  }
	  
	  /**
	   * 执行唤醒
	   * @return
	   */
//	  public static boolean execLYWakeup() {
//		  String[] commands = new String[]{"echo on > /sys/power/state"};
//		  return execCommand(commands, true, true);
//	  }
	  
	  /**
	   * 静默安装
	   * @param appPath app本地路径
	   * @return
	   */
	  public static boolean installApp(String appPath) {
		  String[] commands = new String[]{"pm install -r " + appPath};
		 return execCommand(commands, true, true);
	  }
	  /**
	   * 静默安装
	   * @param appPath app本地路径
	   * @return
	   */
	  public static boolean cpApp(String appPath) {
		  String[] commandsMount = new String[]{"mount -o remount rw /data"};
		  String[] commands = new String[]{"cp " + appPath + " /data/app"};
		  execCommand(commandsMount, true, true);
		  return execCommand(commands, true, true);
	  }
	  
	  /**
	   * 静默卸载
	   * @param apkName app名称
	   * @return
	   */
	  public static boolean uninstallAppToPackageName(String apkPackageName) {
		  String[] commandsMount = new String[]{"mount -o remount rw /data"};
		  String[] commands = new String[]{"rm -rf /data/app/" + apkPackageName + "*"};
		  String[] rmPackgeName = new String[]{"rm -rf  /data/data/" + apkPackageName};
		  execCommand(commandsMount, true, true);
		  execCommand(commands, true, true);
		  return execCommand(rmPackgeName, true, true);
	  }
	  
	  /**
	   * 静默卸载
	   * @param apkName app名称
	   * @return
	   */
	  public static boolean uninstallApp(String apkPackageName) {
		  String[] commands = new String[]{"pm uninstall " + apkPackageName};
		  return  execCommand(commands, true, true);
	  }
	  
	  /**
	   * 执行shell 命令
	   * @param commands 一组命令
	   * @param isRoot 是否需要root权限
	   * @param isNeedResultMsg 
	   * @return true表示成功
	   */
	  public static boolean execCommand(String[] commands, 
	    		boolean isRoot, boolean isNeedResultMsg) {
	        if (commands == null || commands.length == 0) {
	            return false;
	        }
	        Process process = null;
	        BufferedReader successResult = null;
	        BufferedReader errorResult = null;
	        StringBuilder successMsg = null;
	        StringBuilder errorMsg = null;

	        DataOutputStream os = null;
	        try {
	            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
	            os = new DataOutputStream(process.getOutputStream());
	            for (String command : commands) {
	                if (command == null) {
	                    continue;
	                }

	                // donnot use os.writeBytes(commmand), avoid chinese charset error
	                os.write(command.getBytes());
	                os.writeBytes(COMMAND_LINE_END);
	                os.flush();
	            }
	            os.writeBytes(COMMAND_EXIT);
	            os.flush();

	            // get command result
	            if (isNeedResultMsg) {
	                successMsg = new StringBuilder();
	                errorMsg = new StringBuilder();
	                successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
	                errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
	                String s;
	                while ((s = successResult.readLine()) != null) {
	                    successMsg.append(s);
	                }
	                while ((s = errorResult.readLine()) != null) {
	                    errorMsg.append(s);
	                }
	            }
	            if (StringUtil.isNullStr(errorMsg.toString())) {
	            	  return true;
	            }
	            Log.d(RunCommandeUtile.class.getSimpleName(), "successMsg : " + successMsg + ", errorMsg : " + errorMsg);
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                if (os != null) {
	                    os.close();
	                }
	                if (successResult != null) {
	                    successResult.close();
	                }
	                if (errorResult != null) {
	                    errorResult.close();
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	            }

	            if (process != null) {
	                process.destroy();
	            }
	        }
	        
	        return false;
	    }

}
