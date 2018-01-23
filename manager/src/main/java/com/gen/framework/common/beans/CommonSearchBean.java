package com.gen.framework.common.beans;

/**
 * 自定义查询
 * @author gen
 *
 */
public class CommonSearchBean extends CommonBean {
	/**
	 * 开始行号
	 */
	private Integer startNum;
	/**
	 * 结速行号
	 */
	private Integer endNum;
	
	
	public Integer getStartNum() {
		return startNum;
	}
	public void setStartNum(Integer startNum) {
		this.startNum = startNum;
	}
	public Integer getEndNum() {
		return endNum;
	}
	public void setEndNum(Integer endNum) {
		this.endNum = endNum;
	}
	public CommonSearchBean() {
		// TODO Auto-generated constructor stub
		super();
	}
	/**
	 * 
	 * @param tablename 表名 (必须)
	 * @param ordername 排序字段
	 * @param custom 自定义返回字段
	 * @param startNum 行开始
	 * @param endNum 行结束
	 * @param condition 主表查询条件
	 * @param childConditions 从表查询条件
	 */
	public CommonSearchBean(String tablename ,String ordername, String custom,Integer startNum,Integer endNum, Object condition,
			CommonChildBean ... childConditions) {
		super(tablename, ordername, custom, condition, childConditions);
		this.endNum=endNum;
		this.startNum=startNum;
	}
	public CommonSearchBean(String tablename , Object condition,
			CommonChildBean ... childConditions) {
		super(tablename, null, null, condition, childConditions);
	}
}
