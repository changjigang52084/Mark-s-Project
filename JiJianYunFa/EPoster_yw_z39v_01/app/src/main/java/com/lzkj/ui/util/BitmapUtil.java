package com.lzkj.ui.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.widget.ImageView;

import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Area;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lzkj.ui.R;
import com.lzkj.ui.app.EPosterApp;
import com.lzkj.ui.fragment.BaseFragment;
import com.lzkj.ui.util.LogUtils.LogTag;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtil {
    private static final LogTag TAG = LogUtils.getLogTag(BitmapUtil.class.getSimpleName(), true);

    /**
     * This method constructs a scaled bitmap if it is the case. We check if the
     * view in which the picture is set is smaller than the original image ,
     * than we should resize it. The resize ratio is an integer and is equal to
     * the sqrt(original_image_size/view_size). The scaled image is saved to be
     * reused later. (the original version is never deleted or modified)
     *
     * @param filePath   the original file path
     * @param viewWidth  the view width
     * @param viewHeight the view height
     * @return the original bitmap or a scaled version of it if the view is much
     * smaller than the original image.
     */
    public static Bitmap getBitmapWithScale(String filePath, int viewWidth, int viewHeight, boolean isSave) {
        long l1 = DateTimeUtil.uptimeMillis();
//		String fileName = getFileNameByPath(filePath);
        if (null == filePath || !new File(filePath).exists()) {
            LogUtils.d(TAG, "getBitmapWithScale ", "file path is null");
            return getBitmapFromResourceWithScale(EPosterApp.getApplication().getResources(),
                    R.drawable.no_photo, viewWidth, viewHeight);
        }
        String fileName = Helper.getFolderNameByPath(filePath);//Add the folder name as the file name
        Bitmap bitmap = null;
        Options opt = new Options();
        opt.inJustDecodeBounds = true;
//		opt.inPreferredConfig = Config.RGB_565;
        opt.inPurgeable = true;
        opt.inPreferQualityOverSpeed = false;
        opt.inDensity = (int) ConfigSettings.SCALED_DENSITY;
        opt.inTargetDensity = (int) ConfigSettings.SCALED_DENSITY;
        BitmapFactory.decodeFile(filePath, opt);
        int requiredScale = calculateInSampleSize(opt, viewWidth, viewHeight);
        long l3 = DateTimeUtil.uptimeMillis();
        LogUtils.timeConsume(TAG, "decode bitmap scale: " + requiredScale + ", filePath： " + filePath, l3 - l1);
        if (requiredScale > 1) {
            // check scaled image path
            String pathScaled = FileStore.getInstance()
                    .getPathForPictureScaled(fileName, requiredScale);
            File file = new File(pathScaled);
            if (file.exists() && file.length() > 0) {
                LogUtils.d(TAG, "getBitmapWithScale", "> 0 load scaled picture: " + pathScaled + ", filePath： " + filePath);
                bitmap = BitmapFactory.decodeFile(pathScaled);
            } else {
                LogUtils.d(TAG, "getBitmapWithScale", "< 0 load scaled picture: " + pathScaled + ", filePath： " + filePath);
                // rescale bitmap
                opt.inJustDecodeBounds = false;
                opt.inScaled = true;
                opt.inSampleSize = requiredScale;
                bitmap = BitmapFactory.decodeFile(filePath, opt);
                if (bitmap != null && isSave) {
                    FileStore.getInstance()
                            .storeScaledImage(bitmap, fileName, requiredScale);
                }
            }
        } else {
            LogUtils.d(TAG, "getBitmapWithScale ", "else bitmap is null filePath: " + filePath);
            // rescale bitmap
            opt.inJustDecodeBounds = false;
            opt.inSampleSize = 1;
            bitmap = BitmapFactory.decodeFile(filePath, opt);
        }
        if (bitmap != null && !bitmap.isRecycled()) {
            LogUtils.d(TAG, "getBitmapWithScale", filePath + ": " + bitmap.getWidth() + ", " + bitmap.getHeight() + ", " + bitmap.getByteCount());
        } else {//如果不存在图片则返回默认的图片
            if (bitmap == null) {
                LogUtils.d(TAG, "getBitmapWithScale ", "bitmap is null filePath ");
            }

            LogUtils.d(TAG, "getBitmapWithScale ", "bitmap is null filePath: " + filePath);
            return getBitmapFromResourceWithScale(EPosterApp.getApplication().getResources(),
                    R.drawable.no_photo, viewWidth, viewHeight);
        }
        long l2 = DateTimeUtil.uptimeMillis();
        LogUtils.timeConsume(TAG, "decode bitmap: " + filePath, l2 - l1);
        return bitmap;
    }


    public static boolean loadPic(ImageView imageView, String picPath, Area area, BaseFragment baseFragment) {
        if (new File(picPath).exists()) {
            try {
                imageView.setTag(imageView.getId(), picPath);
                EPosterApp.getApplication().clearGlideMemory();
                int wh[] = BitmapUtil.getBitmapWHToPath(picPath);

                loadCutPic(area, imageView, picPath);

                if (LayoutUtil.MAX_WH < wh[0] || LayoutUtil.MAX_WH < wh[1]) {
                    loadCutPic(area, imageView, picPath);
                }
                if ((area.getW() == LayoutUtil.getInstance().BASE_WIDTH && LayoutUtil.getInstance().BASE_HEIGHT == area.getH()) ||
                        (area.getW() == LayoutUtil.getInstance().BASE_HEIGHT
                                && LayoutUtil.getInstance().BASE_WIDTH == area.getH())) {
                    loadCutPic(area, imageView, picPath);
                } else {
                    Glide.with(baseFragment)
                            .load(new File(picPath))
//                            .asGif()
                            .asBitmap()//取消GIF播放
                            .dontAnimate()//取消动画
                            .thumbnail(0.1f)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存
                            .into(imageView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    private static void loadCutPic(Area area, ImageView imageView, String picPath) {
        int width = 1080;
        int height = 1920;
        if (area.getW() > area.getH()) {//w > h
            width = 1920;
            height = 1080;
        }
        BitmapUtil.loadImageView(imageView, picPath, width, height);
    }

    /**
     * 异步加载图片
     *
     * @param imageView
     * @param filePath
     * @param viewWidth
     * @param viewHeight
     */
    private static void loadImageView(final ImageView imageView, final String filePath, final int viewWidth, final int viewHeight) {
        imageView.post(new Runnable() {
            @Override
            public void run() {
                if (null != imageView.getDrawable()) {
                    Bitmap oldBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    if (null != oldBitmap && !oldBitmap.isRecycled()) {
//						oldBitmap.recycle();
                        oldBitmap = null;
                        LogUtils.d(TAG, "loadImageView ", "recycled oldBitmap");
                    } else {
                        LogUtils.d(TAG, "loadImageView ", "oldBitmap is recycled");
                    }
                } else {
                    LogUtils.d(TAG, "loadImageView ", "get drawable is null");
                }
                imageView.setImageBitmap(null);
                imageView.setImageDrawable(null);
                Bitmap bitmap = getBitmapWithScale(filePath, viewWidth, viewHeight, true);
//                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                imageView.setImageBitmap(bitmap);
            }
        });
    }


    //使用Bitmap加Matrix来缩放
    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        if (null == bitmap || bitmap.isRecycled()) {
            return null;
        }

        Bitmap bitmapOrg = bitmap;
        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap   
        // matrix.postRotate(45);   
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }

    /**
     * This method constructs a scaled bitmap if it is the case. We check if the
     * view in which the picture is set is smaller than the original image ,
     * than we should resize it. The resize ratio is an integer and is equal to
     * the sqrt(original_image_size/view_size). The scaled image is saved to be
     * reused later. (the original version is never deleted or modified)
     *
     * @param viewWidth  the view width
     * @param viewHeight the view height
     * @return the original bitmap or a scaled version of it if the view is much
     * smaller than the original image.
     */
    public static Bitmap getBitmapFromResourceWithScale(Resources res,
                                                        int resId, int viewWidth, int viewHeight) {
        Options opt = new Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId);
        int requiredScale = calculateInSampleSize(opt, viewWidth, viewHeight);
        opt.inJustDecodeBounds = false;
        opt.inSampleSize = requiredScale;
        opt.inPreferredConfig = Config.RGB_565;
        opt.inPurgeable = true;
        opt.inPreferQualityOverSpeed = false;
        opt.inDensity = (int) ConfigSettings.SCALED_DENSITY;
        opt.inTargetDensity = (int) ConfigSettings.SCALED_DENSITY;
        return BitmapFactory.decodeResource(res, resId);
    }

//	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
//    // Raw height and width of image
//    final int height = options.outHeight;
//    final int width = options.outWidth;
//    int inSampleSize = 1;
//    if (height > reqHeight || width > reqWidth) {
//        final int halfHeight = height / 3;
//        final int halfWidth = width / 3;
//        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
//        // height and width larger than the requested height and width.
//        while ((halfHeight / inSampleSize) > reqHeight
//                && (halfWidth / inSampleSize) > reqWidth) {
//            inSampleSize *= 3;
//        }
//    }
//    return inSampleSize;
//}

    /**
     * Calculate an inSampleSize for use in a {@link Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that is a power of 2 and will result in the final decoded bitmap
     * having a width and height equal to or larger than the requested width and height.
     *
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
//    public static int calculateInSampleSize(BitmapFactory.Options options,
//            int reqWidth, int reqHeight) {
//        // Raw height and width of image
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//        if (height > reqHeight || width > reqWidth) {
//            final int halfHeight = height / 2;
//            final int halfWidth = width / 2;
//            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
//            // height and width larger than the requested height and width.
//            while ((halfHeight / inSampleSize) > reqHeight
//                    && (halfWidth / inSampleSize) > reqWidth) {
//                inSampleSize *= 2;
//            }
//            // This offers some additional logic in case the image has a strange
//            // aspect ratio. For example, a panorama may have a much larger
//            // width than height. In these cases the total pixels might still
//            // end up being too large to fit comfortably in memory, so we should
//            // be more aggressive with sample down the image (=larger inSampleSize).
// 
//            long totalPixels = width * height / inSampleSize;
// 
//            // Anything more than 2x the requested pixels we'll sample down further
//            final long totalReqPixelsCap = reqWidth * reqHeight * 2;
// 
//            while (totalPixels > totalReqPixelsCap) {
//                inSampleSize *= 2;
//                totalPixels /= 2;
//            }
//        }
//        return inSampleSize;
//    }
    public static int calculateInSampleSize(Options opts, int reqWidth, int reqHeight) {
        final int height = opts.outHeight;
        final int width = opts.outWidth;
        int inSampleSize = 1;
        if (reqWidth == 0 && reqHeight > 0) {
            inSampleSize = Math.round((float) height / (float) reqHeight);
        } else if (reqWidth > 0 && reqHeight == 0) {
            inSampleSize = Math.round((float) width / (float) reqWidth);
        } else if (reqWidth == 0 && reqHeight == 0) {
            inSampleSize = 1;
        } else if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger
            // inSampleSize).
            final float totalPixels = width * height;
            // Anything more than 2x the requested pixels we'll sample down
            // further.
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    /**
     * 根据传入的图片路径返回图片的宽高
     *
     * @param filePath 图片的本地路径
     * @return 返回宽[0], 高[1]
     */
    public static int[] getBitmapWHToPath(String filePath) {
        Options opt = new Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opt);
        int h = opt.outHeight;
        int w = opt.outWidth;
        return new int[]{w, h};
    }

    /**
     * 将图片纠正到正确方向
     *
     * @param degree ： 图片被系统旋转的角度
     * @param bitmap ： 需纠正方向的图片
     * @return 纠向后的图片
     */
    public static Bitmap rotateBitmap(int degree, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return bm;
    }

    /**
     * 通过ExifInterface类读取图片文件的被旋转角度
     *
     * @param path 图片文件的路径
     * @return 图片文件的被旋转角度
     */
    public static int readPicDegree(String path) {
        int degree = 0;
        // 读取图片文件信息的类ExifInterface
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        }
        return degree;
    }

    /**
     * 保存bitmap到本地
     *
     * @param srcBitmap
     * @return 返回保存在本地的图片路径
     */
    public static String saveBitmapToSdCard(Bitmap srcBitmap, String savePath) {
        String picPath = savePath;
        if (null == srcBitmap || srcBitmap.isRecycled()) {
            return picPath;
        }
        try {
            if (null == picPath) {
                picPath = FileStore.getInstance()
                        .getShotFolder() + File.separator + System.currentTimeMillis() + ".jpg";
            }
            FileOutputStream fileOutputStream = new FileOutputStream(picPath);
            srcBitmap.compress(CompressFormat.JPEG, 80, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();//43502.92
        }
        return picPath;
    }
}
