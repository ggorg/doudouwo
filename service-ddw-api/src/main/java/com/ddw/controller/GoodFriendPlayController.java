package com.ddw.controller;

import com.ddw.beans.*;
import com.ddw.beans.vo.GoodFriendPlayChatCenterVO;
import com.ddw.services.GoodFriendPlayService;
import com.ddw.token.Token;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 访问地址：/swagger-ui.html
 */
@Api(value = "约战用例",description = "约战用例",tags = {"约战用例"})
@RestController
@RequestMapping("/ddwapp/goodfriendplay")
public class GoodFriendPlayController {
    private final Logger logger = Logger.getLogger(GoodFriendPlayController.class);
    @Autowired
    private GoodFriendPlayService goodFriendPlayService;

    @Token
    @ApiOperation(value = "进入大聊天室")
    @PostMapping("/chatCenter/gointo/{token}")
    public ResponseApiVO<GoodFriendPlayChatCenterVO<GoodFriendPlayRoomListVO>> toChatCenter(@PathVariable String token){
        try {

            return this.goodFriendPlayService.getChatCenter(token);
        }catch (Exception e){
            logger.error("GoodFriendPlayController->toChatCenter",e);
            return new ResponseApiVO(-1,"进入大聊天室失败",null);
        }
    }
    @Token
    @ApiOperation(value = "小房间列表")
    @PostMapping("/room/list/{token}")
    public ResponseApiVO<ListVO<GoodFriendPlayRoomListVO>> toRoomList(@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)GoodFriendPlayRoomListDTO args){
        try {

            return this.goodFriendPlayService.getRoomList(token,args);
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

            return this.goodFriendPlayService.getRoom(token,args);
        }catch (Exception e){
            logger.error("GoodFriendPlayController->toRoom",e);
            return new ResponseApiVO(-1,"进入小房间失败",null);
        }
    }
    @Token
    @ApiOperation(value = "约玩开桌")
    @PostMapping("/offlineplay/create/{token}")
    public ResponseApiVO<GoodFriendPlayRoomVO> createOffLinePlay(@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)CodeDTO args){
        try {

            return this.goodFriendPlayService.createOffLinePlay(token);
        }catch (Exception e){
            logger.error("GoodFriendPlayController->createOffLinePlay",e);
            return new ResponseApiVO(-1,"约玩开桌失败",null);
        }
    }
    @Token
    @ApiOperation(value = "加入约玩")
    @PostMapping("/offlineplay/join/{token}")
    public ResponseApiVO<GoodFriendPlayRoomVO> joinOffLinePlay(@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)CodeDTO args){
        try {

            return this.goodFriendPlayService.join(token,args);
        }catch (Exception e){
            logger.error("GoodFriendPlayController->joinOffLinePlay",e);
            return new ResponseApiVO(-1,"加入约玩失败",null);
        }
    }
    @Token
    @ApiOperation(value = "踢除开桌里的用户")
    @PostMapping("/offlineplay/out/{token}")
    public ResponseApiVO outUserOffLinePlay(@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)GoodFriendPlayOutUserDTO args){
        try {

            return this.goodFriendPlayService.outUser(token,args);
        }catch (Exception e){
            logger.error("GoodFriendPlayController->outUserOffLinePlay",e);
            return new ResponseApiVO(-1,"约玩踢人失败",null);
        }
    }
    @Token
    @ApiOperation(value = "禁止某用户加入开桌")
    @PostMapping("/offlineplay/disabledjoin/{token}")
    public ResponseApiVO<GoodFriendPlayRoomVO> disabledUserOffLinePlay(@PathVariable String token, @RequestBody @ApiParam(name="args",value="传入json格式",required=true)GoodFriendPlayOutUserDTO args){
        try {

            return this.goodFriendPlayService.disabledUserJoin(token,args);
        }catch (Exception e){
            logger.error("GoodFriendPlayController->disabledUserOffLinePlay",e);
            return new ResponseApiVO(-1,"约玩踢人失败",null);
        }
    }
    @Token
    @ApiOperation(value = "创建房间")
    @PostMapping("/room/create/{token}")
    public ResponseApiVO createRoom(@PathVariable String token,GoodFriendPlayCreateRoomDTO args){
        try {

            return this.goodFriendPlayService.createRoom(token,args);
        }catch (Exception e){
            logger.error("GoodFriendPlayController->createRoom",e);
            return new ResponseApiVO(-1,"创建预约房间失败",null);
        }
    }
    @Token
    @ApiOperation(value = "查询桌号列表")
    @PostMapping("/tables/query/{token}")
    public ResponseApiVO<ListVO<GoodFriendPlayTableVO>> searchTables(@PathVariable String token){
        try {

            return this.goodFriendPlayService.getTables(token);
        }catch (Exception e){
            logger.error("GoodFriendPlayController->searchTables",e);
            return new ResponseApiVO(-1,"查询空闲桌号失败",null);
        }
    }
}
