package com.ddw.controller;


import com.ddw.beans.MyAttentionDTO;
import com.ddw.beans.MyAttentionPO;
import com.ddw.services.MyAttentionService;
import com.ddw.token.Token;
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

    @Token
    @ApiOperation(value = "关注添加")
    @PostMapping("/save/{token}")
    public ResponseVO save(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)MyAttentionDTO args){
        try {
            MyAttentionPO myAttentionPO = myAttentionService.query(args);
            if(myAttentionPO == null){
                return myAttentionService.save(args);
            }else{
                return new ResponseVO(-2,"已关注",null);
            }
        }catch (Exception e){
            logger.error("MyAttentionController->save",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @Token
    @ApiOperation(value = "取消关注")
    @PostMapping("/delete/{token}")
    public ResponseVO delete(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)MyAttentionDTO args){
        try {
            return myAttentionService.delete(args);
        }catch (Exception e){
            logger.error("MyAttentionController->save",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @Token
    @ApiOperation(value = "根据用户id查询关注的女神列表")
    @PostMapping("/query/{token}")
    public ResponseVO queryGoddessByUserId(@PathVariable String token,@RequestParam @ApiParam(name = "userId",value="userId", required = true) int userId){
        try {
            return new ResponseVO(1,"成功",myAttentionService.queryGoddessByUserId(userId));
        }catch (Exception e){
            logger.error("MyAttentionController->queryGoddessByUserId",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @Token
    @ApiOperation(value = "查询女神下有多少关注用户数")
    @PostMapping("/coundUserByGoddess/{token}")
    public ResponseVO coundUserByGoddess(@PathVariable String token,@RequestParam @ApiParam(name = "userId",value="userId", required = true) int userId){
        try {
            return new ResponseVO(1,"成功",myAttentionService.coundUserByGoddess(userId));
        }catch (Exception e){
            logger.error("MyAttentionController->coundUserByGoddess",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }
}
