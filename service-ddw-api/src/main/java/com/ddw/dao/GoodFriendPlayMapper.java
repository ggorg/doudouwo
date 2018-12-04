package com.ddw.dao;

import com.ddw.beans.vo.GoodFriendPlayRoomListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Jacky on 2018/6/3.
 */
public interface GoodFriendPlayMapper {
    List<GoodFriendPlayRoomListVO> getRoomList(@Param("centerId") Integer centerId, @Param("disabled") Integer disabled, @Param("type") Integer type, @Param("status") String status, @Param("roomOwner") Integer roomOwner, @Param("startNum") Integer startNum, @Param("endNum") Integer endNum);
    List<GoodFriendPlayRoomListVO> getIndexRoomList(@Param("centerId") Integer centerId, @Param("disabled") Integer disabled, @Param("startNum") Integer startNum, @Param("endNum") Integer endNum);
    List selectIsCanCreateRoom(@Param("userId") Integer userId);
    List selecrHistoryFriend(@Param("roomId") Integer roomId,@Param("userId") Integer userId, @Param("startNum") Integer startNum, @Param("endNum") Integer endNum);
}
