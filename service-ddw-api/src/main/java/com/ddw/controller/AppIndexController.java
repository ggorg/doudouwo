package com.ddw.controller;

import com.ddw.beans.AppIndexDTO;
import com.ddw.beans.AppIndexVO;
import com.ddw.beans.PageNoDTO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.services.AppIndexService;
import com.ddw.token.Token;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/ddwapp/index")
@Api(description="首页",tags = "首页")
public class AppIndexController {
    private final Logger logger = Logger.getLogger(AppIndexController.class);
    @Autowired
    private AppIndexService appIndexService;
    @PostMapping("/{token}")
    @ApiOperation(value = "首页",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Token
    public ResponseApiVO<AppIndexVO> toIndex(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)AppIndexDTO args){
        try {
            return appIndexService.toIndex(token,args);
        } catch (Exception e) {
            logger.error("AppIndexController->toIndex->首页->系统异常",e);
        }
        return new ResponseApiVO(-1,"失败",null);

    }


}
