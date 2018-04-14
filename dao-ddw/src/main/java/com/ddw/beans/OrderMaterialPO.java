package com.ddw.beans;

import java.util.Date;

public class OrderMaterialPO {
    private Integer id;
    /**
     * 订单ID
     */
    private Integer orderId;

    /**
     * 订单编号
     */
    private String orderNo;
    /**
     * 原材料ID
     */
    private Integer materialId;
    /**
     * 原材料名称
     */
    private String materialName;
    /**
     * 原材料总额
     */
    private Integer materialCountPrice;

    /**
     * 原材料单价
     */
    private Integer materialUnitPrice;

    /**
     * 原材料购买数量
     */
    private Integer materialBuyNumber;
    private Date createTime;
    private Date updateTime;
}
