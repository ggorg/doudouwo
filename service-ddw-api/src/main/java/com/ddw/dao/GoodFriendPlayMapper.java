package com.ddw.dao;

import com.ddw.beans.GoodFriendPlayRoomListVO;
import com.ddw.beans.vo.AppIndexGoddessVO;
import com.ddw.beans.vo.LiveRadioListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Jacky on 2018/6/3.
 */
@Mapper
public interface GoodFriendPlayMapper {
    List<GoodFriendPlayRoomListVO> getRoomList(@Param("centerId") Integer centerId,@Param("disabled") Integer disabled,@Param("type") Integer type,@Param("status") Integer status, @Param("startNum") Integer startNum, @Param("endNum") Integer endNum);}
