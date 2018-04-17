package com.gen.common.dict;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DictionaryUtils {

	private static Map<String, Dictionary[]> cache = new ConcurrentHashMap<String, Dictionary[]>();

	/**
	 * 获得所有枚举类型
	 * 
	 * @param clazz
	 * @return
	 */
	public static Dictionary[] getDictionaries(Class<?> clazz) {
		Dictionary[] dictionaries = null;
		if (null != clazz) {
			dictionaries = (Dictionary[]) clazz.getEnumConstants();
		}
		return dictionaries;
	}

	/**
	 * 根据枚举类名获得所有该类下的所有枚举类型
	 * 
	 * @param className
	 *            枚举类名
	 * @return
	 */
	public static synchronized Dictionary[] getDictionaries(String className) {
		// 简单的缓存
		if (cache.containsKey(className)) {
			Dictionary[] ds = cache.get(className);
			if (null != ds && ds.length > 0) {
				return ds;
			}
		}
		
		Dictionary[] dictionaries = null;
		Class<?> clazz = getDictClass(className);
		if (null != clazz) {
			dictionaries = (Dictionary[]) clazz.getEnumConstants();
		}

		if (null != dictionaries) {
			cache.put(className, dictionaries);
		}
		return dictionaries;
	}

	/**
	 * 获得枚举类的全路径
	 * 
	 * @param className
	 *            类名
	 * @return
	 */
	public static String getDictCompleteClassName(String className) {
		return "com.tuandai.common.dict.lib." + className;
	}

	/**
	 * 使用枚举类的类名实例化该类
	 * 
	 * @param className
	 *            枚举类名
	 * @return
	 */
	public static Class<?> getDictClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}