package weixin.entity;


import java.util.Date;

/**
 * 用户分析
 * @author Jacky
 *
 */
public class UserAnalysis{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id; 
	/**应用ID */
	private String appid;
	/**新增的用户数量 */
	private int newUser;
	/**取消关注的用户数量 */
	private int cancelUser;
	/**净增用户数量（new_user减去cancel_user即为净增用户数量） */
	private int netgainUser;
	/**用户的渠道（0代表其他合计 1代表公众号搜索 17代表名片分享 30代表扫描二维码 43代表图文页右上角菜单 51代表支付后关注（在支付完成页） 57代表图文页内公众号名称 75代表公众号文章广告 78代表朋友圈广告） */
	private int userSource;
	/**累计用户数 */
	private int cumulateUser;
	/**数据的日期 */
	private Date refDate;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public int getNewUser() {
		return newUser;
	}
	public void setNewUser(int newUser) {
		this.newUser = newUser;
	}
	public int getCancelUser() {
		return cancelUser;
	}
	public void setCancelUser(int cancelUser) {
		this.cancelUser = cancelUser;
	}
	public int getNetgainUser() {
		return netgainUser;
	}
	public void setNetgainUser(int netgainUser) {
		this.netgainUser = netgainUser;
	}
	public int getUserSource() {
		return userSource;
	}
	public void setUserSource(int userSource) {
		this.userSource = userSource;
	}
	public int getCumulateUser() {
		return cumulateUser;
	}
	public void setCumulateUser(int cumulateUser) {
		this.cumulateUser = cumulateUser;
	}
	public Date getRefDate() {
		return refDate;
	}
	public void setRefDate(Date refDate) {
		this.refDate = refDate;
	}
	  
}
