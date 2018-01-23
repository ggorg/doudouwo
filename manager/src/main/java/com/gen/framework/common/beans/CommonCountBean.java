package com.gen.framework.common.beans;

/**
 * 计数总数
 * @author gen
 *
 */
public class CommonCountBean extends CommonBean{
	/**
	 * 
	 * @param tablename  表名  (必须)
	 * @param condition 主表查询条件
	 * @param childConditions 从表查询条件
	 */
	public CommonCountBean(String tablename,Object condition,CommonChildBean ... childConditions) {
		super(tablename, null, null, condition, childConditions);
	}
	public CommonCountBean() {
		super();
	}
}
