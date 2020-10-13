package com.lzkj.downloadservice.util;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Area;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Material;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Program;
import com.lzkj.downloadservice.util.LogUtils.LogTag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2015年7月3日 下午3:07:51
 * @parameter 获取素材的hash值
 */
public class PrmFileHashUtil {
    private static final LogTag TAG = LogUtils.getLogTag(PrmFileHashUtil.class.getSimpleName(), true);
    private static Map<String, String> fileHashMap = new HashMap<String, String>();
    private static PrmFileHashUtil prmFileHashUtil = null;

    private PrmFileHashUtil() {
    }

    public static PrmFileHashUtil get() {
        if (null == prmFileHashUtil) {
            prmFileHashUtil = new PrmFileHashUtil();
        }
        return prmFileHashUtil;
    }

    /**
     * 初始化获取节目里文件的hash值
     */
    public void init(String prmId) {
        String prmFilePath = FileUtil.getInstance().getPrmFolder() + File.separator + prmId + ".temp";
        LogUtils.d(TAG, "init", "prmFile:" + prmFilePath);
        File folderFile = new File(prmFilePath);
        if (folderFile.exists()) {
            addPrmHashToFile(folderFile);
        } else {
            prmFilePath = FileUtil.getInstance().getPrmFolder() + File.separator + prmId + ".prm";
            LogUtils.d(TAG, "else", "prmFile:" + prmFilePath);
            File prmFile = new File(prmFilePath);
            if (prmFile.exists()) {
                addPrmHashToFile(prmFile);
            }
        }
    }

    /**
     * 添加所有节目的hash
     */
    public void addAllPrmHash() {
        File prmFolder = new File(FileUtil.getInstance().getPrmFolder());
        File[] subFiles = prmFolder.listFiles();
        if (null == subFiles || subFiles.length == 0) {
            return;
        }
        for (File prmFile : subFiles) {
            addPrmHashToFile(prmFile);
        }
    }

    /**
     * 根据文件读取hash值添加
     *
     * @param prmFile
     */
    private void addPrmHashToFile(File prmFile) {
        try {
            BufferedReader buf = new BufferedReader(new FileReader(prmFile));
            StringBuffer buffer = new StringBuffer();
            String temp = null;
            while ((temp = buf.readLine()) != null) {
                buffer.append(temp);
            }
            buf.close();
            String json = buffer.toString();
            LogUtils.d(TAG, "init", "addPrmHashToFile json: " + json);
            Program program = JSON.parseObject(json, Program.class);
            getAllPrmFileInfo(program);
//			 deleteInvalidFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除无效的文件
     */
    private void deleteInvalidFile() {
        File picFolder = new File(FileUtil.getInstance().getPicFolder());
        File videoFolder = new File(FileUtil.getInstance().getVideoFolder());
        if (picFolder.exists()) {
            File picFiles[] = picFolder.listFiles();
            for (File picFile : picFiles) {
                String picName = picFile.getName();
                if (!fileHashMap.containsKey(picName)) {
                    picFile.delete();
                }
            }
        }
        if (videoFolder.exists()) {
            File videoFiles[] = videoFolder.listFiles();
            for (File videoFile : videoFiles) {
                String videoName = videoFile.getName();
                if (!fileHashMap.containsKey(videoName)) {
                    videoFile.delete();
                }
            }
        }
    }

    /**
     * 获取节目列表中所有的节目素材
     *
     * @return
     */
    private void getAllPrmFileInfo(Program program) {
        if (isExistBackgroundPic(program)) {
            fileHashMap.put(FileUtil.getFileName(program.getBi().getU()), program.getBi().getM());
        }
        if (isExistBackgroundMusic(program)) {
            fileHashMap.put(FileUtil.getFileName(program.getBgm().getU()), program.getBgm().getM());
        }
        List<Area> areaList = program.getAs();
        if (null != areaList && !areaList.isEmpty()) {
            for (Area a : areaList) {
                List<Material> mas = a.getMas();
                if (null != mas && !mas.isEmpty()) {
                    for (Material material : mas) {
                        fileHashMap.put(FileUtil.getFileName(material.getU()), material.getM());
//						fileHashMap.put(m.getN(), m.getM());
                    }
                }
            }
        }
    }

    /**
     * 是否存在背景音乐
     *
     * @param program 节目
     * @return true表示添加了, false表示未添加
     */
    private static boolean isExistBackgroundMusic(Program program) {
        Material material = program.getBgm();
        if (null != material) {
            String backgroundPicUrl = material.getU();
            String backgroundPicMd5 = material.getM();
            if (!TextUtils.isEmpty(backgroundPicUrl) && !TextUtils.isEmpty(backgroundPicMd5)) {
                return true;
            }
        }
        return false;
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
            if (!TextUtils.isEmpty(backgroundPicUrl) && !TextUtils.isEmpty(backgroundPicMd5)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据文件名获取hash值
     *
     * @param fileName 文件名
     * @return 返回hash值
     */
    public String getHash(String fileName) {
        if (fileHashMap.containsKey(fileName)) {
            LogUtils.d(TAG, "getHash", "fileName exists:" + fileName);
            return fileHashMap.get(fileName);
        } else {
            LogUtils.d(TAG, "getHash", "fileName not exists:" + fileName);
            return null;
        }
    }

    /**
     * 清除缓存
     */
    public static void clearCache() {
        fileHashMap.clear();
    }

    /**
     * 添加hash值和文件名
     *
     * @param fileName
     * @param hashMd
     */
    public void addHash(String fileName, String hashMd) {
        if (null != fileName && null != hashMd) {
            fileHashMap.put(fileName, hashMd);
        }
    }
}
