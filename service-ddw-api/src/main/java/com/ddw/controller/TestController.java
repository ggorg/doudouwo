package com.ddw.controller;

import com.ddw.beans.ResponseApiVO;
import com.ddw.services.StraregyService;
import com.ddw.token.Token;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ddwapp/test")
@Api(description="调试",tags = "调试")
public class TestController {
    private final Logger logger = Logger.getLogger(TestController.class);

    @Autowired
    private StraregyService straregyService;

    @PostMapping("/recharge/{token}")
    @ResponseBody
    @Token
    public ResponseApiVO recharge(@PathVariable String token, @RequestParam(value = "money") @ApiParam(name = "money",value="充值金额", required = true) int money,
                           @RequestParam(value = "userId") @ApiParam(name = "userId",value="会员id", required = true) int userId){
       try{
           straregyService.recharge(money,userId);
           return new ResponseApiVO(1,"成功",null);
       }catch (Exception e){
           logger.error("TestController->recharge-》系统异常",e);
           return new ResponseApiVO(-1,"失败",null);
       }
    }

}
