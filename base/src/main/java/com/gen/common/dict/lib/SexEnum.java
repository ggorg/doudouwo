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
	private String name;

	private SexEnum(Integer code, String name) {
		this.code = code;
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Integer getCode() {
		return code;
	}
}
