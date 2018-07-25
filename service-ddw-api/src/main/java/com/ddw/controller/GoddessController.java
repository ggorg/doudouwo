package com.ddw.controller;


import com.ddw.beans.PageDTO;
import com.ddw.beans.UserInfoVO;
import com.ddw.services.ReviewGoddessService;
import com.ddw.services.UserInfoService;
import com.ddw.token.Token;
import com.ddw.token.TokenUtil;
import com.gen.common.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 访问地址：/swagger-ui.html
 */
@Api(value = "女神用例",description = "女神用例",tags = {"女神用例"})
@RestController
@RequestMapping("/ddwapp/goddess")
public class GoddessController {
    private final Logger logger = Logger.getLogger(GoddessController.class);
    @Autowired
    private ReviewGoddessService reviewGoddessService;
    @Autowired
    private UserInfoService userInfoService;

    @Token
    @ApiOperation(value = "申请成为女神")
    @PostMapping("/apply/{token}")
    public ResponseVO apply(@PathVariable String token){
        try {
            String openid = TokenUtil.getUserObject(token).toString();
            int storeId = TokenUtil.getStoreId(token);
            UserInfoVO user = userInfoService.queryByOpenid(openid);
            if(!StringUtils.isBlank(user.getIdcard())) {
                return reviewGoddessService.apply(user, storeId);
            }else{
                return new ResponseVO(-2,"请先实名认证",null);
            }
        }catch (Exception e){
            logger.error("GoddessController->save",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }


    @Token
    @ApiOperation(value = "女神信息查询")
    @PostMapping("/query/{token}")
    public ResponseVO query(@PathVariable String token){
        try {
            UserInfoVO user = userInfoService.query(TokenUtil.getUserId(token));
            return new ResponseVO(1,"成功",user);
        }catch (Exception e){
            logger.error("GoddessController->query",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @Token
    @ApiOperation(value = "女神排行榜")
    @PostMapping("/queryList/{token}")
    public ResponseVO queryList(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)PageDTO pageDTO){
        try {
            return new ResponseVO(1,"成功",reviewGoddessService.goddessList(TokenUtil.getUserId(token),pageDTO));
        }catch (Exception e){
            logger.error("GoddessController->queryList",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

}
