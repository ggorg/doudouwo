package com.ddw.controller;


import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.*;
import com.ddw.services.RealNameReviewService;
import com.ddw.services.UserInfoService;
import com.ddw.token.Token;
import com.ddw.token.TokenUtil;
import com.gen.common.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 访问地址：/swagger-ui.html
 */
@Api(value = "会员用户用例",description = "会员用户用例",tags = {"会员用户用例"})
@RestController
@RequestMapping("/ddwapp/user")
public class UserController {
    private final Logger logger = Logger.getLogger(UserController.class);
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private RealNameReviewService realNameReviewService;

    @ApiOperation(value = "会员注册用例")
   // @ApiImplicitParam(name = "args", value = "参数", required = true, dataType = "UserInfoDTO")
    @PostMapping("/save")
    public ResponseVO save(@RequestBody @ApiParam(name="args",value="传入json格式",required=true)UserInfoDTO userInfoDTO){
        try {
            if(!StringUtils.isBlank(userInfoDTO.getOpenid())){
                UserInfoPO userPO = userInfoService.queryByOpenid(userInfoDTO.getOpenid());
                UserInfoVO userVO = new UserInfoVO();
                String token = TokenUtil.createToken(userInfoDTO.getOpenid());
                JSONObject json = new JSONObject();
                json.put("token",token);
                if(userPO == null){
                    userInfoService.save(userInfoDTO);
                    userPO = userInfoService.queryByOpenid(userInfoDTO.getOpenid());
                    PropertyUtils.copyProperties(userVO,userPO);
                    json.put("userInfo",userVO);
                    return new ResponseVO(1,"注册成功",json);
                }else{
                    PropertyUtils.copyProperties(userVO,userPO);
                    List<PhotographPO> photographList = userInfoService.queryPhotograph(String.valueOf(userPO.getId()));
                    userVO.setPhotograph(photographList);
                    json.put("userInfo",userVO);
                    json.put("userInfo",userVO);
                    return new ResponseVO(2,"账号已存在",json);
                }
            }else{
                return new ResponseVO(-2,"用户openid不能为空",null);
            }
        }catch (Exception e){
            logger.error("UserController->save",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @Token
    @ApiOperation(value = "会员修改资料用例")
    @PostMapping("/update/{token}")
    public ResponseVO update( @PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)UserInfoUpdateDTO userInfoUpdateDTO){
        try {
            return userInfoService.update(userInfoUpdateDTO);
        }catch (Exception e){
            logger.error("UserController->update",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @Token
    @ApiOperation(value = "会员实名认证申请用例,是否需要实名认证可以通过用户资料的身份证是否为空判断")
    @PostMapping("/realName/{token}")
    public ResponseVO realName( @PathVariable String token,
                                @RequestParam(value = "id") @ApiParam(name = "id",value="会员id", required = true) String id,
                                @RequestParam(value = "realName") @ApiParam(name = "realName",value="真实姓名", required = true) String realName,
                                @RequestParam(value = "idcard") @ApiParam(name = "idcard",value="身份证", required = true) String idcard,
                                @RequestParam(value = "idcardFront") @ApiParam(name = "idcardFront",value="身份证正面", required = true) MultipartFile idcardFront,
                                @RequestParam(value = "idcardOpposite") @ApiParam(name = "idcardOpposite",value="身份证反面", required = true) MultipartFile idcardOpposite){
        try {
            return realNameReviewService.realName(id,realName,idcard,idcardFront,idcardOpposite);
        }catch (Exception e){
            logger.error("UserController->realName",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @Token
    @ApiOperation(value = "会员相册上传用例")
    @PostMapping(value = "/uploadPhotograph/{token}",consumes = "multipart/*",headers = "content-type=multipart/form-data" )
    public ResponseVO uploadPhotograph( @PathVariable String token,
                                @RequestParam(value = "id") @ApiParam(name = "id",value="会员id", required = true) String id,
                                @RequestParam(value = "photograph") @ApiParam(name = "photograph",value="上传照片,支持多张", required = true) MultipartFile[]photograph){
        try {
            if(StringUtils.isBlank(id) || photograph.length == 0){
                return new ResponseVO(-2,"参数不正确",null);
            }
            return userInfoService.uploadPhotograph(id,photograph);
        }catch (Exception e){
            logger.error("UserController->uploadPhotograph",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

}
