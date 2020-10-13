package com.unccr.zclh.dsdps.util;

import com.bumptech.glide.Glide;
import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.models.Area;
import com.unccr.zclh.dsdps.models.Material;
import com.unccr.zclh.dsdps.models.Program;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @version 1.0
 * @data 创建时间：2019年10月09日 下午7:26:11
 * @parameter PreLoadImgTool 预加载图片资源
 */
public class PreLoadImgTool {
	private static final String TAG = "PreLoadImgTool";

	private static List<String> prePrmId;
	private static List<String> cachePrmPicPath;

	static {
		initOption();
	}

	/**
	 * 初始化配置
	 */
	private static void initOption() {
		cachePrmPicPath = new ArrayList<String>();
		prePrmId = new ArrayList<String>();
	}

	/**
	 * 根据要播放的节目提前加载图片素材
	 *
	 * @param program 节目对象
	 */
	public static void preLoadImgToProgram(final Program program) {
		if (prePrmId.contains(program.getKey())) {
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				prePrmId.add(program.getKey());
				List<Area> areas = program.getAs();
				for (Area area : areas) {
					List<Material> materials = area.getMas();
					preLoadImgToMaterials(materials);
				}
			}
		}).start();
	}

	/**
	 * 预加载要播放的图片根据素材列表加载
	 *
	 * @param materials
	 */
	public static void preLoadImgToMaterials(final List<Material> materials) {
		for (Material material : materials) {
			if (Constants.PIC_FRAGMENT == material.getT()) {
				final String picPath = FileStore.getInstance()
						.getImageFilePath(FileStore.getFileName(material.getU()));
				if (cachePrmPicPath.contains(picPath)) {
					continue;
				}
				if (new File(picPath).exists()) {
					cachePrmPicPath.add(picPath);
					new Thread(new Runnable() {
						@Override
						public void run() {
							ifClearMemoryCache();
						}
					}).start();
				}
			}
		}
	}

	/**
	 * 是否清理缓存在内存中的图片
	 */
	private static void ifClearMemoryCache() {
		int memoryCacheSize = cachePrmPicPath.size();
		if (memoryCacheSize > 50) {
			clearCache();
		}
	}

	/**
	 * 清除缓存
	 */
	public static void clearCache() {
		Glide.get(DsdpsApp.getDsdpsApp()).clearMemory();
		cachePrmPicPath.clear();
		prePrmId.clear();
	}
}
