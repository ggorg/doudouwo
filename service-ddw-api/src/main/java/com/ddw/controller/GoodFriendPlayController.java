package com.ddw.controller;

import com.ddw.beans.*;
import com.ddw.token.Token;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;

/**
 * 访问地址：/swagger-ui.html
 */
@Api(value = "约战用例",description = "约战用例",tags = {"约战用例"})
@RestController
@RequestMapping("/ddwapp/goodfriendplay")
public class GoodFriendPlayController {
    private final Logger logger = Logger.getLogger(GoodFriendPlayController.class);


    @Token
    @ApiOperation(value = "进入大聊天室")
    @PostMapping("/chatCenter/gointo/{token}")
    public ResponseApiVO<GoodFriendPlayChatCenterVO<GoodFriendPlayRoomListVO>> toChatCenter(@PathVariable String token){
        try {

            return new ResponseApiVO(1,"成功",null);
        }catch (Exception e){
            logger.error("GoodFriendPlayController->toChatCenter",e);
            return new ResponseApiVO(-1,"进入大聊天室失败",null);
        }
    }
    @Token
    @ApiOperation(value = "小房间列表")
    @PostMapping("/room/list/{token}")
    public ResponseApiVO<ListVO<GoodFriendPlayRoomListVO>> toRoomList(@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)PageNoDTO args){
        try {

            return new ResponseApiVO(1,"成功",null);
        }catch (Exception e){
            logger.error("GoodFriendPlayController->toRoomList",e);
            return new ResponseApiVO(-1,"加载小房间列表失败",null);
        }
    }
    @Token
    @ApiOperation(value = "进入小房间")
    @PostMapping("/room/gointo/{token}")
    public ResponseApiVO<GoodFriendPlayRoomVO> toRoom(@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)CodeDTO args){
        try {

            return new ResponseApiVO(1,"成功",null);
        }catch (Exception e){
            logger.error("GoodFriendPlayController->toRoom",e);
            return new ResponseApiVO(-1,"进入小房间失败",null);
        }
    }
    @Token
    @ApiOperation(value = "创建预约房间")
    @PostMapping("/room/create/{token}")
    public ResponseApiVO createRoom(@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)GoodFriendPlayCreateRoomDTO args){
        try {

            return new ResponseApiVO(1,"成功",null);
        }catch (Exception e){
            logger.error("GoodFriendPlayController->createRoom",e);
            return new ResponseApiVO(-1,"创建预约房间失败",null);
        }
    }
    @Token
    @ApiOperation(value = "查询空闲桌号")
    @PostMapping("/tables/query/{token}")
    public ResponseApiVO<ListVO<GoodFriendPlayTableVO>> searchTables(@PathVariable String token){
        try {

            return new ResponseApiVO(1,"成功",null);
        }catch (Exception e){
            logger.error("GoodFriendPlayController->searchTables",e);
            return new ResponseApiVO(-1,"查询空闲桌号失败",null);
        }
    }
}
