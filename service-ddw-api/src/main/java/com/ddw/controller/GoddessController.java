package com.ddw.controller;


import com.ddw.beans.GoddessDTO;
import com.ddw.beans.GoddessPO;
import com.ddw.services.GoddessService;
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
@Api(value = "女神用例",description = "女神用例",tags = {"女神用例"})
@RestController
@RequestMapping("/ddwapp/goddess")
public class GoddessController {
    private final Logger logger = Logger.getLogger(GoddessController.class);
    @Autowired
    private GoddessService goddessService;

    @Token
    @ApiOperation(value = "女神信息添加")
    @PostMapping("/save/{token}")
    public ResponseVO save(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)GoddessDTO args){
        try {
            GoddessPO goddessPO = goddessService.query(args.getUserId());
            if(goddessPO == null){
                return goddessService.save(args);
            }else{
                return goddessService.update(args);
            }
        }catch (Exception e){
            logger.error("GoddessController->save",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }


    @Token
    @ApiOperation(value = "女神信息查询")
    @PostMapping("/query/{token}")
    public ResponseVO query(@PathVariable String token,@RequestParam @ApiParam(name = "userId",value="userId", required = true) int userId){
        try {
            return new ResponseVO(1,"成功",goddessService.query(userId));
        }catch (Exception e){
            logger.error("GoddessController->query",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }
}
