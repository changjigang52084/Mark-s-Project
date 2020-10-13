package com.lzkj.ui.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Area;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Material;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Program;
import com.bumptech.glide.Glide;
import com.lzkj.ui.app.EPosterApp;
import com.lzkj.ui.util.LogUtils.LogTag;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年3月15日 下午2:34:08 
 * @version 1.0 
 * @parameter  预加载图片资源
 */
public class PreLoadImgTool {
	private static final LogTag TAG = LogUtils.getLogTag(PreLoadImgTool.class.getSimpleName(), true);
//	public static DisplayImageOptions options;
//	public static ImageSize targetImageSize;  
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
//		targetImageSize = new ImageSize(500, 500);  
//		options = new DisplayImageOptions.Builder()
////		.showImageOnLoading(R.drawable.pic_loading)
//		.showImageForEmptyUri(R.drawable.no_photo)
//		.showImageOnFail(R.drawable.no_photo)
//		.cacheInMemory(true)
//		.cacheOnDisc(true)
//		.resetViewBeforeLoading(true)
//		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示  
//		.considerExifParams(true)
//		.bitmapConfig(Bitmap.Config.RGB_565)
//		.build();
	}
	
	/**
	 * 根据要播放的节目提前加载图片素材
	 * @param program
	 * 			节目对象
	 */
	public static void preLoadImgToProgram(final Program program) {
		if (prePrmId.contains(program.getKey())) {
			LogUtils.d(TAG, "preLoadImgToProgram", "pre cache over");
			return;
		}
//		LogUtils.d(TAG, "preLoadImgToProgram", "pre new");
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
	 * @param materials
	 */
	public static void preLoadImgToMaterials(final List<Material> materials) {
			for (Material material : materials) {
				if (Constants.PIC_FRAGMENT == material.getT()) {
					final String picPath = FileStore.getInstance()
							.getImageFilePath(FileStore.getFileName(material.getU()));
//					final String picPath = FileStore.getImageFilePath(material.getN());
					if (cachePrmPicPath.contains(picPath)) {
						LogUtils.d(TAG, "preLoadImgToMaterials", "cachePrmPicPath contains picPath:" + picPath);
						continue;
					}
					if (new File(picPath).exists()) {
						cachePrmPicPath.add(picPath);
						LogUtils.d(TAG, "preLoadImgToMaterials", "cachePrmPicPath add picPath : " + picPath);
						new Thread(new Runnable() {
							@Override
							public void run() {
//								Picasso.with(EPosterApp.getApplication())
//								.load(new File(picPath))
//								.resize(Constant.TARGET_WIDTH, Constant.TARGET_HEIGHT);
//								Glide.with(EPosterApp.getApplication())
//								.load(new File(picPath))
//								.dontAnimate()
//								.diskCacheStrategy(DiskCacheStrategy.SOURCE);
								ifClearMemoryCache();
//								ImageLoader.getInstance().loadImage(Scheme.FILE.wrap(picPath), targetImageSize, options, null);
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
//		int memoryCacheSize = ImageLoader.getInstance().getMemoryCache().keys().size();
//		LogUtils.d(TAG, "ifClearMemoryCache", "memoryCacheSize" + memoryCacheSize);
		if (memoryCacheSize > 50) {
//			ImageLoader.getInstance().clearMemoryCache();
			clearCache();
		}
	}
	
	/**
	 * 清除缓存
	 */
	public static void clearCache() {
		Glide.get(EPosterApp.getApplication()).clearMemory();
//		Glide.get(EPosterApp.getApplication()).clearDiskCache();
		cachePrmPicPath.clear();
		prePrmId.clear();
	}
}
