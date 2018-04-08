package com.ddw.controller;

import com.ddw.beans.UserInfoDTO;
import com.gen.common.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * 访问地址：/swagger-ui.html
 */
@Api(description = "会员用户用例")
@RestController
@RequestMapping("/user")
public class UserController {

    @ApiOperation(value = "会员注册用例")
   // @ApiImplicitParam(name = "args", value = "参数", required = true, dataType = "UserInfoDTO")
    @PostMapping("/save")
    public ResponseVO save( @RequestBody @ApiParam(name="参数",value="传入json格式",required=true)UserInfoDTO args){
        System.out.println(args+"---");
        return new ResponseVO();
    }

    @ApiOperation(value = "会员修改资料用例")
    @PostMapping("/update")
    public ResponseVO update( @RequestBody @ApiParam(name="参数",value="传入json格式",required=true)UserInfoDTO args){
        System.out.println(args+"---");
        return new ResponseVO();
    }

    @ApiOperation(value = "会员查询资料用例")
    @PostMapping("/query")
    public ResponseVO query(@ApiParam(value = "账号", required = true) @RequestParam String userName){
        System.out.println(userName+"---");
        return new ResponseVO();
    }
}
