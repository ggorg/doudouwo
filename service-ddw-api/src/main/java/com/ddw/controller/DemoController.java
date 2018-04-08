package com.ddw.controller;

import com.ddw.beans.DemoDTO;
import com.gen.common.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 访问地址：/swagger-ui.html
 */
@RestController
@RequestMapping("/demo")
@Api(description="demo用例")
public class DemoController {

    @ApiOperation(value = "demo用例")
   // @ApiImplicitParam(name = "args", value = "参数", required = true, dataType = "DemoDTO")

    @PostMapping("/t")
    public ResponseVO test( @RequestBody @ApiParam(name="参数",value="传入json格式",required=true)DemoDTO args){
        System.out.println(args+"---");
        return new ResponseVO();
    }
}
