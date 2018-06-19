package com.ddw.beans;

import java.io.Serializable;
import java.util.Date;

public class ReviewPO implements Serializable {
    private static final long serialVersionUID = -6728013719441257262L;
    private Integer id;
    private Integer drProposer ;//申请人ID
    private Integer drReviewer ;//审核人
    private String drProposerName ;//申请人名称
    private String drReviewerName ;//审批人名称
    private Integer drBusinessType ;//业务类型
    private String drBusinessCode ;//所属业务编号或者ID
    private Integer drReviewStatus ;//审核状态，待审核：0，审核通过：1，审核不通过：2
    private String drReviewDesc ;//审核说明
    private Date createTime;
    private Date updateTime;

    private Integer drProposerType ;//申请人类型，门店：0，会员：1
    private Integer drReviewerType ;//审批人类型，总店：0，门店：1
    private Integer drBusinessStatus ;//审批业务状态
    private String drApplyDesc ;//申请人说明
    private Integer drBelongToStoreId;//所属门店，门店ID号，空值表示属于总店

    private String drExtend;//扩展字段

    public String getDrExtend() {
        return drExtend;
    }

    public void setDrExtend(String drExtend) {
        this.drExtend = drExtend;
    }

    public Integer getDrBelongToStoreId() {
        return drBelongToStoreId;
    }

    public void setDrBelongToStoreId(Integer drBelongToStoreId) {
        this.drBelongToStoreId = drBelongToStoreId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDrProposer() {
        return drProposer;
    }

    public void setDrProposer(Integer drProposer) {
        this.drProposer = drProposer;
    }

    public Integer getDrReviewer() {
        return drReviewer;
    }

    public void setDrReviewer(Integer drReviewer) {
        this.drReviewer = drReviewer;
    }

    public String getDrProposerName() {
        return drProposerName;
    }

    public void setDrProposerName(String drProposerName) {
        this.drProposerName = drProposerName;
    }

    public String getDrReviewerName() {
        return drReviewerName;
    }

    public void setDrReviewerName(String drReviewerName) {
        this.drReviewerName = drReviewerName;
    }

    public Integer getDrBusinessType() {
        return drBusinessType;
    }

    public void setDrBusinessType(Integer drBusinessType) {
        this.drBusinessType = drBusinessType;
    }

    public String getDrBusinessCode() {
        return drBusinessCode;
    }

    public void setDrBusinessCode(String drBusinessCode) {
        this.drBusinessCode = drBusinessCode;
    }

    public Integer getDrReviewStatus() {
        return drReviewStatus;
    }

    public void setDrReviewStatus(Integer drReviewStatus) {
        this.drReviewStatus = drReviewStatus;
    }

    public String getDrReviewDesc() {
        return drReviewDesc;
    }

    public void setDrReviewDesc(String drReviewDesc) {
        this.drReviewDesc = drReviewDesc;
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



    public Integer getDrProposerType() {
        return drProposerType;
    }

    public void setDrProposerType(Integer drProposerType) {
        this.drProposerType = drProposerType;
    }

    public Integer getDrReviewerType() {
        return drReviewerType;
    }

    public void setDrReviewerType(Integer drReviewerType) {
        this.drReviewerType = drReviewerType;
    }

    public Integer getDrBusinessStatus() {
        return drBusinessStatus;
    }

    public void setDrBusinessStatus(Integer drBusinessStatus) {
        this.drBusinessStatus = drBusinessStatus;
    }

    public String getDrApplyDesc() {
        return drApplyDesc;
    }

    public void setDrApplyDesc(String drApplyDesc) {
        this.drApplyDesc = drApplyDesc;
    }
}
