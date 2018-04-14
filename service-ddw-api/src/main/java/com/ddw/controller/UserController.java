package com.ddw.controller;


import com.ddw.beans.UserInfoDTO;
import com.ddw.services.UserInfoService;
import com.gen.common.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 访问地址：/swagger-ui.html
 */
@Api(description = "会员用户用例")
@RestController
@RequestMapping("/ddwapp/user")
public class UserController {
    private final Logger logger = Logger.getLogger(UserController.class);
    @Autowired
    private UserInfoService userInfoService;

    @ApiOperation(value = "会员注册用例")
   // @ApiImplicitParam(name = "args", value = "参数", required = true, dataType = "UserInfoDTO")
    @PostMapping("/save")
    public ResponseVO save(@RequestBody @ApiParam(name="args",value="传入json格式",required=true)UserInfoDTO userInfoDTO){
        try {
            Map userMap = userInfoService.queryByOpenid(userInfoDTO.getOpenid());
            if(userMap == null || userMap.isEmpty()){
                return userInfoService.save(userInfoDTO);
            }else{
                return new ResponseVO(-2,"账号已存在",null);
            }
        }catch (Exception e){
            logger.error("UserController->save",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @ApiOperation(value = "会员修改资料用例")
    @PostMapping("/update")
    public ResponseVO update( @RequestBody @ApiParam(name="args",value="传入json格式",required=true)UserInfoDTO userInfoDTO){
        try {
            return userInfoService.update(userInfoDTO);
        }catch (Exception e){
            logger.error("UserController->update",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @ApiOperation(value = "会员查询资料用例")
    @PostMapping("/query")
    public Map query(@ApiParam(value = "args", required = true) String username){
        try {
            return userInfoService.query(username);
        }catch (Exception e){
            logger.error("UserInfoController->update",e);
            return null;
        }
    }
}
