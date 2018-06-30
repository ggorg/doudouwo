package com.ddw.beans;

import java.util.Date;

/**
 * Created by Jacky on 2018/6/29.
 */
public class WalletPO {
    private int id;
    private int userId;
    private int money;
    private int coin;
    private int goddessIncome;
    private int practiceIncome;
    private String payPwd;
    private int version;
    private String describe;
    private Date createTime;
    private Date updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public int getGoddessIncome() {
        return goddessIncome;
    }

    public void setGoddessIncome(int goddessIncome) {
        this.goddessIncome = goddessIncome;
    }

    public int getPracticeIncome() {
        return practiceIncome;
    }

    public void setPracticeIncome(int practiceIncome) {
        this.practiceIncome = practiceIncome;
    }

    public String getPayPwd() {
        return payPwd;
    }

    public void setPayPwd(String payPwd) {
        this.payPwd = payPwd;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
