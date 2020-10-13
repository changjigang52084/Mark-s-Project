package com.lzkj.ui.util;

import java.util.List;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年4月25日 上午11:22:14 
 * @version 1.0 
 * @parameter  list
 */
public class ListUitl {
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
}
