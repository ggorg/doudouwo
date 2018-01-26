package com.weixin.dao;

import com.gen.common.util.Page;
import com.weixin.entity.Push;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Jacky on 2017/12/18.
 */
@Mapper
public interface PushMapper {
    List<Push> findList(@Param("page") Page page, @Param("appid") String appid);
    /** 查询总数*/
    int findListCount(@Param("appid") String appid);
    List<Push> findListAll(@Param("appid") String appid);
    /** 定时器调用查询需要推送的列表*/
    List<Push> findPushList();
    int insert(Push entity);
    int delete(@Param("id") Integer id);
    int update(Push entity);
    Push selectById(@Param("id") int id);
}
