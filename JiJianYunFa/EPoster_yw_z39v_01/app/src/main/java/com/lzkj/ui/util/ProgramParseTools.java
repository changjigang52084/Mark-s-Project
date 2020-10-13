package com.lzkj.ui.util;

import android.text.TextPaint;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Area;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Material;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Program;
import com.lzkj.ui.app.EPosterApp;
import com.lzkj.ui.fragment.FragmentText;
import com.lzkj.ui.fragment.view.MarqueeTextView;
import com.lzkj.ui.log.LogManager;
import com.lzkj.ui.util.LogUtils.LogTag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author kchang changkai@lz-mr.com
 * @Description:节目解析工具类
 * @time:2016年3月21日 下午5:15:21
 */
public class ProgramParseTools {
    private static final LogTag TAG = LogUtils.getLogTag(ProgramParseTools.class.getSimpleName(), true);

    /**
     * 获取本地所有节目
     *
     * @return 返回所有节目对象列表
     */
    private static List<Program> getLocalProgramList() {
        File prmFolder = new File(FileStore.getInstance().getLayoutFolderPath());
        File[] files = prmFolder.listFiles();
        if (null != files && files.length > 0) {
            List<Program> programList = new ArrayList<Program>();
            for (File file : files) {
                //Method: getLocalProgramList, Message: File name is :0932baee9d234b92875d497eb9ef4156.prm
                //Method: getLocalProgramList, Message: File name is :5aebcd17416a4caba18f3981da0a9c3f.prm
                LogUtils.d(TAG, "getLocalProgramList", "File name is :" + file.getName());
                if (!file.getName().endsWith(FileStore.PRM_SUFFIX_NAME)) {
                    LogUtils.w(TAG, "getLocalProgramList", "prm suffix error file name is :" + file.getName());
                    continue;
                }
                // /mnt/internal_sd/mallposter/prm/0932baee9d234b92875d497eb9ef4156.prm
                // /mnt/internal_sd/mallposter/prm/5aebcd17416a4caba18f3981da0a9c3f.prm
                String prmJson = getProgramJsonToProgramPath(file.getAbsolutePath());
                try {
                    if (StringUtil.isNullStr(prmJson)) {
                        String delMsg = "Eposter (getLocalProgramList) Del program is null file path : " + file.getAbsolutePath()
                                + "_" + DateTimeUtil.getStringTimeToFormat("yyyy_MM_dd_HH_mm_ss");
                        LogManager.get().insertOperationMessage(delMsg);
                        file.delete();//节目文件错误则删除
                        continue;
                    }
                    // 将节目json字符串转化成节目对象
                    LogUtils.d(TAG, "getLocalProgramList", "prmJson = " + prmJson);
                    Program program = JSON.parseObject(prmJson, Program.class);
                    if (program == null) {
                        LogUtils.w(TAG, "getLocalProgramList", "Program parse error: program is null." + file.getName());
                        continue;
                    }
                    if (checkProgramIsNotMaterial(program)) {
                        // 将节目对象添加到节目对象集合中
                        programList.add(program);
                    }
                } catch (Exception e) {
                    StringBuffer buffer = new StringBuffer(6);
                    buffer.append("Del program parse error file path : ");
                    buffer.append(file.getAbsolutePath());
                    buffer.append(", error msg : ");
                    buffer.append(e.getMessage());
                    buffer.append("_");
                    buffer.append(DateTimeUtil.getStringTimeToFormat("yyyy_MM_dd_HH_mm_ss"));
                    LogManager.get().insertOperationMessage(buffer.toString());

                    file.delete();//节目文件错误则删除
                    LogUtils.w(TAG, "getLocalProgramList", "Program parse error:" + e.getMessage());
                    continue;
                }
            }
            return programList;
        } else {
            LogUtils.w(TAG, "getLocalProgramList", "No local program files.");
        }
        return null;
    }

    /**
     * 验证节目是否有素材
     *
     * @return true表示有素材，false表示没有素材
     */
    private static boolean checkProgramIsNotMaterial(Program program) {
        boolean isNotMaterial = false;
        List<Area> areas = program.getAs();
        if (null == areas || areas.isEmpty()) {
            return isNotMaterial;
        }
        if (1 == areas.size()) {
            Area area = areas.get(0);
            if (null == area) {
                isNotMaterial = false;
            } else {
                List<Material> materials = area.getMas();
                if (null == materials || materials.isEmpty()) {
                    isNotMaterial = false;
                } else {
                    isNotMaterial = true;
                }
            }
        } else {
            isNotMaterial = true;
        }
        return isNotMaterial;
    }

