package com.gen.framework.common.beans;

/**
 * 分页传参实体
 * @author gen
 *
 */

public class CommonPageBean extends CommonBean {


	/**
	 * 开始行号
	 */
	private Integer startNum;
	/**
	 * 结速行号
	 */
	private Integer endNum;
	/**
	 * 主表主键关联字段（SQLServer分页的特性）
	 */
	private String idname;

	
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
	public String getIdname() {
		return idname;
	}
	public void setIdname(String idname) {
		this.idname = idname;
	}

	public CommonPageBean() {
		// TODO Auto-generated constructor stub
		super();
	}
	/**
	 * 
	 * @param tablename 表名 (必须)
	 * @param idname 主表主键关联字段（SQLServer分页的特性）(必须)
	 * @param startNum 开始行号 (必须)
	 * @param endNum 结速行号 (必须)
	 * @param ordername 排序字段
	 * @param custom 自定义返回字段
	 * @param condition 主表查询条件
	 * @param childConditions 从表查询条件
	 */
	public CommonPageBean(String tablename ,String idname,Integer startNum,Integer endNum,String ordername, String custom, Object condition,
			CommonChildBean ... childConditions) {
		super(tablename, ordername, custom, condition, childConditions);
		this.idname=idname;
		this.startNum=startNum;
		this.endNum=endNum;
	}
}
