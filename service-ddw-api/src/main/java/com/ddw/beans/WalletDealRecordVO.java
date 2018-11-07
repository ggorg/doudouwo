package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/6/30.
 */
@ApiModel
public class WalletDealRecordVO {
    @ApiModelProperty(name="date",value="日期",example="yyyy-MM-dd HH:mm")
    private String createTime;
    @ApiModelProperty(name="type",value="消费类型",example="消费类型")
    @JsonIgnore
    private Integer type;
    @ApiModelProperty(name="String",value="标题",example="标题")
    private String title;
    @ApiModelProperty(name="cost",value="金额",example="金额")
    private Integer cost;
    @ApiModelProperty(name="dealType",value="支付：1，退款：2，收益转入钱包：3，充值：4",example="支付：1，退款：2，收益转入钱包：3，充值：4")
    private Integer dealType;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Integer getDealType() {
        return dealType;
    }

    public void setDealType(Integer dealType) {
        this.dealType = dealType;
    }
}
