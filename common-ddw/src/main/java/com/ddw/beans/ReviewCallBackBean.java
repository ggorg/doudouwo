package com.ddw.beans;

public class ReviewCallBackBean {
    private String businessCode;

    /**
     * 审批所属门店ID，-1或者空表示总店
     */
    private Integer storeId;

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }
}
