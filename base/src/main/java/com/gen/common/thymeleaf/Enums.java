package com.gen.common.thymeleaf;


import com.alibaba.fastjson.JSON;
import com.gen.common.dict.Dictionary;
import com.gen.common.dict.DictionaryUtils;

import java.util.HashMap;
import java.util.Map;

public final class Enums {

	/**
	 * 
	 * @param className
	 *            Enum的类名，如AuditorEnum
	 * @param code
	 *            编码
	 * @return
	 */
	public String text(final String className, Integer code) {
		if (null == className) {
			return "";
		}
		// 如果为空，则设置为最小值，即未定义
		if (null == code) {
			code = Integer.MIN_VALUE;
		}
		Dictionary unknownDic = null;
		Dictionary[] dictionaries = DictionaryUtils.getDictionaries(className);
		for (Dictionary dictionary : dictionaries) {
			if (dictionary.getCode() == code || dictionary.getCode().equals( code)) {
				return dictionary.getName();
			} else if (dictionary.getCode() == Integer.MIN_VALUE) {
				unknownDic = dictionary;
			}
		}
		if (null != unknownDic) {
			return unknownDic.getName();
		}
		return "";
	}
	public String jsArray(String classNames){
		StringBuilder builder=new StringBuilder();
		Map m=new HashMap();
		String[] cs=classNames.split(",");
		String className=null;
		for(String c:cs){
			className=c.replaceAll("^(.*)[.]([^.]+)$","$2");
			Dictionary[] dictionaries = DictionaryUtils.getDictionaries(c);
			for (Dictionary dictionary : dictionaries) {
				m.put(className+dictionary.getCode(),dictionary.getName());

			}
		}
		return JSON.toJSONString(m);

	}
	public String createDictOptions( String className,Integer code){
		StringBuilder builder=new StringBuilder();
		Dictionary[] dictionaries = DictionaryUtils.getDictionaries(className);
		builder.append("<option value=''>未选择</option>");
		for (Dictionary dictionary : dictionaries) {
			builder.append("<option value='").append(dictionary.getCode()).append("'").append(dictionary.getCode()==code || dictionary.getCode().equals(code)?"selected='selected'":"").append(" >");
			builder.append(dictionary.getName()).append("</option>");
		}
		return builder.toString();
	}
}
