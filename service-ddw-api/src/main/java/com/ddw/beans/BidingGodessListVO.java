package com.ddw.beans;

import com.ddw.beans.vo.BiddingVO;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BidingGodessListVO {

    @ApiModelProperty(name="list",value="竞价数据",example="xxxx")
    private List<BiddingVO> list;

    @ApiModelProperty(name="status",value="状态,竞价中：1，接单中：2，用户已支付：3，用户取消支付：4",example="xxxx")
    private Integer status;

    @ApiModelProperty(name="statusMsg",value="状态信息",example="xxxx")
    private String statusMsg;

    @ApiModelProperty(name="orderNo",value="支付的订单",example="xxxx")
    private String orderNo;

    @ApiModelProperty(name="bidSuccessOpenId",value="已选用户的openId",example="xxxx")
    private String bidSuccessOpenId;

    @ApiModelProperty(name="bidEndTime",value="竞价结束时间",example="xxxx")
    private String bidEndTime;


    public String getBidEndTime() {
        return bidEndTime;
    }

    public void setBidEndTime(String bidEndTime) {
        this.bidEndTime = bidEndTime;
    }

    public List<BiddingVO> getList() {
        return list;
    }

    public void setList(List<BiddingVO> list) {
        this.list = list;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getBidSuccessOpenId() {
        return bidSuccessOpenId;
    }

    public void setBidSuccessOpenId(String bidSuccessOpenId) {
        this.bidSuccessOpenId = bidSuccessOpenId;
    }
}
