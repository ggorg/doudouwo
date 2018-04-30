package com.ddw.controller;

import com.ddw.beans.ResponseApiVO;
import com.ddw.services.LiveRadioClientService;
import com.ddw.services.ReviewService;
import com.ddw.token.Token;
import com.ddw.token.TokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ddwapp/liveradio")
@Api(description="直播",tags = "直播")
public class LiveRadioController {
    private final Logger logger = Logger.getLogger(LiveRadioController.class);

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private LiveRadioClientService liveRadioClientService;

    @Token
    @ApiOperation(value = "女神申请直播",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/appl/{token}")
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
            return this.reviewService.applyLiveRadio((String) TokenUtil.getUserObject(token),TokenUtil.getStoreIdObject(token));
        }catch (Exception e){
            logger.error("LiveRadioController->applLiveRadio",e);
            return new ResponseApiVO(-1,"提交申请失败",null);

        }
    }
    @Token
    @ApiOperation(value = "查看是否有直播权限",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/queryLiveRadioPower/{token}")
    @ResponseBody
    @ApiResponses(value={
            @ApiResponse(code= 1,message="成功"),
            @ApiResponse(code= -1,message="系统异常"),
            @ApiResponse(code= -2,message="失败"),
            @ApiResponse(code= -2000,message="用户不存在"),
            @ApiResponse(code= -2001,message="请先申请当女神"),
            @ApiResponse(code= -2004,message="请向管理员申请开通直播")
    })
    public ResponseApiVO toLiveRadio(@PathVariable String token){
        try {
            return this.liveRadioClientService.toLiveRadio((String) TokenUtil.getUserObject(token),TokenUtil.getStoreIdObject(token));
        }catch (Exception e){
            logger.error("LiveRadioController->applLiveRadio",e);
            return new ResponseApiVO(-1,"查看失败",null);

        }
    }
}
