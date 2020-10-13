package com.lzkj.aidlservice.util;

import java.io.File;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Program;
import com.lzkj.aidlservice.util.LogUtils.LogTag;

/**
 * 过期节目和素材的处理
 *
 * @author lyhuang
 * @date 2016-2-16 下午4:00:21
 */
public class OverdueProgramUtil implements Runnable {
	private static final LogTag TAG = LogUtils.getLogTag(OverdueProgramUtil.class.getSimpleName(), true);
	
	@Override
	public void run() {
		checkOverdueProgram();
	}
	
	/**
	 * 检测过期节目，并删除过期节目和素材文件。
	 */
	private void checkOverdueProgram(){
		LogUtils.d(TAG, "checkOverdueProgram", "Check overdue program.");
		File prmFolder = new File(FileUtile.getInstance().getLayoutFolderPath());
		File[] files = prmFolder.listFiles();
		if (null != files) {
			int length = files.length;
			if (length > 0) {
				for (int i = 0 ; i < length;i++) {
					try {
						if (!files[i].getName().endsWith(".prm")) {
							continue;
						}
						String prmJson = FileUtile.readPrmToFile(files[i]);
						Program program = null;
						program = JSON.parseObject(prmJson, Program.class);
						if (program == null) {
							LogUtils.e(TAG, "checkOverdueProgram", "Program parse error: program is null." + files[i].getName());
							continue;
						} 
						long currentTime = Util.getCurrentMillisTime("yyyy-MM-dd");
						long prmEndTime = program.getDe();
						if(currentTime > prmEndTime){
							LogUtils.i(TAG, "checkOverdueProgram", "Overdue program：" + program.getKey());
							//删除节目和素材
							FileUtile.getInstance().delProgramToPrmId(program.getKey());
						}
					} catch (Exception e) {
						LogUtils.e(TAG, "checkOverdueProgram", "Program parse error:" + e.getMessage());
						continue;
					}
				}
			} else {
				LogUtils.w(TAG, "checkOverdueProgram", "No local program files.");
			}
		}
	}

}
