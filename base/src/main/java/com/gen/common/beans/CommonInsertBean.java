package com.gen.common.beans;


import com.gen.common.util.BeanToMapUtil;

import java.util.Map;

public class CommonInsertBean {
	private String tablename;
	private Integer id;
	private Object params;
	public String getTablename() {
		return tablename;
	}
	public Object getParams() {
		return params;
	}
	public void setParams(Object params) {
		if(!(params instanceof Map)){
			this.params= BeanToMapUtil.beanToMap(params);
		}else{
			this.params = params;
		}
	
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
	public CommonInsertBean() {
		// TODO Auto-generated constructor stub
	}
	public CommonInsertBean(String tablename,Object params){
		this.tablename=tablename;
		this.setParams(params);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
