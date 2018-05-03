package com.ddw.controller;

import com.ddw.beans.*;
import com.ddw.services.AppStoresService;
import com.ddw.token.Token;
import io.swagger.annotations.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/ddwapp/stores")
@Api(description="门店接口",tags ="门店接口")
public class AppStoresController {

    private final Logger logger = Logger.getLogger(AppStoresController.class);


    @Autowired
    private AppStoresService appStoresService;


    @Token
    @PostMapping("/show-nearby-stores/{token}")
    @ApiOperation(value = "定位展示门店",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseApiVO<ListVO<AppStoresShowNearbyVO>> showNearbyStores(@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)AppStoresShowNearbyDTO args){
        try{
            return this.appStoresService.showNearby(args);
        }catch (Exception e){
            logger.error("AppStoresController->showNearbyStores",e);
            return new ResponseApiVO<>(-1,"展示门店失败",null);
        }
    }

    public static void main(String[] args) {
        System.out.println(Base64Utils.encodeToString("12412409125839395555".getBytes()));
    }

}
