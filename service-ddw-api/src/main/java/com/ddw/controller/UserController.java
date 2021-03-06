package com.ddw.controller;


import com.ddw.beans.*;
import com.ddw.enums.SendVaildTypeEnum;
import com.ddw.services.ConsumeRankingListService;
import com.ddw.services.ReviewRealNameService;
import com.ddw.services.ReviewService;
import com.ddw.services.UserInfoService;
import com.ddw.token.Token;
import com.ddw.token.TokenUtil;
import com.ddw.util.LoginAuthApiUtil;
import com.ddw.util.MsgUtil;
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

    @Autowired
    private ConsumeRankingListService consumeRankingListService;

    @Autowired
    private ReviewService reviewService;

    @ApiOperation(value = "微信和QQ登录")
    @PostMapping("/loginPublic")
    public ResponseApiVO<UserInfoVO> loginPublic(@RequestBody @ApiParam(name="args",value="传入json格式",required=true)LoginPublicDTO dto){
        try {
            ResponseApiVO<UserInfoDTO> res=LoginAuthApiUtil.oauth(dto);
            if(res.getReCode()==1 || res.getReCode().equals(1)){
                return save(res.getData());
            }
        }catch (Exception e){
            logger.error("loginWeiXin",e);

        }
        return new ResponseApiVO<>(-2,"登录失败",null);
    }

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
                UserInfoVO userVO = userInfoService.loginByOpenid(userInfoDTO.getOpenid());
                String token = TokenUtil.createToken(userInfoDTO.getOpenid());
                if (userVO == null) {
                    ResponseVO re = userInfoService.save(userInfoDTO);
                    userVO = userInfoService.loginByOpenid(userInfoDTO.getOpenid());
                    userVO.setToken(token);
                    userVO.setIdentifier(userVO.getOpenid());
                    userVO.setInviteCode(userInfoService.createInviteCode(userVO.getId()));
                    userVO.setLiveRadioFlag(0);
                    userVO.setUserSign(ts.createSign(userVO.getOpenid()));
                    TokenUtil.putUserInfo(token, userVO);
                    userInfoService.updateInviteCode(userVO);//更新邀请码
                    return new ResponseApiVO(1, "注册成功", userVO);
                } else {
                    List<PhotographPO> photographList = userInfoService.queryPhotograph(userVO.getId());
                    userVO.setPhotograph(photographList);
                    userVO.setToken(token);
                    userVO.setIdentifier(userVO.getOpenid());
                    userInfoService.setLiveRadioFlag(userVO,token);
                    userVO.setUserSign(ts.createSign(userVO.getOpenid()));
                    TokenUtil.putUserInfo(token, userVO);
                    // 这里需要判断是老带新带来的新用户登录,首次登录赠送新老用户优惠券,更新老带新状态
                    if(userVO.getFirstLoginFlag().equals(0) || userVO.getFirstLoginFlag() == 0){
                        userInfoService.dealOldBringingNew(userVO.getOpenid(),userVO.getId());
                    }
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
    public ResponseApiVO<UserInfoVO> query(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=false)UserQueryDTO userQueryDTO){
        try {
            UserInfoVO userVO = new UserInfoVO();
            if(userQueryDTO != null && (userQueryDTO.getId() != null || !StringUtils.isBlank(userQueryDTO.getOpenid()))){
                if(userQueryDTO.getId() != null){
                    userVO = userInfoService.query(userQueryDTO.getId());
                }else {
                    userVO = userInfoService.queryByOpenid(userQueryDTO.getOpenid());
                }
            }else{
                userVO = userInfoService.query(TokenUtil.getUserId(token));
            }
            if (userVO == null) {
                return new ResponseApiVO(-2,"账号不存在",null);
            }
            List<PhotographPO> photographList = userInfoService.queryPhotograph(userVO.getId());
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
                                @RequestParam(value = "phone") @ApiParam(name = "phone",value="手机号码", required = true) String phone,
                                @RequestParam(value = "code") @ApiParam(name = "code",value="验证码", required = true) String code,
                                @RequestParam(value = "idcard") @ApiParam(name = "idcard",value="身份证", required = true) String idcard,
                                @RequestParam(value = "selfie") @ApiParam(name = "selfie",value="自拍", required = true) MultipartFile selfie,
                                @RequestParam(value = "idcardFront") @ApiParam(name = "idcardFront",value="身份证正面", required = true) MultipartFile idcardFront,
                                @RequestParam(value = "idcardOpposite") @ApiParam(name = "idcardOpposite",value="身份证反面", required = true) MultipartFile idcardOpposite){
        try {
            if(MsgUtil.verifyCode(phone,code)){
                return reviewRealNameService.realName(TokenUtil.getUserId(token),realName,phone,idcard,selfie,idcardFront,idcardOpposite);
            }else {
                return new ResponseApiVO(-2,"失败,验证码不对",null);
            }
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
    public ResponseVO deletePhotograph(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true) UserDeletePhotoDTO userDeletePhotoDTO){
        try {
            if(userDeletePhotoDTO != null){
                return userInfoService.deletePhotograph(userDeletePhotoDTO.getPhotograph());
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
    public ResponseVO getPhotograph(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)PageNoDTO pageNoDTO){
        try {
            return userInfoService.getPhotograph(TokenUtil.getUserId(token),pageNoDTO.getPageNo(),10);
        }catch (Exception e){
            logger.error("UserController->getPhotograph",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @ApiOperation(value = "发送手机验证码")
    @PostMapping("/sendVaildCode/{token}")
    public ResponseVO sendVaildCode(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)UserValidPhoneDTO userValidPhoneDTO){
        try {
            logger.info("sendVaildCode->request->"+userValidPhoneDTO);
            if(StringUtils.isBlank(SendVaildTypeEnum.getName(userValidPhoneDTO.getType()))){
                return new ResponseVO(-3,"验证码类型异常",null);
            }
            String res= "";
            //发送验证码类型,1实名验证,2找回支付密码
            switch (userValidPhoneDTO.getType()){
                case 1:
                    res=MsgUtil.sendVaildCode(userValidPhoneDTO.getTelphone());
                    break;
                case 2:
                    res=MsgUtil.sendPayPwdMsg(userValidPhoneDTO.getTelphone());
                    break;
                default:
                    res=MsgUtil.sendOtherVaildCode(userValidPhoneDTO.getTelphone());
                break;
            }
            if(res.equals("-1")){
                return new ResponseVO(-2,"抱歉，操作过于频繁",null);

            }if(res.equals("-2")){
                return new ResponseVO(-2,"抱歉，短信发送已超过一天5条上限",null);

            }
            return new ResponseVO(1,"成功",null);
        }catch (Exception e){
            logger.error("UserController->sendVaildCode",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @ApiOperation(value = "手机短信认证,绑定手机")
    @PostMapping("/bindPhone/{token}")
    public ResponseVO bindPhone(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)UserValidPhoneCodeDTO userValidPhoneCodeDTO){
        try {
            if(MsgUtil.verifyCode(userValidPhoneCodeDTO.getTelphone(),userValidPhoneCodeDTO.getCode())){
                userInfoService.updatePhone(TokenUtil.getUserId(token),userValidPhoneCodeDTO.getTelphone());
                return new ResponseVO(1,"成功",null);
            }else {
                return new ResponseVO(-2,"失败,验证码不对",null);
            }
        }catch (Exception e){
            logger.error("UserController->bindPhone",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @ApiOperation(value = "只验证手机验证码")
    @PostMapping("/vaildCode/{token}")
    public ResponseVO vaildCode(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)UserValidPhoneCodeDTO userValidPhoneCodeDTO){
        try {
            if(MsgUtil.verifyCode(userValidPhoneCodeDTO.getTelphone(),userValidPhoneCodeDTO.getCode())){
                return new ResponseVO(1,"成功",null);
            }else {
                return new ResponseVO(-2,"失败,验证码不对",null);
            }
        }catch (Exception e){
            logger.error("UserController->vaildCode",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @Token
    @ApiOperation(value = "查询用户的贡献值")
    @PostMapping("/query/user/allcons/{token}")
    public ResponseApiVO<ContributeNumVO> queryCons(@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)OpenIdDTO dto){
        try {
            return this.consumeRankingListService.getUserSum(token,dto);
        }catch (Exception e){
            logger.error("UserController->queryCons",e);
            return new ResponseApiVO(-1,"查询用户的贡献值失败",null);
        }
    }

}
