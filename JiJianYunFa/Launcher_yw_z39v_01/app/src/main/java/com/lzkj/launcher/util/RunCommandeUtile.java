package com.lzkj.launcher.util;

import android.util.Log;

import com.lzkj.launcher.bo.InstallBo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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
	   * 静默安装
	   * @param appPath app本地路径
	   * @return
	   */
	  public static InstallBo installApp(String appPath) {
		  String[] commands = new String[]{"pm install -r " + appPath};
//			try {									
//				Process su = Runtime.getRuntime().exec("/system/xbin/su");
//				String cmd = "pm install /mnt/sdcard/qq.apk " + "\n"
//						+ "exit\n";
//				su.getOutputStream().write(cmd.getBytes());
//				if ((su.waitFor() != 0) ) {
//					throw new SecurityException();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				throw new SecurityException();
//			}
			
		 return execCommand(commands, true, true);
	  }
	  
	  
	  private static InstallBo execCommand(String[] commands, 
	    		boolean isRoot, boolean isNeedResultMsg) {
	        int result = -1;
	        if (commands == null || commands.length == 0) {
	            return null;
	        }
	        InstallBo bo = new InstallBo();
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

	            result = process.waitFor();
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
	            bo.resultCode = result;
	            bo.errorMsg = errorMsg.toString();
	            bo.successMsg = successMsg.toString();
	            Log.d(RunCommandeUtile.class.getSimpleName(), "result : " + result +",successMsg : " + successMsg + ", errorMsg : " + errorMsg);
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
	        
	        return  bo;
	    }

}
