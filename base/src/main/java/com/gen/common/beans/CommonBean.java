package com.gen.common.beans;


import com.gen.common.util.BeanToMapUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 查询公共传参实体
 * @author gen
 *
 */
@SuppressWarnings(value={"rawtypes","unchecked"})
public abstract class CommonBean {
	/**
	 * 主表表名
	 */
	private String tablename;
	/**
	 * 排序字段
	 */
	private String ordername;
	
	/**
	 * 自定义返回字段
	 */
	private String custom;
	
	/**
	 * 主表查询条件
	 */
	private Object condition;
	
	/**
	 * 从表查询条件
	 */
	private List childConditions;
	
	/**
	 * 
	 * true表示 条件默认以and并接，false表示手动加括号或者or,and
	 */
	public Boolean isAuto=true;
	

	public Boolean isAuto() {
		return isAuto;
	}
	public void setAuto(Boolean isAuto) {
		this.isAuto = isAuto;
	}
	public String getTablename() {
		return tablename;
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getOrdername() {
		return ordername;
	}
	public void setOrdername(String ordername) {
		this.ordername = ordername;
	}
	public String getCustom() {
		return custom;
	}
	public void setCustom(String custom) {
		this.custom = custom;
	}
	public Object getCondition() {
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
	public List getChildConditions() {
		return childConditions;
	}
	public void setChildConditions(List childConditions) {
		this.childConditions = childConditions;
	}
	public CommonBean() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * 
	 * @param tablename  表名
	 * @param ordername 排序字段
	 * @param custom 自定义返回字段
	 * @param condition 主表查询条件
	 * @param childConditions 从表查询条件
	 */
	public CommonBean(String tablename,String ordername,String custom,Object condition,CommonChildBean... childConditions){
		this.tablename=tablename;
		this.ordername=ordername;
		this.custom=custom;
		this.setCondition(condition);

		if(childConditions!=null){
			if(this.childConditions==null){
				this.childConditions=new ArrayList<CommonChildBean>();
			}
			for(CommonChildBean ccb:childConditions){
				this.childConditions.add(ccb);
			}
		}
	}
	
	
}
