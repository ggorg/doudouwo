package com.ddw.beans;

/**
 * Created by Jacky on 2018/5/29.
 */
public class StrategyOldBringingNewDTO {
    private int id;
    private String name;
    private int levelId;
    private String describe;
    private Integer[]couponId;
    private Integer[]newCouponId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public Integer[] getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer[] couponId) {
        this.couponId = couponId;
    }

    public Integer[] getNewCouponId() {
        return newCouponId;
    }

    public void setNewCouponId(Integer[] newCouponId) {
        this.newCouponId = newCouponId;
    }
}
