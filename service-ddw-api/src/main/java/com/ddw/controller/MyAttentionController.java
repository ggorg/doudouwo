package com.ddw.controller;


import com.ddw.beans.MyAttentionDTO;
import com.ddw.beans.MyAttentionPO;
import com.ddw.beans.UserInfoVO;
import com.ddw.services.MyAttentionService;
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

/**
 * 访问地址：/swagger-ui.html
 */
@Api(value = "我的关注用例",description = "我的关注用例",tags = {"我的关注用例"})
@RestController
@RequestMapping("/ddwapp/myAttention")
public class MyAttentionController {
    private final Logger logger = Logger.getLogger(MyAttentionController.class);
    @Autowired
    private MyAttentionService myAttentionService;
    @Autowired
    private UserInfoService userInfoService;

    @Token
    @ApiOperation(value = "关注添加")
    @PostMapping("/saveOrdelete/{token}")
    public ResponseVO saveOrdelete(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式,女神id和代练id只传其一",required=true)MyAttentionDTO args){
        try {
            String openid = TokenUtil.getUserObject(token).toString();
            UserInfoVO userVO = userInfoService.queryByOpenid(openid);
            MyAttentionPO myAttentionPO = myAttentionService.query(userVO.getId(),args);
            //添加关注
            if(myAttentionPO == null){
                return myAttentionService.save(userVO.getId(),args);
            //取消关注
            }else{
                return myAttentionService.delete(userVO.getId(),args);
            }
        }catch (Exception e){
            logger.error("MyAttentionController->saveOrdelete",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @Token
    @ApiOperation(value = "当前会员关注的女神列表")
    @PostMapping("/query/{token}")
    public ResponseVO queryGoddessByUserId(@PathVariable String token){
        try {
            String openid = TokenUtil.getUserObject(token).toString();
            UserInfoVO userVO = userInfoService.queryByOpenid(openid);
            return new ResponseVO(1,"成功",myAttentionService.queryGoddessByUserId(userVO.getId()));
        }catch (Exception e){
            logger.error("MyAttentionController->queryGoddessByUserId",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @Token
    @ApiOperation(value = "查询女神下有多少关注用户数")
    @PostMapping("/coundUserByGoddess/{token}")
    public ResponseVO coundUserByGoddess(@PathVariable String token,@RequestParam @ApiParam(name = "userId",value="女神id", required = true) int userId){
        try {
            return new ResponseVO(1,"成功",myAttentionService.coundUserByGoddess(userId));
        }catch (Exception e){
            logger.error("MyAttentionController->coundUserByGoddess",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }
}
