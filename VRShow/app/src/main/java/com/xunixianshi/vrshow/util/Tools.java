package com.xunixianshi.vrshow.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;

public class Tools {
	
	/**
	 * 由文件生成Bitmap，指定尺寸，避免分辨率过大内存溢�?
	 * @param dst
	 * @param width
	 * @param height
	 * @return
	 */
    public static Bitmap getBitmapFromFile(File dst, int width, int height) {  
        if (null != dst && dst.exists()) {  
            BitmapFactory.Options opts = null;  
            if (width > 0 && height > 0) {  
                opts = new BitmapFactory.Options();  
                opts.inJustDecodeBounds = true;  
                BitmapFactory.decodeFile(dst.getPath(), opts);  
                // 计算图片缩放比例  
                final int minSideLength = Math.min(width, height);  
                opts.inSampleSize = computeSampleSize(opts, minSideLength,  
                        width * height);  
                opts.inJustDecodeBounds = false;  
                opts.inInputShareable = true;  
                opts.inPurgeable = true;  
            }  
            try {  
                return BitmapFactory.decodeFile(dst.getPath(), opts);  
            } catch (OutOfMemoryError e) {  
                e.printStackTrace();  
            }  
        }  
        return null;  
    }  

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
	//===============由文件生成Bitmap，指定尺寸，避免分辨率过大内存溢�?===================
	
	/**
     * 获取圆角位图的方�?
     * @param bitmap �?��转化成圆角的位图
     * @param pixels 圆角的度数，数�?越大，圆角越�?
     * @return 处理后的圆角位图
     */
    public static Bitmap getRoundCornerBitmap(Bitmap bitmap, int pixels) {
    	Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
    			bitmap.getHeight(), Config.ARGB_8888);
    	Canvas canvas = new Canvas(output);
    	final int color = 0xff424242;
    	final Paint paint = new Paint();
    	final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    	final RectF rectF = new RectF(rect);
    	final float roundPx = pixels;
    	paint.setAntiAlias(true);
    	canvas.drawARGB(0, 0, 0, 0);
    	paint.setColor(color);
    	canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
    	paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
    	canvas.drawBitmap(bitmap, rect, rect, paint);
    	return output;
    }
    
    /**
     * 将Bitmap保存到指定文�?
     * @param bmp
     * @throws IOException
     */
    public static void saveMyBitmap(Bitmap bmp, File file) throws Exception {
        file.createNewFile();
        FileOutputStream fos = null;
        fos = new FileOutputStream(file);
        bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
    }
}