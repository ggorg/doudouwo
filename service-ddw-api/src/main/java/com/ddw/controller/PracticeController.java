package com.ddw.controller;


import com.ddw.beans.ResponseApiVO;
import com.ddw.beans.UserInfoVO;
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
            return reviewPracticeService.apply(user,gameId,rankId,photograph1,photograph2,photograph3);
        }catch (Exception e){
            logger.error("UserController->realName",e);
            return new ResponseApiVO(-1,"提交失败",null);
        }
    }


    @Token
    @ApiOperation(value = "代练信息查询")
    @PostMapping("/query/{token}")
    public ResponseVO query(@PathVariable String token){
        try {
            String openid = TokenUtil.getUserObject(token).toString();
            UserInfoVO user = userInfoService.queryByOpenid(openid);
            return new ResponseVO(1,"成功",user);
        }catch (Exception e){
            logger.error("GoddessController->query",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @Token
    @ApiOperation(value = "代练列表查询")
    @PostMapping("/queryList/{token}")
    public ResponseVO queryList(@PathVariable String token,
                                @RequestParam(value = "pageNum") @ApiParam(name = "pageNum",value="页码", required = true) Integer pageNum,
                                @RequestParam(value = "pageSize") @ApiParam(name = "pageSize",value="显示数量", required = true) Integer pageSize){
        try {
            return reviewPracticeService.practiceList(token,pageNum,pageSize);
        }catch (Exception e){
            logger.error("GoddessController->queryList",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

}
