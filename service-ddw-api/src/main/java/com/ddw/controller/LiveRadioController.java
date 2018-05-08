package com.ddw.controller;

import com.ddw.beans.*;
import com.ddw.services.LiveRadioClientService;
import com.ddw.services.ReviewService;
import com.ddw.token.Token;
import com.ddw.token.TokenUtil;
import io.swagger.annotations.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
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
            return this.reviewService.applyLiveRadio((String) TokenUtil.getUserObject(token),TokenUtil.getStoreId(token));
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
            return this.liveRadioClientService.toLiveRadio((String) TokenUtil.getUserObject(token),TokenUtil.getStoreId(token));
        }catch (Exception e){
            logger.error("LiveRadioController->applLiveRadio",e);
            return new ResponseApiVO(-1,"查看失败",null);

        }
    }
    @Token
    @ApiOperation(value = "获取女神直播列表",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/queryLiveRadioList/{token}")
    @ResponseBody
    public ResponseApiVO<ListVO<LiveRadioListVO>> toLiveRadioList(@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)PageNoDTO args){
        try {
            return this.liveRadioClientService.getLiveRadioListByStore(args.getPageNo(),TokenUtil.getStoreId(token));
        }catch (Exception e){
            logger.error("LiveRadioController->toLiveRadioList",e);
            return new ResponseApiVO(-1,"获取直播列表失败",null);
        }
    }
    @Token
    @ApiOperation(value = "选择直播房间",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/selectLiveRadioRoom/{token}")
    @ResponseBody
    public ResponseApiVO<SelectLiveRadioVO> selectLiveRadioRoom(@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)CodeDTO args){
        try {
            return this.liveRadioClientService.selectLiveRadio(args,TokenUtil.getStoreId(token));
        }catch (Exception e){
            logger.error("LiveRadioController->toLiveRadioList",e);
            return new ResponseApiVO(-1,"选择直播房间",null);
        }
    }

}
