package com.ddw.beans;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by Jacky on 2018/5/28.
 */
public class PracticeVO {
    @ApiModelProperty(name="userId",value="会员id",example="1")
    private int userId;
    @ApiModelProperty(name="nickName",value="昵称",example="代练大神")
    private String nickName;
    @ApiModelProperty(name="imgUrl",value="相册第一张",example="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1529384238691&di=7e0db331f1a4b1d1b11e2643e8bb6ac3&imgtype=0&src=http%3A%2F%2Fi1.mopimg.cn%2Fimg%2Fdzh%2F2015-07%2F414%2F20150717120946794.jpg")
    private String imgUrl;
    @ApiModelProperty(name="headImgUrl",value="头像URL",example="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1522752012429&di=b26668f45e547cb644bb85d054242abe&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2Fbba1cd11728b4710655829d1c9cec3fdfc0323bc.jpg")
    private String headImgUrl;
    @ApiModelProperty(name="pgradeName",value="代练等级名称",example="青铜")
    private String pgradeName;
    @ApiModelProperty(name="starSign",value="星座",example="水瓶座")
    private String starSign;
    @ApiModelProperty(name="orders",value="接单数",example="3243")
    private long orders;
    @ApiModelProperty(name="star",value="评价星级，一颗星等于2，半颗为1，最低为2",example="10")
    private int star;
    @ApiModelProperty(name="focus",value="是否关注,关注true,未关注false",example="true")
    private boolean focus;
    @ApiModelProperty(name="practiceGameList",value="代练简历列表",example="")
    private List<ReviewPracticeVO> reviewPracticeList;
    @ApiModelProperty(name="photograph",value="相册列表",example="1")
    private List<PhotographPO> photograph;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getPgradeName() {
        return pgradeName;
    }

    public void setPgradeName(String pgradeName) {
        this.pgradeName = pgradeName;
    }

    public String getStarSign() {
        return starSign;
    }

    public void setStarSign(String starSign) {
        this.starSign = starSign;
    }

    public long getOrders() {
        return orders;
    }

    public void setOrders(long orders) {
        this.orders = orders;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public boolean isFocus() {
        return focus;
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    public List<ReviewPracticeVO> getReviewPracticeList() {
        return reviewPracticeList;
    }

    public void setReviewPracticeList(List<ReviewPracticeVO> reviewPracticeList) {
        this.reviewPracticeList = reviewPracticeList;
    }

    public List<PhotographPO> getPhotograph() {
        return photograph;
    }

    public void setPhotograph(List<PhotographPO> photograph) {
        this.photograph = photograph;
    }
}
