package com.gen.common.beans;


import com.gen.common.util.BeanToMapUtil;

import java.util.Map;

/**
 * 从表实体
 * @author Td
 *
 */
@SuppressWarnings(value={"rawtypes"})
public class CommonChildBean {
	/**
	 * 从表名称
	 */
	private String childTablename;
	/**
	 * 从表主键名称
	 */
	private String keyname;
	/**
	 * 主表键名名称
	 */
	private String foreignKeyname;
	
	/**
	 * 自定义条件
	 */
	private Map condition;
	

	public String getChildTablename() {
		return childTablename;
	}


	public void setChildTablename(String childTablename) {
		this.childTablename = childTablename;
	}


	public String getKeyname() {
		return keyname;
	}


	public void setKeyname(String keyname) {
		this.keyname = keyname;
	}


	public String getForeignKeyname() {
		return foreignKeyname;
	}


	public void setForeignKeyname(String foreignKeyname) {
		this.foreignKeyname = foreignKeyname;
	}


	public Map getCondition() {
		return condition;
	}


	public void setCondition(Object condition) {
		if(condition!=null){
			if(condition instanceof Map){
				this.condition=(Map)condition;
			}else{
				this.condition= BeanToMapUtil.beanToMap(condition);
			}
		}
	}

	/**
	 * 
	 * @param childTablename 从表名称  (必须)
	 * @param keyname 从表主键名称  (必须)
	 * @param foreignKeyname 主表键名名称  (必须)
	 * @param condition 自定义条件
	 */
	public CommonChildBean(String childTablename,String keyname,String foreignKeyname,Object condition) {
		// TODO Auto-generated constructor stub
		this.childTablename=childTablename;
		this.keyname=keyname;
		this.foreignKeyname=foreignKeyname;
		this.setCondition(condition);
	}

}
