package com.ddw.controller;

import com.ddw.beans.*;
import com.ddw.services.AppOrderService;
import com.ddw.services.BiddingService;
import com.ddw.token.Token;
import com.ddw.token.TokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ddwapp/order")
@Api(description="订单",tags = "订单")
public class AppOrderController {
    private final Logger logger = Logger.getLogger(AppOrderController.class);

    @Autowired
    private AppOrderService appOrderService;
    @Autowired
    private BiddingService biddingService;
    @Token
    @ApiOperation(value = "查询订单列表",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/list/{token}")
    @ResponseBody
    public ResponseApiVO<ListVO<OrderViewVO>> getOrderList(@PathVariable String token , @RequestBody @ApiParam(name="args",value="传入json格式",required=true)OrderViewDTO dto){
        try {
            return appOrderService.getOrderList(token,dto);
        }catch (Exception e){
            logger.error("AppOrderController-getOrderList-》查询订单列表-》系统异常",e);
        }
        return new ResponseApiVO(-1,"查询失败",null);

    }
    @Token
    @ApiOperation(value = "查看约玩订单信息",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/bid/order/info/{token}")
    @ResponseBody
    public ResponseApiVO<BiddingOrderInfoVO> getBidOrderInfoByGoddess(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)BiddingCodeDTO args){
        try {
            return this.biddingService.getBidOrderInfoByGoddess(token,args.getBidCode());
        }catch (Exception e){
            logger.error("BiddingController->getBidOrderInfoByGoddess-》获取订单信息-》系统异常",e);
            return new ResponseApiVO(-1,"失败",null);
        }
    }
    @Token
    @ApiOperation(value = "获取约玩订单列表",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/bid/orderlist/{token}")
    @ResponseBody
    public ResponseApiVO<ListVO<BiddingOrderListVO>> getBidOrderList(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)PageNoDTO args){
        try {
            return this.biddingService.getBidOrderList(token,args.getPageNo());
        }catch (Exception e){
            logger.error("BiddingController->getBidOrderLists-》获取订单列表-》系统异常",e);
            return new ResponseApiVO(-1,"失败",null);
        }
    }
}
