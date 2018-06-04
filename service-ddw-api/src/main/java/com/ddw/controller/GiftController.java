package com.ddw.controller;

import com.ddw.beans.AppIndexDTO;
import com.ddw.beans.AppIndexVO;
import com.ddw.beans.PageNoDTO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.services.GiftService;
import com.ddw.token.Token;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/ddwapp/gift")
@Api(description="礼物",tags = "礼物")
public class GiftController {
    private final Logger logger = Logger.getLogger(GiftController.class);

    @Autowired
    private GiftService giftService;

    @PostMapping("/list/{token}")
    @ApiOperation(value = "列表",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Token
    public ResponseApiVO list(@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)PageNoDTO args){

       try{
           return this.giftService.getAllGift();
       }catch (Exception e){
           logger.error("GiftController->list-》礼物列表-》系统异常",e);
            return new ResponseApiVO(-1,"失败",null);
       }

    }

}
