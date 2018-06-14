package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LiveRadioListVO {

    @ApiModelProperty(name="code",value="直播ID",example="1")
    @JsonProperty("code")
    private Integer id;

    @ApiModelProperty(name="nickName",value="女神昵称",example="xxxxx")
    private String nickName;

    @ApiModelProperty(name="userName",value="女神用户名",example="xxxxx")
    private String userName;

    @ApiModelProperty(name="headImgUrl",value="头像",example="http://xxxxx")
    private String headImgUrl;

    @ApiModelProperty(name="label",value="标签",example="xxx,xx,xx")
    private String label;

    @ApiModelProperty(name="city",value="所在城市",example="成都")
    private String city;

    @ApiModelProperty(name="distance",value="距离",example="1km")
    private String distance;

    @ApiModelProperty(name="age",value="年龄",example="18岁")
    private String age;

    @ApiModelProperty(name="backImgUrl",value="背景图",example="http://xxxx")
    private String backImgUrl;

    public String getBackImgUrl() {
        return backImgUrl;
    }

    public void setBackImgUrl(String backImgUrl) {
        this.backImgUrl = backImgUrl;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
