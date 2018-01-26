package com.weixin.dao;

import com.gen.common.util.Page;
import com.weixin.entity.EventRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Jacky on 2017/12/15.
 */
@Mapper
public interface EventRuleMapper {
    List<EventRule> findList(@Param("page") Page page, @Param("appid") String appid);
    /** 查询总数*/
    int findListCount(@Param("appid") String appid);
    List<EventRule> findListAll(@Param("appid") String appid);
    int insert(EventRule entity);
    int delete(@Param("id") Integer id);
    int update(EventRule entity);
    EventRule selectById(@Param("id") int id);
    /** 查询事件类型,1 关注时自动回复（唯一）*/
    EventRule getSubscribe(@Param("appid") String appid);
    /** 查询事件类型2 消息自动回复(唯一）*/
    EventRule getAutoreply(@Param("appid") String appid);
}
