package com.lzkj.aidlservice.util;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Area;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Material;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Program;
import com.lzkj.aidlservice.manager.DownloadManager;
import com.lzkj.aidlservice.util.LogUtils.LogTag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2015年6月25日 上午11:11:59
 * @parameter 解析节目获取里面的数据
 */
public class ParsePrmUitl {
    private static final LogTag TAG_LOG = LogUtils.getLogTag(ParsePrmUitl.class.getSimpleName(), true);
    /**
     * 下载文件
     */
    private static final int DOWNLOAD_FILE = 2;
    /**
     * md5值类型
     **/
    private static final int MD5_TYPE = 2;
    /**
     * 素材类型
     **/
    private static final int MATERIAL_NAME_TYPE = 1;
    /**
     * 素材url类型
     **/
    private static final int URL_TYPE = 0;

    /**
     * 保存节目并且下载素材
     *
     * @param programList
     */
    public static void savePrmAndDownloads(List<Program> programList) {
        if (ConfigSettings.DEFAULT_MAX_FOLW != -1 && checkIsExceedFolw(programList, true)) {
            LogUtils.w(TAG_LOG, "savePrmAndDownloads", "folw exceed.");
            return;
        }
        for (Program program : programList) {
            if (null != program) {
                savePrm(program);
            }
        }
    }

    public static boolean checkIsExceedFolw(List<Program> programList, boolean isDownload) {
        boolean isExceed = false;
        long useFolw = ConfigSettings.getUsedFolw();
        long maxFolw = ConfigSettings.getMaxFolw();
        float currentFileTotal = getFileTotalToProgramList(programList) / 1024f / 1024;
        float newUseFolw = useFolw + currentFileTotal;
        if (newUseFolw > maxFolw) {
            LogUtils.d(TAG_LOG, "checkIsExceedFolw", "newUseFolw: " + newUseFolw);
            //保存当前要下载的节目到本地，等待系统重新设置上限流量
            FileUtile.getInstance().writePrmListToSd(programList);
            isExceed = true;
        } else {
            if (isDownload) {
                SharedUtil.newInstance().setLong(SharedUtil.USE_FOLW_KEY, (long) newUseFolw);
            }
        }
        LogUtils.d(TAG_LOG, "checkIsExceedFolw", "useFolw:" + useFolw + ",newUseFolw : " + newUseFolw
                + "M" + ", currentFileTotal : " + currentFileTotal + ",maxFolw : " + maxFolw + "M");
        return isExceed;
    }

    /**
     * 保存单个节目 并且下载
     *
     * @param program
     */
    public static void savePrm(Program program) {
        if (null != program) {
            try {
                String key = program.getKey();
                SharedUtil.newInstance().setString(SharedUtil.PROGRAM_LIST_ID, key);
                LogUtils.d(TAG_LOG, "savePrm", "key: " + key);
                DownloadManager.newInstance().addDownloadList(parsePrmToType(program, URL_TYPE), DOWNLOAD_FILE, key);
                FileUtile.getInstance().writePrm(key, program);//将节目写入到SD卡指定的目录
                Thread.sleep(Constant.RETRY_SLEEP_TIME);//休眠2秒钟
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            LogUtils.d(TAG_LOG, "savePrm", "program is null.. ");
        }
    }

    /**
     * @param programList
     * @return 获取节目列表所有的节目素材总大小
     */
    private static long getFileTotalToProgramList(List<Program> programList) {
        long fileTotal = 0;
        for (Program program : programList) {
            fileTotal += getProgramFileTotal(program);
        }
        return fileTotal;
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
        if (ListUitl.isNotEmpty(areas)) {
            for (Area area : areas) {
                if (null != area) {
                    List<Material> materials = area.getMas();
                    if (ListUitl.isNotEmpty(materials)) {
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
        materialNameList.addAll(parsePrmToType(program, MATERIAL_NAME_TYPE));
        return materialNameList;
    }

    /**
     * 获取节目里面所有素材md值
     *
     * @return 素材md5列表
     */
    public static List<String> getPrmMaterialMD5List(Program program) {
        List<String> materialMd5List = new ArrayList<String>();
        if (isExistBackgroundPic(program)) {
            materialMd5List.add(program.getBi().getM());
        }
        materialMd5List.addAll(parsePrmToType(program, MD5_TYPE));
        return materialMd5List;
    }

    /**
     * 解析节目根据传入的类型返回不同的列表
     *
     * @param program
     * @param type
     * @return
     */
    private static List<String> parsePrmToType(Program program, int type) {
        List<String> files = new ArrayList<String>();
        if (URL_TYPE == type) {
            // 判断节目是否添加了背景图片
            if (isExistBackgroundPic(program)) {
                files.add(program.getBi().getU());
            }
            // 判断节目是否添加了背景音乐
            String url = isExistBackgroundMusic(program);
            if (null != url) {
                files.add(url);
            }
        }
        addMaterialInfo(parsePrmToMaterials(program), type, files);
        return files;
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

    /**
     * 是否存在背景音乐
     *
     * @param program 节目
     * @return 返回背景音乐url, 返回null表示没有背景音乐
     */
    private static String isExistBackgroundMusic(Program program) {
        Material material = program.getBgm();
        if (null != material) {
            String backgroundPicUrl = material.getU();
            String backgroundPicMd5 = material.getM();
            if (!StringUtil.isNullStr(backgroundPicUrl) && !StringUtil.isNullStr(backgroundPicMd5)) {
                return backgroundPicUrl;
            }
        }
        return null;
    }

    /**
     * 根据不同的类型 添加不同数据到list中
     *
     * @param materials 素材集合
     * @param type      类型(MD5_TYPE, MATERIAL_NAME_TYPE, URL_TYPE)
     * @param files     添加数据的list
     */
    private static void addMaterialInfo(List<Material> materials, int type, List<String> files) {
        switch (type) {
            case MD5_TYPE:
                addMaterialInfoMd5(materials, files);
                break;
            case MATERIAL_NAME_TYPE:
                addMaterialInfoFileName(materials, files);
                break;
            case URL_TYPE:
                addMaterialInfoURL(materials, files);
                break;
            default:
                break;
        }
    }

    /**
     * 添加素材的md5
     *
     * @param materials 素材列表
     * @param files     添加数据的list
     */
    private static void addMaterialInfoMd5(List<Material> materials, List<String> files) {
        for (Material material : materials) {
            if (null != material) {
                files.add(material.getM());
            }
        }
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
     * 添加素材的文件名
     *
     * @param materials 素材列表
     * @param files     添加数据的list
     */
    private static void addMaterialInfoFileName(List<Material> materials, List<String> files) {
        for (Material material : materials) {
            if (null != material && null != material.getU()) {
                files.add(FileUtile.getFileName(material.getU()));
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
        Material backgroundMusicMaterial = program.getBgm();//背景音乐
        if (null != backgroundMusicMaterial) {
            prmMaterials.add(backgroundMusicMaterial);
        }
        Material backgroundPicMaterial = program.getBi();//背景图
        if (null != backgroundPicMaterial) {
            prmMaterials.add(backgroundPicMaterial);
        }
        List<Area> areas = program.getAs();
        LogUtils.d(TAG_LOG, "getPrmMaterialNameList", "key = " + areas == null ? "area is null.." : "" + areas.size());
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
}
