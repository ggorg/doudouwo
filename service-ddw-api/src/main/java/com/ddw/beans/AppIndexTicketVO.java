package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 门票
 */
@ApiModel
public class AppIndexTicketVO {
    @ApiModelProperty(name="name",value="门票名称",example="门票")
    private String dtName;

    @ApiModelProperty(name="cost",value="价格",example="10元")
    private String dghSalesPrice;

    @ApiModelProperty(name="code",value="编号",example="xxxxxxxx")
    private String dtCode;

    public String getDtName() {
        return dtName;
    }

    public void setDtName(String dtName) {
        this.dtName = dtName;
    }

    public String getDghSalesPrice() {
        return dghSalesPrice;
    }

    public void setDghSalesPrice(String dghSalesPrice) {
        this.dghSalesPrice = dghSalesPrice;
    }

    public String getDtCode() {
        return dtCode;
    }

    public void setDtCode(String dtCode) {
        this.dtCode = dtCode;
    }
}
