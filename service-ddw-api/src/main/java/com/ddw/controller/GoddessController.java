package com.ddw.controller;


import com.ddw.beans.GoddessDTO;
import com.ddw.beans.GoddessPO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.enums.ErrorCodeEnum;
import com.ddw.services.GoddessService;
import com.ddw.services.ReviewService;
import com.ddw.token.Token;
import com.ddw.token.TokenUtil;
import com.gen.common.vo.ResponseVO;
import io.swagger.annotations.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

    @Autowired
    private ReviewService reviewService;

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

    @Token
    @ApiOperation(value = "女神申请直播",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/applLiveRadio/{token}")
    @ResponseBody
    @ApiResponses(value={
            @ApiResponse(code= 1,message="成功"),
            @ApiResponse(code= -1,message="系统异常"),
            @ApiResponse(code= -2,message="失败"),
            @ApiResponse(code= -2000,message="用户不存在"),
            @ApiResponse(code= -2001,message="请先申请当女神"),
            @ApiResponse(code= -2002,message="直播房间已开，请关闭再申请"),
            @ApiResponse(code= -2003,message="正在审核中，请耐心等待")
    })
    public ResponseApiVO applLiveRadio(@PathVariable String token){
        try {
            return this.reviewService.applyLiveRadio((String)TokenUtil.getUserObject(token),TokenUtil.getStoreIdObject(token));
        }catch (Exception e){
            logger.error("GoddessController->applLiveRadio",e);
            return new ResponseApiVO(-1,"提交申请失败",null);

        }
    }
}
