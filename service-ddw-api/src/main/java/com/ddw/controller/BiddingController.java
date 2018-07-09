package com.ddw.controller;

import com.ddw.beans.*;
import com.ddw.beans.vo.BiddingVO;
import com.ddw.services.BiddingService;
import com.ddw.token.Token;
import com.gen.common.exception.GenException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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

            ResponseApiVO rs=this.biddingService.getCurrentMaxPrice(token);
            logger.info("queryBidPrice->获取本轮竞价列表（普通用户）->response->"+rs);

            return rs;

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
            logger.info("submitPrice->提交竞价金额（普通用户）->request->"+args);
            ResponseApiVO rs=this.biddingService.putPrice(token,args);
            logger.info("submitPrice->提交竞价金额（普通用户）->response->"+rs);

            return rs;
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
            ResponseApiVO rs=this.biddingService.getCurrentAllBidding(token);
            logger.info("getCurrentAll->获取当前竞价列表（女神）->response->"+rs);

            return rs;

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
            ResponseApiVO rs=this.biddingService.doEndPlay(token);
            logger.info("doEnd->结束约玩（女神）->response->"+rs);

            return rs;
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
            ResponseApiVO rs=this.biddingService.makeSureFinishPay(token);
            logger.info("makeSure->确认完成（女神）->response->"+rs);

            return rs;
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
            logger.info("chooseBidding->选择某个用户的竞价（女神）->request->"+args);
            ResponseApiVO rs=this.biddingService.chooseBidding(args.getOpenId(),token);
            logger.info("chooseBidding->选择某个用户的竞价（女神）->response->"+rs);

            return rs;

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
            logger.info("waitpay->查询待支付的竞价金额(普通用户)->request->"+args);
            ResponseApiVO rs=this.biddingService.searchWaitPayByUser(args.getGroupId(),token);
            logger.info("waitpay->查询待支付的竞价金额(普通用户)->response->"+rs);

            return rs;

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

            ResponseApiVO rs=this.biddingService.searchWaitPayByGoddess(token);
            logger.info("waitpayByGoddess->查询待支付的竞价金额(女神)->response->"+rs);

            return rs;

        }catch (Exception e){
            logger.error("BiddingController->waitpayByGoddess-》查看竞价待支付金额-》系统异常",e);
            return new ResponseApiVO(-1,"失败",null);
        }
    }
    @Token
    @ApiOperation(value = "取消支付(普通用户)",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/cancel/pay/user/{token}")
    @ResponseBody
    public ResponseApiVO<BiddingPayVO> cancelByUser(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)BiddingUserCancelPayDTO args){
        try {
            ResponseApiVO rs=this.biddingService.cancelBidPayByUserId(token,args,true);
            logger.info("cancelByUser->取消支付(普通用户)->response->"+rs);

            return rs;
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

            ResponseApiVO rs=this.biddingService.cancelBidPayByGoddess(token);
            logger.info("cancelByGoddess->取消支付(女神)->response->"+rs);
            return rs;

        }catch (Exception e){
            logger.error("BiddingController->cancelByGoddess-》女神取消支付-》系统异常",e);
            return new ResponseApiVO(-1,"失败",null);
        }
    }
    @Token
    @ApiOperation(value = "查询续费金额",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/renew/{token}")
    @ResponseBody
    public ResponseApiVO<BiddingRenewVO> queryRenewInfo(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)BiddingCodeDTO args){
        try {
            logger.info("queryRenewInfo->查询续费金额->request->"+args);
            ResponseApiVO rs=this.biddingService.getBidOrderInfoByBidCode(args.getBidCode(),token,true);
            logger.info("queryRenewInfo->查询续费金额->response->"+rs);

            return rs;
        }catch (Exception e){
            logger.error("BiddingController->queryRenewInfo-》查询续费金额-》系统异常",e);
            return new ResponseApiVO(-1,"失败",null);
        }
    }

    @Token
    @ApiOperation(value = "确认续费(用户)",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/makesure/renew/{token}")
    @ResponseBody
    public ResponseApiVO makeSureRenew(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)BiddingRenewDTO args){
        try {
            logger.info("makeSureRenew->确认续费(用户)->request->"+args);
            ResponseApiVO rs=this.biddingService.makeSureRenew(args,token);
            logger.info("makeSureRenew->确认续费(用户)->response->"+rs);

            return rs;
        }catch (Exception e){
            logger.error("BiddingController->makeSureRenew-》确认续费(用户)-》系统异常",e);
            return new ResponseApiVO(-1,"失败",null);
        }
    }
}
