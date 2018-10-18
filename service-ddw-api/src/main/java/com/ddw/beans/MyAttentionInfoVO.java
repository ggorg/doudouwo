package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value="关注用户信息用例",description="关注用户信息用例")
public class MyAttentionInfoVO {
    @ApiModelProperty(name="userId",value="会员编号）",example="1")
    private int userId;
    @ApiModelProperty(name="userName",value="账号（微信、QQ登录时，为空）",example="某某某")
    private String userName;
    @ApiModelProperty(name="openid",value="用户openid",example="oNSHajg7OZ-K3yqzERRHOzudEm26102")
    private String openid;
    @ApiModelProperty(name="nickName",value="昵称",example="某某某")
    private String nickName;
    @ApiModelProperty(name="headImgUrl",value="头像URL",example="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1522752012429&di=b26668f45e547cb644bb85d054242abe&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2Fbba1cd11728b4710655829d1c9cec3fdfc0323bc.jpg")
    private String headImgUrl;
    @ApiModelProperty(name="sex",value="用户的性别，值为1时是男性，值为2时是女性，值为0时是未知",example="1")
    private Integer sex = 0;
    @ApiModelProperty(name="age",value="年龄",example="22")
    private int age = 0;
    @ApiModelProperty(name="storeId",value="门店id",example="1")
    private Integer storeId;
    @ApiModelProperty(name="storeName",value="门店名称",example="成都门店")
    private String storeName;
    @ApiModelProperty(name="focus",value="是否关注",example="true")
    private boolean focus = true;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public boolean isFocus() {
        return focus;
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }
}
