package com.ddw.controller;

import com.ddw.beans.*;
import com.ddw.enums.OrderTypeEnum;
import com.ddw.enums.PayTypeEnum;
import com.ddw.services.PayCenterService;
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

    @Autowired
    private PayCenterService payCenterService;

    @Token
    @ApiOperation(value = "查询钱包余额",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/balance/{token}")
    @ResponseBody
    public ResponseApiVO<WalletBalanceVO> getBalance(@PathVariable String token ){
        try {
            ResponseApiVO vo=this.walletService.getBalance(TokenUtil.getUserId(token));
            if(vo.getData()==null){
                ResponseApiVO createVo=this.walletService.createWallet(TokenUtil.getUserId(token));
                if(createVo.getReCode()==1){
                    return vo;
                }
            }else if(vo.getReCode()==1){
                return vo;
            }

        }catch (Exception e){
            logger.error("WalletController-getBalance-》查询钱包余额-》系统异常",e);
        }
        return new ResponseApiVO(-1,"查询失败",null);

    }



}