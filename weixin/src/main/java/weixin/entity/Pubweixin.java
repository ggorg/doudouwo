package weixin.entity;


import java.util.Date;

/**
 * 微信公众号
 * @author Jacky
 *
 */
public class Pubweixin{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	/**公众号名称 */
	private String name;
	/**应用ID */
	private String appid;
	/**应用密钥 */
	private String appsecret;
	/**令牌 */
	private String token;
	/**原始ID */
	private String openid;
	/**是否可用（0 不可用 1 可用） */
	private String usable;
	/**创建人 */
	private String create_by;
	/**创建时间 */
	private Date create_date;
	/**修改人 */
	private String update_by;
	/**修改时间 */
	private Date update_date;
	/**备注 */
	private String remarks;
	/**删除标志（0 正常 1 已删除） */
	private String del_flag;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getAppsecret() {
		return appsecret;
	}
	public void setAppsecret(String appsecret) {
		this.appsecret = appsecret;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getUsable() {
		return usable;
	}
	public void setUsable(String usable) {
		this.usable = usable;
	}
	public String getCreate_by() {
		return create_by;
	}
	public void setCreate_by(String create_by) {
		this.create_by = create_by;
	}
	public Date getCreate_date() {
		return create_date;
	}
	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}
	public String getUpdate_by() {
		return update_by;
	}
	public void setUpdate_by(String update_by) {
		this.update_by = update_by;
	}
	public Date getUpdate_date() {
		return update_date;
	}
	public void setUpdate_date(Date update_date) {
		this.update_date = update_date;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getDel_flag() {
		return del_flag;
	}
	public void setDel_flag(String del_flag) {
		this.del_flag = del_flag;
	}
}
