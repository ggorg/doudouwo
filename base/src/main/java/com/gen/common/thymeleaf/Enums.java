package com.gen.common.thymeleaf;


import com.gen.common.dict.Dictionary;
import com.gen.common.dict.DictionaryUtils;

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
			if (dictionary.getCode() == code) {
				return dictionary.getText();
			} else if (dictionary.getCode() == Integer.MIN_VALUE) {
				unknownDic = dictionary;
			}
		}
		if (null != unknownDic) {
			return unknownDic.getText();
		}
		return "";
	}
	public String createDictOptions( String className,Integer code){
		StringBuilder builder=new StringBuilder();
		Dictionary[] dictionaries = DictionaryUtils.getDictionaries(className);
		builder.append("<option value=''>不限</option>");
		for (Dictionary dictionary : dictionaries) {
			builder.append("<option value='").append(dictionary.getCode()).append("'").append(dictionary.getCode()==code?"selected='selected'":"").append(" >");
			builder.append(dictionary.getText()).append("</option>");
		}
		return builder.toString();
	}
}
