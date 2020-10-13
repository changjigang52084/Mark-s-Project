package com.unccr.zclh.dsdps.util;

import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.fragment.FragmentText;
import com.unccr.zclh.dsdps.fragment.view.MarqueeTextView;
import com.unccr.zclh.dsdps.models.Area;
import com.unccr.zclh.dsdps.models.Material;
import com.unccr.zclh.dsdps.models.Program;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @version 1.0
 * @data 创建时间：2019年10月09日 上午10:57:12
 * @parameter ProgramParseTools 节目解析工具类
 */
public class ProgramParseTools {

    private static final String TAG = "ProgramParseTools";

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
                if (!file.getName().endsWith(FileStore.PRM_SUFFIX_NAME)) {
                    continue;
                }
                String prmJson = getProgramJsonToProgramPath(file.getAbsolutePath());
                Log.d(TAG, "prmJson: " + prmJson);
                try {
                    if (StringUtil.isNullStr(prmJson)) {
                        String delMsg = "Eposter (getLocalProgramList) Del program is null file path : "
                                + file.getAbsolutePath()
                                + "_" + DateTimeUtil.getStringTimeToFormat("yyyy_MM_dd_HH_mm_ss");
                        file.delete();//节目文件错误则删除
                        continue;
                    }
                    Program program = JSON.parseObject(prmJson, Program.class);
                    if (program == null) {
                        continue;
                    }
//                    if (checkProgramIsNotMaterial(program)) {
//                        // 将节目对象添加到节目对象集合中
//                        programList.add(program);
//                    }
                    programList.add(program);
                } catch (Exception e) {
                    Log.d(TAG, "getLocalProgramList e: " + e.getMessage());
                    StringBuffer buffer = new StringBuffer(6);
                    buffer.append("Del program parse error file path : ");
                    buffer.append(file.getAbsolutePath());
                    buffer.append(", error msg : ");
                    buffer.append(e.getMessage());
                    buffer.append("_");
                    buffer.append(DateTimeUtil.getStringTimeToFormat("yyyy_MM_dd_HH_mm_ss"));

                    file.delete();//节目文件错误则删除
                    continue;
                }
            }
            return programList;
        } else {
            Log.d(TAG, "getLocalProgramList file is null or file <= 0.");
        }
        return null;
    }

    /**
     * 获取本地所有素材名称列表
     *
     * @return 返回本地所有节目素材名称列表
     */
    public static List<String> getAllMaterialName() {
        List<String> materialNames = new ArrayList<String>();
        List<Program> programList = getLocalProgramList();
        for (Program program : programList) {
            List<Area> areas = program.getAs();
            if (null != areas && !areas.isEmpty()) {
                for (Area area : areas) {
                    List<Material> materials = area.getMas();
                    if (null != materials && !materials.isEmpty()) {
                        for (Material material : materials) {
                            if (null != material) {
                                String fileName = FileUtil.getFileName(material.getU());
//								 String fileName = material.getN();
                                if (!TextUtils.isEmpty(fileName)) {
                                    materialNames.add(fileName);
                                }
                            }
                        }
                    }
                }
            }
        }
        return materialNames;
    }

    private static String checkPrmIsExits(String prmId) {
        String tempPrmFile = prmId + FileUtil.TEMP_SUFFIX_NAME;
        String prmFile = prmId + FileUtil.PRM_SUFFIX_NAME;
        File prmFolder = new File(FileUtil.getInstance().getPrmFolder(), tempPrmFile);
        if (!prmFolder.exists()) {//
            prmFolder = new File(FileUtil.getInstance().getPrmFolder(), prmFile);
            if (!prmFolder.exists()) {
                return null;
            }
        }
        return prmFolder.getAbsolutePath();
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
            Log.d(TAG, "getToDayProgramList localProgramList is null.");
            return toDayProgramList;
        }
        //筛选出当天的节目
        for (Program program : localProgramList) {
            //验证节目是否过期 没过期则执行下面的方法
            boolean dateIsOverdue = checkProgrameDateIfOverdue(program);
            Log.d(TAG, "getToDayProgramList dateIsOverdue: " + dateIsOverdue);
            if (!dateIsOverdue) {
                String key = SharedUtil.newInstance().getString(SharedUtil.KEY_ERROR_PRM);
                Log.d(TAG, "getToDayProgramList key: " + key);
                if (!program.getKey().equals(key)) {
                    toDayProgramList.add(program);
                }
            }
        }
        //根据节目开始时间进行排序
        Collections.sort(toDayProgramList, new Comparator<Program>() {
            @Override
            public int compare(Program object1, Program object2) {
                return object1.getDs().compareTo(object2.getDs());
            }
        });
        return toDayProgramList;
    }

    /**
     * 获取节目列表（电梯）
     */
    public static ArrayList<String> getProgrammeList(ArrayList<String> programmeNameList) {
        File folder;
        ArrayList<String> playList = new ArrayList<>();
        if (programmeNameList == null || programmeNameList.isEmpty()) {
            return playList;
        }
        for (String programmeName : programmeNameList) {
            String suffix = FileUtil.getSuffix(programmeName).trim().toLowerCase();
            if (FileUtil.getInstance().suffixToProgrammeFolder.containsKey(suffix)) {
                folder = new File(FileStore.getInstance().getImagePrmFolderPath());
                File[] files = folder.listFiles();
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        String fileName = file.getName();
                        if (programmeName.equals(fileName)) {
                            playList.add(FileStore.getInstance().getImagePrmFolderPath()+ File.separator+ programmeName);
                        }
                    }
                }
            }else{
                folder = new File(FileStore.getInstance().getVideoPrmFolderPath());
                File[] files = folder.listFiles();
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        String fileName = file.getName();
                        if (programmeName.equals(fileName)) {
                            playList.add(FileStore.getInstance().getVideoPrmFolderPath()+ File.separator+ programmeName);
                        }
                    }
                }
            }
        }
        return playList;
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
        if (currentDate >= startDate && currentDate <= endDate) {//大于开始时间小于结束时间
            return false;
        }
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
            return 0;
        }
        List<Integer> areasPlayTime = new ArrayList<Integer>();//获取节目的各区域的播放时长
        List<Area> areas = program.getAs();
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
                    areaPlayTime = getTxtPlayTime(area);
                }
                areasPlayTime.add(areaPlayTime);
            }
            return Collections.max(areasPlayTime);
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

                MarqueeTextView textView = new MarqueeTextView(DsdpsApp.getDsdpsApp().getApplicationContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(areaWidth, areaHeight);
                textView.setLayoutParams(params);
                textView.setTextSize(fontSize);
                String content = area.getM().getC();
                TextPaint paint = textView.getPaint();
                float contentWidth = paint.measureText(content);
                //跑马灯移动速度
                int textSpeed = (1000 / MarqueeTextView.SCROLL_INTERVAL) * FragmentText.MARQUEE_SPEED;
                //跑马灯跑完一圈的时间 = 字幕滚动距离（字幕长度 + 区域长度）/速度  +  偏移量
                playTime = (int) ((contentWidth + area.getW()) / textSpeed);
                playTime = playTime > 5 ? playTime + FragmentText.MARQUEE_SPEED_OFFSET : 5;
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
            testSize = (int) (areaH * FragmentText.AREA_TEXTSIZE_SCALE_COEFFICIENT);
            testSize = testSize > FragmentText.MAX_MARQUEE_TEXTSIZE ?
                    FragmentText.MAX_MARQUEE_TEXTSIZE : testSize;
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
            return null;
        }
        StringBuffer endTimeSb = new StringBuffer(3);
        endTimeSb.append(DateTimeUtil.getStringTimeToFormat(null));
        endTimeSb.append(" ");
        endTimeSb.append(program.getTe());
        return endTimeSb.toString();
    }

}
