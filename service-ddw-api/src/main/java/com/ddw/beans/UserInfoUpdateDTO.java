package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="会员修改资料用例",description="UserInfoUpdateDTO")
public class UserInfoUpdateDTO {
    @ApiModelProperty(name="id",value="主键",example="1")
    private Integer id;
    @ApiModelProperty(name="userName",value="账号（微信、QQ登录时，为空）",example="某某某")
    private String userName;
    @ApiModelProperty(name="userPwd",value="密码（微信、QQ登录时，为空）",example="123456")
    private String userPwd;
    @ApiModelProperty(name="nickName",value="昵称",example="某某某")
    private String nickName;
    @ApiModelProperty(name="phone",value="手机号码",example="18500000000")
    private String phone;
    @ApiModelProperty(name="label",value="标签",example="1,2,3,4")
    private String label;
    @ApiModelProperty(name="starSign",value="星座",example="水瓶座")
    private String starSign;
    @ApiModelProperty(name="province",value="用户所在省份",example="广东省")
    private String province;
    @ApiModelProperty(name="city",value="用户所在城市",example="广州市")
    private String city;
    @ApiModelProperty(name="area",value="用户所在地区",example="天河区")
    private String area;
    @ApiModelProperty(name="signature",value="个性签名",example="这个人很懒,什么都没有留下...")
    private String signature;
    @ApiModelProperty(name="sex",value="用户的性别，值为1时是男性，值为2时是女性，值为0时是未知",example="1")
    private Integer sex;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getStarSign() {
        return starSign;
    }

    public void setStarSign(String starSign) {
        this.starSign = starSign;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }
}
