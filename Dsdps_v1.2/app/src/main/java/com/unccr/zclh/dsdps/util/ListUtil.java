package com.unccr.zclh.dsdps.util;

import android.util.Log;

import com.unccr.zclh.dsdps.models.Program;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @data 创建时间：2019年10月09日 下午4:47:33
 * @parameter VideoPicFragment
 */
public class ListUtil {

    /**
     * 判断list是否为空，返回true表示空
     * @param list
     * @return 返回true表示空
     */
    public static <E> boolean isEmpty(List<E> list) {
        if (null == list || list.isEmpty()) {
            return true;
        }
        return false;
    }
    /**
     * 判断list是否不为空，返回true表示不为空
     * @param list
     * @return 返回true表示不空
     */
    public static <E> boolean isNotEmpty(List<E> list) {
        if (null != list && !list.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * @param <T>
     * @param isEqualsList 是否为相同(true表示筛选出相同的)
     * @param filterList 筛选的列表
     * @param filteredList 被筛选的列表
     * @param cls 筛选的对象类
     * @param methodName  筛选的对象类的方法名
     * @param methodPramCls 筛选的对象类的方法的参数类型
     * @param methodParam  筛选的对象类的方法的传入参数
     * @return 获取筛2个列表相同的数据或者是不相同的数据
     */
    public static <T> List<T> getFilterProgramList(boolean isEqualsList, List<T> filterList, List<T> filteredList,
                                                   Class<T> cls, String methodName, Class<?> methodPramCls, Object... methodParam) {
        List<T> differenceList = new ArrayList<T>();
        if (null == filterList || null == filteredList || null == methodName || null == cls) {
            return differenceList;
        }
        try {
            for (T filter : filterList) {
                Program obj = (Program)filter;
                String methodReturnValue = obj.getKey();
//					Object obj = (Object)filter;
//					Method method = cls.getMethod(methodName, methodPramCls);
//					String methodReturnValue = (String)method.invoke(obj, methodParam);
                boolean isNotEques = false;
                for (T filtered : filteredList) {
                    Program objt = (Program)filtered;
                    String methodReturnValueT = objt.getKey();
//						Object objt = (Object)filtered;
//						Method methodt = cls.getMethod(methodName, methodPramCls);
//						String methodReturnValueT = (String)methodt.invoke(objt, methodParam);
                    if (isEqualsList) {//筛选出相等的
                        if (methodReturnValue.equals(methodReturnValueT)) {
                            differenceList.add(filter);
                            continue;
                        }
                    }
                    if (!isEqualsList && methodReturnValue.equals(methodReturnValueT)) {
                        isNotEques = true;
                    }
                }
                if (!isEqualsList && !isNotEques) {//筛选出不相等的
                    differenceList.add(filter);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return differenceList;
    }

    /**
     *  获取需要下载的节目url
     * @param filterList
     * @return
     */
    public static ArrayList<String> getFilterProgrammeList(List<String> filterList) {
        ArrayList<String> differenceList = new ArrayList<String>();
        if (null == filterList) {
            return differenceList;
        }
        ArrayList<String> localProgrammeList = getLocalProgrammeList(filterList);
        try {
            for (String filter : filterList) {
                boolean isNotEques = false;
                String fileName = FileStore.getFileName(filter);
                Log.d(TAG,"getFilterProgrammeList fileName: " + fileName);
                for (String filtered : localProgrammeList) {
                    if (fileName.substring(0,fileName.indexOf(".")).equals(filtered.substring(0,filtered.indexOf(".")))) {
                        isNotEques = true;
                    }
                }
                Log.d(TAG,"getFilterProgrammeList isNotEques: " + isNotEques);
                if (!isNotEques) {//筛选出不相等的
                    Log.d(TAG,"getFilterProgrammeList filter: " + filter);
                    differenceList.add(filter);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return differenceList;
    }

    private static final String TAG = "ListUtil";

    private static ArrayList<String> getLocalProgrammeList(List<String> filterList) {
        ArrayList<String> localProgrammeList = new ArrayList<>();
        boolean isPicProgram = false;
        File programmeFold = null;
        //首先读取节目文件目录里面所有的节目
        for (String filter : filterList) {
            Log.d(TAG,"getLocalProgrammeList filter: " + filter);
            String suffix = FileUtil.getSuffix(filter).trim().toLowerCase();
            Log.d(TAG,"getLocalProgrammeList suffix: " + suffix);
            if (FileUtil.getInstance().suffixToProgrammeFolder.containsKey(suffix)) {
                isPicProgram = true;
            }
        }
        if (isPicProgram) {
            Log.d(TAG,"getLocalProgrammeList picFold: " + FileStore.getInstance().getImagePrmFolderPath());
            programmeFold = new File(FileStore.getInstance().getImagePrmFolderPath());//本地图片目录
        }else{
            Log.d(TAG,"getLocalProgrammeList videoFold: " + FileStore.getInstance().getVideoPrmFolderPath());
            programmeFold = new File(FileStore.getInstance().getVideoPrmFolderPath());//本地视频目录
        }
        File[] programFileList = programmeFold.listFiles();
        if (programFileList == null || programFileList.length == 0) {
            return localProgrammeList;
        }
        for (File programFile : programFileList) {
            Log.d(TAG,"getLocalProgrammeList programFileName: " + programFile.getName());
            localProgrammeList.add(programFile.getName());
        }
        return localProgrammeList;
    }

}
