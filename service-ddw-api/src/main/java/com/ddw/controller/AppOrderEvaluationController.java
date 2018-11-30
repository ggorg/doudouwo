package com.ddw.controller;

import com.ddw.beans.*;
import com.ddw.services.AppOrderEvaluationService;
import com.ddw.token.Idemp;
import com.ddw.token.Token;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ddwapp/order/evaluate")
@Api(description="订单评价",tags = "订单评价")
public class AppOrderEvaluationController {
    private final Logger logger = Logger.getLogger(AppOrderEvaluationController.class);

    @Autowired
    private AppOrderEvaluationService appOrderEvaluationService;

    @Idemp("doEvaluate")
    @Token
    @ApiOperation(value = "评价",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/doEvaluate/{token}")
    @ResponseBody
    public ResponseApiVO doEvaluate(@PathVariable String token , @RequestBody @ApiParam(name="args",value="传入json格式",required=true)OrderViewEvaluateDTO dto){
        try {

            return this.appOrderEvaluationService.orderEvaluate(token,dto);
        }catch (Exception e){
            logger.error("AppOrderEvaluationController-doEvaluate-》评价-》系统异常",e);
        }
        return new ResponseApiVO(-1,"评价失败",null);
    }

    @Token
    @ApiOperation(value = "查询某商品评价列表",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/toEvaluateList/{token}")
    @ResponseBody
    public ResponseApiVO<OrderViewEvaluateListVO> toEvaluateList(@PathVariable String token , @RequestBody @ApiParam(name="args",value="传入json格式",required=true)OrderViewEvaluateListDTO dto){
        try {

            return this.appOrderEvaluationService.evaluateList(token,dto);
        }catch (Exception e){
            logger.error("AppOrderEvaluationController-toEvaluateList-》查询某商品评价列表-》系统异常",e);
        }
        return new ResponseApiVO(-1,"查询某商品评价列表失败",null);
    }


    @Token
    @ApiOperation(value = "查询某店铺的总商品评价",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/toEvaluateStoreList/{token}")
    @ResponseBody
    public ResponseApiVO<OrderViewEvaluateStoreListVO> toEvaluateStoreList(@PathVariable String token , @RequestBody @ApiParam(name="args",value="传入json格式",required=true)OrderViewEvaluateStoreListDTO dto){
        try {

            return this.appOrderEvaluationService.evaluateListByStore(token,dto);
        }catch (Exception e){
            logger.error("AppOrderEvaluationController-toEvaluateStoreList-》查询某店铺的总商品评价-》系统异常",e);
        }
        return new ResponseApiVO(-1,"查询某店铺的总商品评价失败",null);
    }
}
