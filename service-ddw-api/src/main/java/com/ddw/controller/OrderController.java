package com.ddw.controller;

import com.ddw.beans.PayStatusDTO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.services.WalletService;
import com.ddw.token.Token;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@Api(value = "订单",description = "订单",tags = {"订单"})
@RestController
@RequestMapping("/ddwapp/order")
public class OrderController {
    private final Logger logger = Logger.getLogger(OrderController.class);

    @Autowired
    private WalletService walletService;

    @Token
    @ApiOperation(value = "查询支付状态",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/paystatus/{token}")
    @ResponseBody
    public ResponseApiVO searchPayStatus (@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)PayStatusDTO args){
        try {
            return this.walletService.searchPayStatus(token,args);
        }catch (Exception e){
            logger.error("OrderController-searchPayStatus-》支付状态查询-》系统异常",e);
            return new ResponseApiVO(-1,"支付状态查询失败",null);
        }
    }
}
