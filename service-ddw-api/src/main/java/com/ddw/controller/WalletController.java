package com.ddw.controller;

import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.*;
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
            if(vo.getReCode()==1){
                return vo;
            }
        }catch (Exception e){
            logger.error("WalletController-getBalance-》查询钱包余额-》系统异常",e);
        }
        return new ResponseApiVO(-1,"查询失败",null);

    }
    @Token
    @ApiOperation(value = "查询钱包所剩逗币",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/coin/{token}")
    @ResponseBody
    public ResponseApiVO<WalletDoubiVO> getCoin(@PathVariable String token ){
        try {
            ResponseApiVO vo=this.walletService.getCoin(TokenUtil.getUserId(token));
            if(vo.getReCode()==1){
                return vo;
            }
        }catch (Exception e){
            logger.error("WalletController-getCoin-》查询钱包所剩逗币-》系统异常",e);
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
            if(vo.getReCode()==1){
                return vo;
            }
        }catch (Exception e){
            logger.error("WalletController-getAsset-》查询总资产-》系统异常",e);
        }
        return new ResponseApiVO(-1,"查询失败",null);

    }
    @Token
    @ApiOperation(value = "查询优惠卷",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/coupon/{token}")
    @ResponseBody
    public ResponseApiVO<ListVO<CouponVO>> getCoupon(@PathVariable String token ){
        try {
            ResponseApiVO vo=this.walletService.getCouponList(TokenUtil.getUserId(token));
            return vo;
        }catch (Exception e){
            logger.error("WalletController-getCoupon-》查询优惠卷-》系统异常",e);
        }
        return new ResponseApiVO(-1,"查询失败",null);

    }
    @Token
    @ApiOperation(value = "查询背包礼物",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/giftpacket/{token}")
    @ResponseBody
    public ResponseApiVO<ListVO<GiftPacketVO>> getGiftPacket(@PathVariable String token ){
        try {
            ResponseApiVO vo=this.walletService.getGiftPackge(TokenUtil.getUserId(token));
            return vo;
        }catch (Exception e){
            logger.error("WalletController-getGiftPacket-》查询背包礼物-》系统异常",e);
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
            if(vo.getReCode()==1){
                return vo;
            }
        }catch (Exception e){
            logger.error("WalletController-getPracticeIn-》查询代练收益-》系统异常",e);
        }
        return new ResponseApiVO(-1,"查询失败",null);

    }

    @Token
    @ApiOperation(value = "查询是否首次使用钱包支付,首次需要修改支付密码,1首次,2非首次",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/isFirst/{token}")
    @ResponseBody
    public ResponseApiVO isFirst(@PathVariable String token){
        try {
            WalletPO walletPO = this.walletService.getWallet(TokenUtil.getUserId(token));
            if(walletPO.getPayPwd() == null || walletPO.getPayPwd().equals("")){
                return new ResponseApiVO(1,"首次",null);
            }else {
                return new ResponseApiVO(2,"非首次",null);
            }
        }catch (Exception e){
            logger.error("WalletController-isFirst-》查询是否首次使用钱包支付-》系统异常",e);
        }
        return new ResponseApiVO(-1,"查询失败",null);

    }

    @Token
    @ApiOperation(value = "修改钱包密码",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/updatePayPwd/{token}")
    @ResponseBody
    public ResponseApiVO updatePayPwd(@PathVariable String token , @RequestBody @ApiParam(name="args",value="传入json格式,oldPwd钱包原支付密码,首次原密码为空,newPwd新密码",required=true)WalletUpdatePayPwdDTO args){
        try {
            return this.walletService.updatePayPwd(TokenUtil.getUserId(token),args.getOldPwd(),args.getNewPwd());
        }catch (Exception e){
            logger.error("WalletController-updatePayPwd-》修改钱包支付密码-》系统异常",e);
        }
        return new ResponseApiVO(-1,"失败",null);

    }

    @Token
    @ApiOperation(value = "校验钱包密码",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/verifyPayPwd/{token}")
    @ResponseBody
    public ResponseApiVO verifyPayPwd(@PathVariable String token , @RequestBody @ApiParam(name="args",value="传入json格式,payPwd支付密码",required=true)WalletVerifyPayPwdDTO args){
        try {
            ResponseApiVO responseApiVO = this.walletService.verifyPayPwd(TokenUtil.getUserId(token),args.getPayPwd());
            if(responseApiVO.getReCode() == 1){
                JSONObject js = new JSONObject();
                String payCode = this.walletService.getRandomCode(8);
                js.put("payCode",payCode);
                TokenUtil.putPayCode(token,payCode);
                responseApiVO.setData(js);
            }
            return responseApiVO;
        }catch (Exception e){
            logger.error("WalletController-verifyPayPwd-》校验钱包支付密码-》系统异常",e);
        }
        return new ResponseApiVO(-1,"失败",null);

    }
}
