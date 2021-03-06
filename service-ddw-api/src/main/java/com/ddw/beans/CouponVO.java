package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class CouponVO {

    @ApiModelProperty(name="couponCode",value="优惠卷code",example="1")
    private Integer couponCode;
    @ApiModelProperty(name="type",value="优惠卷类型，代金卷：1，折扣卷：2，满减卷：3，首减卷：4",example="优惠卷类型，代金卷：1，折扣卷：2，满减卷：3，首减卷：4")
    private Integer type;

    @ApiModelProperty(name="mop",value="优惠幅度，折扣类型时候是0-100（0折-10折），其它类型是具体金额",example="优惠幅度，折扣类型时候是0-100（0折-10折），其它类型是具体金额(分)")
    private Integer mop;

    @ApiModelProperty(name="startTime",value="开始时间",example="yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date startTime;
    @ApiModelProperty(name="endTime",value="结束时间",example="yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date endTime;
    @ApiModelProperty(name="desc",value="描述",example="xxxx")
    private String desc;

    @ApiModelProperty(name="storeName",value="所属门店",example="xxxxx")
    private String storeName;

    @ApiModelProperty(name="minPrice",value="最低消费金额",example="单位：分")
    private Integer minPrice;

    @ApiModelProperty(name="expire",value="是否过期",example="是否过期，未过期：1，已过期：-1")
    private Integer expire;

    @ApiModelProperty(name="used",value="是否用过的",example="是否用过的，未用过：0，已用过：1")
    private Integer used;

    public Integer getUsed() {
        return used;
    }

    public void setUsed(Integer used) {
        this.used = used;
    }

    public Integer getExpire() {
        return expire;
    }

    public void setExpire(Integer expire) {
        this.expire = expire;
    }

    public Integer getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(Integer couponCode) {
        this.couponCode = couponCode;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getMop() {
        return mop;
    }

    public void setMop(Integer mop) {
        this.mop = mop;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
    }

    @Override
    public String toString() {
        return "CouponVO{" +
                "couponCode=" + couponCode +
                ", type=" + type +
                ", mop=" + mop +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", desc='" + desc + '\'' +
                ", storeName='" + storeName + '\'' +
                ", minPrice=" + minPrice +
                ", expire=" + expire +
                ", used=" + used +
                '}';
    }
}
