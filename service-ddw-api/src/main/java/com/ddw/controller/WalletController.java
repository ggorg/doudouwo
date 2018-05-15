package com.ddw.controller;

import com.ddw.beans.*;
import com.ddw.enums.OrderTypeEnum;
import com.ddw.enums.PayTypeEnum;
import com.ddw.services.WalletService;
import com.ddw.token.Token;
import com.ddw.token.TokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 钱包
 */
@Api(value = "我的钱包",description = "我的钱包",tags = {"我的钱包"})
@RestController
@RequestMapping("/ddwapp/wallet")
public class WalletController {
    private final Logger logger = Logger.getLogger(WalletController.class);

    @Autowired
    private WalletService walletService;

    @Token
    @ApiOperation(value = "查询钱包余额",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/balance/{token}")
    @ResponseBody
    public ResponseApiVO<WalletBalanceVO> getBalance(@PathVariable String token ){
        try {
            return this.walletService.getBalance(TokenUtil.getUserId(token));
        }catch (Exception e){
            logger.error("WalletController-getBalance-》查询钱包余额-》系统异常",e);
            return new ResponseApiVO(-1,"查询失败",null);
        }
    }

    @Token
    @ApiOperation(value = "微信充值",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/submit/weixin/recharge/{token}")
    @ResponseBody
    public ResponseApiVO<WalletWeixinRechargeVO> doWeixinRecharge (@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)WalletRechargeDTO args){
        try {
            return this.walletService.prePay(token,args.getMoney(), PayTypeEnum.PayType1.getCode(), OrderTypeEnum.OrderType3);
        }catch (Exception e){
            logger.error("WalletController-getBalance-》查询钱包余额-》系统异常",e);
            return new ResponseApiVO(-1,"微信充值失败",null);
        }
    }


    @Token
    @ApiOperation(value = "支付宝充值",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/submit/alipay/recharge/{token}")
    @ResponseBody
    public ResponseApiVO<WalletAlipayRechargeVO> doAlipayRecharge (@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)WalletRechargeDTO args){
        try {
            return this.walletService.prePay(token,args.getMoney(), PayTypeEnum.PayType2.getCode(), OrderTypeEnum.OrderType3);
        }catch (Exception e){
            logger.error("WalletController-getBalance-》支付宝充值-》系统异常",e);
            return new ResponseApiVO(-1,"支付宝充值失败",null);
        }
    }



}
