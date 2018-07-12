package com.ddw.controller;


import com.ddw.beans.*;
import com.ddw.services.ActiveCenterService;
import com.ddw.token.Token;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/ddwapp/active/center")
@Api(description="活动中心",tags = "活动中心")
public class ActiveCenterController {
    private final Logger logger = Logger.getLogger(ActiveCenterController.class);

    @Autowired
    private ActiveCenterService activeCenterService;


    @PostMapping("/query/{token}")
    @ApiOperation(value = "活动列表",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Token
    public ResponseApiVO<ListVO<ActiveVO>> toList(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)PageNoDTO args){
        try {
            return activeCenterService.getList(token,args);
        } catch (Exception e) {
            logger.error("ActiveCenterController->toList->活动列表->系统异常",e);
        }
        return new ResponseApiVO(-1,"失败",null);

    }
}
