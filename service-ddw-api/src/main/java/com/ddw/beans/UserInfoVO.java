package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value="会员展示用例对象",description="用例对象UserInfoDTO")
public class UserInfoVO {
    @ApiModelProperty(name="id",value="主键",example="1")
    private Integer id;
    @ApiModelProperty(name="userName",value="账号（微信、QQ登录时，为空）",example="某某某")
    private String userName;
    @ApiModelProperty(name="userPwd",value="密码（微信、QQ登录时，为空）",example="123456")
    private String userPwd;
    @ApiModelProperty(name="openid",value="用户openid",example="oNSHajg7OZ-K3yqzERRHOzudEm26102")
    private String openid;
    @ApiModelProperty(name="realName",value="真是姓名",example="金三胖")
    private String realName;
    @ApiModelProperty(name="nickName",value="昵称",example="某某某")
    private String nickName;
    @ApiModelProperty(name="headImgUrl",value="头像URL",example="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1522752012429&di=b26668f45e547cb644bb85d054242abe&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2Fbba1cd11728b4710655829d1c9cec3fdfc0323bc.jpg")
    private String headImgUrl;
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
    @ApiModelProperty(name="gradeId",value="会员等级，关联ddw_grade表",example="1")
    private Integer gradeId;
    @ApiModelProperty(name="goddessGradeId",value="女神等级，关联ddw_goddess_grade表",example="1")
    private Integer goddessGradeId;
    @ApiModelProperty(name="practiceGradeId",value="代练等级，关联ddw_practice_grade表",example="1")
    private Integer practiceGradeId;
    @ApiModelProperty(name="goddessFlag",value="女神标记，0非女神，1女神",example="1")
    private Integer goddessFlag;
    @ApiModelProperty(name="practiceFlag",value="代练标记，0非代练，1代练",example="1")
    private Integer practiceFlag;
    @ApiModelProperty(name="inviteCode",value="邀请码",example="aabbccddee")
    private String inviteCode;
    @ApiModelProperty(name="signature",value="个性签名",example="这个人很懒,什么都没有留下...")
    private String signature;
    @ApiModelProperty(name="sex",value="用户的性别，值为1时是男性，值为2时是女性，值为0时是未知",example="1")
    private Integer sex;
    @ApiModelProperty(name="registerType",value="注册类型1 微信注册,2 QQ注册",example="1")
    private Integer registerType;
    @ApiModelProperty(name="idcard",value="身份证号码",example="4402911182736736261")
    private String idcard;
    @ApiModelProperty(name="idcardFrontUrl",value="身份证正面图片URL",example="")
    private String idcardFrontUrl;
    @ApiModelProperty(name="idcardOppositeUrl",value="身份证背面图片URL",example="")
    private String idcardOppositeUrl;
    @ApiModelProperty(name="photograph",value="相册列表",example="1")
    private List<PhotographPO> photograph;

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

    public Integer getGoddessFlag() {
        return goddessFlag;
    }

    public void setGoddessFlag(Integer goddessFlag) {
        this.goddessFlag = goddessFlag;
    }

    public Integer getGoddessGradeId() {
        return goddessGradeId;
    }

    public void setGoddessGradeId(Integer goddessGradeId) {
        this.goddessGradeId = goddessGradeId;
    }

    public Integer getGradeId() {
        return gradeId;
    }

    public void setGradeId(Integer gradeId) {
        this.gradeId = gradeId;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getIdcardFrontUrl() {
        return idcardFrontUrl;
    }

    public void setIdcardFrontUrl(String idcardFrontUrl) {
        this.idcardFrontUrl = idcardFrontUrl;
    }

    public String getIdcardOppositeUrl() {
        return idcardOppositeUrl;
    }

    public void setIdcardOppositeUrl(String idcardOppositeUrl) {
        this.idcardOppositeUrl = idcardOppositeUrl;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
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

    public List<PhotographPO> getPhotograph() {
        return photograph;
    }

    public void setPhotograph(List<PhotographPO> photograph) {
        this.photograph = photograph;
    }

    public Integer getPracticeFlag() {
        return practiceFlag;
    }

    public void setPracticeFlag(Integer practiceFlag) {
        this.practiceFlag = practiceFlag;
    }

    public Integer getPracticeGradeId() {
        return practiceGradeId;
    }

    public void setPracticeGradeId(Integer practiceGradeId) {
        this.practiceGradeId = practiceGradeId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
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
