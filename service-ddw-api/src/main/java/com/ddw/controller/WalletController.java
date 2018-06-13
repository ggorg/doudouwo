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
    @ApiOperation(value = "查询收益明细",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/income/{token}")
    @ResponseBody
    public ResponseApiVO<ListVO<IncomeVO>> getIncome(@PathVariable String token , @RequestBody @ApiParam(name="args",value="传入json格式",required=true)IncomeDTO args){
        try {
            return this.walletService.getIncome(args.getIncomeType(),args.getPageNo(),token);
        }catch (Exception e){
            logger.error("WalletController-getIncome-》查询收益明细-》系统异常",e);
        }
        return new ResponseApiVO(-1,"查询失败",null);

    }

    @Token
    @ApiOperation(value = "查询钱包余额",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/balance/{token}")
    @ResponseBody
    public ResponseApiVO<WalletBalanceVO> getBalance(@PathVariable String token ){
        try {
            ResponseApiVO vo=this.walletService.getBalance(TokenUtil.getUserId(token));
            vo=createWallet(vo,token);
            if(vo.getReCode()==1){
                return vo;
            }
        }catch (Exception e){
            logger.error("WalletController-getBalance-》查询钱包余额-》系统异常",e);
        }
        return new ResponseApiVO(-1,"查询失败",null);

    }
    @Token
    @ApiOperation(value = "查询总资产",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/asset/{token}")
    @ResponseBody
    public ResponseApiVO<WalletAssetVO> getAsset(@PathVariable String token ){
        try {
            ResponseApiVO vo=this.walletService.getAsset(TokenUtil.getUserId(token));
            vo=createWallet(vo,token);
            if(vo.getReCode()==1){
                return vo;
            }
        }catch (Exception e){
            logger.error("WalletController-getAsset-》查询总资产-》系统异常",e);
        }
        return new ResponseApiVO(-1,"查询失败",null);

    }
    @Token
    @ApiOperation(value = "查询女神收益",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/goddessIn/{token}")
    @ResponseBody
    public ResponseApiVO<WalletGoddessInVO> getGoddessIn(@PathVariable String token ){
        try {
            ResponseApiVO vo=this.walletService.getGoddessIn(TokenUtil.getUserId(token));
            vo=createWallet(vo,token);
            if(vo.getReCode()==1){
                return vo;
            }
        }catch (Exception e){
            logger.error("WalletController-getGoddessIn-》查询女神收益-》系统异常",e);
        }
        return new ResponseApiVO(-1,"查询失败",null);

    }
    @Token
    @ApiOperation(value = "查询代练收益",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/practiceIn/{token}")
    @ResponseBody
    public ResponseApiVO<WalletPracticeInVO> getPracticeIn(@PathVariable String token ){
        try {
            ResponseApiVO vo=this.walletService.getPracticeIn(TokenUtil.getUserId(token));
            vo=createWallet(vo,token);
            if(vo.getReCode()==1){
                return vo;
            }
        }catch (Exception e){
            logger.error("WalletController-getPracticeIn-》查询代练收益-》系统异常",e);
        }
        return new ResponseApiVO(-1,"查询失败",null);

    }
    private ResponseApiVO createWallet(ResponseApiVO vo,String token){
        if(vo.getData()==null){
            ResponseApiVO createVo=this.walletService.createWallet(TokenUtil.getUserId(token));
            if(createVo.getReCode()==1){
                return vo;
            }
        }else if(vo.getReCode()==1){
            return vo;
        }
        return vo;
    }


}
