package com.ddw.controller;

import com.ddw.beans.*;
import com.ddw.services.BiddingService;
import com.ddw.token.Token;
import com.ddw.token.TokenUtil;
import com.gen.common.exception.GenException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "竞价",description = "竞价",tags = {"竞价"})
@RestController
@RequestMapping("/ddwapp/bidding")
public class BiddingController {

    @Autowired
    private BiddingService biddingService;

    private final Logger logger = Logger.getLogger(BiddingController.class);



    @Token
    @ApiOperation(value = "获取本轮竞价列表（普通用户）",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/maxprice/{token}")
    @ResponseBody
    public ResponseApiVO<BiddingDataVO<BiddingVO>> queryBidPrice(@PathVariable String token){
        try {
            return this.biddingService.getCurrentMaxPrice(token);
        }catch (Exception e){
            logger.error("BiddingController-queryMaxPrice-》获取当前最高价位-》系统异常",e);
            return new ResponseApiVO(-1,"查询失败",null);
        }
    }


    @Token
    @ApiOperation(value = "提交竞价金额（普通用户）",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/submit/price/{token}")
    @ResponseBody
    public ResponseApiVO submitPrice(@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)BiddingDTO args){
        try {
            return this.biddingService.putPrice(token,args);
        }catch (Exception e){
            logger.error("BiddingController->submitPrice-》提交竞价价位-》系统异常",e);
            if(e instanceof GenException){
                return new ResponseApiVO(-2,e.getMessage(),null);

            }else{
                return new ResponseApiVO(-1,"提交竞价价位失败",null);

            }
        }
    }


    @Token
    @ApiOperation(value = "获取当前竞价列表（女神）",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/currentall/{token}")
    @ResponseBody
    public ResponseApiVO<BidingGodessListVO> getCurrentAll(@PathVariable String token){
        try {
            return this.biddingService.getCurrentAllBidding(token);
        }catch (Exception e){
            logger.error("BiddingController->getCurrentAll-》获取当前竞价列表-》系统异常",e);
            return new ResponseApiVO(-1,"失败",null);
        }
    }


    @Token
    @ApiOperation(value = "结束约玩（女神）",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/end/{token}")
    @ResponseBody
    public ResponseApiVO doEnd(@PathVariable String token){
        try {
            return this.biddingService.doEndPlay(token);
        }catch (Exception e){
            logger.error("BiddingController->doEnd-》结束约玩-》系统异常",e);
            return new ResponseApiVO(-1,"失败",null);
        }
    }
    @Token
    @ApiOperation(value = "确认完成（女神）",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/makesure/{token}")
    @ResponseBody
    public ResponseApiVO makeSure(@PathVariable String token){
        try {
            return this.biddingService.makeSureFinishPay(token);
        }catch (Exception e){
            logger.error("BiddingController->makeSure-》确认完成（女神）-》系统异常",e);
            return new ResponseApiVO(-1,"失败",null);
        }
    }

    @Token
    @ApiOperation(value = "选择某个用户的竞价（女神）",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/choose/bidding/{token}")
    @ResponseBody
    public ResponseApiVO chooseBidding(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)UserOpenIdDTO args){
        try {
            return this.biddingService.chooseBidding(args.getOpenId(),token);
        }catch (Exception e){
            logger.error("BiddingController->chooseBidding-》选择某个用户的竞价（女神）-》系统异常",e);
            return new ResponseApiVO(-1,"失败",null);
        }
    }
    @Token
    @ApiOperation(value = "查询待支付的竞价金额(普通用户)",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/bidding/waitpay/{token}")
    @ResponseBody
    public ResponseApiVO<BiddingPayVO> waitpay(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)GroupIdDTO args){
        try {
            return this.biddingService.searchWaitPayByUser(args.getGroupId(),token);
        }catch (Exception e){
            logger.error("BiddingController->waitpay-》查看竞价待支付金额-》系统异常",e);
            return new ResponseApiVO(-1,"失败",null);
        }
    }
    @Token
    @ApiOperation(value = "查询待支付的竞价金额(女神)",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/bidding/waitpay/goddess/{token}")
    @ResponseBody
    public ResponseApiVO<BiddingPayVO> waitpayByGoddess(@PathVariable String token){
        try {
            return this.biddingService.searchWaitPayByGoddess(token);
        }catch (Exception e){
            logger.error("BiddingController->waitpayByGoddess-》查看竞价待支付金额-》系统异常",e);
            return new ResponseApiVO(-1,"失败",null);
        }
    }
    @Token
    @ApiOperation(value = "取消支付(普通用户)",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/cancel/pay/user/{token}")
    @ResponseBody
    public ResponseApiVO<BiddingPayVO> cancelByUser(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)GroupIdDTO args){
        try {
            return this.biddingService.cancelBidPayByUserId(token,args,true);
        }catch (Exception e){
            logger.error("BiddingController->cancelByUser-》用户取消支付-》系统异常",e);
            return new ResponseApiVO(-1,"失败",null);
        }
    }
    @Token
    @ApiOperation(value = "取消支付(女神)",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/cancel/pay/goddess/{token}")
    @ResponseBody
    public ResponseApiVO<BiddingPayVO> cancelByGoddess(@PathVariable String token){
        try {
            return this.biddingService.cancelBidPayByGoddess(token);
        }catch (Exception e){
            logger.error("BiddingController->cancelByGoddess-》女神取消支付-》系统异常",e);
            return new ResponseApiVO(-1,"失败",null);
        }
    }
}
