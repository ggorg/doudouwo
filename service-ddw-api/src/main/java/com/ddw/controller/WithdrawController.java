package com.ddw.controller;

import com.ddw.beans.*;
import com.ddw.services.ReviewService;
import com.ddw.services.WithdrawService;
import com.ddw.token.Idemp;
import com.ddw.token.Token;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Api(value = "提现",description = "提现",tags = {"提现"})
@RestController
@RequestMapping("/ddwapp/withdraw")
public class WithdrawController {
    private final Logger logger = Logger.getLogger(WithdrawController.class);

    @Autowired
    private WithdrawService withdrawService;

    @Autowired
    private ReviewService reviewService;

    @Idemp
    @Token
    @ApiOperation(value = "提现申请",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/review/withdrawmoney/{token}")
    @ResponseBody
    public ResponseApiVO withdrawMoney(@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)WithdrawDTO args){
        try {
            logger.info("withdrawMoney->request："+args);
            ResponseApiVO res=this.reviewService.applyWithdraw(token,args);
            logger.info("withdrawMoney->response："+res);

            return res;
        }catch (Exception e){
            logger.error("WithdrawController-withdrawMoney-》提现申请-》系统异常",e);
        }
        return new ResponseApiVO(-1,"提现申请失败",null);
    }


    @Token
    @ApiOperation(value = "解除绑定",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/unbind/{token}")
    @ResponseBody
    public ResponseApiVO unbind(@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)CodeDTO args){
        try {
            logger.info("unbind->request："+args);
            return this.withdrawService.delete(token,args);
            //return this.walletService.searchPayStatus(token,args);
        }catch (Exception e){
            logger.error("WithdrawController-unbind-》解除绑定-》系统异常",e);
        }
        return new ResponseApiVO(-1,"解除绑定",null);
    }


    @Token
    @ApiOperation(value = "查询已绑定账号列表",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/searchAll/{token}")
    @ResponseBody
    public ResponseApiVO<ListVO<WithdrawWayVO>> searchWithdraw(@PathVariable String token){
        try {

            return this.withdrawService.search(token);
            //return this.walletService.searchPayStatus(token,args);
        }catch (Exception e){
            logger.error("WithdrawController-searchWithdraw-》查询已绑定账号列表-》系统异常",e);
        }
        return new ResponseApiVO(-1,"解除绑定",null);
    }

    @Token
    @ApiOperation(value = "绑定转账账号",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/save/{token}")
    @ResponseBody
    public ResponseApiVO save(@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)WithdrawWayDTO args){
        try {
            logger.info("save->request："+args);
            return this.withdrawService.save(token,args);
            //return this.walletService.searchPayStatus(token,args);
        }catch (Exception e){
            logger.error("WithdrawController-save-》绑定转账账号-》系统异常",e);
        }
        return new ResponseApiVO(-1,"绑定转账账号",null);
    }

}
