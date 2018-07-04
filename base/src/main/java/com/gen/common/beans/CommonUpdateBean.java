package com.gen.common.beans;


import com.gen.common.util.BeanToMapUtil;

import java.util.Map;

public class CommonUpdateBean extends CommonInsertBean {
	private Object condition;
	public Boolean isAuto=true;


	public Boolean isAuto() {
		return isAuto;
	}
	public void setAuto(Boolean isAuto) {
		this.isAuto = isAuto;
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
	
	public CommonUpdateBean() {

		super();
	}
	public CommonUpdateBean(String tablename,Object params,Object condition) {
		super( tablename, params);
		this.setCondition(condition);
	}
	
}
