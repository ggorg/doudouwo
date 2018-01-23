package com.gen.framework.common.beans;

import java.util.Map;

import com.gen.framework.common.util.BeanToMapUtil;

public class CommonUpdateBean extends CommonInsertBean {
	private Object condition;

	public Object getCondition() {
		return condition;
	}

	public void setCondition(Object condition) {
		if(condition!=null){
			if(condition instanceof Map){
				this.condition=(Map)condition;
			}else{
				this.condition=BeanToMapUtil.beanToMap(condition);
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
