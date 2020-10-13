package com.unccr.zclh.dsdps.fragment.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.unccr.zclh.dsdps.models.Material;
import com.unccr.zclh.dsdps.models.Program;
import com.unccr.zclh.dsdps.play.ProgramPlayManager;
import com.unccr.zclh.dsdps.util.BitmapCache;
import com.unccr.zclh.dsdps.util.BitmapUtil;
import com.unccr.zclh.dsdps.util.ConfigSettings;
import com.unccr.zclh.dsdps.util.Constants;
import com.unccr.zclh.dsdps.util.FileStore;
import com.unccr.zclh.dsdps.util.Helper;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PictureView extends ImageView {
    private List<Material> materialList;
    private Material currentMaterial;
    private String filePathName;
    private int index = -1;
    private int size = 0;
    private long startPlayTime = 0;//开始播放时间
    private int width = 0;
    private int height = 0;
    private int recordIndex;//记录播放次数
    private boolean isRecordLog = false;//是否记录日志
    private Handler mHandler = new Handler();

    public PictureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public PictureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PictureView(Context context) {
        super(context);
    }

    /**
     * 设置播放的图片的参数
     */
    public void loadImage(List<Material> materialList) {
        if (null == materialList || materialList.size() == 0) {
            return;
        }
        this.materialList = materialList;
        //根据素材播放顺序进行排序
        Collections.sort(materialList, new Comparator<Material>() {
            @Override
            public int compare(Material object1, Material object2) {
                return object1.getI().compareTo(object2.getI());
            }
        });
        this.size = materialList.size();
        width = ConfigSettings.SCREEN_WIDTH;
        height = ConfigSettings.SCREEN_HEIGHT;
        setScaleType(ScaleType.FIT_XY);
        playPicture();
    }


    /**
     * 执行图片切换
     */
    private void playPicture() {
        recordIndex++;
        if (isRecordLog && materialList.size() >= recordIndex) {
            recordPlayLog();
        }
        getNextPicture();
        isRecordLog = true;
        String picPath = FileStore.getInstance().getImageFilePath(filePathName);
        if (FileStore.getInstance().checkFileLegal(picPath, currentMaterial)) {
            startPlayTime = System.currentTimeMillis();
            setImageBitmap(getBitmapToPath(picPath));
            checkMaterialIsExists(picPath);
            mHandler.postDelayed(switchImgRun, currentMaterial.getD() * 1000);
        } else {
            playPicture();
        }
    }

    /**
     * 判断当前素材是否存在
     */
    private void checkMaterialIsExists(String picPath) {
        File picFile = new File(picPath);
        if (!picFile.exists()) {
            //素材存在则修复下载素材
            Helper.handlerMaterialError(currentMaterial, ProgramPlayManager.getInstance().getCurrentProgram());
        }
    }

    /**
     * 记录播放日志
     */
    private void recordPlayLog() {
        long endPlayTime = System.currentTimeMillis();//结束播放的时间
        Program program = ProgramPlayManager.getInstance().getProgramSchedule().getCurrentProgram();
        if (program == null) {
            return;
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(program.getKey());
        buffer.append(Constants.SPLIT);
        buffer.append(currentMaterial.getKey());
        buffer.append(Constants.SPLIT);
        buffer.append(filePathName);
        buffer.append(Constants.SPLIT);
        buffer.append(startPlayTime);
        buffer.append(Constants.SPLIT);
        buffer.append(endPlayTime);
        buffer.append(Constants.SPLIT);
        buffer.append(currentMaterial.getD());
    }

    private void getNextPicture() {
        nextMaterial();
        currentMaterial = materialList.get(index);
        filePathName = FileStore.getFileName(currentMaterial.getU());
    }

    /**
     * 下一个素材
     */
    private void nextMaterial() {
        index++;
        if (size <= index) {
            index = 0;
        }
    }

    /**
     * 线程定时切换图片
     */
    private Runnable switchImgRun = new Runnable() {
        @Override
        public void run() {
            playPicture();
        }
    };

    /**
     * 获取图片
     */
    private Bitmap getBitmapToPath(String picPath) {
        Bitmap bitmap = BitmapCache.get().getBitmapFromMemCache(picPath);
        if (null == bitmap) {
            bitmap = BitmapUtil.getBitmapWithScale(picPath, width, height, true);
            BitmapCache.get().addBitmapToMemoryCache(picPath, bitmap);
        }
        return bitmap;
    }

    /**
     * 停止播放图片
     **/
    public void onDestry() {
        mHandler.removeCallbacks(switchImgRun);
        recordPlayLog();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}