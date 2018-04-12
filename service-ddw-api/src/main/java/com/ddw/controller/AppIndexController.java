package com.ddw.controller;

import com.ddw.beans.*;
import com.ddw.token.Token;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/ddwapp/index")
@Api(description="首页",tags = "首页")
public class AppIndexController {

    @PostMapping("/{token}")
    @ApiOperation(value = "首页",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Token
    public ResponseVO<AppIndexVO> toIndex(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)AppIndexDTO args){

        List array=new ArrayList();


        return new ResponseVO(1,"获取成功",new AppIndexVO());
    }
}
