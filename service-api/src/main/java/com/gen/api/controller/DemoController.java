package com.gen.api.controller;

import com.gen.api.beans.DemoBean;
import com.gen.api.vo.DemoVo;
import com.gen.common.vo.ResponseVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @ApiOperation(value = "用例")
    @ApiImplicitParam(name = "args", value = "参数", required = true, dataType = "DemoVo")
    @PostMapping("/t")
    public ResponseVO test(@ApiIgnore @RequestBody DemoVo args){
        System.out.println(args+"---");
        return new ResponseVO();
    }
}
