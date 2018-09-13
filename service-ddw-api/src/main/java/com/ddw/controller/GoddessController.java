package com.ddw.controller;


import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.*;
import com.ddw.enums.DynamicsRoleTypeEnum;
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
    @ApiOperation(value = "女神信息查询,不传参数默认查询自己")
    @PostMapping("/query/{token}")
    public ResponseVO query(@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=false)CodeDTO dto){
        try {
            int userId = TokenUtil.getUserId(token);
            if(dto != null && dto.getCode() !=null){
                userId = dto.getCode();
            }
            UserInfoVO user = userInfoService.query(userId);
            user.setPhotograph(userInfoService.queryPhotograph(userId));
            return new ResponseVO(1,"成功",user);
        }catch (Exception e){
            logger.error("GoddessController->query",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }
    @Token
    @ApiOperation(value = "女神（关注，粉丝，贡献）查询")
    @PostMapping("/query/afc/{token}")
    public ResponseApiVO<GoddessInfoVO> queryAfc(@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)GoddessCodeDTO dto){
        try {
            return this.reviewGoddessService.getGoddessFansAndContr(token,dto.getGoddessCode());
        }catch (Exception e){
            logger.error("GoddessController->queryAfc",e);
            return new ResponseApiVO(-1,"查询失败",null);
        }
    }

    @Token
    @ApiOperation(value = "女神动态")
    @PostMapping("/query/dynamics/{token}")
    public ResponseApiVO<DynamicsVO> queryDynamics(@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)DynamicsDTO dto){
        try {
            return this.reviewGoddessService.getDynamics(token, DynamicsRoleTypeEnum.RoleType1.getCode()+","+DynamicsRoleTypeEnum.RoleType3.getCode(),dto);
        }catch (Exception e){
            logger.error("GoddessController->queryDynamics",e);
            return new ResponseApiVO(-1,"女神动态",null);
        }
    }

    @Token
    @ApiOperation(value = "女神排行榜")
    @PostMapping("/queryList/{token}")
    public ResponseVO queryList(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)PageDTO pageDTO){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("list",reviewGoddessService.goddessList(TokenUtil.getUserId(token),pageDTO));
            return new ResponseVO(1,"成功",jsonObject);
        }catch (Exception e){
            logger.error("GoddessController->queryList",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @ApiOperation(value = "评价")
    @PostMapping("/evaluation/{token}")
    public ResponseVO evaluation(@PathVariable String token,
                                 @RequestBody @ApiParam(name = "args",value="传入json格式", required = false) GoddessEvaluationDetailDTO goddessEvaluationDetailDTO){
        try {
            //写入ddw_goddess_evaluation_detail女神评分明细表,以及更新ddw_goddess_evaluation女神平均评分表
            ResponseVO responseVO = reviewGoddessService.insertGoddessEvaluationDetail(token,goddessEvaluationDetailDTO);

            return responseVO;
        }catch (Exception e){
            logger.error("GoddessController->evaluation",e);
            return new ResponseVO(-1,"评价失败",null);
        }
    }

    @Token
    @ApiOperation(value = "查询女神评价列表")
    @PostMapping("/evaluationDetailList/{token}")
    public ResponseApiVO getGoddessEvaluationDetailList(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)GoddessEvaluationDetailListDTO goddessEvaluationDetailListDTO){
        try {
            return reviewGoddessService.getGoddessEvaluationDetailList(goddessEvaluationDetailListDTO);
        }catch (Exception e){
            logger.error("GoddessController->getGoddessEvaluationDetailList",e);
            return new ResponseApiVO(-1,"查询女神评价详情",null);
        }
    }

}
