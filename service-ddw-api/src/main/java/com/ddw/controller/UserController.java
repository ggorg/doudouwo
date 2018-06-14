package com.ddw.controller;


import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.*;
import com.ddw.services.ReviewRealNameService;
import com.ddw.services.UserInfoService;
import com.ddw.token.Token;
import com.ddw.token.TokenUtil;
import com.gen.common.vo.ResponseVO;
import com.tls.sigcheck.tls_sigcheck;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
    private ReviewRealNameService reviewRealNameService;

    @Autowired
    private tls_sigcheck ts;

    @ApiOperation(value = "会员注册用例")
    // @ApiImplicitParam(name = "args", value = "参数", required = true, dataType = "UserInfoDTO")
    @PostMapping("/save")
    public ResponseApiVO<UserInfoVO> save(@RequestBody @ApiParam(name="args",value="传入json格式",required=true)UserInfoDTO userInfoDTO){
        try {
            if(StringUtils.isBlank(userInfoDTO.getOpenid())) {
                return new ResponseApiVO(-2,"用户openid不能为空",null);
            }else if(userInfoDTO.getRegisterType() == null){
                return new ResponseApiVO(-3,"registerType注册类型不能为空",null);
            }else{
                UserInfoVO userVO = userInfoService.queryByOpenid(userInfoDTO.getOpenid());
                String token = TokenUtil.createToken(userInfoDTO.getOpenid());
                if (userVO == null) {
                    userInfoService.save(userInfoDTO);
                    userVO = userInfoService.queryByOpenid(userInfoDTO.getOpenid());
                    userVO.setToken(token);
                    userVO.setIdentifier(userVO.getOpenid());
                    userInfoService.setLiveRadioFlag(userVO,token);
                    userVO.setUserSign(ts.createSign(userVO.getOpenid()));
                    TokenUtil.putUseridAndName(token, userVO.getId(), userVO.getNickName());
                    return new ResponseApiVO(1, "注册成功", userVO);
                } else {
                    List<PhotographPO> photographList = userInfoService.queryPhotograph(String.valueOf(userVO.getId()));
                    userVO.setPhotograph(photographList);
                    userVO.setToken(token);
                    userVO.setIdentifier(userVO.getOpenid());
                    userInfoService.setLiveRadioFlag(userVO,token);
                    userVO.setUserSign(ts.createSign(userVO.getOpenid()));
                    TokenUtil.putUseridAndName(token, userVO.getId(), userVO.getNickName());
                    return new ResponseApiVO(2, "账号已存在", userVO);
                }
            }
        }catch (Exception e){
            logger.error("UserController->save",e);
            return new ResponseApiVO(-1,"提交失败",null);
        }
    }

    @ApiOperation(value = "会员资料查询用例")
    @PostMapping("/query/{token}")
    public ResponseApiVO<UserInfoVO> query(@PathVariable String token,
                                           @RequestBody @ApiParam(name = "id",value="会员id,传入json格式,如:{\"id\":\"1\"}", required = false) JSONObject json){
        try {
            UserInfoVO userVO = new UserInfoVO();
            if(!json.isEmpty()){
                userVO = userInfoService.query(Integer.valueOf(json.getString("id")));
            }else{
                userVO = userInfoService.query(TokenUtil.getUserId(token));
            }
            if (userVO == null) {
                return new ResponseApiVO(-2,"账号不存在",null);
            }
            userInfoService.setLiveRadioFlag(userVO,token);
            List<PhotographPO> photographList = userInfoService.queryPhotograph(String.valueOf(userVO.getId()));
            userVO.setPhotograph(photographList);
            userVO.setToken(token);
            userVO.setIdentifier(userVO.getOpenid());
            userVO.setUserSign(ts.createSign(userVO.getOpenid()));
            return new ResponseApiVO(1,"成功",userVO);
        }catch (Exception e){
            logger.error("UserController->query",e);
            return new ResponseApiVO(-1,"提交失败",null);
        }
    }

    @Token
    @ApiOperation(value = "会员修改资料用例")
    @PostMapping("/update/{token}")
    public ResponseVO update( @PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)UserInfoUpdateDTO userInfoUpdateDTO){
        try {
            return userInfoService.update(TokenUtil.getUserId(token),userInfoUpdateDTO);
        }catch (Exception e){
            logger.error("UserController->update",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @Token
    @ApiOperation(value = "会员实名认证申请用例,是否需要实名认证可以通过用户资料的身份证是否为空判断")
    @PostMapping("/realName/{token}")
    public ResponseApiVO realName( @PathVariable String token,
                                @RequestParam(value = "realName") @ApiParam(name = "realName",value="真实姓名", required = true) String realName,
                                @RequestParam(value = "idcard") @ApiParam(name = "idcard",value="身份证", required = true) String idcard,
                                @RequestParam(value = "idcardFront") @ApiParam(name = "idcardFront",value="身份证正面", required = true) MultipartFile idcardFront,
                                @RequestParam(value = "idcardOpposite") @ApiParam(name = "idcardOpposite",value="身份证反面", required = true) MultipartFile idcardOpposite){
        try {
            return reviewRealNameService.realName(TokenUtil.getUserId(token),realName,idcard,idcardFront,idcardOpposite);
        }catch (Exception e){
            logger.error("UserController->realName",e);
            return new ResponseApiVO(-1,"提交失败",null);
        }
    }

    @Token
    @ApiOperation(value = "会员相册上传用例")
    @PostMapping(value = "/uploadPhotograph/{token}",consumes = "multipart/*",headers = "content-type=multipart/form-data" )
    public ResponseVO uploadPhotograph( @PathVariable String token,
                                        @RequestParam(value = "photograph") @ApiParam(name = "photograph",value="上传照片,支持多张", required = true) MultipartFile[]photograph){
        try {
            if(photograph != null && photograph.length>0){
                return userInfoService.uploadPhotograph(TokenUtil.getUserId(token).toString(),photograph);
            }else{
                return new ResponseVO(-2,"参数有误",null);
            }
        }catch (Exception e){
            logger.error("UserController->uploadPhotograph",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @ApiOperation(value = "删除会员相册用例")
    @PostMapping("/deletePhotograph/{token}")
    public ResponseVO deletePhotograph(@PathVariable String token,
                                           @RequestBody @ApiParam(name = "photograph",value="相册id,传入json格式,如:{\"photograph\":\"1,2,3\"}", required = true) JSONObject json){
        try {
            if(!json.isEmpty() && json.containsKey("photograph")){
                return userInfoService.deletePhotograph(json.getString("photograph"));
            }else{
                return new ResponseVO(-2,"photograph不能为空",null);
            }
        }catch (Exception e){
            logger.error("UserController->deletePhotograph",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @ApiOperation(value = "查询会员相册用例")
    @PostMapping("/getPhotograph/{token}")
    public ResponseVO getPhotograph(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)PageDTO pageDTO){
        try {
            return userInfoService.getPhotograph(TokenUtil.getUserId(token),pageDTO.getPageNum(),pageDTO.getPageSize());
        }catch (Exception e){
            logger.error("UserController->getPhotograph",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

}
