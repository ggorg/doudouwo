package com.ddw.controller;

import com.ddw.vo.DemoVo;
import com.gen.common.vo.ResponseVO;
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
public class DemoController {

    @ApiOperation(value = "demo用例")
   // @ApiImplicitParam(name = "args", value = "参数", required = true, dataType = "DemoVo")

    @PostMapping("/t")
    public ResponseVO test( @RequestBody @ApiParam(name="参数",value="传入json格式",required=true)DemoVo args){
        System.out.println(args+"---");
        return new ResponseVO();
    }
}
