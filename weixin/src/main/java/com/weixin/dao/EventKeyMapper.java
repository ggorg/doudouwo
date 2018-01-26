package com.weixin.dao;

import com.gen.common.util.Page;
import com.weixin.entity.EventKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Jacky on 2017/12/15.
 */
@Mapper
public interface EventKeyMapper {
    List<EventKey> findList(@Param("page") Page page, @Param("appid") String appid);
    /** 查询总数*/
    int findListCount(@Param("appid") String appid);
    List<EventKey> findListAll(@Param("appid") String appid);
    int insert(EventKey entity);
    int delete(@Param("id") Integer id);
    int update(EventKey entity);
    EventKey selectById(@Param("id") int id);
}
