package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 代练
 */
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppIndexDaiLianVO {
    @ApiModelProperty(name="id",value="id",example="1")
    private Integer id;
    @ApiModelProperty(name="nickName",value="昵称",example="代练大神")
    private String nickName;
    @ApiModelProperty(name="headImgUrl",value="头像URL",example="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1522752012429&di=b26668f45e547cb644bb85d054242abe&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2Fbba1cd11728b4710655829d1c9cec3fdfc0323bc.jpg")
    private String headImgUrl;
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
    @ApiModelProperty(name="gradeId",value="会员等级编号，关联ddw_grade表",example="1")
    private Integer gradeId;
    @ApiModelProperty(name="ugradeName",value="会员等级名称",example="青铜")
    private String ugradeName;
    @ApiModelProperty(name="ulevel",value="会员等级水平",example="VIP0")
    private String ulevel;
    @ApiModelProperty(name="practiceGradeId",value="代练等级编号，关联ddw_practice_grade表",example="1")
    private Integer practiceGradeId;
    @ApiModelProperty(name="pgradeName",value="代练等级名称",example="青铜")
    private String pgradeName;
    @ApiModelProperty(name="plevel",value="代练等级水平",example="VIP0")
    private String plevel;
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

    public String getStarSign() {
        return starSign;
    }

    public void setStarSign(String starSign) {
        this.starSign = starSign;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Integer getGradeId() {
        return gradeId;
    }

    public void setGradeId(Integer gradeId) {
        this.gradeId = gradeId;
    }

    public String getUgradeName() {
        return ugradeName;
    }

    public void setUgradeName(String ugradeName) {
        this.ugradeName = ugradeName;
    }

    public String getUlevel() {
        return ulevel;
    }

    public void setUlevel(String ulevel) {
        this.ulevel = ulevel;
    }

    public Integer getPracticeGradeId() {
        return practiceGradeId;
    }

    public void setPracticeGradeId(Integer practiceGradeId) {
        this.practiceGradeId = practiceGradeId;
    }

    public String getPgradeName() {
        return pgradeName;
    }

    public void setPgradeName(String pgradeName) {
        this.pgradeName = pgradeName;
    }

    public String getPlevel() {
        return plevel;
    }

    public void setPlevel(String plevel) {
        this.plevel = plevel;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }
}