    /**
     * 获取当天节目列表
     *
     * @return 返回当天节目列表
     */
    public static List<Program> getToDayProgramList() {
        // 获取本地节目列表清单 保存到节目对象的集合里面
        List<Program> localProgramList = getLocalProgramList();
        // 今天要播放的节目清单
        List<Program> toDayProgramList = new ArrayList<Program>();
        if (null == localProgramList || localProgramList.isEmpty()) {
            LogUtils.w(TAG, "getToDayProgramList", "No accord program files.");
            return toDayProgramList;
        }
        //筛选出当天的节目
        for (Program program : localProgramList) {
            //验证节目是否过期 没过其他执行下面的方法
            if (!checkProgrameDateIfOverdue(program)) {
                String key = SharedUtil.newInstance().getString(SharedUtil.KEY_ERROR_PRM);
                LogUtils.d(TAG, "getTodayProgramList", "key : " + key + "\t" + "program.getKey() : " + program.getKey());
                if (!program.getKey().equals(key)) {
                    LogUtils.d(TAG, "getToDayProgramList", "节目的key和本地的key不一样");
                    toDayProgramList.add(program);
                } else {
                    LogUtils.d(TAG, "getToDayProgramList", "节目的key和本地的key一样");
                }
            }
        }
        for (Program program : toDayProgramList) {
            LogUtils.d(TAG, "no sort", "old toDayProgramList: " + program.getDs() + " ,\t");
        }
        //根据节目开始时间进行排序
        Collections.sort(toDayProgramList, new Comparator<Program>() {
            @Override
            public int compare(Program object1, Program object2) {
                LogUtils.d(TAG, "sort", "object1 ds: " + object1.getDs() + " ,object2 ds: " + object2.getDs());
                return object1.getDs().compareTo(object2.getDs());
            }
        });
        for (Program program : toDayProgramList) {
            LogUtils.d(TAG, "sort", "new toDayProgramList: " + program.getDs() + " ,\t");
        }
        LogUtils.d(TAG, "getToDayProgramList", "toDayProgramList.size: " + toDayProgramList.size() + " ,localProgramList size: " + localProgramList.size());
        return toDayProgramList;
    }

    /**
     * 判断节目列表播放的日期是否过期
     *
     * @param program 节目对象
     * @return true表示节目已过期, false表示没过期
     */
    private static boolean checkProgrameDateIfOverdue(Program program) {
        if (null == program) {
            return true;
        }

        long startDate = program.getDs();//开始日期 1519574400000 | 1519574400000
        long endDate = program.getDe();//结束日期 1538236800000 | 1535558400000
        // 获取当前的日期格式为：2018-8-18 转化长整形 ： 1534521600000 | 1534521600000
        long currentDate = DateTimeUtil.getTimeToDate(DateTimeUtil.getStringTimeToFormat(null));
        LogUtils.d(TAG, "checkProgrameIfOverdue", "currentDate:" + currentDate + ",startDate:" + startDate + ",endDate：" + endDate);
        if (currentDate >= startDate && currentDate <= endDate) {//大于开始时间小于结束时间
            LogUtils.d(TAG, "checkProgramIfOverdue", "节目没过期");
            return false;
        }
        LogUtils.d(TAG, "checkProgramIfOverdue", "节目过期");
        return true;
    }

    /**
     * 判断节目是否在播放时间段内
     *
     * @param program 节目对象
     * @return true表示节目已过期, false表示没过期
     */
    public static boolean checkProgrameTimeIfOverdue(Program program) {
        if (null == program) {
            return true;
        }
        long startMillis = DateTimeUtil.getMillisTimeFromStringTime(null, program.getTs());
        long endMillis = DateTimeUtil.getMillisTimeFromStringTime(null, program.getTe());
        long currentMillis = DateTimeUtil.getCurrentMillisTime(null);
        LogUtils.d(TAG, "checkProgrameTimeIfOverdue", "currentTime:"
                + DateTimeUtil.getStringTimeToFormat("HH:mm", currentMillis));
        if (currentMillis >= startMillis && currentMillis <= endMillis) {//大于开始时间小于结束时间
            return false;
        }
        return true;
    }

