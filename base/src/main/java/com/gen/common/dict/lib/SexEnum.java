package com.gen.common.dict.lib;


import com.gen.common.dict.Dictionary;

/**
 * 性别
 * @author Td
 *
 */
public enum SexEnum implements Dictionary {
	/**
	 * 男
	 */
	STATUS1(1,"男"),
	
	/**
	 * 女
	 */
	STATUS2(2,"女");
	

	private Integer code;
	private String text;

	private SexEnum(Integer code, String text) {
		this.code = code;
		this.text = text;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public Integer getCode() {
		return code;
	}
}
