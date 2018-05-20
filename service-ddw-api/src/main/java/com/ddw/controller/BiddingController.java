package com.ddw.controller;

import com.ddw.beans.*;
import com.ddw.services.BiddingService;
import com.ddw.token.Token;
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
    @ApiOperation(value = "获取当前最高价位",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/maxprice/{token}")
    @ResponseBody
    public ResponseApiVO queryMaxPrice(@PathVariable String token){
        try {
            return this.biddingService.getCurrentMaxPrice(token);
        }catch (Exception e){
            logger.error("BiddingController-queryMaxPrice-》获取当前最高价位-》系统异常",e);
            return new ResponseApiVO(-1,"查询失败",null);
        }
    }


    @Token
    @ApiOperation(value = "提交竞价价位",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/submit/price/{token}")
    @ResponseBody
    public ResponseApiVO submitPrice(@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)BiddingDTO args){
        try {
            return this.biddingService.putPrice(token,args);
        }catch (Exception e){
            logger.error("BiddingController->submitPrice-》提交竞价价位-》系统异常",e);
            return new ResponseApiVO(-1,"提交竞价价位失败",null);
        }
    }


    @Token
    @ApiOperation(value = "获取当前竞价列表",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/currentall/{token}")
    @ResponseBody
    public ResponseApiVO<ListVO<List<BiddingVO>>> getCurrentAll(@PathVariable String token){
        try {
            return this.biddingService.getCurrentCacheBidding(token);
        }catch (Exception e){
            logger.error("BiddingController->getCurrentAll-》获取当前竞价列表-》系统异常",e);
            return new ResponseApiVO(-1,"获取失败",null);
        }
    }

    @Token
    @ApiOperation(value = "选择某个竞价",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/choose/bidding/{token}")
    @ResponseBody
    public ResponseApiVO chooseBidding(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)UserOpenIdDTO args){
        try {
            return new ResponseApiVO(-1,"获取失败",null);
        }catch (Exception e){
            logger.error("BiddingController->getCurrentAll-》获取当前竞价列表-》系统异常",e);
            return new ResponseApiVO(-1,"获取失败",null);
        }
    }
}
