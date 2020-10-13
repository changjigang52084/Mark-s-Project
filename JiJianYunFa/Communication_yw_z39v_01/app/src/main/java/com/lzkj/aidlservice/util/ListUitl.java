package com.lzkj.aidlservice.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Program;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年4月25日 上午11:22:14 
 * @version 1.0 
 * @parameter  list工具类
 */
public class ListUitl {
	/**
	 * 判断list是否为空，返回true表示空
	 * @param list
	 * @return 返回true表示空
	 */
	 public static <E> boolean isEmpty(List<E> list) {
		 return null == list || list.isEmpty();
	 }
	 
	 /**
	  * 判断list是否不为空，返回true表示不为空
	  * @param list
	  * @return 返回true表示不空
	  */
	 public static <E> boolean isNotEmpty(List<E> list) {
		 return null != list && !list.isEmpty();
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
}
