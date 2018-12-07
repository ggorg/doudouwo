package com.ddw.controller;

import com.ddw.beans.ResponseApiVO;
import com.ddw.services.PayCenterService;
import com.ddw.services.StraregyService;
import com.ddw.services.UserInfoService;
import com.ddw.token.Token;
import com.gen.common.util.CacheUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private PayCenterService  payCenterService;

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
    @ApiOperation(value = "清缓存")
    @PostMapping("/cleanCache/{token}")
    @ResponseBody
    @Token
    public ResponseApiVO cleanCache(@PathVariable String token,
                                    @RequestParam(value = "storeId") @ApiParam(name = "storeId",defaultValue = "1",value="门店ID", required = false) Integer storeId){
        try{
            CacheUtil.delete("publicCache","appIndexPractice"+storeId);
            CacheUtil.delete("publicCache", "appIndexGoddess");
            CacheUtil.delete("publicCache", "gameList");
            return new ResponseApiVO(1,"成功",null);
        }catch (Exception e){
            logger.error("TestController->cleanCache-》系统异常",e);
            return new ResponseApiVO(-1,"失败",null);
        }
    }

    @ApiOperation(value = "删用户")
    @PostMapping("/deleteUser/{token}")
    @ResponseBody
    @Token
    public ResponseApiVO deleteUser(@PathVariable String token,
                                    @RequestParam(value = "userId") @ApiParam(name = "userId",defaultValue = "120",value="需要删除的会员编号", required = true) Integer userId){
        try{
            return userInfoService.deleteUser(userId);
        }catch (Exception e){
            logger.error("TestController->deleteUser-》系统异常",e);
            return new ResponseApiVO(-1,"失败",null);
        }
    }

    @ApiOperation(value = "订单生成合并图片")
    @PostMapping("/privatehandle/{token}")
    @ResponseBody
    @Token
    public ResponseApiVO privatehandle(@PathVariable String token,
                                    @RequestParam(value = "cmd") String cmd){
        try{
            if("84837891".equals(cmd)){
                payCenterService.handleOrderImg();
            }else if("84837892".equals(cmd)){

            }
            return new ResponseApiVO(1,"成功",null);

        }catch (Exception e){
            logger.error("TestController->mergeOrderImg-》系统异常",e);
            return new ResponseApiVO(-1,"失败",null);
        }
    }
}
