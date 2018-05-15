package com.ddw.controller;

import com.ddw.beans.MoneyDTO;
import com.ddw.beans.PayStatusDTO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.services.ReviewService;
import com.ddw.services.WalletService;
import com.ddw.token.Token;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@Api(value = "支付中心",description = "支付中心",tags = {"支付中心"})
@RestController
@RequestMapping("/ddwapp/paycenter")
public class PayCenterController {
    private final Logger logger = Logger.getLogger(PayCenterController.class);

    @Autowired
    private WalletService walletService;

    @Autowired
    private ReviewService reviewService;

    @Token
    @ApiOperation(value = "查询支付状态",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/paystatus/{token}")
    @ResponseBody
    public ResponseApiVO searchPayStatus (@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)PayStatusDTO args){
        try {
            return this.walletService.searchPayStatus(token,args);
        }catch (Exception e){
            logger.error("PayCenterController-searchPayStatus-》支付状态查询-》系统异常",e);
            return new ResponseApiVO(-1,"支付状态查询失败",null);
        }
    }
    @Token
    @ApiOperation(value = "提现申请",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/review/withdrawmoney/{token}")
    @ResponseBody
    public ResponseApiVO withdrawMoney(@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)MoneyDTO args){
        try {
            //return this.walletService.searchPayStatus(token,args);
        }catch (Exception e){
            logger.error("PayCenterController-withdrawMoney-》提现申请-》系统异常",e);
        }
        return new ResponseApiVO(-1,"提现申请失败",null);

    }

}
