package com.ddw.controller;

import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.*;
import com.ddw.enums.AppOrderTypeEnum;
import com.ddw.enums.PayTypeEnum;
import com.ddw.services.AppOrderService;
import com.ddw.services.BiddingService;
import com.ddw.token.Token;
import com.ddw.token.TokenUtil;
import com.gen.common.util.ThreadLocalUtil;
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
    @ApiOperation(value = "查询订单列表(H5)",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/h5/list")
    @ResponseBody
    public ResponseApiVO<ListVO<OrderViewVO>> getH5OrderList(PageNoDTO dto){
        try {
            JSONObject jsonObj=(JSONObject) ThreadLocalUtil.get();
            if(jsonObj!=null){
                OrderViewDTO ovdto=new OrderViewDTO();
                ovdto.setType(AppOrderTypeEnum.OrderType3.getCode());
                ovdto.setPageNo(dto.getPageNo());
                String base64Token=jsonObj.getString("t");
                return appOrderService.getOrderList(base64Token,ovdto);
            }

        }catch (Exception e){
            logger.error("AppOrderController-getH5OrderList-》查询订单列表(H5)-》系统异常",e);
        }
        return new ResponseApiVO(-1,"查询失败",null);

    }
    @Token
    @ApiOperation(value = "退款订单列表",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/refund/list/{token}")
    @ResponseBody
    public ResponseApiVO<ListVO<RefundOrderViewVO>> getRefundOrderList(@PathVariable String token , @RequestBody @ApiParam(name="args",value="传入json格式",required=true)PageNoDTO dto){
        try {

            return appOrderService.getExitOrderList(token,dto);
        }catch (Exception e){
            logger.error("AppOrderController-RefundOrderViewVO-》退款订单列表-》系统异常",e);
        }
        return new ResponseApiVO(-1,"查询失败",null);

    }
    @Token
    @ApiOperation(value = "查看约玩订单信息（用户）",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/bid/user/order/info/{token}")
    @ResponseBody
    public ResponseApiVO<BiddingOrderInfoVO> getBidOrderInfoByUser(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)BiddingCodeDTO args){
        try {
            logger.info("getBidOrderInfoByUser->查看约玩订单信息（用户）->request->"+args);
            ResponseApiVO rs=this.biddingService.getBidOrderInfoByGoddess(token,args.getBidCode(),false);
            logger.info("getBidOrderInfoByUser->查看约玩订单信息（用户）->response->"+rs);

            return rs;
        }catch (Exception e){
            logger.error("BiddingController->getBidOrderInfoByUser-》查看约玩订单信息（用户）-》系统异常",e);
            return new ResponseApiVO(-1,"失败",null);
        }
    }
    @Token
    @ApiOperation(value = "查看约玩订单信息（女神）",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/bid/goddess/order/info/{token}")
    @ResponseBody
    public ResponseApiVO<BiddingOrderInfoVO> getBidOrderInfoByGoddess(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)BiddingCodeDTO args){
        try {
            logger.info("getBidOrderInfoByGoddess->查看约玩订单信息（女神）->request->"+args);
            ResponseApiVO rs=this.biddingService.getBidOrderInfoByGoddess(token,args.getBidCode(),true);
            logger.info("getBidOrderInfoByGoddess->查看约玩订单信息（女神）->response->"+rs);

            return rs;
        }catch (Exception e){
            logger.error("BiddingController->getBidOrderInfoByGoddess-》查看约玩订单信息（女神）-》系统异常",e);
            return new ResponseApiVO(-1,"失败",null);
        }
    }
    @Token
    @ApiOperation(value = "获取约玩订单列表（用户下单）",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/bid/user/orderlist/{token}")
    @ResponseBody
    public ResponseApiVO<ListVO<BiddingOrderListVO>> getBidUserOrderList(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)PageNoDTO args){
        try {

            logger.info("getBidUserOrderList->获取约玩订单列表（用户下单）->request->"+args);
            ResponseApiVO rs=this.biddingService.getBidOrderList(token,args.getPageNo(),false);
            logger.info("getBidUserOrderList->获取约玩订单列表（用户下单）->response->"+rs);

            return rs;
        }catch (Exception e){
            logger.error("BiddingController->getBidUserOrderList-》获取约玩订单列表（用户下单）-》系统异常",e);
            return new ResponseApiVO(-1,"失败",null);
        }
    }
    @Token
    @ApiOperation(value = "获取约玩订单列表(女神接单)",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/bid/goddess/orderlist/{token}")
    @ResponseBody
    public ResponseApiVO<ListVO<BiddingOrderListVO>> getBidGoddessOrderList(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)PageNoDTO args){
        try {
            logger.info("getBidGoddessOrderList->获取约玩订单列表(女神接单)->request->"+args);
            ResponseApiVO rs=this.biddingService.getBidOrderList(token,args.getPageNo(),true);
            logger.info("getBidGoddessOrderList->获取约玩订单列表(女神接单)->response->"+rs);

            return rs;
        }catch (Exception e){
            logger.error("BiddingController->getBidGoddessOrderList-》获取约玩订单列表(女神接单)-》系统异常",e);
            return new ResponseApiVO(-1,"失败",null);
        }
    }
}
