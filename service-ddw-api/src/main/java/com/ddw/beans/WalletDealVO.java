package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by Jacky on 2018/6/30.
 */
@ApiModel
public class WalletDealVO<T> {

    @ApiModelProperty(name="pay",value="支出",example="支出")
    private Integer pay=0;
    @ApiModelProperty(name="income",value="收入",example="收入")
    private Integer income=0;
    @ApiModelProperty(name="list",value="交易记录",example="交易记录")
    private List<WalletDealRecordVO> list;

    public Integer getPay() {
        return pay;
    }

    public void setPay(Integer pay) {
        this.pay = pay;
    }

    public Integer getIncome() {
        return income;
    }

    public void setIncome(Integer income) {
        this.income = income;
    }

    public List<WalletDealRecordVO> getList() {
        return list;
    }

    public void setList(List<WalletDealRecordVO> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "WalletDealVO{" +
                "pay=" + pay +
                ", income=" + income +
                ", list=" + list +
                '}';
    }
}
