package com.ddw.beans;

import java.io.Serializable;

public class RequestAliOrderVO implements Serializable {
    private static final long serialVersionUID = -3250423523126266986L;
    private String orderString;
    private String orderNo;

    public String getOrderString() {
        return orderString;
    }

    public void setOrderString(String orderString) {
        this.orderString = orderString;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @Override
    public String toString() {
        return "RequestAliOrderVO{" +
                "orderString='" + orderString + '\'' +
                ", orderNo='" + orderNo + '\'' +
                '}';
    }
}
