package com.unccr.zclh.dsdps.util;

import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.models.Area;
import com.unccr.zclh.dsdps.models.Material;
import com.unccr.zclh.dsdps.models.Program;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ParsePrmUtil {

    private static final String TAG = "ParsePrmUtil";

    /**
     * 保存节目并且下载素材
     *
     * @param programList
     */
    public static void savePrmAndDownloads(List<Program> programList) {
        Log.d(TAG,"savePrmAndDownloads...");
        for (Program program : programList) {
            if (null != program) {
                savePrm(program);
            }
        }
    }

    /**
     * 获取当个节目文件总大小
     *
     * @param program
     * @return
     */
    public static long getProgramFileTotal(Program program) {
        long fileTotal = 0;
        if (null == program) {
            return fileTotal;
        }
        if (null != program.getBgm() && null != program.getBgm().getS()) {
            fileTotal += program.getBgm().getS();
        }

        if (null != program.getBi() && null != program.getBi().getS()) {
            fileTotal += program.getBi().getS();
        }

        List<Area> areas = program.getAs();
        if (ListUtil.isNotEmpty(areas)) {
            for (Area area : areas) {
                if (null != area) {
                    List<Material> materials = area.getMas();
                    if (ListUtil.isNotEmpty(materials)) {
                        for (Material material : materials) {
                            if (null != material) {
                                fileTotal += material.getS();
                            }
                        }
                    }
                }
            }
        }
        return fileTotal;
    }

    /**
     * 保存单个节目 并且下载
     *
     * @param program
     */
    public static void savePrm(Program program) {
        if (null != program) {
            try {
                final String key = program.getKey();
                SharedUtil.newInstance().setString(SharedUtil.PROGRAM_LIST_ID, key);
                SharedUtil.newInstance().setString(SharedUtil.PROGRAM_SOURCE_TYPE, "program");
                Log.d(TAG, "savePrm key: " + key);
                final List<String> materialList = parsePrmToType(program);
                int materialListSize = materialList.size();
                Log.d(TAG,"savePrm materialList size: " + materialListSize);
                if(materialListSize>0){
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            intent.setAction(Constants.DOWNLOAD_BROADCAST);
                            intent.putExtra("key",key);
                            intent.putStringArrayListExtra("materialList", (ArrayList<String>) materialList);
                            DsdpsApp.getDsdpsApp().sendBroadcast(intent);
                        }
                    },5000);
                }

                FileUtile.getInstance().writePrm(key, program);//将节目写入到SD卡指定的目录
                Thread.sleep(Constants.RETRY_SLEEP_TIME);//休眠2秒钟
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解析节目根据传入的类型返回不同的列表
     *
     * @param program
     * @return
     */
    private static List<String> parsePrmToType(Program program) {
        List<String> files = new ArrayList<String>();
        addMaterialInfo(parsePrmToMaterials(program), files);
        return files;
    }

    /**
     * 根据不同的类型 添加不同数据到list中
     *
     * @param materials 素材集合
     * @param files     添加数据的list
     */
    private static void addMaterialInfo(List<Material> materials, List<String> files) {
        addMaterialInfoURL(materials, files);
    }

    /**
     * 添加素材的url地址
     *
     * @param materials 素材列表
     * @param files     添加数据的list
     */
    private static void addMaterialInfoURL(List<Material> materials, List<String> files) {
        for (Material material : materials) {
            if (null != material && null != material.getU()) {
                files.add(material.getU());
            }
        }
    }

    /**
     * 获取节目里面所有素材对象列表
     *
     * @return 素材对象列表
     */
    public static List<Material> parsePrmToMaterials(Program program) {
        List<Material> prmMaterials = new ArrayList<Material>();
        List<Area> areas = program.getAs();
        if (null != areas) {
            for (Area area : areas) {
                if (null != area) {
                    // 获取素材对象
                    List<Material> materials = area.getMas();
                    if (null != materials && !materials.isEmpty()) {
                        // 素材对象集合里面添加素材对象
                        prmMaterials.addAll(materials);
                    }
                }
            }
        }
        return prmMaterials;
    }

    /**
     * 传入节目路径返回节目对象
     *
     * @param programFilePath 节目的本地路径
     * @return 节目对象
     */
    public static Program getProgramToPath(String programFilePath) {
        Program program = null;
        if (!StringUtil.isNullStr(programFilePath)) {
            File programFile = new File(programFilePath);
            if (programFile.exists()) {
                String programJson = getProgramJsonToProgramPath(programFilePath);
                program = JSON.parseObject(programJson, Program.class);
            }
        }
        return program;
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
     * 获取节目里面所有素材名称
     *
     * @return 素材列表
     */
    public static List<String> getPrmMaterialNameList(Program program) {
        List<String> materialNameList = new ArrayList<String>();
        if (isExistBackgroundPic(program)) {
            materialNameList.add(FileUtile.getFileName(program.getBi().getU()));
        }
        materialNameList.addAll(parsePrmToType(program));
        return materialNameList;
    }

    /**
     * 根据传入的节目判断是节目是否添加了背景图
     *
     * @param program 节目对象
     * @return true表示添加了背景图, false表示未添加
     */
    private static boolean isExistBackgroundPic(Program program) {
        Material material = program.getBi();
        if (null != material) {
            String backgroundPicUrl = material.getU();
            String backgroundPicMd5 = material.getM();
            if (!StringUtil.isNullStr(backgroundPicUrl) && !StringUtil.isNullStr(backgroundPicMd5)) {
                return true;
            }
        }
        return false;
    }

}