    /**
     * 根据路径获取json数据
     *
     * @param filePath 节目的路径
     * @return 返回节目的json格式数据
     */
    private static String getProgramJsonToProgramPath(String filePath) {
        try {
            File programFile = new File(filePath);
            if (!programFile.exists()) {
                return null;
            }
            FileInputStream fileInputStream = new FileInputStream(programFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            StringBuffer stringBuffer = new StringBuffer();
            String temp = null;
            while ((temp = bufferedReader.readLine()) != null) {
                stringBuffer.append(temp);
            }
            bufferedReader.close();
            fileInputStream.close();
            return stringBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取节目播放时长
     *
     * @param program 节目对象
     * @return 节目播放时长
     */
    public static int getProgramPlayTime(Program program) {
        if (program == null) {
            LogUtils.w(TAG, "getProgramPlayTime", "Program is null");
            return 0;
        }
        List<Integer> areasPlayTime = new ArrayList<Integer>();//获取节目的各区域的播放时长
        List<Area> areas = program.getAs();
        LogUtils.d(TAG, "getProgramPlayTime", "Area class is : " + Area.class.getSimpleName() + ",Area size: " + program.getAs().size());
        if (null != areas && !areas.isEmpty()) {
            for (Area area : areas) {
                int areaPlayTime = 0;//区域内的素材播放时总长
                List<Material> materials = area.getMas();
                if (null != materials && !materials.isEmpty()) {
                    for (Material material : materials) {
                        if (null != material) {
                            areaPlayTime += material.getD();
                        }
                    }
                } else {
                    LogUtils.w(TAG, "getProgramPlayTime", "null == mas program key:" + program.getKey());
                    areaPlayTime = getTxtPlayTime(area);
                }
                areasPlayTime.add(areaPlayTime);
            }
            return Collections.max(areasPlayTime);
        } else {
            LogUtils.w(TAG, "getProgramPlayTime", "null == areas");
        }
        return 5;
    }

    /**
     * 获取滚动字幕播放的时长
     */
    private static int getTxtPlayTime(Area area) {
        int playTime = 5;
        if (null != area && null != area.getT()) {
            if (Constants.TEXT_FRAGMENT == area.getT() && null != area.getM()) {
                int areaWidth = area.getW();//区域的宽度
                int areaHeight = area.getH();//区域的高度
                int fontSize = getTextSize(areaHeight);

                MarqueeTextView textView = new MarqueeTextView(EPosterApp.getApplication().getApplicationContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(areaWidth, areaHeight);
                textView.setLayoutParams(params);
                textView.setTextSize(fontSize);
                String content = area.getM().getC();
                TextPaint paint = textView.getPaint();
                float contentWidth = paint.measureText(content);
                LogUtils.e(TAG, "getTxtPlayTime", "contentWidth : " + contentWidth);
                //跑马灯移动速度
                int textSpeed = (1000 / MarqueeTextView.SCROLL_INTERVAL) * FragmentText.MARQUEE_SPEED;
                //跑马灯跑完一圈的时间 = 字幕滚动距离（字幕长度 + 区域长度）/速度  +  偏移量
                playTime = (int) ((contentWidth + area.getW()) / textSpeed);
                playTime = playTime > 5 ? playTime + FragmentText.MARQUEE_SPEED_OFFSET : 5;
                LogUtils.e(TAG, "getTxtPlayTime", "playTime : " + playTime);
//				TextView textView = new TextView(EPosterApp.getApplication().getApplicationContext());
//				LayoutParams params = new LayoutParams(areaWidth, areaHeight);
//				textView.setLayoutParams(params);
//				textView.setTextSize(fontSize);
//				String content = area.getM().getC();
//				Rect bounds = new Rect();
//				TextPaint paint;
//				paint = textView.getPaint();
//				paint.getTextBounds(content, 0, content.length(), bounds);
//				int width = bounds.width();
//				int fontWidth = width / content.length();//每个字的宽度
//				LogUtils.d(TAG, "getTxtPlayTime", "fontWidth : " + fontWidth + ", bounds.width : " + width);
//				if (width > areaWidth) {
//					int surplusWidth = width - areaWidth;
//					LogUtils.d(TAG, "getTxtPlayTime", "surplusWidth : " + surplusWidth + ", date : " + (surplusWidth / fontWidth) * 3);
//					return (surplusWidth / fontWidth) * 3;//获取每个字符滚动五秒钟
//				}
            }
        }
        return playTime;
    }

    /**
     * 获取滚动文字字体大小
     */
    public static int getTextSize(int areaH) {
        int testSize = 0;
        if (areaH > 0) {
            LogUtils.e(TAG, "getTextSize", "Area height:" + areaH);
            testSize = (int) (areaH * FragmentText.AREA_TEXTSIZE_SCALE_COEFFICIENT);
            testSize = testSize > FragmentText.MAX_MARQUEE_TEXTSIZE ?
                    FragmentText.MAX_MARQUEE_TEXTSIZE : testSize;
            LogUtils.e(TAG, "getTextSize", "Text size:" + testSize);
        }
        return testSize;
    }

    /**
     * 获取节目播放时间点
     *
     * @param program 节目对象
     * @return 节目播放时长
     */
    public static String getProgramStartTime(Program program) {
        if (program == null) {
            LogUtils.w(TAG, "getProgramStartTime", "Program is null");
            return null;
        }
        StringBuffer startTimeSb = new StringBuffer(3);
        startTimeSb.append(DateTimeUtil.getStringTimeToFormat(null));
        startTimeSb.append(" ");
        startTimeSb.append(program.getTs());
        return startTimeSb.toString();
    }

    /**
     * 获取节目播放时间点
     *
     * @param program 节目对象
     * @return 节目播放时长
     */
    public static String getProgramEndTime(Program program) {
        if (program == null) {
            LogUtils.w(TAG, "getProgramStartTime", "Program is null");
            return null;
        }
        StringBuffer endTimeSb = new StringBuffer(3);
        endTimeSb.append(DateTimeUtil.getStringTimeToFormat(null));
        endTimeSb.append(" ");
        endTimeSb.append(program.getTe());
        return endTimeSb.toString();
    }
}
