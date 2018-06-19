package com.ddw.beans.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppIndexGoddessVO implements Serializable{

    @ApiModelProperty(name="id",value="女神id,对应会员表id",example="1")
    private Integer id;
    @ApiModelProperty(name="nickName",value="昵称",example="女神")
    private String nickName;
    @ApiModelProperty(name="imgUrl",value="相册第一张",example="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1529384238691&di=7e0db331f1a4b1d1b11e2643e8bb6ac3&imgtype=0&src=http%3A%2F%2Fi1.mopimg.cn%2Fimg%2Fdzh%2F2015-07%2F414%2F20150717120946794.jpg")
    private String imgUrl;
    @ApiModelProperty(name="headImgUrl",value="头像URL",example="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1522752012429&di=b26668f45e547cb644bb85d054242abe&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2Fbba1cd11728b4710655829d1c9cec3fdfc0323bc.jpg")
    private String headImgUrl;
    @ApiModelProperty(name="label",value="标签",example="1,2,3,4")
    private String label;
    @ApiModelProperty(name="ggradeName",value="女神等级名称",example="青铜")
    private String ggradeName;
    @ApiModelProperty(name="bidPrice",value="竞价金额",example="3000")
    private Integer bidPrice;
    @ApiModelProperty(name="liveRadioFlag",value="直播标记，0未申请，1审核通过,2审核中,3拒绝",example="1")
    private Integer liveRadioFlag;
    @ApiModelProperty(name="fans",value="粉丝数",example="16253")
    private Integer fans;
    @ApiModelProperty(name="followed",value="已关注true,未关注false",example="true")
    private boolean followed;

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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
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

    public Integer getFans() {
        return fans;
    }

    public void setFans(Integer fans) {
        this.fans = fans;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }
}
