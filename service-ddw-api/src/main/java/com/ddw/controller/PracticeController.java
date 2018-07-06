package com.ddw.controller;


import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.*;
import com.ddw.services.GameService;
import com.ddw.services.ReviewPracticeService;
import com.ddw.services.UserInfoService;
import com.ddw.token.Token;
import com.ddw.token.TokenUtil;
import com.gen.common.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 访问地址：/swagger-ui.html
 */
@Api(value = "代练用例",description = "代练用例",tags = {"代练用例"})
@RestController
@RequestMapping("/ddwapp/practice")
public class PracticeController {
    private final Logger logger = Logger.getLogger(PracticeController.class);
    @Autowired
    private ReviewPracticeService reviewPracticeService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private GameService gameService;

    @Token
    @ApiOperation(value = "代练认证申请用例")
    @PostMapping("/apply/{token}")
    public ResponseApiVO apply(@PathVariable String token,
                               @RequestParam(value = "gameId") @ApiParam(name = "gameId",value="游戏表对应id", required = true) String gameId,
                               @RequestParam(value = "rankId") @ApiParam(name = "rankId",value="段位表对应id", required = true) String rankId,
                               @RequestParam(value = "photograph1") @ApiParam(name = "photograph1",value="游戏截图", required = true) MultipartFile photograph1,
                               @RequestParam(value = "photograph2") @ApiParam(name = "photograph2",value="游戏截图", required = true) MultipartFile photograph2,
                               @RequestParam(value = "photograph3") @ApiParam(name = "photograph3",value="游戏截图", required = true) MultipartFile photograph3){
        try {
            String openid = TokenUtil.getUserObject(token).toString();

            UserInfoVO user = userInfoService.queryByOpenid(openid);
            return reviewPracticeService.apply(TokenUtil.getUserId(token),TokenUtil.getUserName(token),gameId,rankId,photograph1,photograph2,photograph3);
        }catch (Exception e){
            logger.error("PracticeController->apply",e);
            return new ResponseApiVO(-1,"提交失败",null);
        }
    }


//    @Token
//    @ApiOperation(value = "代练信息查询")
//    @PostMapping("/query/{token}")
//    public ResponseVO query(@PathVariable String token){
//        try {
//            UserInfoVO user = userInfoService.query(TokenUtil.getUserId(token));
//            return new ResponseVO(1,"成功",user);
//        }catch (Exception e){
//            logger.error("GoddessController->query",e);
//            return new ResponseVO(-1,"提交失败",null);
//        }
//    }

    @Token
    @ApiOperation(value = "游戏列表")
    @PostMapping("/gameList/{token}")
    public ResponseVO gameList(@PathVariable String token){
        try {
            JSONObject json = new JSONObject();
            json.put("gameList",gameService.getGameList());
            return new ResponseVO(1,"成功",json);
        }catch (Exception e){
            logger.error("PracticeController->gameList",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @Token
    @ApiOperation(value = "代练排行榜")
    @PostMapping("/queryList/{token}")
    public ResponseVO queryList(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)PageDTO pageDTO){
        try {
            //TODO 根据接单排行代练信息
            return reviewPracticeService.practiceList(token,pageDTO.getPageNum(),pageDTO.getPageSize());
        }catch (Exception e){
            logger.error("PracticeController->queryList",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @ApiOperation(value = "代练资料查询用例")
    @PostMapping("/query/{token}")
    public ResponseApiVO<PracticeVO> query(@PathVariable String token,
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
            //TODO 查询代练信息,舍弃原本会员资料返回
            List<PhotographPO> photographList = userInfoService.queryPhotograph(String.valueOf(userVO.getId()));
            userVO.setPhotograph(photographList);
            userVO.setToken(token);
            userVO.setIdentifier(userVO.getOpenid());
            return new ResponseApiVO(1,"成功",userVO);
        }catch (Exception e){
            logger.error("PracticeController->query",e);
            return new ResponseApiVO(-1,"提交失败",null);
        }
    }

    @ApiOperation(value = "约代练申请")
    @PostMapping("/gameApply/{token}")
    public ResponseApiVO gameApply(@PathVariable String token,
                                           @RequestBody @ApiParam(name = "args",value="传入json格式", required = false) PracticeGameApplyDTO practiceGameApplyDTO){
        try {
            //TODO 判断代练是否开启预约,提交游戏编号,当前段位包含几星,目标段位包含几星,代练编号,返回drBusinessCode
            return new ResponseApiVO(1,"成功",null);
        }catch (Exception e){
            logger.error("PracticeController->gameApply",e);
            return new ResponseApiVO(-1,"提交失败",null);
        }
    }

    @ApiOperation(value = "约代练状态查询")
    @PostMapping("/gameApplyState/{token}")
    public ResponseApiVO gameApplyState(@PathVariable String token,
                                   @RequestBody @ApiParam(name = "args",value="传入json格式", required = false) PracticeGameApplyStateDTO practiceGameApplyStateDTO){
        try {
            //TODO 查询代练接单状态
            return new ResponseApiVO(1,"成功",null);
        }catch (Exception e){
            logger.error("PracticeController->gameApplyState",e);
            return new ResponseApiVO(-1,"提交失败",null);
        }
    }

    @ApiOperation(value = "提交结算申请")
    @PostMapping("/settlement/{token}")
    public ResponseApiVO<PracticeVO> settlement(@PathVariable String token,
                                               @RequestBody @ApiParam(name = "args",value="传入json格式", required = false) PracticeSettlementDTO practiceSettlementDTO){
        try {
            //TODO 原段位包含几星(不可修改),目前段位包含几星,代练编号,返回需要支付金额,如果段位星级比原段位星级低,则走赔付流程,双倍赔付
            return new ResponseApiVO(1,"成功",null);
        }catch (Exception e){
            logger.error("PracticeController->settlement",e);
            return new ResponseApiVO(-1,"提交失败",null);
        }
    }

    @ApiOperation(value = "评价")
    @PostMapping("/evaluation/{token}")
    public ResponseApiVO<PracticeVO> evaluation(@PathVariable String token,
                                                @RequestBody @ApiParam(name = "args",value="传入json格式", required = false) PracticeEvaluationDTO practiceEvaluationDTO){
        try {
            //TODO 写入ddw_practice_evaluation_detail代练评分明细表,以及更新ddw_practice_evaluation代练平均评分表
            return new ResponseApiVO(1,"成功",null);
        }catch (Exception e){
            logger.error("PracticeController->evaluation",e);
            return new ResponseApiVO(-1,"提交失败",null);
        }
    }
}
