package com.weixin.entity;

import java.util.Date;

/**
 * 红包发送记录
 * @author Jacky
 *
 */
public class RedPackRecord {
	private int id;
	/**应用ID */
	private String appid;
	/**商户号 */
	private String mchId;
	/**商户订单号 */
	private String mchBillno;
	/**付款金额，单位分 */
	private int totalAmount; 
	/**用户openid */
	private String reOpenid;
	/**错误代码 */
	private String errCode;
	/**返回信息 */
	private String returnMsg;
	/**业务结果 */
	private String resultCode;
	/**错误代码描述 */
	private String errCodeDes;
	/**返回状态码 */
	private String returnCode;
	/**创建时间 */
	private Date createDate;
	/**发放成功时间 */
	private int sendTime;
	/**微信单号 */
	private String sendListid;
	/**商户名称(红包发送者名称) */
	private String sendName;
	/**红包祝福语 */
	private String wishing;
	/**活动名称 */
	private String actName;
	/**备注 */
	private String remark;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getMchId() {
		return mchId;
	}
	public void setMchId(String mchId) {
		this.mchId = mchId;
	}
	public String getMchBillno() {
		return mchBillno;
	}
	public void setMchBillno(String mchBillno) {
		this.mchBillno = mchBillno;
	}
	public int getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getReOpenid() {
		return reOpenid;
	}
	public void setReOpenid(String reOpenid) {
		this.reOpenid = reOpenid;
	}
	public String getErrCode() {
		return errCode;
	}
	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}
	public String getReturnMsg() {
		return returnMsg;
	}
	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public String getErrCodeDes() {
		return errCodeDes;
	}
	public void setErrCodeDes(String errCodeDes) {
		this.errCodeDes = errCodeDes;
	}
	public String getReturnCode() {
		return returnCode;
	}
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public int getSendTime() {
		return sendTime;
	}
	public void setSendTime(int sendTime) {
		this.sendTime = sendTime;
	}
	public String getSendListid() {
		return sendListid;
	}
	public void setSendListid(String sendListid) {
		this.sendListid = sendListid;
	}
	public String getSendName() {
		return sendName;
	}
	public void setSendName(String sendName) {
		this.sendName = sendName;
	}
	public String getWishing() {
		return wishing;
	}
	public void setWishing(String wishing) {
		this.wishing = wishing;
	}
	public String getActName() {
		return actName;
	}
	public void setActName(String actName) {
		this.actName = actName;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
