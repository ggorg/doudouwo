package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="会员用例对象",description="用例对象UserInfoDTO")
public class UserInfoDTO{
    @ApiModelProperty(name="userName",value="账号（微信、QQ登录时，为空）",example="某某某")
    private String userName;
    @ApiModelProperty(name="userPwd",value="密码（微信、QQ登录时，为空）",example="123456")
    private String userPwd;
    @ApiModelProperty(name="openid",value="用户openid",example="oNSHajg7OZ-K3yqzERRHOzudEm26102")
    private String openid;
    @ApiModelProperty(name="nickName",value="昵称",example="某某某")
    private String nickName;
    @ApiModelProperty(name="headImgUrl",value="头像URL",example="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1522752012429&di=b26668f45e547cb644bb85d054242abe&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2Fbba1cd11728b4710655829d1c9cec3fdfc0323bc.jpg")
    private String headImgUrl;
    @ApiModelProperty(name="phone",value="手机号码",example="18500000000")
    private String phone;
    @ApiModelProperty(name="signature",value="个性签名",example="这个人很懒,什么都没有留下...")
    private String signature;
    @ApiModelProperty(name="sex",value="用户的性别，值为1时是男性，值为2时是女性，值为0时是未知",example="1")
    private Integer sex;
    @ApiModelProperty(name="registerType",value="注册类型1 微信注册,2 QQ注册",example="1")
    private Integer registerType;

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getRegisterType() {
        return registerType;
    }

    public void setRegisterType(Integer registerType) {
        this.registerType = registerType;
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
