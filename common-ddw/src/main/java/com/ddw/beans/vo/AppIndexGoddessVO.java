package com.ddw.beans.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppIndexGoddessVO implements Serializable{


    private static final long serialVersionUID = -5332689512101751207L;
    @ApiModelProperty(name="id",value="女神id,对应会员表id",example="1")
    private Integer id;
    @ApiModelProperty(name="nickName",value="昵称",example="女神")
    private String nickName;
    @ApiModelProperty(name="headImgUrl",value="头像URL",example="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1522752012429&di=b26668f45e547cb644bb85d054242abe&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2Fbba1cd11728b4710655829d1c9cec3fdfc0323bc.jpg")
    private String headImgUrl;
    @ApiModelProperty(name="label",value="标签",example="1,2,3,4")
    private String label;
    @ApiModelProperty(name="ggradeName",value="女神等级名称",example="青铜")
    private String ggradeName;
    @ApiModelProperty(name="bidPrice",value="竞价金额",example="3000")
    private Integer bidPrice;
    @ApiModelProperty(name="liveRadioFlag",value="直播标记，等待直播：0，直播中：1，关闭：2",example="1")
    private Integer liveRadioFlag;
    @ApiModelProperty(name="code",value="直播房间号,直播状态为1时才有",example="1")
    private Integer code;
    @ApiModelProperty(name="storeId",value="门店编号",example="1")
    private Integer storeId;
    @ApiModelProperty(name="dsName",value="门店名称",example="成都店")
    private String dsName;
    @ApiModelProperty(name="fans",value="粉丝数",example="16253")
    private Integer fans = 0;
    @ApiModelProperty(name="focus",value="已关注true,未关注false",example="true")
    private boolean focus;
    @ApiModelProperty(name="age",value="年龄",example="22")
    private int age = 0;
    @ApiModelProperty(name="job",value="职业",example="文化/广告/传媒")
    private String job = "";
    @ApiModelProperty(name="signature",value="个性签名",example="这个人很懒,什么都没有留下...")
    private String signature = "";
    @ApiModelProperty(name="interest",value="兴趣",example="LOL/美食/旅游")
    private String interest = "";
    @ApiModelProperty(name="starSign",value="星座",example="水瓶座")
    private String starSign = "";
    @ApiModelProperty(name="viewingNum",value="观看人数",example="1")
    private Integer viewingNum=0;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getGgradeName() {
        return ggradeName;
    }

    public void setGgradeName(String ggradeName) {
        this.ggradeName = ggradeName;
    }

    public Integer getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(Integer bidPrice) {
        this.bidPrice = bidPrice;
    }

    public Integer getLiveRadioFlag() {
        return liveRadioFlag;
    }

    public void setLiveRadioFlag(Integer liveRadioFlag) {
        this.liveRadioFlag = liveRadioFlag;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getDsName() {
        return dsName;
    }

    public void setDsName(String dsName) {
        this.dsName = dsName;
    }

    public Integer getFans() {
        return fans;
    }

    public void setFans(Integer fans) {
        this.fans = fans;
    }

    public boolean isFocus() {
        return focus;
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getStarSign() {
        return starSign;
    }

    public void setStarSign(String starSign) {
        this.starSign = starSign;
    }

    public Integer getViewingNum() {
        return viewingNum;
    }

    public void setViewingNum(Integer viewingNum) {
        this.viewingNum = viewingNum;
    }

    @Override
    public String toString() {
        return "AppIndexGoddessVO{" +
                ", liveRadioFlag=" + liveRadioFlag +
                ", dsName='" + dsName + '\'' +
                ", fans=" + fans +
                '}';
    }
}
