package com.ddw.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Jacky on 2018/7/12.
 */
public class PracticeOrderPO implements Serializable{
    private static final long serialVersionUID = 4891646275143545349L;
    private int id;
    private int userId;
    private int orderId;
    private String orderNo;
    private int practiceId;
    private int gameId;
    private int rankId;
    private int star;
    private int targetRankId;
    private int targetStar;
    private int realityRankId;
    private int realityStar;
    private int status;
    private int money;
    private int realityMoney;
    private int refund;
    private int storeId;
    private int incomeState;
    private String picUrl;
    private int payState;
    private int evaluationStatus;
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

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public int getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(int practiceId) {
        this.practiceId = practiceId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getRankId() {
        return rankId;
    }

    public void setRankId(int rankId) {
        this.rankId = rankId;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getTargetRankId() {
        return targetRankId;
    }

    public void setTargetRankId(int targetRankId) {
        this.targetRankId = targetRankId;
    }

    public int getTargetStar() {
        return targetStar;
    }

    public void setTargetStar(int targetStar) {
        this.targetStar = targetStar;
    }

    public int getRealityRankId() {
        return realityRankId;
    }

    public void setRealityRankId(int realityRankId) {
        this.realityRankId = realityRankId;
    }

    public int getRealityStar() {
        return realityStar;
    }

    public void setRealityStar(int realityStar) {
        this.realityStar = realityStar;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getRealityMoney() {
        return realityMoney;
    }

    public void setRealityMoney(int realityMoney) {
        this.realityMoney = realityMoney;
    }

    public int getRefund() {
        return refund;
    }

    public void setRefund(int refund) {
        this.refund = refund;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getIncomeState() {
        return incomeState;
    }

    public void setIncomeState(int incomeState) {
        this.incomeState = incomeState;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public int getPayState() {
        return payState;
    }

    public void setPayState(int payState) {
        this.payState = payState;
    }

    public int getEvaluationStatus() {
        return evaluationStatus;
    }

    public void setEvaluationStatus(int evaluationStatus) {
        this.evaluationStatus = evaluationStatus;
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
