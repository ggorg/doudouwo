package com.ddw.dao;

import com.ddw.beans.vo.AppIndexGoddessVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Jacky on 2018/6/3.
 */
@Mapper
public interface GoddessMapper {
    public List<AppIndexGoddessVO> getGoddessList(@Param("storeId") Integer storeId, @Param("start")Integer start, @Param("end")Integer end);
    public Integer getGoddessListCount(@Param("storeId") Integer storeId);
}
